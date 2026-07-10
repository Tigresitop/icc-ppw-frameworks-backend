package ec.edu.ups.icc.fundamentos01.products.services;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.stereotype.Service;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.data.domain.Sort;
import org.springframework.transaction.annotation.Transactional;

import ec.edu.ups.icc.fundamentos01.categories.entities.CategoryEntity;
import ec.edu.ups.icc.fundamentos01.categories.repositories.CategoryRepository;
import ec.edu.ups.icc.fundamentos01.core.dtos.PaginationDto;
import ec.edu.ups.icc.fundamentos01.core.exceptions.domain.BadRequestException;
import ec.edu.ups.icc.fundamentos01.core.exceptions.domain.ConflictException;
import ec.edu.ups.icc.fundamentos01.core.exceptions.domain.NotFoundException;
import ec.edu.ups.icc.fundamentos01.products.dtos.CreateProductDto;
import ec.edu.ups.icc.fundamentos01.products.dtos.PartialUpdateProductDto;
import ec.edu.ups.icc.fundamentos01.products.dtos.ProductFilterByCategoryDto;
import ec.edu.ups.icc.fundamentos01.products.dtos.ProductFilterByUserDto;
import ec.edu.ups.icc.fundamentos01.products.dtos.UpdateProductDto;
import ec.edu.ups.icc.fundamentos01.products.dtos.ProductResponseDto;
import ec.edu.ups.icc.fundamentos01.products.entities.ProductEntity;
import ec.edu.ups.icc.fundamentos01.products.mappers.ProductMapper;
import ec.edu.ups.icc.fundamentos01.products.models.ProductModel;
import ec.edu.ups.icc.fundamentos01.products.repositories.ProductRepository;
import ec.edu.ups.icc.fundamentos01.users.entities.UserEntity;
import ec.edu.ups.icc.fundamentos01.users.repositories.UserRepository;
import ec.edu.ups.icc.fundamentos01.security.services.UserDetailsImpl;


@Service
public class ProductServiceImpl implements ProductService {

    private final ProductRepository productRepository;
    private final UserRepository userRepository;
    private final CategoryRepository categoryRepository;

    public ProductServiceImpl(
            ProductRepository productRepository,
            UserRepository userRepository,
            CategoryRepository categoryRepository
    ) {
        this.productRepository = productRepository;
        this.userRepository = userRepository;
        this.categoryRepository = categoryRepository;
    }

    @Override
    public List<ProductResponseDto> findByUserId(Long userId) {
        if (!userRepository.existsByIdAndDeletedFalse(userId)) {
            throw new NotFoundException("User not found");
        }

        return productRepository.findByOwner_IdAndDeletedFalse(userId)
                .stream()
                .map(ProductMapper::toModelFromEntity)
                .map(ProductMapper::toResponse)
                .toList();
    }

    @Override
    public List<ProductResponseDto> findAll() {
        return productRepository.findAll()
                .stream()
                .filter(entity -> !entity.isDeleted())
                .map(ProductMapper::toModelFromEntity)
                .map(ProductMapper::toResponse)
                .toList();
    }

    @Override
    public ProductResponseDto findOne(Long id) {
        ProductEntity entity = findActiveProductOrThrow(id);
        ProductModel model = ProductMapper.toModelFromEntity(entity);
        return ProductMapper.toResponse(model);
    }

    @Override
    @Transactional
    public ProductResponseDto create(CreateProductDto dto, UserDetailsImpl currentUser) {
        UserEntity owner = findCurrentUserEntity(currentUser);
        Set<CategoryEntity> categories = validateAndGetCategories(dto.getCategoryIds());

        if (productRepository.findByNameIgnoreCaseAndDeletedFalse(dto.getName()).isPresent()) {
            throw new ConflictException("Product name already registered");
        }

        ProductEntity entity = new ProductEntity();
        entity.setName(dto.getName());
        entity.setPrice(dto.getPrice());
        entity.setStock(dto.getStock());
        entity.setOwner(owner);
        entity.setCategories(categories);

        ProductEntity savedEntity = productRepository.save(entity);
        ProductModel savedModel = ProductMapper.toModelFromEntity(savedEntity);
        return ProductMapper.toResponse(savedModel);
    }

    @Override
    @Transactional
    public ProductResponseDto update(Long id, UpdateProductDto dto, UserDetailsImpl currentUser) {
        ProductEntity entity = findActiveProductOrThrow(id);
        
        // SE VALIDA QUE EL USUARIO AUTENTICADO PUEDA MODIFICAR ESTE PRODUCTO
        validateOwnership(entity, currentUser);

        Set<CategoryEntity> categories = validateAndGetCategories(dto.getCategoryIds());

        entity.setName(dto.getName());
        entity.setPrice(dto.getPrice());
        entity.setStock(dto.getStock());
        entity.setCategories(categories);

        ProductEntity savedEntity = productRepository.save(entity);
        ProductModel model = ProductMapper.toModelFromEntity(savedEntity);
        return ProductMapper.toResponse(model);
    }

    @Override
    @Transactional
    public ProductResponseDto partialUpdate(Long id, PartialUpdateProductDto dto, UserDetailsImpl currentUser) {
        ProductEntity entity = findActiveProductOrThrow(id);
        
        validateOwnership(entity, currentUser);

        if (dto.getName() != null) {
            entity.setName(dto.getName());
        }
        if (dto.getPrice() != null) {
            entity.setPrice(dto.getPrice());
        }
        if (dto.getStock() != null) {
            entity.setStock(dto.getStock());
        }
        if (dto.getCategoryIds() != null) {
            Set<CategoryEntity> categories = validateAndGetCategories(dto.getCategoryIds());
            entity.setCategories(categories);
        }

        ProductEntity savedEntity = productRepository.save(entity);
        ProductModel model = ProductMapper.toModelFromEntity(savedEntity);
        return ProductMapper.toResponse(model);
    }

    @Override
    @Transactional
    public void delete(Long id, UserDetailsImpl currentUser) {
        ProductEntity entity = findActiveProductOrThrow(id);
        
        validateOwnership(entity, currentUser);

        entity.setDeleted(true);
        productRepository.save(entity);
    }

    @Override
    public List<ProductResponseDto> findByUserIdWithFilters(
            Long userId,
            ProductFilterByUserDto filters
    ) {
        if (!userRepository.existsByIdAndDeletedFalse(userId)) {
            throw new NotFoundException("User not found");
        }

        validateFilters(filters);
        String name = normalizeName(filters.getName());

        return productRepository.findByOwnerIdWithFilters(
                        userId, name, filters.getMinPrice(), filters.getMaxPrice()
                ).stream()
                .map(ProductMapper::toModelFromEntity)
                .map(ProductMapper::toResponse)
                .toList();
    }

    @Override
    public List<ProductResponseDto> findByCategoryIdWithFilters(
            Long categoryId,
            ProductFilterByCategoryDto filters
    ) {
        if (!categoryRepository.existsByIdAndDeletedFalse(categoryId)) {
            throw new NotFoundException("Category not found");
        }

        validateCategoryFilters(filters);
        String name = normalizeName(filters.getName());

        return productRepository.findByCategoryIdWithFilters(
                        categoryId, name, filters.getMinPrice(), filters.getMaxPrice(), filters.getUserId()
                ).stream()
                .map(ProductMapper::toModelFromEntity)
                .map(ProductMapper::toResponse)
                .toList();
    }

    @Override
    @Transactional(readOnly = true)
    public Page<ProductResponseDto> findAllPage(PaginationDto pagination, UserDetailsImpl currentUser) {
        Pageable pageable = createPageable(pagination);
        
        boolean isAdmin = hasRole(currentUser, "ROLE_ADMIN");
        Long currentUserId = currentUser.getId();
        
        return productRepository.findActivePageWithOwnership(currentUserId, isAdmin, pageable)
                .map(ProductMapper::toModelFromEntity)
                .map(ProductMapper::toResponse);
    }

    @Override
    @Transactional(readOnly = true)
    public Slice<ProductResponseDto> findAllSlice(PaginationDto pagination, UserDetailsImpl currentUser) {
        Pageable pageable = createPageable(pagination);
        
        boolean isAdmin = hasRole(currentUser, "ROLE_ADMIN");
        Long currentUserId = currentUser.getId();
        
        return productRepository.findActiveSliceWithOwnership(currentUserId, isAdmin, pageable)
                .map(ProductMapper::toModelFromEntity)
                .map(ProductMapper::toResponse);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<ProductResponseDto> findByCategoryIdWithFiltersPage(
            Long categoryId,
            ProductFilterByCategoryDto filters,
            PaginationDto pagination
    ) {
        if (!categoryRepository.existsByIdAndDeletedFalse(categoryId)) {
            throw new NotFoundException("Category not found");
        }

        validateCategoryFilters(filters);
        String name = normalizeName(filters.getName());
        Pageable pageable = createPageable(pagination);

        return productRepository.findByCategoryIdWithFiltersPage(
                categoryId, name, filters.getMinPrice(), filters.getMaxPrice(), filters.getUserId(), pageable
        ).map(ProductMapper::toModelFromEntity).map(ProductMapper::toResponse);
    }

    @Override
    @Transactional(readOnly = true)
    public Slice<ProductResponseDto> findByCategoryIdWithFiltersSlice(
            Long categoryId,
            ProductFilterByCategoryDto filters,
            PaginationDto pagination
    ) {
        if (!categoryRepository.existsByIdAndDeletedFalse(categoryId)) {
            throw new NotFoundException("Category not found");
        }

        validateCategoryFilters(filters);
        String name = normalizeName(filters.getName());
        Pageable pageable = createPageable(pagination);

        return productRepository.findByCategoryIdWithFiltersSlice(
                categoryId, name, filters.getMinPrice(), filters.getMaxPrice(), filters.getUserId(), pageable
        ).map(ProductMapper::toModelFromEntity).map(ProductMapper::toResponse);
    }

    private ProductEntity findActiveProductOrThrow(Long id) {
        return productRepository.findById(id)
                .filter(product -> !product.isDeleted())
                .orElseThrow(() -> new NotFoundException("Product not found"));
    }

    private UserEntity findCurrentUserEntity(UserDetailsImpl currentUser) {
        if (currentUser == null) {
            throw new AccessDeniedException("Usuario no autenticado");
        }
        return userRepository.findByIdAndDeletedFalse(currentUser.getId())
                .orElseThrow(() -> new AccessDeniedException("Usuario no autorizado"));
    }

    private void validateOwnership(ProductEntity product, UserDetailsImpl currentUser) {
        if (currentUser == null) {
            throw new AccessDeniedException("Usuario no autenticado");
        }

        if (hasRole(currentUser, "ROLE_ADMIN")) {
            return;
        }

        if (product.getOwner() == null || product.getOwner().getId() == null) {
            throw new AccessDeniedException("El producto no tiene propietario válido");
        }

        if (!product.getOwner().getId().equals(currentUser.getId())) {
            throw new AccessDeniedException("No puedes modificar productos ajenos");
        }
    }

    private boolean hasRole(UserDetailsImpl user, String role) {
        return user.getAuthorities()
                .stream()
                .map(GrantedAuthority::getAuthority)
                .anyMatch(authority -> authority.equals(role));
    }

    private Set<CategoryEntity> validateAndGetCategories(Set<Long> categoryIds) {
        if (categoryIds == null || categoryIds.isEmpty()) {
            throw new BadRequestException("Debe seleccionar al menos una categoría");
        }

        Set<CategoryEntity> categories = new HashSet<>();
        for (Long categoryId : categoryIds) {
            CategoryEntity category = categoryRepository.findById(categoryId)
                    .orElseThrow(() -> new NotFoundException("Category not found"));

            if (category.isDeleted()) {
                throw new NotFoundException("Category not found");
            }
            categories.add(category);
        }
        return categories;
    }

    private void validateFilters(ProductFilterByUserDto filters) {
        if (filters == null) return;
        if (!filters.hasValidPriceRange()) {
            throw new BadRequestException("El precio máximo debe ser mayor o igual al precio mínimo");
        }
    }

    private void validateCategoryFilters(ProductFilterByCategoryDto filters) {
        if (filters == null) return;
        if (!filters.hasValidPriceRange()) {
            throw new BadRequestException("El precio máximo debe ser mayor o igual al precio mínimo");
        }
        if (filters.getUserId() != null && !userRepository.existsByIdAndDeletedFalse(filters.getUserId())) {
            throw new NotFoundException("User not found");
        }
    }

    private String normalizeName(String name) {
        if (name == null || name.isBlank()) return null;
        return name.trim();
    }

    private Pageable createPageable(PaginationDto pagination) {
        String sortBy = normalizeSortBy(pagination.getSortBy());
        Sort.Direction direction = normalizeDirection(pagination.getDirection());
        Sort sort = Sort.by(direction, sortBy);

        return PageRequest.of(
                pagination.getPage(),
                pagination.getSize(),
                sort
        );
    }

    private String normalizeSortBy(String sortBy) {
        if (sortBy == null || sortBy.isBlank()) {
            return "id";
        }
        Set<String> allowedFields = Set.of("id", "name", "price", "stock", "createdAt", "updatedAt");
        if (!allowedFields.contains(sortBy)) {
            throw new BadRequestException("Campo de ordenamiento no permitido: " + sortBy);
        }
        return sortBy;
    }

    private Sort.Direction normalizeDirection(String direction) {
        if (direction == null || direction.isBlank()) {
            return Sort.Direction.ASC;
        }
        if (direction.equalsIgnoreCase("asc")) return Sort.Direction.ASC;
        if (direction.equalsIgnoreCase("desc")) return Sort.Direction.DESC;
        
        throw new BadRequestException("Dirección de ordenamiento no válida: " + direction);
    }
}
package ec.edu.ups.icc.fundamentos01.products.mappers;

import ec.edu.ups.icc.fundamentos01.categories.dtos.CategoryResponseDto;
import ec.edu.ups.icc.fundamentos01.products.dtos.CreateProductDto;
import ec.edu.ups.icc.fundamentos01.products.dtos.ProductResponseDto;
import ec.edu.ups.icc.fundamentos01.products.entities.ProductEntity;
import ec.edu.ups.icc.fundamentos01.products.models.ProductModel;
import ec.edu.ups.icc.fundamentos01.users.dtos.UserResponseDto;

public class ProductMapper {

    public static ProductModel toModelFormDTO(CreateProductDto dto) {
        ProductModel model = new ProductModel();
        model.setName(dto.getName());
        model.setPrice(dto.getPrice());
        model.setStock(dto.getStock());
        return model;
    }

    public static ProductModel toModelFromEntity(ProductEntity entity) {
        ProductModel model = new ProductModel();
        model.setId(entity.getId());
        model.setName(entity.getName());
        model.setPrice(entity.getPrice());
        model.setStock(entity.getStock());
        model.setCreatedAt(entity.getCreatedAt());
        model.setUpdatedAt(entity.getUpdatedAt());
        model.setDeleted(entity.isDeleted());
        model.setOwner(entity.getOwner());
        model.setCategory(entity.getCategory());
        return model;
    }

    public static ProductEntity toEntityFromModel(ProductModel model) {
        ProductEntity entity = new ProductEntity();
        entity.setId(model.getId());
        entity.setName(model.getName());
        entity.setPrice(model.getPrice());
        entity.setStock(model.getStock());
        entity.setOwner(model.getOwner());
        entity.setCategory(model.getCategory());
        return entity;
    }

    public static ProductResponseDto toResponse(ProductModel model) {
        ProductResponseDto response = new ProductResponseDto();
        response.setId(model.getId());
        response.setName(model.getName());
        response.setPrice(model.getPrice());
        response.setStock(model.getStock());
        response.setCreatedAt(model.getCreatedAt());
        response.setUpdatedAt(model.getUpdatedAt());

        if (model.getOwner() != null) {
            UserResponseDto ownerDto = new UserResponseDto();
            ownerDto.setId(model.getOwner().getId());
            ownerDto.setName(model.getOwner().getName());
            ownerDto.setEmail(model.getOwner().getEmail());
            response.setOwner(ownerDto);
        }

        if (model.getCategory() != null) {
            CategoryResponseDto categoryDto = new CategoryResponseDto();
            categoryDto.setId(model.getCategory().getId());
            categoryDto.setName(model.getCategory().getName());
            categoryDto.setDescription(model.getCategory().getDescription());
            response.setCategory(categoryDto);
        }

        return response;
    }

    
}
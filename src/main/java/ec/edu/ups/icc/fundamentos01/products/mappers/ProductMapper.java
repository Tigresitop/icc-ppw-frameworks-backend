package ec.edu.ups.icc.fundamentos01.products.mappers;

import java.util.HashSet;

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
        if (entity.getCategories() != null) {
            model.setCategories(entity.getCategories().stream().toList());
        }
        return model;
    }

    public static ProductEntity toEntityFromModel(ProductModel model) {
        ProductEntity entity = new ProductEntity();
        entity.setId(model.getId());
        entity.setName(model.getName());
        entity.setPrice(model.getPrice());
        entity.setStock(model.getStock());
        entity.setOwner(model.getOwner());
        if (model.getCategories() != null) {
            entity.setCategories(new HashSet<>(model.getCategories()));
        }
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

        if (model.getCategories() != null) {
            response.setCategories(model.getCategories().stream().map(cat -> {
                CategoryResponseDto categoryDto = new CategoryResponseDto();
                categoryDto.setId(cat.getId());
                categoryDto.setName(cat.getName());
                categoryDto.setDescription(cat.getDescription());
                return categoryDto;
            }).toList());
        }

        return response;
    }
}
    

package com.practice.infrastructure.persistence.mapper;

import org.springframework.stereotype.Component;

import com.practice.domain.model.Product;
import com.practice.infrastructure.persistence.ProductEntity;

/**
 * Mapper del adaptador de persistencia JPA.
 * Traduce entre el modelo de dominio y la entidad JPA.
 * Vive en infrastructure porque conoce detalles de la base de datos.
 */
@Component
public class ProductPersistenceMapper {

    public ProductEntity toEntity(Product product) {
        ProductEntity productEntity = new ProductEntity();
        productEntity.setId(product.getId());
        productEntity.setName(product.getName());
        productEntity.setDescription(product.getDescription());
        productEntity.setPrice(product.getPrice());
        productEntity.setStock(product.getStock());
        return productEntity;
    }

    public Product toDomain(ProductEntity productEntity) {
        return Product.builder()
            .id(productEntity.getId())
            .name(productEntity.getName())
            .description(productEntity.getDescription())
            .price(productEntity.getPrice())
            .stock(productEntity.getStock())
            .build();
    }

}

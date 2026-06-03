package com.practice.infrastructure.persistence.mapper;

import org.mapstruct.Mapper;

import com.practice.domain.model.Product;
import com.practice.infrastructure.persistence.entity.ProductEntity;

// Traducción en la frontera del adaptador de persistencia: Product (dominio) ↔ ProductEntity (detalle de BD).
@Mapper(componentModel = "spring")
public interface ProductPersistenceMapper {

    ProductEntity toEntity(Product product);

    Product toDomain(ProductEntity productEntity);

}

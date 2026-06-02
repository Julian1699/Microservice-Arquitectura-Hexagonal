package com.practice.infrastructure.persistence.mapper;

import org.mapstruct.Mapper;

import com.practice.domain.model.Product;
import com.practice.infrastructure.persistence.ProductEntity;

@Mapper(componentModel = "spring")
public interface ProductPersistenceMapper {

     ProductEntity toEntity(Product product);

     Product toDomain(ProductEntity productEntity);

}

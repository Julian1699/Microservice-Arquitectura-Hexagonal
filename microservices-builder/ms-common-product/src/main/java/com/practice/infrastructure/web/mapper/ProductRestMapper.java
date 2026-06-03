package com.practice.infrastructure.web.mapper;

import java.util.List;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import com.practice.domain.model.Product;
import com.practice.infrastructure.web.dto.DCreateProductRequest;
import com.practice.infrastructure.web.dto.DProductResponse;
import com.practice.infrastructure.web.dto.DUpdateProductRequest;

// Traducción en la frontera del adaptador web: DTOs HTTP ↔ Product (tipo que entiende ProductUseCase).
@Mapper(componentModel = "spring")
public interface ProductRestMapper {

    @Mapping(target = "id", ignore = true)
    Product toDomain(DCreateProductRequest dCreateProductRequest);

    @Mapping(target = "id", source = "id")
    Product toDomain(Long id, DUpdateProductRequest dUpdateProductRequest);

    DProductResponse toResponse(Product product);

    List<DProductResponse> toResponseList(List<Product> products);

}

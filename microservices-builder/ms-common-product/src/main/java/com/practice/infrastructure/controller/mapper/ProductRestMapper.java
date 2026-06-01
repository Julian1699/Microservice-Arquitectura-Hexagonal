package com.practice.infrastructure.controller.mapper;

import org.springframework.stereotype.Component;

import com.practice.domain.model.Product;
import com.practice.infrastructure.controller.dto.DCreateProductRequest;
import com.practice.infrastructure.controller.dto.DProductResponse;
import com.practice.infrastructure.controller.dto.DUpdateProductRequest;

@Component
public class ProductRestMapper {

    public Product toDomain(DCreateProductRequest dCreateProductRequest) {
        return Product.builder()
                .name(dCreateProductRequest.getName())
                .description(dCreateProductRequest.getDescription())
                .price(dCreateProductRequest.getPrice())
                .stock(dCreateProductRequest.getStock())
                .build();
    }

    public Product toDomain(Long id, DUpdateProductRequest dUpdateProductRequest) {
        return Product.builder()
                .id(id)
                .name(dUpdateProductRequest.getName())
                .description(dUpdateProductRequest.getDescription())
                .price(dUpdateProductRequest.getPrice())
                .stock(dUpdateProductRequest.getStock())
                .build();
    }

    public DProductResponse toResponse(Product product) {
        return DProductResponse.builder()
                .id(product.getId())
                .name(product.getName())
                .description(product.getDescription())
                .price(product.getPrice())
                .stock(product.getStock())
                .build();
    }

}

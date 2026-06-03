package com.practice.infrastructure.web.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.practice.application.port.in.ProductUseCase;
import com.practice.domain.model.Product;
import com.practice.infrastructure.web.dto.DCreateProductRequest;
import com.practice.infrastructure.web.dto.DUpdateProductRequest;
import com.practice.infrastructure.web.mapper.ProductRestMapper;
import com.practice.infrastructure.web.result.ResultHttpMapper;

import lombok.RequiredArgsConstructor;

// Adaptador web primario (driving): frontera HTTP → ProductUseCase; la respuesta sale por ResultHttpMapper.
@RestController
@RequestMapping("/api/product")
@RequiredArgsConstructor
public class ProductRestController {

    private final ProductUseCase productUseCase;

    private final ProductRestMapper productRestMapper;

    private final ResultHttpMapper resultHttpMapper;

    @PostMapping("/create-product")
    public ResponseEntity<?> createProduct(@RequestBody DCreateProductRequest dCreateProductRequest) {
        Product product = productRestMapper.toDomain(dCreateProductRequest);
        return resultHttpMapper.mapApplicationResultToCreatedResponse(productUseCase.createProduct(product), productRestMapper::toResponse);
    }

    @GetMapping("/find-product-by-id/{id}")
    public ResponseEntity<?> findProductById(@PathVariable Long id) {
        return resultHttpMapper.mapApplicationResultToOkResponse(productUseCase.getProductById(id), productRestMapper::toResponse);
    }

    @GetMapping("/find-all-products")
    public ResponseEntity<?> findAllProducts() {
        return resultHttpMapper.mapApplicationResultToOkResponse(productUseCase.findAllProducts(), productRestMapper::toResponseList);
    }

    @PutMapping("/update-product/{id}")
    public ResponseEntity<?> updateProduct(@PathVariable Long id, @RequestBody DUpdateProductRequest dUpdateProductRequest) {
        Product product = productRestMapper.toDomain(id, dUpdateProductRequest);
        return resultHttpMapper.mapApplicationResultToOkResponse(productUseCase.updateProduct(id, product), productRestMapper::toResponse);
    }

    @DeleteMapping("/delete-product/{id}")
    public ResponseEntity<?> deleteProduct(@PathVariable Long id) {
        return resultHttpMapper.mapApplicationResultToNoContentResponse(productUseCase.deleteProduct(id));
    }

}

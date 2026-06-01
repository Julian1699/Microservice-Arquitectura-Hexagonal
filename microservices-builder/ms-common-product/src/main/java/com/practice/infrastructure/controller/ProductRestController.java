package com.practice.infrastructure.controller;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.practice.application.port.in.ICreateProductInputPort;
import com.practice.application.port.in.IDeleteProductInputPort;
import com.practice.application.port.in.IFindAllProductsInputPort;
import com.practice.application.port.in.IFindProductByIdInputPort;
import com.practice.application.port.in.IUpdateProductInputPort;
import com.practice.domain.model.Product;
import com.practice.infrastructure.controller.dto.DCreateProductRequest;
import com.practice.infrastructure.controller.dto.DProductResponse;
import com.practice.infrastructure.controller.dto.DUpdateProductRequest;
import com.practice.infrastructure.controller.mapper.ProductRestMapper;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/product")
@RequiredArgsConstructor
public class ProductRestController {

    private final ICreateProductInputPort createProductInputPort;

    private final IFindProductByIdInputPort findProductByIdInputPort;

    private final IFindAllProductsInputPort findAllProductsInputPort;

    private final IUpdateProductInputPort updateProductInputPort;

    private final IDeleteProductInputPort deleteProductInputPort;

    private final ProductRestMapper productRestMapper;

    @PostMapping("/create-product")
    public ResponseEntity<DProductResponse> createProduct(@RequestBody DCreateProductRequest dCreateProductRequest) {
        Product product = productRestMapper.toDomain(dCreateProductRequest);
        Product productCreated = createProductInputPort.createProduct(product);
        DProductResponse dProductResponse = productRestMapper.toResponse(productCreated);
        return ResponseEntity.status(HttpStatus.CREATED).body(dProductResponse);
    }

    @GetMapping("/find-product-by-id/{id}")
    public ResponseEntity<DProductResponse> findProductById(@PathVariable Long id) {
        return findProductByIdInputPort.findProductById(id)
            .map(productRestMapper::toResponse)
            .map(ResponseEntity::ok)
            .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping("/find-all-products")
    public ResponseEntity<List<DProductResponse>> findAllProducts() {
        List<DProductResponse> products = findAllProductsInputPort.findAllProducts().stream()
            .map(productRestMapper::toResponse).toList();
        return ResponseEntity.ok(products);
    }

    @PutMapping("/update-product/{id}")
    public ResponseEntity<DProductResponse> updateProduct(
            @PathVariable Long id,
            @RequestBody DUpdateProductRequest dUpdateProductRequest) {
        Product product = productRestMapper.toDomain(id, dUpdateProductRequest);
        return updateProductInputPort.updateProduct(id, product)
            .map(productRestMapper::toResponse)
            .map(ResponseEntity::ok)
            .orElse(ResponseEntity.notFound().build());
    }

    @DeleteMapping("/delete-product/{id}")
    public ResponseEntity<Void> deleteProduct(@PathVariable Long id) {
        if (deleteProductInputPort.deleteProduct(id)) {
            return ResponseEntity.noContent().build();
        }
        return ResponseEntity.notFound().build();
    }

}

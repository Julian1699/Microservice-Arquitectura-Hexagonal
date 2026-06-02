package com.practice.application.service.Impl;

import java.util.List;
import java.util.Optional;

import org.springframework.stereotype.Service;

import com.practice.application.port.out.IProductPersistenceOutputPort;
import com.practice.application.service.ProductServiceUseCase;
import com.practice.domain.model.Product;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class ProductService implements ProductServiceUseCase {

    private final IProductPersistenceOutputPort productPersistenceOutputPort;

    @Override
    public Product createProduct(Product product) {
        validateProduct(product);
        return productPersistenceOutputPort.save(product);
    }

    @Override
    public Optional<Product> findProductById(Long id) {
        return productPersistenceOutputPort.findById(id);
    }

    @Override
    public List<Product> findAllProducts() {
        return productPersistenceOutputPort.findAll();
    }

    @Override
    public Optional<Product> updateProduct(Long id, Product product) {
        if (!productPersistenceOutputPort.existsById(id)) {
            return Optional.empty();
        }
        product.setId(id);
        validateProduct(product);
        return Optional.of(productPersistenceOutputPort.save(product));
    }

    @Override
    public boolean deleteProduct(Long id) {
        if (!productPersistenceOutputPort.existsById(id)) {
            return false;
        }
        productPersistenceOutputPort.deleteById(id);
        return true;
    }

    private void validateProduct(Product product) {
        if (product.getName() == null || product.getName().isBlank()) {
            throw new IllegalArgumentException("Product name is required");
        }
        if (product.getPrice() == null || product.getPrice() <= 0) {
            throw new IllegalArgumentException("Product price must be greater than zero");
        }
        if (product.getStock() == null || product.getStock() < 0) {
            throw new IllegalArgumentException("Product stock cannot be negative");
        }
    }

}

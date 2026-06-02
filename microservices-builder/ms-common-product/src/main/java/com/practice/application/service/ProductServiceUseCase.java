package com.practice.application.service;

import java.util.List;
import java.util.Optional;

import com.practice.domain.model.Product;

public interface ProductServiceUseCase {

    Product createProduct(Product product);

    boolean deleteProduct(Long id);

    List<Product> findAllProducts();

    Optional<Product> findProductById(Long id);

    Optional<Product> updateProduct(Long id, Product product);

}

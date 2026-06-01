package com.practice.application.port.in;

import java.util.Optional;

import com.practice.domain.model.Product;

public interface IUpdateProductInputPort {

    Optional<Product> updateProduct(Long id, Product product);

}

package com.practice.application.port.in;

import java.util.Optional;

import com.practice.domain.model.Product;

public interface IFindProductByIdInputPort {

    Optional<Product> findProductById(Long id);

}

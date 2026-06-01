package com.practice.application.port.out;

import java.util.List;
import java.util.Optional;

import com.practice.domain.model.Product;

public interface IProductPersistenceOutputPort {

    Product save(Product product);

    Optional<Product> findById(Long id);

    List<Product> findAll();

    void deleteById(Long id);

    boolean existsById(Long id);

}

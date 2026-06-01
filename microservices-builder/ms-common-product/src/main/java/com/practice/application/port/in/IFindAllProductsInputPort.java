package com.practice.application.port.in;

import java.util.List;

import com.practice.domain.model.Product;

public interface IFindAllProductsInputPort {

    List<Product> findAllProducts();

}

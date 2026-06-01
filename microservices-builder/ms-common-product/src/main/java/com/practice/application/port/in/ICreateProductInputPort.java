package com.practice.application.port.in;

import com.practice.domain.model.Product;

public interface ICreateProductInputPort {

    Product createProduct(Product product);

}

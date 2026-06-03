package com.practice.application.port.in;

import java.util.List;

import com.practice.application.result.ApplicationResult;
import com.practice.domain.model.Product;

// Puerto de entrada (driving): contrato que invoca el adaptador web; application no conoce HTTP ni DTOs.
public interface ProductUseCase {

    ApplicationResult<Product> createProduct(Product product);

    ApplicationResult<Product> getProductById(Long id);

    ApplicationResult<List<Product>> findAllProducts();

    ApplicationResult<Product> updateProduct(Long id, Product product);

    ApplicationResult<Void> deleteProduct(Long id);

}

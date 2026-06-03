package com.practice.application.service;

import java.util.List;
import java.util.Optional;

import org.springframework.stereotype.Service;

import com.practice.application.port.in.ProductUseCase;
import com.practice.application.port.out.ProductPersistencePort;
import com.practice.application.result.ApplicationError;
import com.practice.application.result.ApplicationResult;
import com.practice.application.result.EErrorType;
import com.practice.domain.model.Product;

// Implementación del puerto ProductUseCase: orquesta dominio, validaciones y el puerto de salida ProductPersistencePort.
@Service
public class ProductService implements ProductUseCase {

    private final ProductPersistencePort productPersistencePort;

    public ProductService(ProductPersistencePort productPersistencePort) {
        this.productPersistencePort = productPersistencePort;
    }

    @Override
    public ApplicationResult<Product> createProduct(Product product) {
        ApplicationError applicationError = validateProduct(product);
        if (applicationError != null) {
            return ApplicationResult.fail(applicationError);
        }
        return ApplicationResult.ok(productPersistencePort.save(product));
    }

    @Override
    public ApplicationResult<Product> getProductById(Long id) {
        Optional<Product> productOptional = productPersistencePort.findById(id);
        if (productOptional.isEmpty()) {
            return ApplicationResult.fail(productNotFoundError(id));
        }
        return ApplicationResult.ok(productOptional.get());
    }

    @Override
    public ApplicationResult<List<Product>> findAllProducts() {
        return ApplicationResult.ok(productPersistencePort.findAll());
    }

    @Override
    public ApplicationResult<Product> updateProduct(Long id, Product product) {
        Optional<Product> productOptional = productPersistencePort.findById(id);
        if (productOptional.isEmpty()) {
            return ApplicationResult.fail(productNotFoundError(id));
        }
        ApplicationError applicationError = validateProduct(product);
        if (applicationError != null) {
            return ApplicationResult.fail(applicationError);
        }
        Product productFound = productOptional.get();
        productFound.setName(product.getName());
        productFound.setDescription(product.getDescription());
        productFound.setPrice(product.getPrice());
        productFound.setStock(product.getStock());
        return ApplicationResult.ok(productPersistencePort.save(productFound));
    }

    @Override
    public ApplicationResult<Void> deleteProduct(Long id) {
        Optional<Product> productOptional = productPersistencePort.findById(id);
        if (productOptional.isEmpty()) {
            return ApplicationResult.fail(productNotFoundError(id));
        }
        productPersistencePort.deleteById(id);
        return ApplicationResult.ok(null);
    }

    // Reglas de dominio antes de delegar en ProductPersistencePort.
    private ApplicationError validateProduct(Product product) {
        if (product.getName() == null || product.getName().trim().isEmpty()) {
            return new ApplicationError("PRODUCT_NAME_REQUIRED", "El nombre del producto es obligatorio", EErrorType.VALIDATION);
        }
        if (product.getPrice() == null || product.getPrice() <= 0) {
            return new ApplicationError("PRODUCT_PRICE_INVALID", "El precio del producto debe ser mayor que cero", EErrorType.VALIDATION);
        }
        if (product.getStock() == null || product.getStock() < 0) {
            return new ApplicationError("PRODUCT_STOCK_INVALID", "El stock del producto no puede ser negativo", EErrorType.VALIDATION);
        }
        return null;
    }

    // ApplicationError estándar cuando ProductPersistencePort no encuentra el identificador.
    private ApplicationError productNotFoundError(Long id) {
        return new ApplicationError("PRODUCT_NOT_FOUND", "No se encontró el producto con id " + id, EErrorType.NOT_FOUND);
    }

}

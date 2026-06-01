package com.practice.infrastructure.persistence;

import java.util.List;
import java.util.Optional;

import org.springframework.stereotype.Repository;

import com.practice.application.port.out.IProductPersistenceOutputPort;
import com.practice.domain.model.Product;
import com.practice.infrastructure.persistence.mapper.ProductPersistenceMapper;

import lombok.RequiredArgsConstructor;

@Repository
@RequiredArgsConstructor
public class JpaProductPersistenceAdapter implements IProductPersistenceOutputPort {

    private final ProductJpaRepository productJpaRepository;

    private final ProductPersistenceMapper productPersistenceMapper;

    @Override
    public Product save(Product product) {
        ProductEntity productEntity = productPersistenceMapper.toEntity(product);
        ProductEntity productEntitySaved = productJpaRepository.save(productEntity);
        return productPersistenceMapper.toDomain(productEntitySaved);
    }

    @Override
    public Optional<Product> findById(Long id) {
        return productJpaRepository.findById(id).map(productPersistenceMapper::toDomain);
    }

    @Override
    public List<Product> findAll() {
        return productJpaRepository.findAll().stream()
            .map(productPersistenceMapper::toDomain)
            .toList();
    }

    @Override
    public void deleteById(Long id) {
        productJpaRepository.deleteById(id);
    }

    @Override
    public boolean existsById(Long id) {
        return productJpaRepository.existsById(id);
    }

}

package com.practice.infrastructure.persistence.adapter;

import java.util.List;
import java.util.Optional;

import org.springframework.stereotype.Repository;

import com.practice.application.port.out.ProductPersistencePort;
import com.practice.domain.model.Product;
import com.practice.infrastructure.persistence.entity.ProductEntity;
import com.practice.infrastructure.persistence.mapper.ProductPersistenceMapper;
import com.practice.infrastructure.persistence.repository.ProductJpaRepository;

import lombok.RequiredArgsConstructor;

// Adaptador de persistencia (driven): implementa ProductPersistencePort y oculta el detalle de BD al núcleo.
@Repository
@RequiredArgsConstructor
public class JpaProductPersistenceAdapter implements ProductPersistencePort {

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
        return productJpaRepository.findAll().stream().map(productPersistenceMapper::toDomain).toList();
    }

    @Override
    public void deleteById(Long id) {
        productJpaRepository.deleteById(id);
    }

}

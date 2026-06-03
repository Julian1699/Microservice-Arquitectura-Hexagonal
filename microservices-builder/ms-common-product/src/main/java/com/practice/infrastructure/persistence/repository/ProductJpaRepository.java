package com.practice.infrastructure.persistence.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.practice.infrastructure.persistence.entity.ProductEntity;

// Acceso técnico a BD; solo lo consume JpaProductPersistenceAdapter, no application ni domain.
public interface ProductJpaRepository extends JpaRepository<ProductEntity, Long> {

}

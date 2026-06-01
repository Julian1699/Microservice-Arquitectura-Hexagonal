package com.practice.infrastructure.persistence;

import org.springframework.data.jpa.repository.JpaRepository;

/**
 * Repositorio técnico de Spring Data JPA.
 * Opera directamente con ProductEntity; no conoce el dominio.
 */
public interface ProductJpaRepository extends JpaRepository<ProductEntity, Long> {

}

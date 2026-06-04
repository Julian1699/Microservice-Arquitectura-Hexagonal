package com.practice.infrastructure.web.controller;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.practice.infrastructure.persistence.entity.ProductEntity;
import com.practice.infrastructure.persistence.repository.ProductJpaRepository;
import com.practice.infrastructure.web.dto.DCreateProductRequest;
import com.practice.infrastructure.web.dto.DUpdateProductRequest;

/**
 * Pruebas de <strong>integración</strong> del adaptador web (pruebas <strong>#9 a #13 de 13</strong>).
 */
@SpringBootTest
@AutoConfigureMockMvc
class ProductRestControllerIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private ProductJpaRepository productJpaRepository;

    /** DTO JSON válido para POST /create-product. */
    private DCreateProductRequest validCreateRequest() {
        DCreateProductRequest dCreateProductRequest = new DCreateProductRequest();
        dCreateProductRequest.setName("Teclado mecánico");
        dCreateProductRequest.setDescription("Teclado RGB con switches rojos");
        dCreateProductRequest.setPrice(250000L);
        dCreateProductRequest.setStock(8);
        return dCreateProductRequest;
    }

    /** DTO JSON válido para PUT /update-product/{id}. */
    private DUpdateProductRequest validUpdateRequest() {
        DUpdateProductRequest dUpdateProductRequest = new DUpdateProductRequest();
        dUpdateProductRequest.setName("Teclado Pro");
        dUpdateProductRequest.setDescription("Teclado actualizado");
        dUpdateProductRequest.setPrice(280000L);
        dUpdateProductRequest.setStock(5);
        return dUpdateProductRequest;
    }

    /**
     * Inserta un producto en H2 para preparar escenarios de GET, PUT o DELETE
     * sin depender de otro test.
     */
    private ProductEntity persistProduct() {
        ProductEntity productEntity = new ProductEntity();
        productEntity.setName("Teclado mecánico");
        productEntity.setDescription("Teclado RGB con switches rojos");
        productEntity.setPrice(250000L);
        productEntity.setStock(8);
        return productJpaRepository.save(productEntity);
    }

    @BeforeEach
    void cleanDatabase() {
        // 1. Vacía H2 para que cada prueba arranque sin datos de la anterior.
        productJpaRepository.deleteAll();
    }

    /**
     * Prueba #9 — CREATE: POST crea producto; HTTP 201 y un registro en H2.
     */
    @Test
    void givenValidRequest_whenCreateProduct_thenReturnsCreatedProduct() throws Exception {
        // Given
        // 1. DTO de alta (ver validCreateRequest() arriba).
        DCreateProductRequest dCreateProductRequest = validCreateRequest();
        // When
        // 2. POST con JSON.
        // 3. MockMvc simula HTTP sin abrir puerto real.
        mockMvc.perform(post("/api/product/create-product")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(dCreateProductRequest)))
        // Then — HTTP
        // 4. Status 201 Created.
                .andExpect(status().isCreated())
        // 5. JSON con id y nombre del DTO de alta.
                .andExpect(jsonPath("$.id").exists())
                .andExpect(jsonPath("$.name").value("Teclado mecánico"));
        // Then — H2
        // 6. Un solo registro persistido.
        assertEquals(1, productJpaRepository.count());
    }

    /**
     * Prueba #10 — READ por id: GET devuelve el producto cargado en H2; HTTP 200.
     */
    @Test
    void givenExistingProduct_whenFindProductById_thenReturnsProduct() throws Exception {
        // Given
        // 1. Producto en H2 (ver persistProduct() arriba).
        ProductEntity productEntityPersisted = persistProduct();
        Long productId = productEntityPersisted.getId();
        // When
        // 2. GET con id en la ruta.
        mockMvc.perform(get("/api/product/find-product-by-id/{id}", productId))
        // Then
        // 3. HTTP 200 y JSON coherente con lo insertado en H2.
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(productId))
                .andExpect(jsonPath("$.name").value("Teclado mecánico"));
    }

    /**
     * Prueba #11 — READ listar: GET devuelve array con al menos un producto; HTTP 200.
     */
    @Test
    void givenExistingProduct_whenFindAllProducts_thenReturnsList() throws Exception {
        // Given
        // 1. Un producto en H2.
        persistProduct();
        // When
        // 2. GET listado completo.
        mockMvc.perform(get("/api/product/find-all-products"))
        // Then
        // 3. Array JSON con un elemento.
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(1))
                .andExpect(jsonPath("$[0].name").value("Teclado mecánico"));
    }

    /**
     * Prueba #12 — UPDATE: PUT actualiza respuesta HTTP y fila en H2; HTTP 200.
     */
    @Test
    void givenExistingProduct_whenUpdateProduct_thenReturnsUpdatedProduct() throws Exception {
        // Given
        // 1. Producto base en H2.
        ProductEntity productEntityPersisted = persistProduct();
        Long productId = productEntityPersisted.getId();
        // 2. DTO de actualización (ver validUpdateRequest() arriba).
        DUpdateProductRequest dUpdateProductRequest = validUpdateRequest();
        // When
        // 3. PUT con id y JSON.
        mockMvc.perform(put("/api/product/update-product/{id}", productId)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(dUpdateProductRequest)))
        // Then — HTTP
        // 4. Status 200 y campos del DTO de actualización.
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("Teclado Pro"))
                .andExpect(jsonPath("$.price").value(280000));
        // Then — H2
        // 5. Verificar persistencia del cambio.
        ProductEntity productEntityUpdated = productJpaRepository.findById(productId).orElseThrow();
        assertEquals("Teclado Pro", productEntityUpdated.getName());
        assertEquals(280000L, productEntityUpdated.getPrice());
    }

    /**
     * Prueba #13 — DELETE: DELETE borra el registro; HTTP 204 y H2 sin ese id.
     */
    @Test
    void givenExistingProduct_whenDeleteProduct_thenRemovesItFromDatabase() throws Exception {
        // Given
        // 1. Producto en H2 listo para borrar.
        ProductEntity productEntityPersisted = persistProduct();
        Long productId = productEntityPersisted.getId();
        // When
        // 2. DELETE por id.
        mockMvc.perform(delete("/api/product/delete-product/{id}", productId))
        // Then — HTTP
        // 3. Status 204 No Content.
                .andExpect(status().isNoContent());
        // Then — H2
        // 4. El id ya no existe en la tabla.
        assertTrue(productJpaRepository.findById(productId).isEmpty());
    }

}

package com.practice.application.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;
import static org.mockito.Mockito.never;

import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.practice.application.port.out.ProductPersistencePort;
import com.practice.application.result.ApplicationResult;
import com.practice.domain.model.Product;

/**
 * Pruebas <strong>unitarias</strong> de {@link ProductService} (pruebas <strong>#2 a #8 de 13</strong>).
 */
@ExtendWith(MockitoExtension.class)
class ProductServiceTest {

    @Mock
    private ProductPersistencePort productPersistencePort;

    @InjectMocks
    private ProductService productService;

    /** Producto de dominio válido para alta o actualización (sin id). */
    private Product validProductToCreate() {
        return Product.builder()
                .name("Teclado mecánico")
                .description("Teclado RGB con switches rojos")
                .price(250000L)
                .stock(8)
                .build();
    }

    /** Producto de dominio ya persistido (con id), para simular respuesta del puerto. */
    private Product validProductWithId(Long productId) {
        return Product.builder()
                .id(productId)
                .name("Teclado mecánico")
                .description("Teclado RGB con switches rojos")
                .price(250000L)
                .stock(8)
                .build();
    }

    /**
     * Prueba #2 — CREATE: producto válido devuelve ok y delega en {@code save}.
     */
    @Test
    void givenValidProduct_whenCreateProduct_thenReturnsCreatedProduct() {
        // Given
        // 1. Producto de dominio válido sin id (ver validProductToCreate() arriba).
        Product productToCreate = validProductToCreate();
        // 2. Respuesta simulada del puerto tras persistir.
        Product productCreated = validProductWithId(1L);
        // 3. Mock: save(productToCreate) retorna productCreated.
        given(productPersistencePort.save(productToCreate)).willReturn(productCreated);
        // When
        // 4. Invocar createProduct del caso de uso.
        ApplicationResult<Product> applicationResult = productService.createProduct(productToCreate);
        // Then
        // 5. Resultado exitoso.
        assertTrue(applicationResult.isOk());
        // 6. Id asignado por persistencia simulada.
        assertEquals(1L, applicationResult.getValue().getId());
        // 7. Verificar interacción con el puerto.
        then(productPersistencePort).should().save(productToCreate);
    }

    /**
     * Prueba #3 — CREATE: precio cero → error de validación; {@code save} no se invoca.
     */
    @Test
    void givenInvalidPrice_whenCreateProduct_thenReturnsValidationError() {
        // Given
        // 1. Producto base y precio inválido solo en este test.
        Product productWithInvalidPrice = validProductToCreate();
        productWithInvalidPrice.setPrice(0L);
        // When
        // 2. Intentar crear; la validación corta antes de persistir.
        ApplicationResult<Product> applicationResult = productService.createProduct(productWithInvalidPrice);
        // Then
        // 3. Fallo controlado en ApplicationResult.
        assertTrue(applicationResult.isFail());
        // 4. Código de error esperado.
        assertEquals("PRODUCT_PRICE_INVALID", applicationResult.getError().getCode());
        // 5. El puerto no debe guardar.
        then(productPersistencePort).should(never()).save(any(Product.class));
    }

    /**
     * Prueba #4 — READ: id existente en el mock → ok con el producto.
     */
    @Test
    void givenExistingId_whenGetProductById_thenReturnsProduct() {
        // Given
        // 1. Mock devuelve producto con id 1.
        Product productFound = validProductWithId(1L);
        given(productPersistencePort.findById(1L)).willReturn(Optional.of(productFound));
        // When
        // 2. Consultar por id.
        ApplicationResult<Product> applicationResult = productService.getProductById(1L);
        // Then
        // 3. Ok con el mismo id.
        assertTrue(applicationResult.isOk());
        assertEquals(1L, applicationResult.getValue().getId());
    }

    /**
     * Prueba #5 — READ: id inexistente → {@code PRODUCT_NOT_FOUND}.
     */
    @Test
    void givenMissingId_whenGetProductById_thenReturnsNotFoundError() {
        // Given
        // 1. Mock devuelve Optional vacío para id 99.
        given(productPersistencePort.findById(99L)).willReturn(Optional.empty());
        // When
        // 2. Consultar id inexistente.
        ApplicationResult<Product> applicationResult = productService.getProductById(99L);
        // Then
        // 3. Error not found (en HTTP sería 404 vía ResultHttpMapper).
        assertTrue(applicationResult.isFail());
        assertEquals("PRODUCT_NOT_FOUND", applicationResult.getError().getCode());
    }

    /**
     * Prueba #6 — READ: listar todos según lo que devuelve el puerto mock.
     */
    @Test
    void givenProductsInPort_whenFindAllProducts_thenReturnsList() {
        // Given
        // 1. Mock devuelve lista con un producto (id 1).
        given(productPersistencePort.findAll()).willReturn(List.of(validProductWithId(1L)));
        // When
        // 2. Ejecutar findAllProducts.
        ApplicationResult<List<Product>> applicationResult = productService.findAllProducts();
        // Then
        // 3. Ok y tamaño de lista correcto.
        assertTrue(applicationResult.isOk());
        assertEquals(1, applicationResult.getValue().size());
    }

    /**
     * Prueba #7 — UPDATE: modifica dominio y llama a {@code save}.
     */
    @Test
    void givenExistingProduct_whenUpdateProduct_thenReturnsUpdatedProduct() {
        // Given
        // 1. Producto existente en el mock.
        Product productExisting = validProductWithId(1L);
        // 2. Datos nuevos; el nombre cambia solo en este test.
        Product productUpdateData = validProductToCreate();
        productUpdateData.setName("Teclado Pro");
        // 3. findById ok; save devuelve productExisting actualizado.
        given(productPersistencePort.findById(1L)).willReturn(Optional.of(productExisting));
        given(productPersistencePort.save(productExisting)).willReturn(productExisting);
        // When
        // 4. Actualizar por id.
        ApplicationResult<Product> applicationResult = productService.updateProduct(1L, productUpdateData);
        // Then
        // 5. Ok con nombre actualizado.
        assertTrue(applicationResult.isOk());
        assertEquals("Teclado Pro", applicationResult.getValue().getName());
        // 6. save invocado sobre la entidad de dominio modificada.
        then(productPersistencePort).should().save(productExisting);
    }

    /**
     * Prueba #8 — DELETE: ok y delega en {@code deleteById}.
     */
    @Test
    void givenExistingProduct_whenDeleteProduct_thenDeletesSuccessfully() {
        // Given
        // 1. Producto existente antes de borrar.
        Product productExisting = validProductWithId(1L);
        given(productPersistencePort.findById(1L)).willReturn(Optional.of(productExisting));
        // When
        // 2. Ejecutar deleteProduct.
        ApplicationResult<Void> applicationResult = productService.deleteProduct(1L);
        // Then
        // 3. Resultado ok.
        assertTrue(applicationResult.isOk());
        // 4. deleteById en el puerto.
        then(productPersistencePort).should().deleteById(1L);
    }

}

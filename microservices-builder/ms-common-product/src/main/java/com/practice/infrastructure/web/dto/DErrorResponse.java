package com.practice.infrastructure.web.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

// Contrato de error en la frontera HTTP; se construye desde ApplicationError en ResultHttpMapper o GlobalExceptionHandler.
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "Respuesta de error de la API")
public class DErrorResponse {

    @Schema(example = "PRODUCT_NOT_FOUND")
    private String code;

    @Schema(example = "Product not found with id: 1")
    private String message;

}

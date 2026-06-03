package com.practice.infrastructure.web.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.Setter;

// Contrato de entrada del adaptador web para actualizaciones; se traduce a Product antes de ProductUseCase.
@Getter
@Setter
@Schema(description = "Datos para actualizar un producto")
public class DUpdateProductRequest {

    @Schema(example = "Teclado mecánico Pro", description = "Nombre del producto")
    private String name;

    @Schema(example = "Teclado RGB actualizado", description = "Descripción del producto")
    private String description;

    @Schema(example = "280000", description = "Precio en pesos colombianos (COP)")
    private Long price;

    @Schema(example = "5", description = "Unidades disponibles en inventario")
    private Integer stock;

}

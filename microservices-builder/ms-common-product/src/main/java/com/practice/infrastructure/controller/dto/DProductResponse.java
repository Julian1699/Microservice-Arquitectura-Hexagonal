package com.practice.infrastructure.controller.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "Producto devuelto por la API")
public class DProductResponse {

    @Schema(example = "1", description = "Identificador del producto")
    private Long id;

    @Schema(example = "Teclado mecánico", description = "Nombre del producto")
    private String name;

    @Schema(example = "Teclado RGB con switches rojos", description = "Descripción del producto")
    private String description;

    @Schema(example = "250000", description = "Precio en pesos colombianos (COP)")
    private Long price;

    @Schema(example = "8", description = "Unidades disponibles en inventario")
    private Integer stock;

}

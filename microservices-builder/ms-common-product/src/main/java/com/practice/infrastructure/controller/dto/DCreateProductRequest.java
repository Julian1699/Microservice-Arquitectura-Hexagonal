package com.practice.infrastructure.controller.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Schema(description = "Datos para crear un producto")
public class DCreateProductRequest {

    @Schema(example = "Teclado mecánico", description = "Nombre del producto")
    private String name;

    @Schema(example = "Teclado RGB con switches rojos", description = "Descripción del producto")
    private String description;

    @Schema(example = "250000", description = "Precio en pesos colombianos (COP)")
    private Long price;

    @Schema(example = "8", description = "Unidades disponibles en inventario")
    private Integer stock;

}

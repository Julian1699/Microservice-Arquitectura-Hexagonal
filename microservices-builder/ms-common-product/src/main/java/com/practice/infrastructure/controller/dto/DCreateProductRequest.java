package com.practice.infrastructure.controller.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class DCreateProductRequest {

    private String name;

    private String description;

    private Long price;

    private Integer stock;

}

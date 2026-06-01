package com.practice.infrastructure.controller.dto;

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
public class DProductResponse {

    private Long id;

    private String name;

    private String description;

    private Long price;

    private Integer stock;

}

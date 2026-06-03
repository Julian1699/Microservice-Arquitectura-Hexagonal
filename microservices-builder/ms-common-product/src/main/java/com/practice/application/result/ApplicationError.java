package com.practice.application.result;

import lombok.AllArgsConstructor;
import lombok.Getter;

// Error controlado de application; ResultHttpMapper y GlobalExceptionHandler lo proyectan a DErrorResponse en la frontera HTTP.
@Getter
@AllArgsConstructor
public class ApplicationError {

    private String code;

    private String message;

    private EErrorType type;

}

package com.practice.infrastructure.web;

import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;

import com.practice.application.result.ApplicationError;
import com.practice.application.result.EErrorType;
import com.practice.infrastructure.web.dto.DErrorResponse;
import com.practice.infrastructure.web.result.ResultHttpMapper;

import lombok.RequiredArgsConstructor;

// Adaptador web: captura fallos técnicos en la frontera HTTP (fuera de ApplicationResult) y los expone como DErrorResponse.
@RestControllerAdvice
@RequiredArgsConstructor
public class GlobalExceptionHandler {

    private final ResultHttpMapper resultHttpMapper;

    // Payload ilegible en la frontera HTTP → ApplicationError (VALIDATION) → ResultHttpMapper → 400.
    @ExceptionHandler(HttpMessageNotReadableException.class)
    public ResponseEntity<DErrorResponse> handleHttpMessageNotReadable(HttpMessageNotReadableException exception) {
        ApplicationError applicationError = new ApplicationError("INVALID_REQUEST_BODY", "Cuerpo JSON mal formado o ilegible", EErrorType.VALIDATION);
        return resultHttpMapper.mapApplicationErrorToErrorResponse(applicationError);
    }

    // Parámetro de ruta/query incompatible con el dominio esperado → ApplicationError → ResultHttpMapper → 400.
    @ExceptionHandler(MethodArgumentTypeMismatchException.class)
    public ResponseEntity<DErrorResponse> handleMethodArgumentTypeMismatch(MethodArgumentTypeMismatchException exception) {
        ApplicationError applicationError = new ApplicationError("INVALID_PATH_PARAMETER", "El parámetro '" + exception.getName() + "' tiene un valor inválido", EErrorType.VALIDATION);
        return resultHttpMapper.mapApplicationErrorToErrorResponse(applicationError);
    }

    // Conflicto reportado por el adaptador de persistencia (restricciones de BD) → ApplicationError → ResultHttpMapper → 409.
    @ExceptionHandler(DataIntegrityViolationException.class)
    public ResponseEntity<DErrorResponse> handleDataIntegrityViolation(DataIntegrityViolationException exception) {
        ApplicationError applicationError = new ApplicationError("DATA_INTEGRITY_VIOLATION", "La operación entra en conflicto con datos existentes", EErrorType.CONFLICT);
        return resultHttpMapper.mapApplicationErrorToErrorResponse(applicationError);
    }

    // Red de último recurso en el adaptador web: cualquier fallo no clasificado → ApplicationError (INTERNAL) → 500.
    @ExceptionHandler(Exception.class)
    public ResponseEntity<DErrorResponse> handleUnhandledException(Exception exception) {
        ApplicationError applicationError = new ApplicationError("INTERNAL_SERVER_ERROR", "Ocurrió un error interno inesperado", EErrorType.INTERNAL);
        return resultHttpMapper.mapApplicationErrorToErrorResponse(applicationError);
    }

}

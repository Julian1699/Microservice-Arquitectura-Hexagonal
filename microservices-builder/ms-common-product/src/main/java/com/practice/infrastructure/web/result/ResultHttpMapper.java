package com.practice.infrastructure.web.result;

import java.util.function.Function;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;

import com.practice.application.result.ApplicationError;
import com.practice.application.result.ApplicationResult;
import com.practice.application.result.EErrorType;
import com.practice.infrastructure.web.dto.DErrorResponse;

// Puente adaptador web ↔ application: serializa ApplicationResult/ApplicationError a la frontera HTTP (DTO + status).
@Component
public class ResultHttpMapper {

    // ApplicationResult exitoso del puerto ProductUseCase → DTO de respuesta con status 200.
    public <T, R> ResponseEntity<?> mapApplicationResultToOkResponse(ApplicationResult<T> applicationResult, Function<T, R> responseBodyMapper) {
        return mapApplicationResultToResponseWithStatus(applicationResult, HttpStatus.OK, responseBodyMapper);
    }

    // ApplicationResult exitoso tras alta → DTO de respuesta con status 201.
    public <T, R> ResponseEntity<?> mapApplicationResultToCreatedResponse(ApplicationResult<T> applicationResult, Function<T, R> responseBodyMapper) {
        return mapApplicationResultToResponseWithStatus(applicationResult, HttpStatus.CREATED, responseBodyMapper);
    }

    // Traduce ApplicationResult a HTTP: dominio→DTO si ok; ApplicationError→DErrorResponse si fail (vía EErrorType).
    public <T, R> ResponseEntity<?> mapApplicationResultToResponseWithStatus(ApplicationResult<T> applicationResult, HttpStatus successHttpStatus, Function<T, R> responseBodyMapper) {
        if (applicationResult.isOk()) {
            return ResponseEntity.status(successHttpStatus).body(responseBodyMapper.apply(applicationResult.getValue()));
        }
        return mapApplicationErrorToErrorResponse(applicationResult.getError());
    }

    // Eliminación exitosa → 204; fallo de negocio → DErrorResponse con status según EErrorType.
    public ResponseEntity<?> mapApplicationResultToNoContentResponse(ApplicationResult<Void> applicationResult) {
        if (applicationResult.isOk()) {
            return ResponseEntity.noContent().build();
        }
        return mapApplicationErrorToErrorResponse(applicationResult.getError());
    }

    // Proyecta ApplicationError (application) a DErrorResponse (frontera HTTP) con status mapeado desde EErrorType.
    public ResponseEntity<DErrorResponse> mapApplicationErrorToErrorResponse(ApplicationError applicationError) {
        DErrorResponse dErrorResponse = new DErrorResponse(applicationError.getCode(), applicationError.getMessage());
        return ResponseEntity.status(toHttpStatus(applicationError.getType())).body(dErrorResponse);
    }

    // Traduce la clasificación de error de application (EErrorType) al código de la frontera HTTP.
    private HttpStatus toHttpStatus(EErrorType errorType) {
        switch (errorType) {
            case VALIDATION:
                return HttpStatus.BAD_REQUEST;
            case NOT_FOUND:
                return HttpStatus.NOT_FOUND;
            case CONFLICT:
                return HttpStatus.CONFLICT;
            case INTERNAL:
                return HttpStatus.INTERNAL_SERVER_ERROR;
            default:
                return HttpStatus.SERVICE_UNAVAILABLE;
        }
    }

}

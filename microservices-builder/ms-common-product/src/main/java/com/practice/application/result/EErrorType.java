package com.practice.application.result;

// Clasificación del error en application; ResultHttpMapper la traduce al status HTTP de la frontera web.
public enum EErrorType {

    VALIDATION, //Petición inválida → 400
    NOT_FOUND, // Recurso inexistente → 404
    CONFLICT, // Colisión con datos persistidos → 409
    INTERNAL // Fallo no recuperable en un adaptador → 500

}

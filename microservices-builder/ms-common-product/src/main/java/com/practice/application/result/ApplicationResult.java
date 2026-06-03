package com.practice.application.result;

// Salida de un caso de uso; el adaptador web recibe este tipo desde ProductUseCase y ResultHttpMapper lo expone como HTTP.
public class ApplicationResult<T> {

    private final T value;

    private final ApplicationError error;

    private ApplicationResult(T value, ApplicationError error) {
        this.value = value;
        this.error = error;
    }

    public static <T> ApplicationResult<T> ok(T value) {
        return new ApplicationResult<>(value, null);
    }

    public static <T> ApplicationResult<T> fail(ApplicationError error) {
        return new ApplicationResult<>(null, error);
    }

    public boolean isOk() {
        return error == null;
    }

    public boolean isFail() {
        return error != null;
    }

    public T getValue() {
        return value;
    }

    public ApplicationError getError() {
        return error;
    }

}

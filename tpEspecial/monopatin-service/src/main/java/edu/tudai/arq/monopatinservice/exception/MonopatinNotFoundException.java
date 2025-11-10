package edu.tudai.arq.monopatinservice.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;


@ResponseStatus(HttpStatus.CONFLICT)
public class MonopatinNotFoundException extends RuntimeException {

    public MonopatinNotFoundException(Long id, String estadoActual) {
        super("El monopatín con ID " + id + " no está disponible para esta acción. Estado actual: " + estadoActual);
    }

    public MonopatinNotFoundException(Long id) {
        super("Monopatín con ID " + id + " no fue encontrado.");
    }
}
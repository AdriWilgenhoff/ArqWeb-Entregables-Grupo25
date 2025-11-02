package edu.tudai.arq.monopatinservice.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.BAD_REQUEST)
public class InvalidStateTransitionException extends RuntimeException {

    public InvalidStateTransitionException(Long id, String estadoActual, String estadoSolicitado) {
        super("Transición inválida para el monopatín ID " + id + ". No puede pasar de '" + estadoActual + "' a '" + estadoSolicitado + "'.");
    }
}
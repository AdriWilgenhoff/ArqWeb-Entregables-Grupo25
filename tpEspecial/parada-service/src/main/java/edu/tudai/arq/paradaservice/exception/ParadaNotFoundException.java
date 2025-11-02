package edu.tudai.arq.paradaservice.exception;

public class ParadaNotFoundException extends RuntimeException {

    public ParadaNotFoundException(String message) {
        super(message);
    }

    public ParadaNotFoundException(Long id) {
        super("Parada no encontrada con ID: " + id);
    }
}


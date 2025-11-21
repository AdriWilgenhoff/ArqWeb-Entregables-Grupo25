package edu.tudai.arq.paradaservice.exception;

public class ParadaNotFoundException extends RuntimeException {

    public ParadaNotFoundException(String id) {
        super("Parada no encontrada con ID: " + id);
    }
}


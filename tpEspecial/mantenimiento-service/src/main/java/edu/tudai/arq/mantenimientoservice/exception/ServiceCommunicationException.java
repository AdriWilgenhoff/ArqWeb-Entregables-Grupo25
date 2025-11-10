package edu.tudai.arq.mantenimientoservice.exception;

public class ServiceCommunicationException extends RuntimeException {

    public ServiceCommunicationException(String message) {
        super(message);
    }

    public ServiceCommunicationException(String message, Throwable cause) {
        super(message, cause);
    }
}


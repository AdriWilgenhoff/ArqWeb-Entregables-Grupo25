package edu.tudai.arq.monopatinservice.exception;

import com.fasterxml.jackson.annotation.JsonFormat;
import java.time.LocalDateTime;

public class ApiError {

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm:ss.SSS'Z'")
    private final LocalDateTime timestamp;
    private final int status;
    private final String error;
    private final String message;
    private final String path;

    // ESTE ES EL CONSTRUCTOR DE 4 ARGUMENTOS QUE EL HANDLER ESTÁ BUSCANDO
    public ApiError(int status, String error, String message, String path) {
        this.timestamp = LocalDateTime.now(); // Se inicializa aquí
        this.status = status;
        this.error = error;
        this.message = message;
        this.path = path;
    }

    // Getters necesarios para que Jackson genere el JSON
    public LocalDateTime getTimestamp() {
        return timestamp;
    }

    public int getStatus() {
        return status;
    }

    public String getError() {
        return error;
    }

    public String getMessage() {
        return message;
    }

    public String getPath() {
        return path;
    }
}
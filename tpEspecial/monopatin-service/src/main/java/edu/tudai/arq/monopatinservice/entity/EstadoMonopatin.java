package edu.tudai.arq.monopatinservice.entity;

public enum EstadoMonopatin {
    DISPONIBLE,
    EN_USO,
    EN_MANTENIMIENTO,
    DADO_DE_BAJA  // Baja lógica - el monopatín no se puede usar pero se mantiene el historial
}
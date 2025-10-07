package edu.tudai.arq.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@ToString
@Setter
@Getter
@AllArgsConstructor
public class ReporteCarreraDTO {

    private String nombreCarrera;
    private int anio;
    private long cantGraduados;
    private long cantInscriptos;

}
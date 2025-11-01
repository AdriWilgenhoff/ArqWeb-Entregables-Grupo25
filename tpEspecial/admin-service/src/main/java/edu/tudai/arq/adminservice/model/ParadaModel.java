package edu.tudai.arq.adminservice.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ParadaModel {
    private Long id;
    private String nombre;
    private String ubicacion;
    private Double latitud;
    private Double longitud;
    private Integer capacidadMaxima;
    private Boolean activa;
}


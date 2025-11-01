package edu.tudai.arq.adminservice.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class MonopatinModel {
    private Long id;
    private String ubicacionActual;
    private Double latitud;
    private Double longitud;
    private String estado; // DISPONIBLE, EN_USO, EN_MANTENIMIENTO
    private Double kilometrosRecorridos;
    private Long tiempoUsoTotal;
    private Long idParadaActual;
}


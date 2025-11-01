package edu.tudai.arq.adminservice.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ViajeModel {
    private Long id;
    private Long idCuenta;
    private Long idUsuario;
    private Long idMonopatin;
    private String fechaHoraInicio;
    private String fechaHoraFin;
    private Long idParadaInicio;
    private Long idParadaFin;
    private Double kilometrosRecorridos;
    private String estado;
    private Double costoTotal;
    private Integer numeroPausas;
}


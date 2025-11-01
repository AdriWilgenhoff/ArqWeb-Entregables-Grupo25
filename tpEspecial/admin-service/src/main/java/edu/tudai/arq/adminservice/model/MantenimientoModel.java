package edu.tudai.arq.adminservice.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class MantenimientoModel {
    private Long id;
    private Long idMonopatin;
    private String fechaInicio;
    private String fechaFin;
    private String descripcion;
    private String tipo;
    private Boolean finalizado;
}
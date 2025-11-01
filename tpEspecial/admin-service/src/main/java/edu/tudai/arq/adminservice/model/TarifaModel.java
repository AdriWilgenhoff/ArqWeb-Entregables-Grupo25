package edu.tudai.arq.adminservice.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class TarifaModel {
    private Long id;
    private String tipoTarifa;
    private Double precioPorMinuto;
    private String fechaVigenciaDesde;
    private String fechaVigenciaHasta;
    private Boolean activa;
}


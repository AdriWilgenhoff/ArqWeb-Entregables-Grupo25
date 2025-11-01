package edu.tudai.arq.adminservice.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class TotalFacturadoDTO {
    private Double totalFacturado;
    private String periodoDesde;
    private String periodoHasta;
    private String moneda;
}


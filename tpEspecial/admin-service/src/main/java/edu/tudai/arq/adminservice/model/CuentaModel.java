package edu.tudai.arq.adminservice.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CuentaModel {
    private Long id;
    private String numeroIdentificatorio;
    private String fechaAlta;
    private Double saldo;
    private Boolean habilitada;
    private String idCuentaMercadoPago;
}


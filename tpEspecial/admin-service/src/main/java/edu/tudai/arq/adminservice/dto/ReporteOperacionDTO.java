package edu.tudai.arq.adminservice.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ReporteOperacionDTO {
    private Long monopatinesEnOperacion;
    private Long monopatinesEnMantenimiento;
    private Double porcentajeEnOperacion;
    private Double porcentajeEnMantenimiento;
}

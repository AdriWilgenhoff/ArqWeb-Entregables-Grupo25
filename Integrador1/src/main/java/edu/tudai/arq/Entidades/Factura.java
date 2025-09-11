package edu.tudai.arq.Entidades;


import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@AllArgsConstructor
@ToString
public class Factura {

    private int idFactura;
    private int idCliente;

}
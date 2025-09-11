package edu.tudai.arq.Entidades;


import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@AllArgsConstructor
@ToString
public class FacturaProducto {

    private int idFactura;
    private int idProducto;
    private int cantidad;

}
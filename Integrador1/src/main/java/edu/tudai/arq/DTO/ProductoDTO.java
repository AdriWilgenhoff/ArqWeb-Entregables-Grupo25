package edu.tudai.arq.DTO;


import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class ProductoDTO {

    private int id;
    private String nombre;
    private float valor;

    @Override
    public String toString() {
        return "Producto que mas recaudo: idProducto: " + id +
                ", nombre: '" + nombre + '\'' +
                ", valor: " + valor +
                ".";
    }
}
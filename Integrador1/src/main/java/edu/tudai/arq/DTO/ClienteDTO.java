package edu.tudai.arq.DTO;


import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.ToString;

@Getter
@AllArgsConstructor
@ToString
public class ClienteDTO {

    private int id;
    private String nombre;
    private float totalFacturado;

}
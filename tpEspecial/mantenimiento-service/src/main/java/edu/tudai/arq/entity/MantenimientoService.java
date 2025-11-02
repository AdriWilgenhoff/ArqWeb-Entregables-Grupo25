package edu.tudai.arq.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.io.Serializable;
import java.time.LocalDateTime;


@Entity
@Getter
@ToString
public class MantenimientoService implements  Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_Mantenimiento")
    private Long id;

    @Setter
    @OneToOne
    private Long idMonopatin;

    @Setter
    @Column(nullable = false)
    private LocalDateTime fechaHoraInicio;

    @Setter
    private LocalDateTime fechaHoraFin;

    @Setter
    private String descripcion;

    @Setter
    private Double kilometrosAlIniniciar;

    @Setter
    private Long tiempoUsoAlIniniciar;


    private String idEncargado;

}

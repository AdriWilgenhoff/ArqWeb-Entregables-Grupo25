package edu.tudai.arq.entity;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import javax.persistence.*;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

@Entity
@Getter
@ToString
public class Carrera implements Serializable{

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_carrera")
    private Long idCarrera;

    @OneToMany(mappedBy="carrera", fetch=FetchType.LAZY)
    private List <Inscripcion> inscriptos;

    @Setter
    @Column(nullable = false, unique = true)
    private String nombre;


    public Carrera() {
        this.inscriptos = new ArrayList<>();
    }

    public Carrera(long idCarrera, String nombre) {
        this();
        this.idCarrera = idCarrera;
        this.nombre = nombre;
    }

    public List <Inscripcion> getEstudiantes() {
        return new ArrayList <> (inscriptos);
    }

    public void addEstudiante(Inscripcion e){
        this.inscriptos.add(e);
    }


}

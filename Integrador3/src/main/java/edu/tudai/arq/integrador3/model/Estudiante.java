package edu.tudai.arq.integrador3.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.time.Period;
import java.util.ArrayList;
import java.util.List;
import java.time.LocalDate;


@Entity
@Getter
@ToString
public class Estudiante {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_estudiante")
    private Long idEstudiante;

    @OneToMany(mappedBy = "estudiante", fetch = FetchType.LAZY)
    private List <Inscripcion> inscripciones;

    @Setter
    @Column
    private String nombre;

    @Setter
    @Column
    private String apellido;

    @Setter
    @Column(name="fecha_nacimiento", nullable = false)
    private LocalDate fechaNacimiento;

    @Setter
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Genero genero;

    @Setter
    @Column(unique = true,nullable = false, length = 20)
    private Long dni;

    @Setter
    @Column(name="ciudad_residencia")
    private String ciudadResidencia;

    @Setter
    @Column(name = "num_libreta", unique = true, nullable = false)
    private Long lu;

    public Estudiante() {
        this.inscripciones = new ArrayList <Inscripcion>();
    }

    public Estudiante(String nombres, String apellido, LocalDate fechaNacimiento, Genero genero, Long dni, String ciudadResidencia, Long lu) {
        this.nombre = nombres;
        this.apellido = apellido;
        this.fechaNacimiento = fechaNacimiento;
        this.genero = genero;
        this.dni = dni;
        this.ciudadResidencia = ciudadResidencia;
        this.lu = lu;
        this.inscripciones = new ArrayList <Inscripcion>();
    }

    public List<Inscripcion> getInscripciones() {
        return new ArrayList<>(inscripciones);
    }

    public void addInscripciones(Inscripcion e){
        this.inscripciones.add(e);
    }

    public int getEdad() {
        return Period.between(this.fechaNacimiento, LocalDate.now()).getYears();
    }

}

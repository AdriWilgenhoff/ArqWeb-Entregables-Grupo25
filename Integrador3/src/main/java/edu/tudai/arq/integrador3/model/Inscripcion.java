package edu.tudai.arq.integrador3.model;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;
import java.time.Period;

@Entity
@Getter
@AllArgsConstructor
public class Inscripcion {

    @EmbeddedId
    private InscripcionId idInscripcion;

    @ManyToOne
    @MapsId("idCarrera")
    @JoinColumn(name = "id_carrera")
    private Carrera carrera;

    @ManyToOne
    @MapsId("idEstudiante")
    @JoinColumn(name = "id_estudiante")
    private Estudiante estudiante;

    @Column (nullable = false)
    private LocalDate fecha_inscripcion;

    @Column(name = "fecha_egreso")
    private LocalDate fecha_egreso;


    public Inscripcion( Estudiante estudiante, Carrera carrera) {
        this.estudiante = estudiante;
        this.carrera = carrera;
        this.fecha_egreso = null;
        this.fecha_inscripcion= LocalDate.now();
        this.idInscripcion= new InscripcionId (estudiante.getIdEstudiante(), carrera.getIdCarrera());
    }

    public Inscripcion() {
        super();
    }

    public void setFechaEgreso(LocalDate fecha) {
        this.fecha_egreso=fecha;
    }

    public void setFechaEgresoNow(){
        this.fecha_egreso= LocalDate.now();
    }

    public void setFechaInscripcion(LocalDate fecha_inscripcion) {
        this.fecha_inscripcion = fecha_inscripcion;
    }

    public void imprimirAntiguedad(){
        LocalDate endDate = LocalDate.now();
        Period difference = Period.between(this.fecha_inscripcion, endDate);

        System.out.println("Antiguedad: " + difference.getYears() + " años, " +
                difference.getMonths() + " meses, y " +
                difference.getDays() + " días.");
    }

    public boolean isGraduo(){
        return this.fecha_egreso != null;
    }

    @Override
    public String toString() {
        return "Inscripcion{" +
                "id_inscripcion=" + idInscripcion +
                ", estudianteId=" + (estudiante != null ? estudiante.getIdEstudiante() : "null") +
                ", carreraId=" + (carrera != null ? carrera.getIdCarrera() : "null") +
                ", fecha_inscripcion=" + fecha_inscripcion +
                ", fecha_egreso=" + fecha_egreso +
                '}';
    }
}
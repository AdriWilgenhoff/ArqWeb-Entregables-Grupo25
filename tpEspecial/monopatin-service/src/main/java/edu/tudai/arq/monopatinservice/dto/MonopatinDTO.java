package edu.tudai.arq.monopatinservice.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import edu.tudai.arq.monopatinservice.entity.EstadoMonopatin;
import edu.tudai.arq.monopatinservice.entity.Monopatin;
import jakarta.validation.constraints.*;

public class MonopatinDTO {

    @Schema(description = "DTO de entrada para crear un nuevo Monopatín", name = "MonopatinCreate")
    public static class Create {
        private final EstadoMonopatin estado;
        private final Double latitud;
        private final Double longitud;

        public Create(
                EstadoMonopatin estado,
                Double latitud,
                Double longitud) {
            this.estado = estado;
            this.latitud = latitud;
            this.longitud = longitud;
        }

        public EstadoMonopatin getEstado() { return estado; }
        public Double getLatitud() { return latitud; }
        public Double getLongitud() { return longitud; }
    }

    @Schema(description = "DTO de entrada para actualizar un Monopatín existente", name = "MonopatinUpdate")
    public static class Update {
        private EstadoMonopatin estado;
        private Double latitud;
        private Double longitud;
        private Double kilometrosTotales;
        private Long tiempoUsoTotal;

        public Update() {}
        public EstadoMonopatin getEstado() { return estado; }
        public Double getLatitud() { return latitud; }
        public Double getLongitud() { return longitud; }
        public Double getKilometrosTotales() { return kilometrosTotales; }
        public Long getTiempoUsoTotal() { return tiempoUsoTotal; }
    }

    @Schema(description = "DTO de respuesta que incluye todas las propiedades del Monopatín.", name = "MonopatinResponse")
    public static class Response {
        private final Long id;
        private final EstadoMonopatin estado;
        private final Double latitud;
        private final Double longitud;
        private final Double kilometrosTotales;
        private final Long tiempoUsoTotal;

        public Response(Long id, EstadoMonopatin estado, Double latitud, Double longitud, Double kilometrosTotales, Long tiempoUsoTotal) {
            this.id = id;
            this.estado = estado;
            this.latitud = latitud;
            this.longitud = longitud;
            this.kilometrosTotales = kilometrosTotales;
            this.tiempoUsoTotal = tiempoUsoTotal;
        }

        public Response(Monopatin entity) {
            this(
                    entity.getId(),
                    entity.getEstado(),
                    entity.getLatitud(),
                    entity.getLongitud(),
                    entity.getKilometrosTotales(),
                    entity.getTiempoUsoTotal()
            );
        }

        public Long getId() { return id; }
        public EstadoMonopatin getEstado() { return estado; }
        public Double getLatitud() { return latitud; }
        public Double getLongitud() { return longitud; }
        public Double getKilometrosTotales() { return kilometrosTotales; }
        public Long getTiempoUsoTotal() { return tiempoUsoTotal; }
    }
}
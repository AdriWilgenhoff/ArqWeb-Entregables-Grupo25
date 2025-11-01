package edu.tudai.arq.userservice.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

import java.time.LocalDateTime;

@Entity
@Getter
@ToString
@NoArgsConstructor
@Table(name = "usuario_cuenta")
public class UsuarioCuenta {

    @EmbeddedId
    private UsuarioCuentaId id;

    @ManyToOne(fetch = FetchType.LAZY)
    @MapsId("idUsuario")
    @JoinColumn(name = "id_usuario")
    private Usuario usuario;

    @ManyToOne(fetch = FetchType.LAZY)
    @MapsId("idCuenta")
    @JoinColumn(name = "id_cuenta")
    private Cuenta cuenta;

    @Setter
    @Column(name = "fecha_asociacion", nullable = false)
    private LocalDateTime fechaAsociacion;

    public UsuarioCuenta(Usuario usuario, Cuenta cuenta) {
        this.id = new UsuarioCuentaId(usuario.getId(), cuenta.getId());
        this.usuario = usuario;
        this.cuenta = cuenta;
        this.fechaAsociacion = LocalDateTime.now();
    }
}
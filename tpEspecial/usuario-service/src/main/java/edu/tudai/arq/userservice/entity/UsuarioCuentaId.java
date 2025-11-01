package edu.tudai.arq.userservice.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.io.Serializable;
import java.util.Objects;

@Embeddable
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class UsuarioCuentaId implements Serializable {

    @Column(name = "id_usuario")
    private Long idUsuario;

    @Column(name = "id_cuenta")
    private Long idCuenta;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        UsuarioCuentaId that = (UsuarioCuentaId) o;
        return Objects.equals(idUsuario, that.idUsuario) &&
                Objects.equals(idCuenta, that.idCuenta);
    }

    @Override
    public int hashCode() {
        return Objects.hash(idUsuario, idCuenta);
    }
}
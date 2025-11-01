package edu.tudai.arq.userservice.repository;

import edu.tudai.arq.userservice.entity.UsuarioCuenta;
import edu.tudai.arq.userservice.entity.UsuarioCuentaId;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

@Repository
public interface UsuarioCuentaRepository extends JpaRepository<UsuarioCuenta, UsuarioCuentaId> {

    @Modifying
    @Query("DELETE FROM UsuarioCuenta uc WHERE uc.usuario.id = :idUsuario AND uc.cuenta.id = :idCuenta")
    void deleteByUsuarioIdAndCuentaId(Long idUsuario, Long idCuenta);

}
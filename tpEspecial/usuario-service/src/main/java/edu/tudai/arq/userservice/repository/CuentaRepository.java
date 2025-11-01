package edu.tudai.arq.userservice.repository;


import edu.tudai.arq.userservice.entity.Cuenta;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface CuentaRepository extends JpaRepository<Cuenta, Long> {

    Optional<Cuenta> findByNumeroIdentificatorio(String numeroIdentificatorio);
}

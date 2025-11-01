package edu.tudai.arq.userservice.service.interfaces;

import edu.tudai.arq.userservice.dto.UsuarioDTO;
import java.util.List;

public interface UsuarioService {

    UsuarioDTO.Response create(UsuarioDTO.Create in);

    UsuarioDTO.Response update(Long id, UsuarioDTO.Update in);

    void delete(Long id);

    UsuarioDTO.Response findById(Long id);

    List<UsuarioDTO.Response> findAll();

    UsuarioDTO.Response findByEmail(String email);

    void asociarCuenta(Long idUsuario, Long idCuenta);

    void desasociarCuenta(Long idUsuario, Long idCuenta);
}
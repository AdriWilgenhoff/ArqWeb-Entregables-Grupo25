package edu.tudai.arq.userservice.service.interfaces;

import edu.tudai.arq.userservice.dto.AuthDTO;

public interface AuthService {

    AuthDTO.TokenResponse login(AuthDTO.LoginRequest request);

    AuthDTO.RegisterResponse register(AuthDTO.RegisterRequest request);

    AuthDTO.TokenResponse refresh(AuthDTO.RefreshRequest request);

    void logout(String token);
}
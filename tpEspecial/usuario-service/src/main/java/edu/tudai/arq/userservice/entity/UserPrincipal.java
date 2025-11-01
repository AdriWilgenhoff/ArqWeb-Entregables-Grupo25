package edu.tudai.arq.userservice.entity;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.List;

@Getter
@AllArgsConstructor
public class UserPrincipal {
    private Long userId;
    private String email;
    private List<Long> cuentasHabilitadas;
}
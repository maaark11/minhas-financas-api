package com.maquirino.minhasfinancas.api.dto;

import lombok.Data;
import lombok.Getter;

@Data
public class UsuarioDTO {

    private String email;
    private String nome;
    private String senha;
}

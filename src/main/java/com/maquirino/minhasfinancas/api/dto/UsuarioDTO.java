package com.maquirino.minhasfinancas.api.dto;

import lombok.Builder;
import lombok.Data;
import lombok.Getter;

@Data
@Builder(toBuilder = true)
public class UsuarioDTO {

    private String email;
    private String nome;
    private String senha;
}

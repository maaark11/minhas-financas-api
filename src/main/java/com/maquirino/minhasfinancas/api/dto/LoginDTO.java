package com.maquirino.minhasfinancas.api.dto;

import lombok.Builder;
import lombok.Data;

@Data
@Builder(toBuilder = true)
public class LoginDTO {

    private String email;
    private String senha;
}

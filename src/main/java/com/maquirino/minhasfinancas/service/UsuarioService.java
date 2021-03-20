package com.maquirino.minhasfinancas.service;

import com.maquirino.minhasfinancas.exception.ErroAutenticacao;
import com.maquirino.minhasfinancas.exception.RegraNegocioException;
import com.maquirino.minhasfinancas.model.entity.Usuario;

public interface UsuarioService {

    Usuario autenticar(String email, String senha) throws ErroAutenticacao;

    Usuario salvarUsuario(Usuario usuario);

    void validarEmail(String email) throws RegraNegocioException;
}

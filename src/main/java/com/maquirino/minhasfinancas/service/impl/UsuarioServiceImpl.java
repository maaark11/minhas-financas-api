package com.maquirino.minhasfinancas.service.impl;

import com.maquirino.minhasfinancas.exception.ErroAutenticacao;
import com.maquirino.minhasfinancas.exception.RegraNegocioException;
import com.maquirino.minhasfinancas.model.entity.Usuario;
import com.maquirino.minhasfinancas.model.repository.UsuarioRepository;
import com.maquirino.minhasfinancas.service.UsuarioService;
import java.util.Optional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class UsuarioServiceImpl implements UsuarioService {

    @Autowired
    private UsuarioRepository repository;

    @Override
    public Usuario autenticar(String email, String senha) throws ErroAutenticacao {
        Optional<Usuario> optionalUsuario = repository.findByEmail(email);

        return validaUsuarioESenha(senha, optionalUsuario);
    }


    @Override
    @Transactional
    public Usuario salvarUsuario(Usuario usuario) {
        validarEmail(usuario.getEmail());
        return repository.save(usuario);
    }

    @Override
    public void validarEmail(String email) {
        boolean existe = repository.existsByEmail(email);
        if (existe) {
            throw new RegraNegocioException("Já Existe um usuário cadastrado com o email informado");
        }
    }

    @Override
    public Usuario obterPorId(Long id) {
        Optional<Usuario> optionalUsuario = repository.findById(id);
        return validaUsuario(optionalUsuario);
    }

    private Usuario validaUsuario(Optional<Usuario> optionalUsuario) {
        if (optionalUsuario.isEmpty()) {
            throw new ErroAutenticacao("Usuário não encontrado");
        }
        return optionalUsuario.get();
    }

    private Usuario validaUsuarioESenha(String senha, Optional<Usuario> optionalUsuario) {
        Usuario usuario = validaUsuario(optionalUsuario);
        if (!usuario.getSenha().equals(senha)) {
            throw new ErroAutenticacao("Senha incorreta");
        }
        return usuario;
    }
}

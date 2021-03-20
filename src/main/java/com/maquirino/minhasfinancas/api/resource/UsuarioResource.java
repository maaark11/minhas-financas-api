package com.maquirino.minhasfinancas.api.resource;

import com.maquirino.minhasfinancas.api.dto.LoginDTO;
import com.maquirino.minhasfinancas.api.dto.UsuarioDTO;
import com.maquirino.minhasfinancas.exception.ErroAutenticacao;
import com.maquirino.minhasfinancas.exception.RegraNegocioException;
import com.maquirino.minhasfinancas.model.entity.Usuario;
import com.maquirino.minhasfinancas.service.UsuarioService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/usuarios")
public class UsuarioResource {

    @Autowired
    private UsuarioService service;

    @PostMapping("salvar")
    public ResponseEntity salvar(@RequestBody UsuarioDTO usuariodto) {
        Usuario usuario = Usuario.builder()
                .email(usuariodto.getEmail())
                .nome(usuariodto.getNome())
                .senha(usuariodto.getSenha())
                .build();
        try {
            Usuario usuarioSalvo = service.salvarUsuario(usuario);
            return ResponseEntity.status(HttpStatus.CREATED).body(usuarioSalvo);
        } catch (RegraNegocioException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @PostMapping("autenticar")
    public ResponseEntity autenticar(@RequestBody LoginDTO loginDTO) {
        try {
            Usuario usuarioAutenticado = service.autenticar(loginDTO.getEmail(), loginDTO.getSenha());
            return ResponseEntity.ok(usuarioAutenticado);
        } catch (ErroAutenticacao e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }
}

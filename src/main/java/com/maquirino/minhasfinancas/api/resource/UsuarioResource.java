package com.maquirino.minhasfinancas.api.resource;

import com.maquirino.minhasfinancas.api.dto.LoginDTO;
import com.maquirino.minhasfinancas.api.dto.UsuarioDTO;
import com.maquirino.minhasfinancas.exception.ErroAutenticacao;
import com.maquirino.minhasfinancas.exception.RegraNegocioException;
import com.maquirino.minhasfinancas.model.entity.Usuario;
import com.maquirino.minhasfinancas.service.LancamentoService;
import com.maquirino.minhasfinancas.service.UsuarioService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;


@RestController
@RequestMapping("/api/usuarios")
public class UsuarioResource {

    @Autowired
    private UsuarioService usuarioService;

    @Autowired
    private LancamentoService lancamentoService;

    @PostMapping("autenticar")
    public ResponseEntity autenticar(@RequestBody LoginDTO loginDTO) {
        try {
            Usuario usuarioAutenticado = usuarioService.autenticar(loginDTO.getEmail(), loginDTO.getSenha());
            return ResponseEntity.ok(usuarioAutenticado);
        } catch (ErroAutenticacao e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @PostMapping("salvar")
    public ResponseEntity salvar(@RequestBody UsuarioDTO usuariodto) {
        Usuario usuario = Usuario.builder()
                .email(usuariodto.getEmail())
                .nome(usuariodto.getNome())
                .senha(usuariodto.getSenha())
                .build();
        try {
            Usuario usuarioSalvo = usuarioService.salvarUsuario(usuario);
            return ResponseEntity.status(HttpStatus.CREATED).body(usuarioSalvo);
        } catch (RegraNegocioException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @GetMapping("{id}/saldo")
    public ResponseEntity obterSaldo(@PathVariable("id") Long id) {
        try {
            Usuario usuario = usuarioService.obterPorId(id);
        } catch (ErroAutenticacao e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        }
        return ResponseEntity.ok(lancamentoService.obterSaldo(id));
    }
}

package com.maquirino.minhasfinancas.service;

import com.maquirino.minhasfinancas.exception.ErroAutenticacao;
import com.maquirino.minhasfinancas.exception.RegraNegocioException;
import com.maquirino.minhasfinancas.model.entity.Usuario;
import com.maquirino.minhasfinancas.model.repository.UsuarioRepository;
import com.maquirino.minhasfinancas.service.impl.UsuarioServiceImpl;
import java.util.Optional;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.any;
import static org.mockito.Mockito.anyString;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;


@SpringBootTest
@ActiveProfiles("test")
class UsuarioServiceTest {

    @InjectMocks
    private UsuarioServiceImpl service;

    @Mock
    private UsuarioRepository repository;

    @Test
    void deveAutenticarUsuarioComSucesso() {

        when(repository.findByEmail(anyString())).thenReturn(Optional.of(Usuario.builder().email("test@test.com.br").senha("123").build()));

        assertDoesNotThrow(() -> {
            service.autenticar("test@test.com.br", "123");
        });
    }

    @Test
    void deveNaoAutenticarUsuarioComSucessoAoErrarSenha() {

        when(repository.findByEmail(anyString())).thenReturn(Optional.of(Usuario.builder().email("test@test.com.br").senha("1234").build()));

        assertThrows(ErroAutenticacao.class, () -> {
            service.autenticar("test@test.com.br", "123");
        });
    }

    @Test
    void deveNaoAutenticarUsuarioComSucessoAoErrarEmail() {

        when(repository.findByEmail(anyString())).thenReturn(Optional.empty());

        assertThrows(ErroAutenticacao.class, () -> {
            service.autenticar("test@test.com.br", "123");
        });
    }

    @Test
    void deveNaoAutenticarUsuarioComSucessoAoErrarSenhaEmail() {

        when(repository.findByEmail(anyString())).thenReturn(Optional.of(Usuario.builder().email("alou@email.com.br").senha("1234").build()));

        assertThrows(ErroAutenticacao.class, () -> {
            service.autenticar("test@test.com.br", "123");
        });
    }

    @Test
    void deveValidarEmail() {
        when(repository.existsByEmail(anyString())).thenReturn(false);

        assertDoesNotThrow(() -> {
            service.validarEmail("test@test.com.br");
        });
    }

    @Test
    void deveSoltarExcecaoAoValidarEmailExistente() {
        when(repository.existsByEmail(anyString())).thenReturn(true);

        assertThrows(RegraNegocioException.class, () -> {
            service.validarEmail("test@test.com.br");
        });
    }

    @Test
    public void deveSalvarUmUsuario() {
        when(repository.existsByEmail(anyString())).thenReturn(false);
        Usuario usuario = Usuario.builder()
                .id(1l)
                .nome("nome")
                .email("email@email.com")
                .senha("senha").build();

        when(repository.save(any(Usuario.class))).thenReturn(usuario);

        Usuario usuarioSalvo = service.salvarUsuario(Usuario.builder().build());

        assertNotNull(usuarioSalvo);
        assertEquals(1l, usuarioSalvo.getId());
        assertEquals("nome", usuarioSalvo.getNome());
        assertEquals("email@email.com", usuarioSalvo.getEmail());
        assertEquals("senha", usuarioSalvo.getSenha());
    }

    @Test
    public void naoDeveSalvarUmUsuarioComEmailJaCadastrado() {
        String email = "email@email.com";
        Usuario usuario = Usuario.builder().email(email).build();
        when(repository.existsByEmail(anyString())).thenReturn(true);

        assertThrows(RegraNegocioException.class, () -> service.salvarUsuario(usuario));

        verify(repository, never()).save(usuario);
    }
}
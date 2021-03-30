package com.maquirino.minhasfinancas.api.resource;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.maquirino.minhasfinancas.api.dto.LoginDTO;
import com.maquirino.minhasfinancas.api.dto.UsuarioDTO;
import com.maquirino.minhasfinancas.exception.ErroAutenticacao;
import com.maquirino.minhasfinancas.exception.RegraNegocioException;
import com.maquirino.minhasfinancas.model.entity.Usuario;
import com.maquirino.minhasfinancas.service.LancamentoService;
import com.maquirino.minhasfinancas.service.UsuarioService;
import java.math.BigDecimal;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
public class UsuarioResourceTest {

    public static final String API = "/api/usuarios";
    public static final MediaType JSON = MediaType.APPLICATION_JSON_UTF8;
    public static final String NOME = "testudo";
    public static final String EMAIL = "teste@teste.com.br";
    public static final String SENHA = "123";

    private LoginDTO loginDTO;
    private UsuarioDTO usuarioDTO;
    private Usuario usuario;

    @Autowired
    private MockMvc mvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private UsuarioService service;

    @MockBean
    private LancamentoService lancamentoService;

    @BeforeEach
    public void setUp() {
        loginDTO = LoginDTO.builder()
                .email(EMAIL)
                .senha(SENHA)
                .build();

        usuario = Usuario.builder()
                .id(1l)
                .email(EMAIL)
                .nome(NOME)
                .senha(SENHA)
                .build();

        usuarioDTO = UsuarioDTO.builder()
                .email(EMAIL)
                .senha(SENHA)
                .nome(NOME)
                .build();
    }

    @Test
    public void deveAutenticar() throws Exception {
        when(service.autenticar(anyString(), any())).thenReturn(usuario);

        String json = objectMapper.writeValueAsString(loginDTO);

        MockHttpServletRequestBuilder request = MockMvcRequestBuilders
                .post(API.concat("/autenticar"))
                .contentType(JSON)
                .content(json);

        mvc.perform(request)
                .andExpect(status().isOk())
                .andExpect(jsonPath("id").value(usuario.getId()))
                .andExpect(jsonPath("nome").value(usuario.getNome()))
                .andExpect(jsonPath("email").value(usuario.getEmail()));
    }

    @Test
    public void deveNaoAutenticar() throws Exception {
        when(service.autenticar(anyString(), any())).thenThrow(ErroAutenticacao.class);

        String json = objectMapper.writeValueAsString(loginDTO);

        MockHttpServletRequestBuilder request = MockMvcRequestBuilders
                .post(API.concat("/autenticar"))
                .contentType(JSON)
                .content(json);

        mvc.perform(request).andExpect(status().isBadRequest());
    }

    @Test
    public void deveSalvar() throws Exception {
        when(service.salvarUsuario(any(Usuario.class))).thenReturn(usuario);

        String json = objectMapper.writeValueAsString(usuarioDTO);

        MockHttpServletRequestBuilder request = MockMvcRequestBuilders
                .post(API.concat("/salvar"))
                .contentType(JSON)
                .content(json);

        mvc.perform(request)
                .andExpect(status().isCreated())
                .andExpect(jsonPath("id").value(usuario.getId()))
                .andExpect(jsonPath("nome").value(usuario.getNome()))
                .andExpect(jsonPath("email").value(usuario.getEmail()));
    }

    @Test
    public void deveNaoSalvar() throws Exception {
        when(service.salvarUsuario(any(Usuario.class))).thenThrow(RegraNegocioException.class);

        String json = objectMapper.writeValueAsString(usuarioDTO);

        MockHttpServletRequestBuilder request = MockMvcRequestBuilders
                .post(API.concat("/salvar"))
                .contentType(JSON)
                .content(json);

        mvc.perform(request).andExpect(status().isBadRequest());
    }

    @Test
    public void deveObterSaldo() throws Exception {
        when(service.obterPorId(anyLong())).thenReturn(usuario);
        when(lancamentoService.obterSaldo(anyLong())).thenReturn(BigDecimal.TEN);

        String json = objectMapper.writeValueAsString(usuarioDTO);

        MockHttpServletRequestBuilder request = MockMvcRequestBuilders
                .get(API.concat("/3/saldo"))
                .contentType(JSON)
                .content(json);

        mvc.perform(request)
                .andExpect(status().isOk())
                .andExpect(content().string("10"));
    }

    @Test
    public void deveNaoObterSaldo() throws Exception {
        when(service.obterPorId(anyLong())).thenThrow(ErroAutenticacao.class);

        String json = objectMapper.writeValueAsString(usuarioDTO);

        MockHttpServletRequestBuilder request = MockMvcRequestBuilders
                .get(API.concat("/3/saldo"))
                .contentType(JSON)
                .content(json);

        mvc.perform(request).andExpect(status().isNotFound());
    }
}

package com.maquirino.minhasfinancas.api.resource;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.maquirino.minhasfinancas.api.dto.AtualizaStatusDTO;
import com.maquirino.minhasfinancas.api.dto.LancamentoDTO;
import com.maquirino.minhasfinancas.exception.ErroAutenticacao;
import com.maquirino.minhasfinancas.exception.RegraNegocioException;
import com.maquirino.minhasfinancas.model.entity.Lancamento;
import com.maquirino.minhasfinancas.model.entity.Usuario;
import com.maquirino.minhasfinancas.model.enums.StatusLancamento;
import com.maquirino.minhasfinancas.model.enums.TipoLancamento;
import com.maquirino.minhasfinancas.service.LancamentoService;
import com.maquirino.minhasfinancas.service.UsuarioService;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
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

import static com.maquirino.minhasfinancas.model.enums.StatusLancamento.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
public class LancamentoResourceTest {

    public static final String API = "/api/lancamentos";
    public static final MediaType JSON = MediaType.APPLICATION_JSON_UTF8;

    private LancamentoDTO lancamentoDTO;
    private LancamentoDTO lancamentoDTOAtualizado;
    private Lancamento lancamentoAtualizado;
    private Lancamento lancamento;
    private Usuario usuario;
    private AtualizaStatusDTO atualizaStatusDTO;


    @Autowired
    private MockMvc mvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private UsuarioService usuarioService;

    @MockBean
    private LancamentoService service;

    @BeforeEach
    public void setUp() {
        lancamentoDTO = LancamentoDTO.builder()
                .descricao("Descricao")
                .mes(10)
                .ano(1995)
                .status(EFETIVADO)
                .tipo(TipoLancamento.RECEITA)
                .valor(BigDecimal.ONE)
                .usuario(1L)
                .dataCadastro(LocalDate.now())
                .build();

        atualizaStatusDTO = new AtualizaStatusDTO();
        atualizaStatusDTO.setStatus(PENDENTE.toString());

        lancamentoDTOAtualizado = LancamentoDTO.builder()
                .descricao("Descricao")
                .mes(12)
                .ano(2000)
                .status(PENDENTE)
                .tipo(TipoLancamento.RECEITA)
                .valor(BigDecimal.ONE)
                .usuario(1L)
                .dataCadastro(LocalDate.now())
                .build();

        usuario = Usuario.builder()
                .id(1L)
                .nome("testudo")
                .email("teste@teste.com.br")
                .build();

        lancamento = Lancamento.builder()
                .id(1L)
                .descricao("Descricao")
                .mes(10)
                .ano(1995)
                .status(EFETIVADO)
                .tipo(TipoLancamento.RECEITA)
                .valor(BigDecimal.ONE)
                .usuario(usuario)
                .build();

        lancamentoAtualizado = Lancamento.builder()
                .id(1L)
                .descricao("Descricao")
                .mes(12)
                .ano(2000)
                .status(PENDENTE)
                .tipo(TipoLancamento.RECEITA)
                .valor(BigDecimal.ONE)
                .usuario(usuario)
                .build();

    }

    @Test
    public void deveSalvarLancamento() throws Exception {
        when(service.salvar(any(Lancamento.class))).thenReturn(lancamento);

        String json = objectMapper.writeValueAsString(lancamentoDTO);

        MockHttpServletRequestBuilder request = MockMvcRequestBuilders
                .post(API.concat("/salvar"))
                .contentType(JSON)
                .content(json);

        mvc.perform(request)
                .andExpect(status().isCreated())
                .andExpect(jsonPath("id").value(lancamento.getId()))
                .andExpect(jsonPath("descricao").value(lancamento.getDescricao()))
                .andExpect(jsonPath("valor").value(lancamento.getValor()))
                .andExpect(jsonPath("mes").value(lancamento.getMes()))
                .andExpect(jsonPath("ano").value(lancamento.getAno()))
                .andExpect(jsonPath("usuario").value(lancamento.getUsuario()))
                .andExpect(jsonPath("tipo").value(lancamento.getTipo().toString()))
                .andExpect(jsonPath("status").value(lancamento.getStatus().toString()));
    }

    @Test
    public void deveSalvar() throws Exception {
        when(service.salvar(any(Lancamento.class))).thenThrow(RegraNegocioException.class);
        String json = objectMapper.writeValueAsString(lancamentoDTO);

        MockHttpServletRequestBuilder request = MockMvcRequestBuilders
                .post(API.concat("/salvar"))
                .contentType(JSON)
                .content(json);

        mvc.perform(request)
                .andExpect(status().isBadRequest());
    }

    @Test
    public void deveAtualizarLancamento() throws Exception {
        when(service.obterPorId(anyLong())).thenReturn(lancamento);
        when(usuarioService.obterPorId(anyLong())).thenReturn(usuario);
        when(service.atualizar(lancamento)).thenReturn(lancamentoAtualizado);

        String json = objectMapper.writeValueAsString(lancamentoDTOAtualizado);

        MockHttpServletRequestBuilder request = MockMvcRequestBuilders
                .put(API.concat("/atualizar/1"))
                .contentType(JSON)
                .content(json);

        mvc.perform(request)
                .andExpect(status().isOk())
                .andExpect(jsonPath("id").value(lancamentoAtualizado.getId()))
                .andExpect(jsonPath("descricao").value(lancamentoAtualizado.getDescricao()))
                .andExpect(jsonPath("valor").value(lancamentoAtualizado.getValor()))
                .andExpect(jsonPath("mes").value(lancamentoAtualizado.getMes()))
                .andExpect(jsonPath("ano").value(lancamentoAtualizado.getAno()))
                .andExpect(jsonPath("usuario").value(lancamentoAtualizado.getUsuario()))
                .andExpect(jsonPath("tipo").value(lancamentoAtualizado.getTipo().toString()))
                .andExpect(jsonPath("status").value(lancamentoAtualizado.getStatus().toString()));
    }

    @Test
    public void deveNaoAtualizarLancamento() throws Exception {
        when(service.obterPorId(anyLong())).thenReturn(lancamento);
        when(service.atualizar(any(Lancamento.class))).thenThrow(RegraNegocioException.class);

        String json = objectMapper.writeValueAsString(lancamentoDTO);

        MockHttpServletRequestBuilder request = MockMvcRequestBuilders
                .put(API.concat("/atualizar/1"))
                .contentType(JSON)
                .content(json);

        mvc.perform(request)
                .andExpect(status().isBadRequest());
    }

    @Test
    public void deveAtualizarStatusLancamento() throws Exception {
        when(service.obterPorId(anyLong())).thenReturn(lancamento);

        lancamento.setStatus(PENDENTE);

        when(service.atualizarStatus(any(Lancamento.class), any(StatusLancamento.class))).thenReturn(lancamento);

        String json = objectMapper.writeValueAsString(atualizaStatusDTO);

        MockHttpServletRequestBuilder request = MockMvcRequestBuilders
                .put(API.concat("/1/atualizar/status"))
                .contentType(JSON)
                .content(json);

        mvc.perform(request)
                .andExpect(status().isOk())
                .andExpect(jsonPath("id").value(lancamento.getId()))
                .andExpect(jsonPath("descricao").value(lancamento.getDescricao()))
                .andExpect(jsonPath("valor").value(lancamento.getValor()))
                .andExpect(jsonPath("mes").value(lancamento.getMes()))
                .andExpect(jsonPath("ano").value(lancamento.getAno()))
                .andExpect(jsonPath("usuario").value(lancamento.getUsuario()))
                .andExpect(jsonPath("tipo").value(lancamento.getTipo().toString()))
                .andExpect(jsonPath("status").value(lancamento.getStatus().toString()));
    }

    @Test
    public void deveNaoAtualizarStatusLancamento() throws Exception {
        when(service.obterPorId(anyLong())).thenReturn(lancamento);

        lancamento.setStatus(PENDENTE);

        when(service.atualizarStatus(any(Lancamento.class), any(StatusLancamento.class))).thenThrow(RegraNegocioException.class);

        String json = objectMapper.writeValueAsString(atualizaStatusDTO);

        MockHttpServletRequestBuilder request = MockMvcRequestBuilders
                .put(API.concat("/1/atualizar/status"))
                .contentType(JSON)
                .content(json);

        mvc.perform(request)
                .andExpect(status().isBadRequest());
    }

    @Test
    public void deveNaoAtualizarStatusLancamentoComStatusNulo() throws Exception {
        String json = objectMapper.writeValueAsString(new AtualizaStatusDTO());

        MockHttpServletRequestBuilder request = MockMvcRequestBuilders
                .put(API.concat("/1/atualizar/status"))
                .contentType(JSON)
                .content(json);

        mvc.perform(request)
                .andExpect(status().isBadRequest())
                .andExpect(MockMvcResultMatchers.content().string("Não foi possível atualizar o status do Lançamento, envie um status válido."));
    }

    @Test
    public void deveNaoAtualizarStatusLancamentoComStatusVazio() throws Exception {
        AtualizaStatusDTO value = new AtualizaStatusDTO();
        value.setStatus("");
        String json = objectMapper.writeValueAsString(value);

        MockHttpServletRequestBuilder request = MockMvcRequestBuilders
                .put(API.concat("/1/atualizar/status"))
                .contentType(JSON)
                .content(json);

        mvc.perform(request)
                .andExpect(status().isBadRequest())
                .andExpect(MockMvcResultMatchers.content().string("Não foi possível atualizar o status do Lançamento, envie um status válido."));
    }

    @Test
    public void deveNaoAtualizarStatusLancamentoComStatusBranco() throws Exception {
        AtualizaStatusDTO value = new AtualizaStatusDTO();
        value.setStatus("      ");
        String json = objectMapper.writeValueAsString(value);

        MockHttpServletRequestBuilder request = MockMvcRequestBuilders
                .put(API.concat("/1/atualizar/status"))
                .contentType(JSON)
                .content(json);

        mvc.perform(request)
                .andExpect(status().isBadRequest())
                .andExpect(MockMvcResultMatchers.content().string("Não foi possível atualizar o status do Lançamento, envie um status válido."));
    }

    @Test
    public void deveNaoAtualizarStatusLancamentoComStatusDiferente() throws Exception {
        AtualizaStatusDTO value = new AtualizaStatusDTO();
        value.setStatus("Bla");
        String json = objectMapper.writeValueAsString(value);

        MockHttpServletRequestBuilder request = MockMvcRequestBuilders
                .put(API.concat("/1/atualizar/status"))
                .contentType(JSON)
                .content(json);

        mvc.perform(request)
                .andExpect(status().isBadRequest())
                .andExpect(MockMvcResultMatchers.content().string("Não foi possível atualizar o status do Lançamento, envie um status válido."));
    }

    @Test
    public void deveDeletarLancamento() throws Exception {
        when(service.obterPorId(anyLong())).thenReturn(lancamento);
        doNothing().when(service).deletar(any(Lancamento.class));

        String json = objectMapper.writeValueAsString(lancamentoDTO);

        MockHttpServletRequestBuilder request = MockMvcRequestBuilders
                .delete(API.concat("/deletar/1"))
                .contentType(JSON)
                .content(json);

        mvc.perform(request)
                .andExpect(status().isNoContent());
    }

    @Test
    public void deveNaoDeletarLancamentoNulo() throws Exception {
        when(service.obterPorId(anyLong())).thenThrow(RegraNegocioException.class);
        doNothing().when(service).deletar(any(Lancamento.class));

        String json = objectMapper.writeValueAsString(atualizaStatusDTO);

        MockHttpServletRequestBuilder request = MockMvcRequestBuilders
                .delete(API.concat("/deletar/1"))
                .contentType(JSON)
                .content(json);

        mvc.perform(request)
                .andExpect(status().isBadRequest());
    }

    @Test
    public void deveBuscarLancamento() throws Exception {
        List<Lancamento> listaLancamentos = new ArrayList<>();
        listaLancamentos.add(lancamento);
        listaLancamentos.add(lancamentoAtualizado);

        Lancamento filtro = Lancamento.builder().usuario(usuario).build();

        when(usuarioService.obterPorId(anyLong())).thenReturn(usuario);
        when(service.buscar(filtro)).thenReturn(listaLancamentos);

        MockHttpServletRequestBuilder request = MockMvcRequestBuilders
                .get(API)
                .queryParam("usuario", "1")
                .contentType(JSON);

        mvc.perform(request)
                .andExpect(status().isOk());
    }

    @Test
    public void deveNaoBuscarLancamento() throws Exception {
        when(usuarioService.obterPorId(anyLong())).thenThrow(ErroAutenticacao.class);

        MockHttpServletRequestBuilder request = MockMvcRequestBuilders
                .get(API)
                .queryParam("usuario", "1")
                .contentType(JSON);

        mvc.perform(request)
                .andExpect(status().isBadRequest());
    }
}
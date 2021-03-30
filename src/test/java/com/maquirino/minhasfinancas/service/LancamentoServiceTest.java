package com.maquirino.minhasfinancas.service;

import com.maquirino.minhasfinancas.exception.RegraNegocioException;
import com.maquirino.minhasfinancas.model.entity.Lancamento;
import com.maquirino.minhasfinancas.model.enums.StatusLancamento;
import com.maquirino.minhasfinancas.model.enums.TipoLancamento;
import com.maquirino.minhasfinancas.model.repository.LancamentoRepository;
import com.maquirino.minhasfinancas.service.impl.LancamentoServiceImpl;
import java.math.BigDecimal;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.Example;
import org.springframework.test.context.ActiveProfiles;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.any;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.when;

@SpringBootTest
@ActiveProfiles("test")
public class LancamentoServiceTest {

    @InjectMocks
    private LancamentoServiceImpl service;

    @Mock
    private LancamentoRepository repository;

    private Lancamento lancamento;

    @BeforeEach
    public void setUp() {
        lancamento = Lancamento.builder()
                .id(1L)
                .descricao("test")
                .mes(1)
                .ano(2021)
                .valor(BigDecimal.ONE)
                .tipo(TipoLancamento.RECEITA)
                .status(StatusLancamento.PENDENTE)
                .build();
    }

    @Test
    public void deveSalvarUmLancamento() {
        when(repository.save(any(Lancamento.class))).thenReturn(lancamento);

        assertDoesNotThrow(() -> {
            service.salvar(lancamento);
        });
    }

    @Test
    public void deveAtualizarUmLancamento() {
        when(repository.save(any(Lancamento.class))).thenReturn(lancamento);

        assertDoesNotThrow(() -> {
            service.atualizar(lancamento);
        });
    }

    @Test
    public void deveDeletarUmLancamento() {
        doNothing().when(repository).delete(any(Lancamento.class));
        assertDoesNotThrow(() -> {
            service.atualizar(lancamento);
        });
    }

    @Test
    public void deveBuscarUmLancamento() {
        when(repository.findAll(any(Example.class))).thenReturn(any(List.class));
        assertDoesNotThrow(() -> {
            service.buscar(lancamento);
        });
    }

    @Test
    public void deveBuscarSaldo() {
        when(repository.obterSaldoPorTipoLancamentoEUsuario(any(Long.class), any(TipoLancamento.class))).thenReturn(BigDecimal.TEN);
        BigDecimal saldo = service.obterSaldo(lancamento.getId());
        assertEquals(BigDecimal.ZERO, saldo);
    }

    @Test
    public void deveAtualizarStatus() {
        when(repository.save(any(Lancamento.class))).thenReturn(lancamento);

        assertDoesNotThrow(() -> {
            service.atualizarStatus(lancamento, StatusLancamento.CANCELADO);
        });
    }

    @Test
    public void deveNaoSalvarUmLancamentoErroDescricaoEmBranco() {
        when(repository.save(any(Lancamento.class))).thenReturn(lancamento);

        lancamento.setDescricao("");

        assertThrows(RegraNegocioException.class, () -> {
            service.salvar(lancamento);
        });
    }

    @Test
    public void deveNaoSalvarUmLancamentoErroDescricaoNula() {
        when(repository.save(any(Lancamento.class))).thenReturn(lancamento);

        lancamento.setDescricao(null);

        assertThrows(RegraNegocioException.class, () -> {
            service.salvar(lancamento);
        });
    }

    @Test
    public void deveNaoSalvarUmLancamentoErroAnoFormatoInvalido() {
        when(repository.save(any(Lancamento.class))).thenReturn(lancamento);

        lancamento.setAno(200);

        assertThrows(RegraNegocioException.class, () -> {
            service.salvar(lancamento);

        });
    }

    @Test
    public void deveNaoSalvarUmLancamentoErroAnoNulo() {
        when(repository.save(any(Lancamento.class))).thenReturn(lancamento);

        lancamento.setAno(null);

        assertThrows(RegraNegocioException.class, () -> {
            service.salvar(lancamento);
        });
    }
}

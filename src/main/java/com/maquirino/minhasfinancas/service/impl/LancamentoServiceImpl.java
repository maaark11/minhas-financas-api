package com.maquirino.minhasfinancas.service.impl;

import com.maquirino.minhasfinancas.exception.RegraNegocioException;
import com.maquirino.minhasfinancas.model.entity.Lancamento;
import com.maquirino.minhasfinancas.model.enums.StatusLancamento;
import com.maquirino.minhasfinancas.model.repository.LancamentoRepository;
import com.maquirino.minhasfinancas.service.LancamentoService;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import net.bytebuddy.implementation.bytecode.Throw;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Example;
import org.springframework.data.domain.ExampleMatcher;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class LancamentoServiceImpl implements LancamentoService {

    @Autowired
    LancamentoRepository repository;

    @Override
    @Transactional
    public Lancamento salvar(Lancamento lancamento) {
        validar(lancamento);
        lancamento.setStatus(StatusLancamento.PENDENTE);
        return repository.save(lancamento);
    }

    @Override
    @Transactional
    public Lancamento atualizar(Lancamento lancamento) {
        Objects.requireNonNull(lancamento.getId());
        validar(lancamento);
        return repository.save(lancamento);
    }

    @Override
    @Transactional
    public void deletar(Lancamento lancamento) {
        Objects.requireNonNull(lancamento.getId());
        repository.delete(lancamento);
    }

    @Override
    @Transactional(readOnly = true)
    public List<Lancamento> buscar(Lancamento lancamentoFiltro) {

        Example example = Example.of(lancamentoFiltro,
                ExampleMatcher.matching()
                        .withIgnoreCase()
                        .withStringMatcher(ExampleMatcher.StringMatcher.CONTAINING));

        return repository.findAll(example);
    }

    @Override
    public void atualizarStatus(Lancamento lancamento, StatusLancamento statusLancamento) {

        lancamento.setStatus(statusLancamento);
        atualizar(lancamento);
    }

    @Override
    public void validar(Lancamento lancamento) {

        boolean erroDescricao = (lancamento.getDescricao().isBlank() || lancamento.getDescricao().isEmpty() || lancamento.getDescricao() == null) ? true : false;
        boolean erroMes = (lancamento.getMes() < 1 || lancamento.getMes() > 12 || lancamento.getMes() == null) ? true : false;
        boolean erroAno = (lancamento.getAno().toString().length() != 4 || lancamento.getAno() == null) ? true : false;
        boolean erroValor = (lancamento.getValor().compareTo(BigDecimal.ZERO) < 1 || lancamento.getValor() == null) ? true : false;
        boolean erroTipo = (lancamento.getTipo() == null) ? true : false;

        if (erroDescricao) {
            throw new RegraNegocioException("Descrição informado não é válida, por favor enriqueca sua descrição. Ahh! descriçoes em branco não são aceitas.");
        }
        if (erroMes) {
            throw new RegraNegocioException("Mês informado não é válido, por favor adicione um mês entre 1 e 12 :)");
        }
        if (erroAno) {
            throw new RegraNegocioException("Ano informado não é válido, por favor digite um ano no padrão YYYY.");
        }
        if (erroValor) {
            throw new RegraNegocioException("Valor informado não é válido, por favor insira um valor maior que 0.");
        }
        if (erroTipo) {
            throw new RegraNegocioException("Tipo informado não é válido, por favor adicione um tipo de lançamento.");
        }
    }
}

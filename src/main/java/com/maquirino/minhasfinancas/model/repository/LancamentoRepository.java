package com.maquirino.minhasfinancas.model.repository;

import com.maquirino.minhasfinancas.model.entity.Lancamento;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

public interface LancamentoRepository extends JpaRepository<Lancamento, Long> {

    List<Lancamento> findLancamentoById(Long id);
}

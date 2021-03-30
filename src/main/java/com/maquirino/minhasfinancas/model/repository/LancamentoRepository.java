package com.maquirino.minhasfinancas.model.repository;

import com.maquirino.minhasfinancas.model.entity.Lancamento;
import com.maquirino.minhasfinancas.model.enums.TipoLancamento;
import java.math.BigDecimal;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

public interface LancamentoRepository extends JpaRepository<Lancamento, Long> {
    @Query(value = "select sum(l.valor) from Lancamento l join l.usuario u " +
            "where u.id = :idUsuario and l.tipo = :tipo group by u")
    BigDecimal obterSaldoPorTipoLancamentoEUsuario(Long idUsuario, TipoLancamento tipo);
}

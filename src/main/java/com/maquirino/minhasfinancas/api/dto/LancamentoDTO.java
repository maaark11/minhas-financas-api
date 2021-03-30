package com.maquirino.minhasfinancas.api.dto;

import com.maquirino.minhasfinancas.model.entity.Usuario;
import com.maquirino.minhasfinancas.model.enums.StatusLancamento;
import com.maquirino.minhasfinancas.model.enums.TipoLancamento;
import java.math.BigDecimal;
import java.time.LocalDate;
import lombok.Builder;
import lombok.Data;

@Data
@Builder(toBuilder = true)
public class LancamentoDTO {

    private String descricao;
    private Integer mes;
    private Integer ano;
    private BigDecimal valor;
    private TipoLancamento tipo;
    private StatusLancamento status;
    private Long usuario;
    private LocalDate dataCadastro;
}

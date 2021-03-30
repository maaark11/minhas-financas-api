package com.maquirino.minhasfinancas.model.entity;

import com.maquirino.minhasfinancas.model.enums.StatusLancamento;
import com.maquirino.minhasfinancas.model.enums.TipoLancamento;
import java.math.BigDecimal;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Entity
@Table(schema="financas")
@Builder(toBuilder = true)
@NoArgsConstructor(access = AccessLevel.PACKAGE)
@AllArgsConstructor(access = AccessLevel.PACKAGE)
public class Lancamento {

    @Id
    @Column
    @GeneratedValue(strategy=GenerationType.IDENTITY)
    private Long id;

    @Column
    private String descricao;

    @Column
    private Integer mes;

    @Column
    private Integer ano;

    @Column
    private BigDecimal valor;

    @Column
    @Enumerated(EnumType.STRING)
    private TipoLancamento tipo;

    @Column
    @Enumerated(EnumType.STRING)
    private StatusLancamento status;

    @ManyToOne
    @JoinColumn(name = "id_usuario")
    private Usuario usuario;

}
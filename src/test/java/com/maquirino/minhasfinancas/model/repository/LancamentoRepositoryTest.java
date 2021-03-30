package com.maquirino.minhasfinancas.model.repository;

import com.maquirino.minhasfinancas.model.entity.Lancamento;
import com.maquirino.minhasfinancas.model.entity.Usuario;
import com.maquirino.minhasfinancas.model.enums.StatusLancamento;
import com.maquirino.minhasfinancas.model.enums.TipoLancamento;
import java.math.BigDecimal;
import java.util.Optional;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTestContextBootstrapper;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.BootstrapWith;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase.Replace.NONE;

@DataJpaTest
@SpringBootTest
@ActiveProfiles("test")
@AutoConfigureTestDatabase(replace = NONE)
@BootstrapWith(SpringBootTestContextBootstrapper.class)
class LancamentoRepositoryTest {

    @Autowired
    private LancamentoRepository repository;

    @Autowired
    private TestEntityManager entityManager;

    @Test
    void deveSalvarUmLancamento() {
        Lancamento entidade = repository.save(criarLancamento());

        assertNotNull(entidade);
    }

    @Test
    void deveDeletarUmLancamento() {
        Lancamento lancamento = entityManager.persist(criarLancamento());

        lancamento = entityManager.find(Lancamento.class, lancamento.getId());

        repository.delete(lancamento);

        Lancamento lancamentoDel = entityManager.find(Lancamento.class, lancamento.getId());

        assertEquals(null, lancamentoDel);
    }

    @Test
    void deveExistirUmLancamento() {
        Lancamento persist = entityManager.persist(criarLancamento());

        Optional<Lancamento> entidade = repository.findById(persist.getId());

        assertTrue(entidade.isPresent());
    }

    @Test
    void deveAtualizarUmLancamento() {
        Lancamento lancamento = criarLancamento();
        Lancamento persist = entityManager.persist(criarLancamento());

        persist.setStatus(StatusLancamento.EFETIVADO);
        persist.setDescricao("ALO");

        Lancamento updated = repository.save(persist);

        assertNotEquals(lancamento.getDescricao(), updated.getDescricao());
        assertNotEquals(lancamento.getStatus(), updated.getStatus());
        assertEquals(persist, updated);
    }

    private static Lancamento criarLancamento() {
        return Lancamento.builder()
                .usuario(Usuario.builder()
                        .nome("Test")
                        .senha("123")
                        .email("a@aa.com")
                        .build())
                .valor(BigDecimal.ONE)
                .ano(2021)
                .mes(12)
                .descricao("Test")
                .status(StatusLancamento.PENDENTE)
                .tipo(TipoLancamento.RECEITA)
                .build();
    }


}
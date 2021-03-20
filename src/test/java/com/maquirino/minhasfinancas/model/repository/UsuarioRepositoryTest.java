package com.maquirino.minhasfinancas.model.repository;

import com.maquirino.minhasfinancas.model.entity.Usuario;
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

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase.Replace.NONE;

@DataJpaTest
@SpringBootTest
@ActiveProfiles("test")
@AutoConfigureTestDatabase(replace = NONE)
@BootstrapWith(SpringBootTestContextBootstrapper.class)
class UsuarioRepositoryTest {

    @Autowired
    UsuarioRepository repository;

    @Autowired
    TestEntityManager entityManager;

    @Test
    void deveExistirUmEmail() {
        entityManager.persist(criarUsuario());

        boolean resultado = repository.existsByEmail("test@test.com.br");

        assertTrue(resultado);
    }

    @Test
    void deveNaoExistirOEmail() {
        entityManager.persist(criarUsuario());

        boolean resultado = repository.existsByEmail("alpha@test.com.br");

        assertFalse(resultado);
    }

    @Test
    void deveEncontrarPorEmail() {
        entityManager.persist(criarUsuario());

        Optional<Usuario> resultado = repository.findByEmail("test@test.com.br");

        assertTrue(resultado.isPresent());
    }

    @Test
    void deveNaoEncontrarPorEmail() {
        entityManager.persist(criarUsuario());

        Optional<Usuario> resultado = repository.findByEmail("alpha@test.com.br");

        assertFalse(resultado.isPresent());
    }

    @Test
    void devePersistirNaBase() {
        Usuario resultado = repository.save(criarUsuario());

        assertNotNull(resultado.getId());
    }

    @Test
    void deveBuscarUsuarioPorEmail() {
        entityManager.persist(criarUsuario());

        Optional<Usuario> resultado = repository.findByEmail("test@test.com.br");

        assertTrue(resultado.isPresent());
    }

    @Test
    void deveBuscarUsuarioPorEmailMasNaoEncontrar() {
        Optional<Usuario> resultado = repository.findByEmail("test@test.com.br");

        assertFalse(resultado.isPresent());
    }

    public static Usuario criarUsuario() {
        return Usuario
                .builder()
                .nome("test")
                .email("test@test.com.br")
                .senha("123")
                .build();
    }
}
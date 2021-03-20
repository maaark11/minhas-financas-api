package com.maquirino.minhasfinancas.model.repository;

import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTestContextBootstrapper;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.BootstrapWith;

import static org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase.Replace.NONE;

@DataJpaTest
@SpringBootTest
@ActiveProfiles("test")
@AutoConfigureTestDatabase(replace = NONE)
@BootstrapWith(SpringBootTestContextBootstrapper.class)
class LancamentoRepositoryTest {

}
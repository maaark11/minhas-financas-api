package com.maquirino.minhasfinancas.api.resource;

import com.maquirino.minhasfinancas.api.dto.AtualizaStatusDTO;
import com.maquirino.minhasfinancas.api.dto.LancamentoDTO;
import com.maquirino.minhasfinancas.exception.ErroAutenticacao;
import com.maquirino.minhasfinancas.exception.RegraNegocioException;
import com.maquirino.minhasfinancas.model.entity.Lancamento;
import com.maquirino.minhasfinancas.model.enums.StatusLancamento;
import com.maquirino.minhasfinancas.service.LancamentoService;
import com.maquirino.minhasfinancas.service.UsuarioService;
import java.util.List;
import java.util.Locale;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/lancamentos")
public class LancamentoResource {

    @Autowired
    private LancamentoService lancamentoService;

    @Autowired
    private UsuarioService usuarioService;

    @PostMapping("/salvar")
    public ResponseEntity salvar(@RequestBody LancamentoDTO lancamentoDTO) {
        try {
            Lancamento lancamento = converter(lancamentoDTO);
            lancamento = lancamentoService.salvar(lancamento);
            return ResponseEntity.status(HttpStatus.CREATED).body(lancamento);
        } catch (RegraNegocioException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @PutMapping("/atualizar/{id}")
    public ResponseEntity atualizar(@PathVariable("id") Long idLancamento, @RequestBody LancamentoDTO lancamentoDTO) {
        try {
            Lancamento entidade = lancamentoService.obterPorId(idLancamento);
            Lancamento atualizado = converter(lancamentoDTO);

            atualizado.setId(entidade.getId());

            lancamentoService.atualizar(atualizado);

            return ResponseEntity.ok(atualizado);
        } catch (RegraNegocioException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @PutMapping("{id}/atualizar/status")
    public ResponseEntity atualizarStatus(@PathVariable("id") Long idLancamento, @RequestBody AtualizaStatusDTO atualizaStatusDTO) {
        Lancamento lancamentoComNovoStatus;
        StatusLancamento statusSelecionado;

        try {
            if (atualizaStatusDTO.getStatus() == null) {
                throw new IllegalArgumentException();
            }
            String status = atualizaStatusDTO.getStatus();
            statusSelecionado = StatusLancamento.valueOf(status.toUpperCase(Locale.ROOT));

            Lancamento entidade = lancamentoService.obterPorId(idLancamento);
            lancamentoComNovoStatus = lancamentoService.atualizarStatus(entidade, statusSelecionado);
        } catch (RegraNegocioException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body("Não foi possível atualizar o status do Lançamento, envie um status válido.");
        }
        return ResponseEntity.ok(lancamentoComNovoStatus);
    }

    @DeleteMapping("/deletar/{id}")
    public ResponseEntity deletar(@PathVariable("id") Long idLancamento) {
        try {
            Lancamento lancamento = lancamentoService.obterPorId(idLancamento);
            lancamentoService.deletar(lancamento);
            return ResponseEntity.noContent().build();
        } catch (RegraNegocioException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @GetMapping
    public ResponseEntity buscar(
            @RequestParam(value = "descricao", required = false) String descricao,
            @RequestParam(value = "mes", required = false) Integer mes,
            @RequestParam(value = "ano", required = false) Integer ano,
            @RequestParam(value = "usuario", required = true) long idUsuario) {
        try {
            Lancamento lancamentoFiltro = Lancamento.builder()
                    .descricao(descricao)
                    .mes(mes)
                    .ano(ano)
                    .usuario(usuarioService.obterPorId(idUsuario))
                    .build();

            List<Lancamento> lancamentos = lancamentoService.buscar(lancamentoFiltro);

            return ResponseEntity.ok().body(lancamentos);
        } catch (ErroAutenticacao e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    private Lancamento converter(LancamentoDTO lancamentoDTO) {
        return Lancamento.builder()
                .usuario(usuarioService.obterPorId(lancamentoDTO.getUsuario()))
                .descricao(lancamentoDTO.getDescricao())
                .status(lancamentoDTO.getStatus())
                .valor(lancamentoDTO.getValor())
                .tipo(lancamentoDTO.getTipo())
                .mes(lancamentoDTO.getMes())
                .ano(lancamentoDTO.getAno())
                .build();
    }
}

package com.demo.pedido.service;

import com.demo.pedido.dto.ProdutoDTO;
import com.demo.pedido.dto.UsuarioDTO;
import com.demo.pedido.model.Pedido;
import com.demo.pedido.repository.PedidoRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

@Service
public class PedidoService {

    @Autowired
    private PedidoRepository repository;

    @Autowired
    private RestTemplate restTemplate;

    // URLs dos outros microserviços — lidas do application.properties
    @Value("${produto.service.url}")
    private String produtoServiceUrl;

    @Value("${usuario.service.url}")
    private String usuarioServiceUrl;

    public List<Pedido> listarTodos() {
        return repository.findAllByOrderByDataCriacaoDesc();
    }

    public Optional<Pedido> buscarPorId(Long id) {
        return repository.findById(id);
    }

    /**
     * CONCEITO CENTRAL DA DEMO:
     * Para criar um pedido, este serviço precisa consultar DOIS outros microserviços.
     * Isso demonstra a comunicação inter-serviços via HTTP (RestTemplate).
     *
     * Fluxo:
     * 1. Consulta produto-service (porta 8081) → valida produto e preço
     * 2. Consulta usuario-service (porta 8083) → valida usuário
     * 3. Salva o pedido no próprio banco H2 (porta 8082)
     * 4. Atualiza estoque no produto-service
     */
    public Pedido criarPedido(Long produtoId, Long usuarioId, Integer quantidade) {

        // ── PASSO 1: Consultar produto no produto-service ──────────────
        System.out.println("[pedido-service] 🔗 Consultando produto-service para produto ID: " + produtoId);
        ProdutoDTO produto;
        try {
            produto = restTemplate.getForObject(
                    produtoServiceUrl + "/api/produtos/" + produtoId,
                    ProdutoDTO.class
            );
        } catch (RestClientException e) {
            // CIRCUIT BREAKER SIMPLIFICADO: se o produto-service estiver fora, retornamos erro claro
            System.err.println("[pedido-service] ❌ produto-service indisponível! Aplicando fallback.");
            throw new RuntimeException("Serviço de produtos temporariamente indisponível. Tente novamente em instantes.");
        }

        if (produto == null || !Boolean.TRUE.equals(produto.getAtivo())) {
            throw new RuntimeException("Produto não encontrado ou inativo.");
        }

        if (produto.getEstoque() < quantidade) {
            throw new RuntimeException("Estoque insuficiente. Disponível: " + produto.getEstoque());
        }

        // ── PASSO 2: Consultar usuário no usuario-service ──────────────
        System.out.println("[pedido-service] 🔗 Consultando usuario-service para usuário ID: " + usuarioId);
        UsuarioDTO usuario;
        try {
            usuario = restTemplate.getForObject(
                    usuarioServiceUrl + "/api/usuarios/" + usuarioId,
                    UsuarioDTO.class
            );
        } catch (RestClientException e) {
            System.err.println("[pedido-service] ❌ usuario-service indisponível! Aplicando fallback.");
            throw new RuntimeException("Serviço de usuários temporariamente indisponível.");
        }

        if (usuario == null) {
            throw new RuntimeException("Usuário não encontrado.");
        }

        // ── PASSO 3: Criar o pedido no banco local ─────────────────────
        System.out.println("[pedido-service] 💾 Salvando pedido no banco local...");
        Pedido pedido = new Pedido();
        pedido.setProdutoId(produtoId);
        pedido.setUsuarioId(usuarioId);
        pedido.setNomeProduto(produto.getNome());
        pedido.setNomeUsuario(usuario.getNome());
        pedido.setQuantidade(quantidade);
        pedido.setValorTotal(produto.getPreco().multiply(BigDecimal.valueOf(quantidade)));
        pedido.setStatus("CONFIRMADO");

        Pedido salvo = repository.save(pedido);
        System.out.println("[pedido-service] ✅ Pedido #" + salvo.getId() + " criado com sucesso!");

        // ── PASSO 4: Atualizar estoque no produto-service ──────────────
        try {
            restTemplate.put(
                    produtoServiceUrl + "/api/produtos/" + produtoId + "/estoque/" + quantidade,
                    null
            );
            System.out.println("[pedido-service] 📦 Estoque atualizado no produto-service.");
        } catch (RestClientException e) {
            // Aqui em produção usaríamos o padrão SAGA para compensar
            System.err.println("[pedido-service] ⚠️ Não foi possível atualizar o estoque. Em produção: SAGA compensatória.");
        }

        return salvo;
    }

    public Pedido cancelarPedido(Long id) {
        return repository.findById(id).map(p -> {
            p.setStatus("CANCELADO");
            return repository.save(p);
        }).orElseThrow(() -> new RuntimeException("Pedido não encontrado."));
    }
}

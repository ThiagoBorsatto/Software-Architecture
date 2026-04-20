package com.demo.pedido.controller;

import com.demo.pedido.model.Pedido;
import com.demo.pedido.service.PedidoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/pedidos")
@CrossOrigin(origins = "*")
public class PedidoController {

    @Autowired
    private PedidoService service;

    @GetMapping
    public List<Pedido> listar() {
        return service.listarTodos();
    }

    @GetMapping("/{id}")
    public ResponseEntity<Pedido> buscar(@PathVariable Long id) {
        return service.buscarPorId(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    /**
     * POST /api/pedidos
     * Body: { "produtoId": 1, "usuarioId": 1, "quantidade": 2 }
     *
     * Este endpoint demonstra ao vivo:
     * 1. pedido-service recebe a requisição
     * 2. Chama produto-service para validar produto
     * 3. Chama usuario-service para validar usuário
     * 4. Salva o pedido no banco próprio
     */
    @PostMapping
    public ResponseEntity<?> criar(@RequestBody Map<String, Long> body) {
        try {
            Long produtoId = body.get("produtoId");
            Long usuarioId = body.get("usuarioId");
            Long quantidade = body.getOrDefault("quantidade", 1L);

            Pedido pedido = service.criarPedido(produtoId, usuarioId, quantidade.intValue());
            return ResponseEntity.ok(pedido);
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(Map.of("erro", e.getMessage()));
        }
    }

    @PutMapping("/{id}/cancelar")
    public ResponseEntity<?> cancelar(@PathVariable Long id) {
        try {
            return ResponseEntity.ok(service.cancelarPedido(id));
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(Map.of("erro", e.getMessage()));
        }
    }

    @GetMapping("/health")
    public String health() {
        return "pedido-service UP - porta 8082";
    }
}

package com.demo.produto.controller;

import com.demo.produto.model.Produto;
import com.demo.produto.service.ProdutoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/produtos")
@CrossOrigin(origins = "*")
public class ProdutoController {

    @Autowired
    private ProdutoService service;

    // GET /api/produtos → lista todos
    @GetMapping
    public List<Produto> listar() {
        System.out.println("[produto-service] 📋 Listando todos os produtos...");
        return service.listarTodos();
    }

    // GET /api/produtos/{id} → busca por ID
    @GetMapping("/{id}")
    public ResponseEntity<Produto> buscar(@PathVariable Long id) {
        System.out.println("[produto-service] 🔍 Buscando produto ID: " + id);
        return service.buscarPorId(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    // POST /api/produtos → cria novo
    @PostMapping
    public Produto criar(@RequestBody Produto produto) {
        System.out.println("[produto-service] ➕ Criando produto: " + produto.getNome());
        return service.salvar(produto);
    }

    // PUT /api/produtos/{id}/estoque/{qtd} → reduz estoque
    @PutMapping("/{id}/estoque/{quantidade}")
    public ResponseEntity<String> reduzirEstoque(@PathVariable Long id, @PathVariable int quantidade) {
        boolean ok = service.reduzirEstoque(id, quantidade);
        if (ok) {
            return ResponseEntity.ok("Estoque atualizado com sucesso");
        }
        return ResponseEntity.badRequest().body("Estoque insuficiente ou produto não encontrado");
    }

    // GET /api/produtos/health → health check simples
    @GetMapping("/health")
    public String health() {
        return "produto-service UP - porta 8081";
    }
}

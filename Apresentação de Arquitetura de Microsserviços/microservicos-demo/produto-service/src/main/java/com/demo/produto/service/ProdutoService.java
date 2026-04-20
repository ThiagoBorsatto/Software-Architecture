package com.demo.produto.service;

import com.demo.produto.model.Produto;
import com.demo.produto.repository.ProdutoRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

@Service
public class ProdutoService {

    @Autowired
    private ProdutoRepository repository;

    // Inicializa dados de exemplo ao subir o serviço
    @jakarta.annotation.PostConstruct
    public void inicializarDados() {
        if (repository.count() == 0) {
            repository.save(new Produto("Notebook Dell i5", "Notebook 15 polegadas, 8GB RAM, 256GB SSD", new BigDecimal("3499.90"), 10));
            repository.save(new Produto("Mouse Logitech MX", "Mouse sem fio ergonômico", new BigDecimal("299.90"), 25));
            repository.save(new Produto("Teclado Mecânico", "Teclado mecânico RGB switch blue", new BigDecimal("449.90"), 15));
            repository.save(new Produto("Monitor 24\" Full HD", "Monitor IPS 144Hz", new BigDecimal("1299.90"), 8));
            repository.save(new Produto("Headset Gamer", "Headset 7.1 virtual surround", new BigDecimal("199.90"), 20));
            System.out.println("[produto-service] ✅ Dados de exemplo carregados!");
        }
    }

    public List<Produto> listarTodos() {
        return repository.findByAtivoTrue();
    }

    public Optional<Produto> buscarPorId(Long id) {
        return repository.findById(id);
    }

    public Produto salvar(Produto produto) {
        return repository.save(produto);
    }

    public boolean reduzirEstoque(Long id, int quantidade) {
        Optional<Produto> opt = repository.findById(id);
        if (opt.isPresent()) {
            Produto p = opt.get();
            if (p.getEstoque() >= quantidade) {
                p.setEstoque(p.getEstoque() - quantidade);
                repository.save(p);
                return true;
            }
        }
        return false;
    }
}

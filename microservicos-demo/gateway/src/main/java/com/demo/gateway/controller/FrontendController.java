package com.demo.gateway.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Map;

@Controller
public class FrontendController {

    @Autowired
    private RestTemplate restTemplate;

    @Value("${produto.service.url}")
    private String produtoUrl;

    @Value("${pedido.service.url}")
    private String pedidoUrl;

    @Value("${usuario.service.url}")
    private String usuarioUrl;

    // ─── PÁGINA INICIAL ───────────────────────────────────────────
    @GetMapping("/")
    public String index(Model model) {
        // Status de cada microserviço
        model.addAttribute("produtoStatus", checkHealth(produtoUrl + "/api/produtos/health"));
        model.addAttribute("pedidoStatus", checkHealth(pedidoUrl + "/api/pedidos/health"));
        model.addAttribute("usuarioStatus", checkHealth(usuarioUrl + "/api/usuarios/health"));
        return "index";
    }

    // ─── PRODUTOS ─────────────────────────────────────────────────
    @GetMapping("/produtos")
    public String produtos(Model model) {
        try {
            Object[] produtos = restTemplate.getForObject(produtoUrl + "/api/produtos", Object[].class);
            model.addAttribute("produtos", produtos != null ? Arrays.asList(produtos) : Collections.emptyList());
            model.addAttribute("servicoOnline", true);
        } catch (RestClientException e) {
            model.addAttribute("produtos", Collections.emptyList());
            model.addAttribute("servicoOnline", false);
            model.addAttribute("erro", "produto-service indisponível: " + e.getMessage());
        }
        return "produtos";
    }

    // ─── PEDIDOS ──────────────────────────────────────────────────
    @GetMapping("/pedidos")
    public String pedidos(Model model) {
        try {
            Object[] pedidos = restTemplate.getForObject(pedidoUrl + "/api/pedidos", Object[].class);
            model.addAttribute("pedidos", pedidos != null ? Arrays.asList(pedidos) : Collections.emptyList());

            Object[] usuarios = restTemplate.getForObject(usuarioUrl + "/api/usuarios", Object[].class);
            model.addAttribute("usuarios", usuarios != null ? Arrays.asList(usuarios) : Collections.emptyList());

            Object[] produtos = restTemplate.getForObject(produtoUrl + "/api/produtos", Object[].class);
            model.addAttribute("produtos", produtos != null ? Arrays.asList(produtos) : Collections.emptyList());

            model.addAttribute("servicoOnline", true);
        } catch (RestClientException e) {
            model.addAttribute("pedidos", Collections.emptyList());
            model.addAttribute("servicoOnline", false);
            model.addAttribute("erro", "Serviço indisponível: " + e.getMessage());
        }
        return "pedidos";
    }

    // ─── CRIAR PEDIDO (POST do formulário) ────────────────────────
    @PostMapping("/pedidos/criar")
    public String criarPedido(@RequestParam Long produtoId,
                               @RequestParam Long usuarioId,
                               @RequestParam(defaultValue = "1") Integer quantidade,
                               Model model) {
        try {
            Map<String, Long> body = Map.of(
                    "produtoId", produtoId,
                    "usuarioId", usuarioId,
                    "quantidade", quantidade.longValue()
            );
            restTemplate.postForObject(pedidoUrl + "/api/pedidos", body, Object.class);
            return "redirect:/pedidos?sucesso=true";
        } catch (RestClientException e) {
            return "redirect:/pedidos?erro=true";
        }
    }

    // ─── USUARIOS ─────────────────────────────────────────────────
    @GetMapping("/usuarios")
    public String usuarios(Model model) {
        try {
            Object[] usuarios = restTemplate.getForObject(usuarioUrl + "/api/usuarios", Object[].class);
            model.addAttribute("usuarios", usuarios != null ? Arrays.asList(usuarios) : Collections.emptyList());
            model.addAttribute("servicoOnline", true);
        } catch (RestClientException e) {
            model.addAttribute("usuarios", Collections.emptyList());
            model.addAttribute("servicoOnline", false);
        }
        return "usuarios";
    }

    // ─── HELPER: verifica se um serviço está online ────────────────
    private boolean checkHealth(String url) {
        try {
            restTemplate.getForObject(url, String.class);
            return true;
        } catch (Exception e) {
            return false;
        }
    }
}

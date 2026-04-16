package com.demo.pedido.dto;

import java.math.BigDecimal;

/**
 * DTO (Data Transfer Object) que representa o Produto recebido do produto-service.
 * CONCEITO CHAVE: cada microserviço define sua própria representação dos dados externos.
 * O pedido-service NÃO compartilha a classe Produto com o produto-service.
 * Cada serviço tem seu próprio modelo — isso é isolamento real.
 */
public class ProdutoDTO {

    private Long id;
    private String nome;
    private BigDecimal preco;
    private Integer estoque;
    private Boolean ativo;

    public ProdutoDTO() {}

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public String getNome() { return nome; }
    public void setNome(String nome) { this.nome = nome; }
    public BigDecimal getPreco() { return preco; }
    public void setPreco(BigDecimal preco) { this.preco = preco; }
    public Integer getEstoque() { return estoque; }
    public void setEstoque(Integer estoque) { this.estoque = estoque; }
    public Boolean getAtivo() { return ativo; }
    public void setAtivo(Boolean ativo) { this.ativo = ativo; }
}

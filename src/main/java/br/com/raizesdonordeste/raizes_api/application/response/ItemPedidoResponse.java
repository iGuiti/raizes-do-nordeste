package br.com.raizesdonordeste.raizes_api.application.response;

import java.math.BigDecimal;

import br.com.raizesdonordeste.raizes_api.domain.entity.ItemPedido;

public class ItemPedidoResponse {

    private Integer idProduto;
    private String nomeProduto;
    private Integer quantidade;
    private BigDecimal precoUnitario;
    private BigDecimal subtotal;

    	// Representa um item na resposta do pedido
    
    public ItemPedidoResponse(ItemPedido item) {
        this.idProduto = item.getProduto().getIdProduto();
        this.nomeProduto = item.getProduto().getNome();
        this.quantidade = item.getQuantidade();
        this.precoUnitario = item.getPrecoUnitario();

        this.subtotal = precoUnitario.multiply(
                BigDecimal.valueOf(quantidade));
    }

    public Integer getIdProduto() {
        return idProduto;
    }

    public String getNomeProduto() {
        return nomeProduto;
    }

    public Integer getQuantidade() {
        return quantidade;
    }

    public BigDecimal getPrecoUnitario() {
        return precoUnitario;
    }

    public BigDecimal getSubtotal() {
        return subtotal;
    }
}
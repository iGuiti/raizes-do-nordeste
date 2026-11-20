package br.com.raizesdonordeste.raizes_api.application.request;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

	// Dados do produto que foi solicitado no pedido
 
public class ItemPedidoRequest {

    @NotNull(message = "Informe o produto.")
    @Positive(message = "O ID do produto deve ser positivo.")
    private Integer idProduto;

    @NotNull(message = "Informe a quantidade.")
    @Positive(message = "A quantidade deve ser maior que zero.")
    private Integer quantidade;

    public Integer getIdProduto() {
        return idProduto;
    }

    public void setIdProduto(Integer idProduto) {
        this.idProduto = idProduto;
    }

    public Integer getQuantidade() {
        return quantidade;
    }

    public void setQuantidade(Integer quantidade) {
        this.quantidade = quantidade;
    }
}
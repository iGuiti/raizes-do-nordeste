package br.com.raizesdonordeste.raizes_api.application.request;

import java.util.List;

import br.com.raizesdonordeste.raizes_api.domain.enums.CanalPedido;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

	// Dados solicitados para criação de um pedido

public class CriarPedidoRequest {

    @NotNull(message = "Informe o cliente.")
    @Positive(message = "O ID do cliente deve ser positivo.")
    private Integer idCliente;

    @NotNull(message = "Informe a unidade.")
    @Positive(message = "O ID da unidade deve ser positivo.")
    private Integer idUnidade;

    @NotNull(message = "Informe o canal do pedido.")
    private CanalPedido canalPedido;

    @NotBlank(message = "Informe a forma de pagamento.")
    private String formaPagamento;

    @NotEmpty(message = "O pedido deve conter pelo menos um item.")
    @Valid
    private List<ItemPedidoRequest> itens;

    public Integer getIdCliente() {
        return idCliente;
    }

    public void setIdCliente(Integer idCliente) {
        this.idCliente = idCliente;
    }

    public Integer getIdUnidade() {
        return idUnidade;
    }

    public void setIdUnidade(Integer idUnidade) {
        this.idUnidade = idUnidade;
    }

    public CanalPedido getCanalPedido() {
        return canalPedido;
    }

    public void setCanalPedido(CanalPedido canalPedido) {
        this.canalPedido = canalPedido;
    }

    public String getFormaPagamento() {
        return formaPagamento;
    }

    public void setFormaPagamento(String formaPagamento) {
        this.formaPagamento = formaPagamento;
    }

    public List<ItemPedidoRequest> getItens() {
        return itens;
    }

    public void setItens(List<ItemPedidoRequest> itens) {
        this.itens = itens;
    }
    
    // Permite simular uma recusa no pagamento
    
    private boolean simularFalha;

    public boolean isSimularFalha() {
        return simularFalha;
    }

    public void setSimularFalha(boolean simularFalha) {
        this.simularFalha = simularFalha;
    }
}
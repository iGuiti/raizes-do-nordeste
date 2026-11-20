package br.com.raizesdonordeste.raizes_api.application.response;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

import br.com.raizesdonordeste.raizes_api.domain.entity.Pagamento;
import br.com.raizesdonordeste.raizes_api.domain.entity.Pedido;
import br.com.raizesdonordeste.raizes_api.domain.enums.CanalPedido;

	// Representa a resposta de um pedido para a API

public class PedidoResponse {

    private Integer idPedido;
    private Integer idCliente;
    private Integer idUnidade;
    private CanalPedido canalPedido;
    private String statusPedido;
    private BigDecimal valorTotal;
    private LocalDateTime dataCriacao;
    private List<ItemPedidoResponse> itens;
    private String statusPagamento;
    private String idTransacao;

    public PedidoResponse(Pedido pedido, Pagamento pagamento) {
        this.idPedido = pedido.getIdPedido();
        this.idCliente = pedido.getCliente().getIdCliente();
        this.idUnidade = pedido.getUnidade().getIdUnidade();
        this.canalPedido = pedido.getCanalPedido();
        this.statusPedido = pedido.getStatus();
        this.valorTotal = pedido.getValorTotal();
        this.dataCriacao = pedido.getDataCriacao();

        this.itens = pedido.getItens().stream()
                .map(item -> new ItemPedidoResponse(item))
                .toList();

        this.statusPagamento = pagamento.getStatus();
        this.idTransacao = pagamento.getIdTransacao();
    }

    public Integer getIdPedido() {
        return idPedido;
    }

    public Integer getIdCliente() {
        return idCliente;
    }

    public Integer getIdUnidade() {
        return idUnidade;
    }

    public CanalPedido getCanalPedido() {
        return canalPedido;
    }

    public String getStatusPedido() {
        return statusPedido;
    }

    public BigDecimal getValorTotal() {
        return valorTotal;
    }

    public LocalDateTime getDataCriacao() {
        return dataCriacao;
    }

    public List<ItemPedidoResponse> getItens() {
        return itens;
    }

    public String getStatusPagamento() {
        return statusPagamento;
    }

    public String getIdTransacao() {
        return idTransacao;
    }
}
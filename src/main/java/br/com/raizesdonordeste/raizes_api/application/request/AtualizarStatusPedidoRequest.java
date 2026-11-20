package br.com.raizesdonordeste.raizes_api.application.request;

import jakarta.validation.constraints.NotBlank;

public class AtualizarStatusPedidoRequest {

    @NotBlank(message = "Informe o novo status do pedido.")
    private String status;

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }
}
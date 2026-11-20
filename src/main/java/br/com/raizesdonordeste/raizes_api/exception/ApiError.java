package br.com.raizesdonordeste.raizes_api.exception;

import java.time.LocalDateTime;

	//Representa uma resposta de erro da API
public class ApiError {

    private int status;
    private String mensagem;
    private String caminho;
    private LocalDateTime dataHora;

    public ApiError(int status, String mensagem, String caminho) {
        this.status = status;
        this.mensagem = mensagem;
        this.caminho = caminho;
        this.dataHora = LocalDateTime.now();
    }

    public int getStatus() {
        return status;
    }

    public String getMensagem() {
        return mensagem;
    }

    public String getCaminho() {
        return caminho;
    }

    public LocalDateTime getDataHora() {
        return dataHora;
    }
}
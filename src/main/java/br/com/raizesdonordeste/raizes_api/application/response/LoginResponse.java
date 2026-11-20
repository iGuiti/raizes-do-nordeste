package br.com.raizesdonordeste.raizes_api.application.response;

	// Retorno pós autenticação
public class LoginResponse {

    private String token;
    private String tipoToken;

    public LoginResponse(String token) {
        this.token = token;
        this.tipoToken = "Bearer";
    }

    public String getToken() {
        return token;
    }

    public String getTipoToken() {
        return tipoToken;
    }
}
package br.com.raizesdonordeste.raizes_api.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import br.com.raizesdonordeste.raizes_api.application.request.LoginRequest;
import br.com.raizesdonordeste.raizes_api.application.response.LoginResponse;
import br.com.raizesdonordeste.raizes_api.application.service.AutenticacaoService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;

import jakarta.validation.Valid;


@Tag(
	    name = "Autenticação",
	    description = "Cadastro de usuários e autenticação na API."
	)

	// Recebe as requisições de login
@RestController
@RequestMapping("/api/auth")
public class LoginController {

    private final AutenticacaoService autenticacaoService;

    
    public LoginController(AutenticacaoService autenticacaoService) {
        this.autenticacaoService = autenticacaoService;
    }


@Operation(
    summary = "Autenticar usuário",
    description = "Valida as credenciais do usuário e retorna um token JWT."
)

@io.swagger.v3.oas.annotations.parameters.RequestBody(
    description = "Credenciais de acesso do usuário.",
    required = true,
    content = @Content(
        mediaType = "application/json",
        schema = @Schema(implementation = LoginRequest.class),
        examples = @ExampleObject(
            name = "Login",
            value = """
            {
                "email": "usuario@exemplo.com",
                "senha": "Senha123"
            }
            """
        )
    )
)

@ApiResponses(value = {

    @ApiResponse(
        responseCode = "200",
        description = "Autenticação realizada com sucesso",
        content = @Content(
            mediaType = "application/json",
            schema = @Schema(implementation = LoginResponse.class),
            examples = @ExampleObject(
                name = "Login realizado",
                value = """
                {
                    "token": "TOKEN_JWT_GERADO_PELA_API",
                    "tipoToken": "Bearer"
                }
                """
            )
        )
    ),

    @ApiResponse(
        responseCode = "400",
        description = "Dados de entrada inválidos",
        content = @Content(
            mediaType = "application/json",
            examples = @ExampleObject(
                value = """
                {
                    "status": 400,
                    "mensagem": "Informe um e-mail válido.",
                    "caminho": "/api/auth/login",
                    "dataHora": "2026-11-11T19:00:00"
                }
                """
            )
        )
    ),

    @ApiResponse(
        responseCode = "401",
        description = "E-mail ou senha inválidos",
        content = @Content(
            mediaType = "application/json",
            examples = @ExampleObject(
                value = """
                {
                    "status": 401,
                    "mensagem": "E-mail ou senha inválidos.",
                    "caminho": "/api/auth/login",
                    "dataHora": "2026-11-11T19:00:00"
                }
                """
            )
        )
    )
})

    
    // Autentica o usuário
    @PostMapping("/login")
    public ResponseEntity<LoginResponse> login(
            @Valid @RequestBody LoginRequest request) {

        LoginResponse resposta = autenticacaoService.login(request);

        return ResponseEntity.ok(resposta);
    }
}
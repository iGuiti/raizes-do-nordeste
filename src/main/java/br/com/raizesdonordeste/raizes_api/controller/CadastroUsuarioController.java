package br.com.raizesdonordeste.raizes_api.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import br.com.raizesdonordeste.raizes_api.application.request.CadastroUsuarioRequest;
import br.com.raizesdonordeste.raizes_api.application.service.CadastroUsuarioService;

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

	// Recebe as requisições de cadastros de usuários
@RestController
@RequestMapping("/api/auth")
public class CadastroUsuarioController {

    private final CadastroUsuarioService cadastroUsuarioService;

    public CadastroUsuarioController(
            CadastroUsuarioService cadastroUsuarioService) {

        this.cadastroUsuarioService = cadastroUsuarioService;
    }


@Operation(
    summary = "Cadastrar usuário",
    description = "Cadastra um novo usuário com perfil CLIENTE e cria seu registro de cliente."
)

@io.swagger.v3.oas.annotations.parameters.RequestBody(
    description = "Dados necessários para cadastro de usuário.",
    required = true,
    content = @Content(
        mediaType = "application/json",
        schema = @Schema(implementation = CadastroUsuarioRequest.class),
        examples = @ExampleObject(
            name = "Cadastro de usuário",
            value = """
            {
                "nome": "Usuario Teste",
                "email": "usuario@exemplo.com",
                "senha": "Senha123",
                "telefone": "16999999998"
            }
            """
        )
    )
)

@ApiResponses(value = {

    @ApiResponse(
        responseCode = "201",
        description = "Usuário cadastrado com sucesso",
        content = @Content(
            mediaType = "application/json",
            schema = @Schema(type = "integer", example = "5"),
            examples = @ExampleObject(
                name = "Cadastro realizado",
                value = "5"
            )
        )
    ),

    @ApiResponse(
        responseCode = "400",
        description = "Dados inválidos ou e-mail já cadastrado",
        content = @Content(
            mediaType = "application/json",
            examples = {
                @ExampleObject(
                    name = "Dados inválidos",
                    value = """
                    {
                        "status": 400,
                        "mensagem": "Informe um e-mail válido.",
                        "caminho": "/api/auth/cadastro",
                        "dataHora": "2026-11-11T19:00:00"
                    }
                    """
                ),
                @ExampleObject(
                    name = "E-mail já cadastrado",
                    value = """
                    {
                        "status": 400,
                        "mensagem": "Já existe um usuário cadastrado com esse e-mail.",
                        "caminho": "/api/auth/cadastro",
                        "dataHora": "2026-11-11T19:00:00"
                    }
                    """
                )
            }
        )
    )
})

    
    // Cadastra usuário e cliente
    @PostMapping("/cadastro")
    public ResponseEntity<Integer> cadastrar(
            @Valid @RequestBody CadastroUsuarioRequest request) {

        Integer idCliente = cadastroUsuarioService.cadastrar(request);

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(idCliente);
    }
}
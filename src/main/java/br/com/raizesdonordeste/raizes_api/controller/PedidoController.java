package br.com.raizesdonordeste.raizes_api.controller;




import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.data.domain.Page;

import br.com.raizesdonordeste.raizes_api.domain.enums.CanalPedido;
import br.com.raizesdonordeste.raizes_api.exception.ApiError;
import br.com.raizesdonordeste.raizes_api.application.request.CriarPedidoRequest;
import br.com.raizesdonordeste.raizes_api.application.response.PedidoResponse;
import br.com.raizesdonordeste.raizes_api.application.service.PedidoService;
import br.com.raizesdonordeste.raizes_api.application.request.AtualizarStatusPedidoRequest;

import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;

import jakarta.validation.Valid;


@Tag(
	    name = "Pedidos",
	    description = "Operações de criação e consulta de pedidos."
	)
	@SecurityRequirement(name = "bearerAuth")

	// Recebe as requisições de pedidos
@RestController
@RequestMapping("/api/pedidos")
public class PedidoController {

    private final PedidoService pedidoService;

    public PedidoController(PedidoService pedidoService) {
        this.pedidoService = pedidoService;
    }

    @Operation(
    	    summary = "Consultar pedidos por canal",
    	    description = "Retorna os pedidos cadastrados conforme o canal informado, "
    	                + "permitindo filtrar pelo status e navegar entre páginas. "
    	                + "A numeração das páginas começa em 1 e o limite máximo "
    	                + "é de 100 registros por página."
    	)
    	@ApiResponses(value = {
    	    @ApiResponse(
    	        responseCode = "200",
    	        description = "Pedidos consultados com sucesso. Retorna os pedidos em content e os metadados da paginação.",
    	        content = @Content(
    	            mediaType = "application/json",
    	            schema = @Schema(type = "object"),
    	            examples = @ExampleObject(
    	                name = "Consulta paginada realizada",
    	                value = """
    	                {
    	                    "content": [
    	                        {
    	                            "idPedido": 3,
    	                            "idCliente": 1,
    	                            "idUnidade": 1,
    	                            "canalPedido": "TOTEM",
    	                            "statusPedido": "ENTREGUE",
    	                            "valorTotal": 18.90,
    	                            "dataCriacao": "2026-11-09T20:21:38",
    	                            "itens": [
    	                                {
    	                                    "idProduto": 1,
    	                                    "nomeProduto": "Cuscuz Cremoso",
    	                                    "precoUnitario": 18.90,
    	                                    "quantidade": 1,
    	                                    "subtotal": 18.90
    	                                }
    	                            ],
    	                            "statusPagamento": "APROVADO",
    	                            "idTransacao": "MOCK-52181fcf-ccdf-42d6-b76e-12394f0ff4e4"
    	                        }
    	                    ],
    	                    "pageable": {
    	                        "offset": 0,
    	                        "pageNumber": 0,
    	                        "pageSize": 10,
    	                        "paged": true,
    	                        "sort": {
    	                            "empty": false,
    	                            "sorted": true,
    	                            "unsorted": false
    	                        },
    	                        "unpaged": false
    	                    },
    	                    "size": 10,
    	                    "sort": {
    	                        "empty": false,
    	                        "sorted": true,
    	                        "unsorted": false
    	                    },
    	                    "totalElements": 1,
    	                    "totalPages": 1
    	                }
    	                """
    	            )
    	        )
    	    ),
    	    @ApiResponse(
    	        responseCode = "400",
    	        description = "Canal inválido ou parâmetros de consulta fora dos valores permitidos.",
    	        content = @Content(
    	            mediaType = "application/json",
    	            schema = @Schema(implementation = ApiError.class),
    	            examples = @ExampleObject(
    	                value = """
    	                {
    	                    "status": 400,
    	                    "mensagem": "A página deve ser maior que zero.",
    	                    "caminho": "/api/pedidos",
    	                    "dataHora": "2026-11-10T19:00:00"
    	                }
    	                """
    	            )
    	        )
    	    ),
    	    @ApiResponse(
    	        responseCode = "401",
    	        description = "Usuário não autenticado.",
    	        content = @Content(
    	            mediaType = "application/json",
    	            schema = @Schema(implementation = ApiError.class),
    	            examples = @ExampleObject(
    	                value = """
    	                {
    	                    "status": 401,
    	                    "mensagem": "Usuário não autenticado.",
    	                    "caminho": "/api/pedidos",
    	                    "dataHora": "2026-11-10T19:00:00"
    	                }
    	                """
    	            )
    	        )
    	    ),
    	    @ApiResponse(
    	        responseCode = "403",
    	        description = "Usuário sem permissão.",
    	        content = @Content(
    	            mediaType = "application/json",
    	            schema = @Schema(implementation = ApiError.class),
    	            examples = @ExampleObject(
    	                value = """
    	                {
    	                    "status": 403,
    	                    "mensagem": "Você não tem permissão para acessar este recurso.",
    	                    "caminho": "/api/pedidos",
    	                    "dataHora": "2026-11-10T19:00:00"
    	                }
    	                """
    	            )
    	        )
    	    )
    	})
    

    // Consulta pedidos por canal, status e paginação
    @GetMapping
    public ResponseEntity<Page<PedidoResponse>> buscarPorCanal(

        @Parameter(
        	description = "Canal utilizado para realizar o pedido (APP, TOTEM, BALCAO, PICKUP ou WEB)",
            example = "TOTEM",
            required = true
        )
        @RequestParam CanalPedido canalPedido,

        @Parameter(
            description = "Status atual do pedido. Filtro opcional.",
            example = "ENTREGUE"
        )
        @RequestParam(required = false) String status,

        @Parameter(
            description = "Número da página (inicia em 1)",
            example = "1"
        )
        @RequestParam(defaultValue = "1") int page,

        @Parameter(
            description = "Quantidade de registros por página (mínimo 1 e máximo 100)",
            example = "10"
        )
        @RequestParam(defaultValue = "10") int limit) {

        Page<PedidoResponse> pedidos =
                pedidoService.buscarPorCanal(
                        canalPedido, status, page, limit);

        return ResponseEntity.ok(pedidos);
    }

    

@Operation(
    summary = "Criar pedido",
    description = "Registra um pedido, valida seus itens e processa o pagamento simulado."
)

@io.swagger.v3.oas.annotations.parameters.RequestBody(
    description = "Dados necessários para criar um pedido.",
    required = true,
    content = @Content(
        mediaType = "application/json",
        schema = @Schema(implementation = CriarPedidoRequest.class),
        examples = @ExampleObject(
            name = "Criar pedido",
            value = """
            {
                "idCliente": 1,
                "idUnidade": 1,
                "canalPedido": "TOTEM",
                "formaPagamento": "PIX",
                "itens": [
                    {
                        "idProduto": 1,
                        "quantidade": 1
                    }
                ],
                "simularFalha": false
            }
            """
        )
    )
)

@ApiResponses(value = {

    @ApiResponse(
        responseCode = "201",
        description = "Pedido criado com sucesso",
        content = @Content(
            mediaType = "application/json",
            schema = @Schema(implementation = PedidoResponse.class),

examples = {
    @ExampleObject(
        name = "Pagamento aprovado",
        value = """
        {
            "idPedido": 3,
            "idCliente": 1,
            "idUnidade": 1,
            "canalPedido": "TOTEM",
            "statusPedido": "PAGO",
            "valorTotal": 18.90,
            "dataCriacao": "2026-11-09T20:21:38",
            "itens": [
                {
                    "idProduto": 1,
                    "nomeProduto": "Cuscuz Cremoso",
                    "precoUnitario": 18.90,
                    "quantidade": 1,
                    "subtotal": 18.90
                }
            ],
            "statusPagamento": "APROVADO",
            "idTransacao": "MOCK-52181fcf-ccdf-42d6-b76e-12394f0ff4e4"
        }
        """
    ),
    @ExampleObject(
        name = "Pagamento recusado",
        value = """
        {
            "idPedido": 4,
            "idCliente": 1,
            "idUnidade": 1,
            "canalPedido": "APP",
            "statusPedido": "PAGAMENTO_RECUSADO",
            "valorTotal": 37.80,
            "dataCriacao": "2026-11-11T19:23:58.3127859",
            "itens": [
                {
                    "idProduto": 1,
                    "nomeProduto": "Cuscuz Cremoso",
                    "precoUnitario": 18.90,
                    "quantidade": 2,
                    "subtotal": 37.80
                }
            ],
            "statusPagamento": "RECUSADO",
            "idTransacao": "MOCK-e70ae71f-5152-40ab-ad8a-2f0d41344955"
        }
          """
    )
}
        )
    ),
  

    @ApiResponse(
        responseCode = "400",
        description = "Dados inválidos ou erro nas regras de negócio",
        content = @Content(
            mediaType = "application/json",
            examples = @ExampleObject(
                value = """
                {
                    "status": 400,
                    "mensagem": "A quantidade deve ser maior que zero.",
                    "caminho": "/api/pedidos",
                    "dataHora": "2026-11-10T19:00:00"
                }
                """
            )
        )
    ),

    @ApiResponse(
        responseCode = "401",
        description = "Usuário não autenticado",
        content = @Content(
            mediaType = "application/json",
            examples = @ExampleObject(
                value = """
                {
                    "status": 401,
                    "mensagem": "Usuário não autenticado.",
                    "caminho": "/api/pedidos",
                    "dataHora": "2026-11-10T19:00:00"
                }
                """
            )
        )
    ),

    @ApiResponse(
        responseCode = "403",
        description = "Usuário sem permissão",
        content = @Content(
            mediaType = "application/json",
            examples = @ExampleObject(
                value = """
                {
                    "status": 403,
                    "mensagem": "Você não tem permissão para acessar este recurso.",
                    "caminho": "/api/pedidos",
                    "dataHora": "2026-11-10T19:00:00"
                }
                """
            )
        )
    )
})
    
    // Cria um pedido e simula pagamento
    @PostMapping
    public ResponseEntity<PedidoResponse> criarPedido(
            @Valid @RequestBody CriarPedidoRequest request) {

        PedidoResponse resposta = pedidoService.criarPedido(request);

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(resposta);
    }


@Operation(
    summary = "Atualizar status do pedido",
    description = "Atualiza o status operacional do pedido, respeitando as transições permitidas. Requer autenticação JWT e acesso restrito aos perfis COZINHA, ADMIN e GERENTE."
)


@io.swagger.v3.oas.annotations.parameters.RequestBody(
    description = "Novo status operacional do pedido.",
    required = true,
    content = @Content(
        mediaType = "application/json",
        schema = @Schema(implementation = AtualizarStatusPedidoRequest.class),
        examples = @ExampleObject(
            name = "Iniciar preparação",
            value = """
            {
                "status": "EM_PREPARACAO"
            }
            """
        )
    )
)

@ApiResponses(value = {

	    @ApiResponse(
	        responseCode = "200",
	        description = "Status atualizado com sucesso",
	        content = @Content(
	            mediaType = "application/json",
	            schema = @Schema(implementation = PedidoResponse.class),
	            examples = @ExampleObject(
	                name = "Pedido em preparação",
	                value = """
	                {
	                    "idPedido": 3,
	                    "idCliente": 1,
	                    "idUnidade": 1,
	                    "canalPedido": "TOTEM",
	                    "statusPedido": "EM_PREPARACAO",
	                    "valorTotal": 18.90,
	                    "dataCriacao": "2026-11-09T20:21:38",
	                    "itens": [
	                        {
	                            "idProduto": 1,
	                            "nomeProduto": "Cuscuz Cremoso",
	                            "precoUnitario": 18.90,
	                            "quantidade": 1,
	                            "subtotal": 18.90
	                        }
	                    ],
	                    "statusPagamento": "APROVADO",
	                    "idTransacao": "MOCK-52181fcf-ccdf-42d6-b76e-12394f0ff4e4"
	                }
	                """
	            )
	        )
	    ),

	    @ApiResponse(
	        responseCode = "400",
	        description = "Transição de status inválida",
	        content = @Content(
	            mediaType = "application/json",
	            schema = @Schema(implementation = ApiError.class),
	            examples = @ExampleObject(
	                value = """
	                {
	                    "status": 400,
	                    "mensagem": "Não é permitida a alteração de ENTREGUE para PRONTO.",
	                    "caminho": "/api/pedidos/3/status",
	                    "dataHora": "2026-11-15T14:00:00"
	                }
	                """
	            )
	        )
	    ),

	    @ApiResponse(
	        responseCode = "401",
	        description = "Usuário não autenticado",
	        content = @Content(
	            mediaType = "application/json",
	            schema = @Schema(implementation = ApiError.class),
	            examples = @ExampleObject(
	                value = """
	                {
	                    "status": 401,
	                    "mensagem": "Usuário não autenticado.",
	                    "caminho": "/api/pedidos/3/status",
	                    "dataHora": "2026-11-15T14:00:00"
	                }
	                """
	            )
	        )
	    ),

	    @ApiResponse(
	        responseCode = "403",
	        description = "Usuário sem permissão",
	        content = @Content(
	            mediaType = "application/json",
	            schema = @Schema(implementation = ApiError.class),
	            examples = @ExampleObject(
	                value = """
	                {
	                    "status": 403,
	                    "mensagem": "Você não tem permissão para acessar este recurso.",
	                    "caminho": "/api/pedidos/3/status",
	                    "dataHora": "2026-11-15T14:00:00"
	                }
	                """
	            )
	        )
	    ),

	    @ApiResponse(
	        responseCode = "404",
	        description = "Pedido não encontrado",
	        content = @Content(
	            mediaType = "application/json",
	            schema = @Schema(implementation = ApiError.class),
	            examples = @ExampleObject(
	                value = """
	                {
	                    "status": 404,
	                    "mensagem": "Pedido não encontrado.",
	                    "caminho": "/api/pedidos/999999/status",
	                    "dataHora": "2026-11-15T14:00:00"
	                }
	                """
	            )
	        )
	    )
	})
	// Atualiza status do pedido na cozinha
	@PatchMapping("/{idPedido}/status")
	public ResponseEntity<PedidoResponse> atualizarStatus(

	    @Parameter(
	        description = "Identificador do pedido que terá o status atualizado.",
	        example = "3"
	    )
	    @PathVariable Integer idPedido,

	    @Valid @RequestBody AtualizarStatusPedidoRequest request) {

	    PedidoResponse resposta =
	            pedidoService.atualizarStatus(idPedido, request.getStatus());

	    return ResponseEntity.ok(resposta);
	}
}
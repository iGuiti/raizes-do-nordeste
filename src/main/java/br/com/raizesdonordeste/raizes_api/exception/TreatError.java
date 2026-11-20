package br.com.raizesdonordeste.raizes_api.exception;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;
import org.springframework.web.server.ResponseStatusException;

import jakarta.servlet.http.HttpServletRequest;

// Centraliza o tratamento dos erros da API
 
@RestControllerAdvice
public class TreatError {

    // Trata erros de validação dos campos recebidos
     
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ApiError> tratarValidacao(
            MethodArgumentNotValidException ex,
            HttpServletRequest request) {

        String mensagem = "Revise os dados enviados.";

        if (!ex.getBindingResult().getFieldErrors().isEmpty()) {
            mensagem = ex.getBindingResult()
                    .getFieldErrors()
                    .get(0)
                    .getDefaultMessage();
        }

        return criarResposta(
                HttpStatus.BAD_REQUEST, mensagem, request);
    }

    // Trata dados invalidos no corpo da requisição
    
    @ExceptionHandler(HttpMessageNotReadableException.class)
    public ResponseEntity<ApiError> tratarCorpoInvalido(
            HttpMessageNotReadableException ex,
            HttpServletRequest request) {

        return criarResposta(
                HttpStatus.BAD_REQUEST,
                "Corpo da requisição inválido. Verifique os campos enviados.",
                request);
    }

    //Trata parametros ausentes ou com valores invalidos
     
    @ExceptionHandler({
        MissingServletRequestParameterException.class,
        MethodArgumentTypeMismatchException.class
    })
    public ResponseEntity<ApiError> tratarParametroInvalido(
            Exception ex,
            HttpServletRequest request) {

        return criarResposta(
                HttpStatus.BAD_REQUEST,
                "Parâmetro ausente ou com valor inválido.",
                request);
    }

    // Trata erros de validação das regras de negocio
    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<ApiError> tratarRegraNegocio(
            IllegalArgumentException ex,
            HttpServletRequest request) {

        return criarResposta(
                HttpStatus.BAD_REQUEST,
                ex.getMessage(),
                request);
    }
    
    // Trata exceções com status HTTP definido pela aplicação.
    
    @ExceptionHandler(ResponseStatusException.class)
    public ResponseEntity<ApiError> tratarStatusHttp(
            ResponseStatusException ex,
            HttpServletRequest request) {

        int status = ex.getStatusCode().value();

        String mensagem = ex.getReason() != null
                ? ex.getReason()
                : "A requisição não pôde ser processada.";

        ApiError resposta = new ApiError(
                status,
                mensagem,
                request.getRequestURI());

        return ResponseEntity
                .status(ex.getStatusCode())
                .body(resposta);
    }

    // Trata falhas inesperadas sem expor detalhes internos
     
    @ExceptionHandler(Exception.class)
    public ResponseEntity<ApiError> tratarErroInterno(
            Exception ex,
            HttpServletRequest request) {

        return criarResposta(
                HttpStatus.INTERNAL_SERVER_ERROR,
                "Ocorreu um erro interno ao processar a requisição.",
                request);
    }

    private ResponseEntity<ApiError> criarResposta(
            HttpStatus status,
            String mensagem,
            HttpServletRequest request) {

    	ApiError resposta = new ApiError(
    	        status.value(),
    	        mensagem,
    	        request.getRequestURI());

        return ResponseEntity.status(status).body(resposta);
    }
    
    @ExceptionHandler(AuthenticationException.class)
    public ResponseEntity<ApiError> tratarErroAutenticacao(
            AuthenticationException ex,
            HttpServletRequest request) {

        ApiError erro = new ApiError(
                401,
                "E-mail ou senha inválidos.",
                request.getRequestURI()
        );

        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(erro);
    }

    @ExceptionHandler(AccessDeniedException.class)
    public ResponseEntity<ApiError> tratarAcessoNegado(
            AccessDeniedException ex,
            HttpServletRequest request) {

        ApiError erro = new ApiError(
                403,
                "Você não tem permissão para acessar este recurso.",
                request.getRequestURI()
        );

        return ResponseEntity.status(HttpStatus.FORBIDDEN).body(erro);
    }
    
}
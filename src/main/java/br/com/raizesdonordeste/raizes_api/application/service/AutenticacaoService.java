package br.com.raizesdonordeste.raizes_api.application.service;

import java.util.Locale;

import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import br.com.raizesdonordeste.raizes_api.application.request.LoginRequest;
import br.com.raizesdonordeste.raizes_api.application.response.LoginResponse;
import br.com.raizesdonordeste.raizes_api.infrastructure.security.JwtService;

	// Realiza autenticação e gera o token
@Service
public class AutenticacaoService {

    private final AuthenticationManager authenticationManager;
    private final JwtService jwtService;

    public AutenticacaoService(
            AuthenticationManager authenticationManager,
            JwtService jwtService) {

        this.authenticationManager = authenticationManager;
        this.jwtService = jwtService;
    }

    // Valida a credenciais para gerar o token
    public LoginResponse login(LoginRequest request) {

        String email = request.getEmail()
                .trim()
                .toLowerCase(Locale.ROOT);

        Authentication autenticacao =
                authenticationManager.authenticate(
                        new UsernamePasswordAuthenticationToken(
                                email,
                                request.getSenha()));

        UserDetails usuario = (UserDetails) autenticacao.getPrincipal();

        String token = jwtService.gerarToken(usuario);

        return new LoginResponse(token);
    }
}
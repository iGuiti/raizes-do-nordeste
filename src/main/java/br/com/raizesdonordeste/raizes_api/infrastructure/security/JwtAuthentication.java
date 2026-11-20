package br.com.raizesdonordeste.raizes_api.infrastructure.security;

import java.io.IOException;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.web.filter.OncePerRequestFilter;
import org.springframework.stereotype.Component;
import io.jsonwebtoken.JwtException;

	// Verifica token recebido das requisições
@Component
public class JwtAuthentication extends OncePerRequestFilter {

    private final JwtService jwtService;
    private final UsuarioAuthetication usuarioAuthetication;

    public JwtAuthentication(
            JwtService jwtService,
            UsuarioAuthetication usuarioAuthetication) {

        this.jwtService = jwtService;
        this.usuarioAuthetication = usuarioAuthetication;
    }

    @Override
    protected void doFilterInternal(
            HttpServletRequest request,
            HttpServletResponse response,
            FilterChain filterChain)
            throws ServletException, IOException {

        String cabecalho = request.getHeader("Authorization");

        // Continua sem autenticar se não houver token Bearer
        if (cabecalho == null || !cabecalho.startsWith("Bearer ")) {
            filterChain.doFilter(request, response);
            return;
        }

        String token = cabecalho.substring(7);

        try {
            String email = jwtService.extrairEmail(token);

            if (SecurityContextHolder.getContext()
                    .getAuthentication() == null) {

                UserDetails usuario =
                        usuarioAuthetication.loadUserByUsername(email);

                if (jwtService.tokenValido(token, usuario)) {

                    UsernamePasswordAuthenticationToken autenticacao =
                            new UsernamePasswordAuthenticationToken(
                                    usuario,
                                    null,
                                    usuario.getAuthorities());

                    autenticacao.setDetails(
                            new WebAuthenticationDetailsSource()
                                    .buildDetails(request));

                    SecurityContextHolder.getContext()
                            .setAuthentication(autenticacao);
                }
            }

        } catch (JwtException | IllegalArgumentException
                | UsernameNotFoundException ex) {
            // Token invalido: a requisição seguira sem autenticação
        }

        filterChain.doFilter(request, response);
    }
}
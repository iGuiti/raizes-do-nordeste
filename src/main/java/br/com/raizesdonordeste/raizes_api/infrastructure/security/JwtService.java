package br.com.raizesdonordeste.raizes_api.infrastructure.security;

import java.util.Date;
import javax.crypto.SecretKey;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;

	// Gera e valida os tokens do JWT
@Service
public class JwtService {

    @Value("${app.jwt.secret}")
    private String secret;

    @Value("${app.jwt.expiration-ms}")
    private long expirationMs;

    // Gera o token do usuário autenticado
    public String gerarToken(UserDetails usuario) {

        Date agora = new Date();
        Date expiracao = new Date(agora.getTime() + expirationMs);

        return Jwts.builder()
                .subject(usuario.getUsername())
                .issuedAt(agora)
                .expiration(expiracao)
                .signWith(obterChave())
                .compact();
    }

    // Pega email do usuário armazenado no token
    public String extrairEmail(String token) {
        return extrairDados(token).getSubject();
    }

    // Valida se o token pertence ao usuário e se é valido
    public boolean tokenValido(String token, UserDetails usuario) {

        try {
            String email = extrairEmail(token);

            return email.equals(usuario.getUsername());

        } catch (JwtException | IllegalArgumentException ex) {
            return false;
        }
    }

    // Obtem os dados do token apos verificação de assinatura
    private Claims extrairDados(String token) {

        return Jwts.parser()
                .verifyWith(obterChave())
                .build()
                .parseSignedClaims(token)
                .getPayload();
    }

    // Converte chave de base64 para JWT
    private SecretKey obterChave() {

        byte[] bytes = Decoders.BASE64.decode(secret);

        return Keys.hmacShaKeyFor(bytes);
    }
}
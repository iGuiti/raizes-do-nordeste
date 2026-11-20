package br.com.raizesdonordeste.raizes_api.infrastructure.security;

import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import com.fasterxml.jackson.databind.SerializationFeature;

import br.com.raizesdonordeste.raizes_api.exception.ApiError;
import com.fasterxml.jackson.databind.ObjectMapper;

import jakarta.servlet.http.HttpServletResponse;
	// Configura os recursos de segurança da aplicação
@Configuration
public class SecurityConfig {

	@Bean
	public ObjectMapper objectMapper() {
	    return new ObjectMapper()
	            .findAndRegisterModules()
	            .disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);
	}
	
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public AuthenticationManager authenticationManager(
            AuthenticationConfiguration configuration) throws Exception {

        return configuration.getAuthenticationManager();
    }

    // Define quais funções exigem autenticação e permissões dos perfis
    @Bean
    public SecurityFilterChain securityFilterChain(
            HttpSecurity http,
            JwtAuthentication jwtAuthentication,
            ObjectMapper objectMapper) throws Exception {

        http
            .csrf(csrf -> csrf.disable())

            .formLogin(form -> form.disable())
            .httpBasic(basic -> basic.disable())

            .sessionManagement(session -> session
                .sessionCreationPolicy(SessionCreationPolicy.STATELESS))

            .authorizeHttpRequests(auth -> auth
                .requestMatchers(
                        "/api/auth/**",
                        "/v3/api-docs/**",
                        "/swagger-ui/**",
                        "/swagger-ui.html"
                    ).permitAll()

                .requestMatchers(
                    HttpMethod.POST,
                    "/api/pedidos",
                    "/api/pedidos/**")
                .hasAnyRole("CLIENTE", "ADMIN", "GERENTE")
                
                .requestMatchers(HttpMethod.PATCH, "/api/pedidos/**")
                .hasAnyRole("COZINHA", "ADMIN", "GERENTE")

                .requestMatchers(
                    HttpMethod.GET,
                    "/api/pedidos",
                    "/api/pedidos/**")
                .hasAnyRole("CLIENTE", "ADMIN", "GERENTE")

                .anyRequest().authenticated()
            )
            
            .exceptionHandling(ex -> ex

            	    .authenticationEntryPoint((request, response, authException) -> {

            	        ApiError erro = new ApiError(
            	                401,
            	                "Usuário não autenticado.",
            	                request.getRequestURI()
            	        );

            	        response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            	        response.setContentType("application/json");
            	        response.setCharacterEncoding("UTF-8");

            	        objectMapper.writeValue(response.getWriter(), erro);
            	    })

            	    .accessDeniedHandler((request, response, accessDeniedException) -> {

            	        ApiError erro = new ApiError(
            	                403,
            	                "Você não tem permissão para acessar este recurso.",
            	                request.getRequestURI()
            	        );

            	        response.setStatus(HttpServletResponse.SC_FORBIDDEN);
            	        response.setContentType("application/json");
            	        response.setCharacterEncoding("UTF-8");

            	        objectMapper.writeValue(response.getWriter(), erro);
            	    })
            	)
            	.addFilterBefore(jwtAuthentication, UsernamePasswordAuthenticationFilter.class);
        

        return http.build();
    }

    // Evita que o filtro JWT seja tratado de forma independente fora do container
    @Bean
    public FilterRegistrationBean<JwtAuthentication>
            jwtFilterRegistration(JwtAuthentication filter) {

        FilterRegistrationBean<JwtAuthentication> registration =
                new FilterRegistrationBean<>(filter);

        registration.setEnabled(false);

        return registration;
    }
}
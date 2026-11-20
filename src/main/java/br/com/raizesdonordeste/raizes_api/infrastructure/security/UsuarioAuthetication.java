package br.com.raizesdonordeste.raizes_api.infrastructure.security;

import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import br.com.raizesdonordeste.raizes_api.domain.entity.Usuario;
import br.com.raizesdonordeste.raizes_api.infrastructure.persistence.UsuarioRepository;

	// Busca dados do usuário para autenticação
@Service
public class UsuarioAuthetication implements UserDetailsService {

    private final UsuarioRepository usuarioRepository;

    public UsuarioAuthetication(UsuarioRepository usuarioRepository) {
        this.usuarioRepository = usuarioRepository;
    }

    @Override
    public UserDetails loadUserByUsername(String email)
            throws UsernameNotFoundException {

        Usuario usuario = usuarioRepository.findByEmail(email)
                .orElseThrow(() ->
                        new UsernameNotFoundException(
                                "Usuário não encontrado."));

        return User.withUsername(usuario.getEmail())
                .password(usuario.getSenhaHash())
                .roles(usuario.getPerfil())
                .disabled(!"ATIVO".equalsIgnoreCase(usuario.getStatus()))
                .build();
    }
}
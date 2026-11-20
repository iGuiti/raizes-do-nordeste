package br.com.raizesdonordeste.raizes_api.application.service;

import java.util.Locale;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import br.com.raizesdonordeste.raizes_api.application.request.CadastroUsuarioRequest;
import br.com.raizesdonordeste.raizes_api.domain.entity.Cliente;
import br.com.raizesdonordeste.raizes_api.domain.entity.Usuario;
import br.com.raizesdonordeste.raizes_api.infrastructure.persistence.ClienteRepository;
import br.com.raizesdonordeste.raizes_api.infrastructure.persistence.UsuarioRepository;

	// Realiza o cadastro de usuários e clientes
@Service
public class CadastroUsuarioService {

    private final UsuarioRepository usuarioRepository;
    private final ClienteRepository clienteRepository;
    private final PasswordEncoder passwordEncoder;

    public CadastroUsuarioService(
            UsuarioRepository usuarioRepository,
            ClienteRepository clienteRepository,
            PasswordEncoder passwordEncoder) {

        this.usuarioRepository = usuarioRepository;
        this.clienteRepository = clienteRepository;
        this.passwordEncoder = passwordEncoder;
    }

    
    @Transactional
    public Integer cadastrar(CadastroUsuarioRequest request) {

        String email = request.getEmail()
                .trim()
                .toLowerCase(Locale.ROOT);

        if (usuarioRepository.findByEmail(email).isPresent()) {
            throw new IllegalArgumentException(
                    "Já existe um usuário cadastrado com esse e-mail.");
        }

        Usuario usuario = new Usuario();
        usuario.setEmail(email);
        usuario.setSenhaHash(passwordEncoder.encode(request.getSenha()));
        usuario.setPerfil("CLIENTE");
        usuario.setStatus("ATIVO");

        Usuario usuarioSalvo = usuarioRepository.save(usuario);

        Cliente cliente = new Cliente();
        cliente.setNome(request.getNome());
        cliente.setTelefone(request.getTelefone());
        cliente.setUsuario(usuarioSalvo);
        cliente.setStatus("ATIVO");

        Cliente clienteSalvo = clienteRepository.save(cliente);

        return clienteSalvo.getIdCliente();
    }
}
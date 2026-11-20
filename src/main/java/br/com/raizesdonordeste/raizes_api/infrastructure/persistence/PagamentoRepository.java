package br.com.raizesdonordeste.raizes_api.infrastructure.persistence;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import br.com.raizesdonordeste.raizes_api.domain.entity.Pagamento;

public interface PagamentoRepository
        extends JpaRepository<Pagamento, Integer> {

    List<Pagamento> findByPedido_IdPedido(Integer idPedido);

}
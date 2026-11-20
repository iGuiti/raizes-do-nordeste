package br.com.raizesdonordeste.raizes_api.infrastructure.persistence;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import br.com.raizesdonordeste.raizes_api.domain.entity.ItemPedido;

public interface ItemPedidoRepository
        extends JpaRepository<ItemPedido, Integer> {

    List<ItemPedido> findByPedido_IdPedido(Integer idPedido);

}
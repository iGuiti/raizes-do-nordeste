package br.com.raizesdonordeste.raizes_api.infrastructure.persistence;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import br.com.raizesdonordeste.raizes_api.domain.entity.Pedido;
import br.com.raizesdonordeste.raizes_api.domain.enums.CanalPedido;

public interface PedidoRepository extends JpaRepository<Pedido, Integer> {

    List<Pedido> findByCanalPedido(CanalPedido canalPedido);
    
    Page<Pedido> findByCanalPedido(
            CanalPedido canalPedido,
            Pageable pageable
    );

    Page<Pedido> findByCanalPedidoAndStatus(
            CanalPedido canalPedido,
            String status,
            Pageable pageable
    );

}
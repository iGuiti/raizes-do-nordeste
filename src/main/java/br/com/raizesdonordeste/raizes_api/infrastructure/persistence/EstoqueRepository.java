package br.com.raizesdonordeste.raizes_api.infrastructure.persistence;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import br.com.raizesdonordeste.raizes_api.domain.entity.Estoque;

public interface EstoqueRepository extends JpaRepository<Estoque, Integer> {

    Optional<Estoque> findByUnidade_IdUnidadeAndProduto_IdProduto(
            Integer idUnidade, Integer idProduto);

}
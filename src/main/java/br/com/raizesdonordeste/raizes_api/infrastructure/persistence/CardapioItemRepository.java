package br.com.raizesdonordeste.raizes_api.infrastructure.persistence;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import br.com.raizesdonordeste.raizes_api.domain.entity.CardapioItem;

public interface CardapioItemRepository
        extends JpaRepository<CardapioItem, Integer> {

    Optional<CardapioItem> findByUnidade_IdUnidadeAndProduto_IdProduto(
            Integer idUnidade, Integer idProduto);

}
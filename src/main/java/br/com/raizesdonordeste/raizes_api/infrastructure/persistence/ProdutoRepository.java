package br.com.raizesdonordeste.raizes_api.infrastructure.persistence;

import org.springframework.data.jpa.repository.JpaRepository;

import br.com.raizesdonordeste.raizes_api.domain.entity.Produto;

public interface ProdutoRepository extends JpaRepository<Produto, Integer> {

}
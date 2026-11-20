package br.com.raizesdonordeste.raizes_api.infrastructure.persistence;

import org.springframework.data.jpa.repository.JpaRepository;

import br.com.raizesdonordeste.raizes_api.domain.entity.Unidade;

public interface UnidadeRepository extends JpaRepository<Unidade, Integer> {

}
package br.com.raizesdonordeste.raizes_api.infrastructure.payment;

import java.time.LocalDateTime;
import java.util.UUID;

import org.springframework.stereotype.Service;

import br.com.raizesdonordeste.raizes_api.domain.entity.Pagamento;

	// Simula uma transação de pagamento externo

@Service
public class PagamentoMock{

    // Retorna aprovado ou recusado para a simulação
	
    public Pagamento processarPagamento(
            Pagamento pagamento, boolean simularFalha) {

        pagamento.setDataPagamento(LocalDateTime.now());
        pagamento.setIdTransacao("MOCK-" + UUID.randomUUID());

        if (simularFalha) {
            pagamento.setStatus("RECUSADO");
        } else {
            pagamento.setStatus("APROVADO");
        }

        return pagamento;
    }
}
package br.com.raizesdonordeste.raizes_api.application.service;

import java.math.BigDecimal;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;

import java.util.List;
import java.util.Locale;

import br.com.raizesdonordeste.raizes_api.domain.enums.CanalPedido;
import br.com.raizesdonordeste.raizes_api.application.response.PedidoResponse;
import br.com.raizesdonordeste.raizes_api.application.request.CriarPedidoRequest;
import br.com.raizesdonordeste.raizes_api.application.request.ItemPedidoRequest;
import br.com.raizesdonordeste.raizes_api.domain.entity.CardapioItem;
import br.com.raizesdonordeste.raizes_api.domain.entity.Cliente;
import br.com.raizesdonordeste.raizes_api.domain.entity.ItemPedido;
import br.com.raizesdonordeste.raizes_api.domain.entity.Pagamento;
import br.com.raizesdonordeste.raizes_api.domain.entity.Pedido;
import br.com.raizesdonordeste.raizes_api.domain.entity.Produto;
import br.com.raizesdonordeste.raizes_api.domain.entity.Unidade;
import br.com.raizesdonordeste.raizes_api.infrastructure.payment.PagamentoMock;
import br.com.raizesdonordeste.raizes_api.infrastructure.persistence.CardapioItemRepository;
import br.com.raizesdonordeste.raizes_api.infrastructure.persistence.ClienteRepository;
import br.com.raizesdonordeste.raizes_api.infrastructure.persistence.PagamentoRepository;
import br.com.raizesdonordeste.raizes_api.infrastructure.persistence.PedidoRepository;
import br.com.raizesdonordeste.raizes_api.infrastructure.persistence.ProdutoRepository;
import br.com.raizesdonordeste.raizes_api.infrastructure.persistence.UnidadeRepository;

@Service
public class PedidoService {

    private final ClienteRepository clienteRepository;
    private final UnidadeRepository unidadeRepository;
    private final ProdutoRepository produtoRepository;
    private final CardapioItemRepository cardapioItemRepository;
    private final PedidoRepository pedidoRepository;
    private final PagamentoRepository pagamentoRepository;
    private final PagamentoMock pagamentoMock;

    public PedidoService(
            ClienteRepository clienteRepository,
            UnidadeRepository unidadeRepository,
            ProdutoRepository produtoRepository,
            CardapioItemRepository cardapioItemRepository,
            PedidoRepository pedidoRepository,
            PagamentoRepository pagamentoRepository,
            PagamentoMock pagamentoMock) {

        this.clienteRepository = clienteRepository;
        this.unidadeRepository = unidadeRepository;
        this.produtoRepository = produtoRepository;
        this.cardapioItemRepository = cardapioItemRepository;
        this.pedidoRepository = pedidoRepository;
        this.pagamentoRepository = pagamentoRepository;
        this.pagamentoMock = pagamentoMock;    
        
    }
    

    // Consulta pedidos por canal, status e paginação
    @Transactional(readOnly = true)
    public Page<PedidoResponse> buscarPorCanal(
            CanalPedido canalPedido,
            String status,
            int page,
            int limit) {

        if (page < 1) {
            throw new IllegalArgumentException(
                    "A página deve ser maior que zero.");
        }

        if (limit < 1 || limit > 100) {
            throw new IllegalArgumentException(
                    "O limite deve estar entre 1 e 100.");
        }

        Pageable paginacao = PageRequest.of(
                page - 1,
                limit,
                Sort.by(Sort.Direction.DESC, "dataCriacao"));

        Page<Pedido> pedidos;

        if (status == null || status.isBlank()) {

            pedidos = pedidoRepository.findByCanalPedido(
                    canalPedido, paginacao);

        } else {

            pedidos = pedidoRepository.findByCanalPedidoAndStatus(
                    canalPedido,
                    status.trim().toUpperCase(Locale.ROOT),
                    paginacao);
        }

        return pedidos.map(pedido -> {

            List<Pagamento> pagamentos =
                    pagamentoRepository.findByPedido_IdPedido(
                            pedido.getIdPedido());

            if (pagamentos.isEmpty()) {
                throw new IllegalStateException(
                        "Pagamento não encontrado para o pedido.");
            }

            return new PedidoResponse(pedido, pagamentos.get(0));
        });
    }


    // Busca um cliente e verifica status
    
    public Cliente buscarClienteAtivo(Integer idCliente) {

        Cliente cliente = clienteRepository.findById(idCliente)
                .orElseThrow(() ->
                        new IllegalArgumentException(
                                "Cliente não encontrado."));

        if (!"ATIVO".equalsIgnoreCase(cliente.getStatus())) {
            throw new IllegalArgumentException("Cliente inativo.");
        }

        return cliente;
    }

    // Busca uma unidade e verifica status
    public Unidade buscarUnidadeAtiva(Integer idUnidade) {

        Unidade unidade = unidadeRepository.findById(idUnidade)
                .orElseThrow(() ->
                        new ResponseStatusException(
                                HttpStatus.NOT_FOUND,
                                "Unidade não encontrada."));

        if (!"ATIVO".equalsIgnoreCase(unidade.getStatus())) {
            throw new IllegalArgumentException("Unidade inativa.");
        }

        return unidade;
    }

    // Busca um produto e verifica status
    
    public Produto buscarProdutoAtivo(Integer idProduto) {

        Produto produto = produtoRepository.findById(idProduto)
                .orElseThrow(() ->
                        new ResponseStatusException(
                                HttpStatus.NOT_FOUND,
                                "Produto não encontrado."));

        if (!"ATIVO".equalsIgnoreCase(produto.getStatus())) {
            throw new IllegalArgumentException("Produto inativo.");
        }

        return produto;
    }

    // Verifica se o produto está ativo no cardapio da unidade
    
    public CardapioItem validarProdutoNoCardapio(
            Integer idUnidade, Integer idProduto) {

        CardapioItem item = cardapioItemRepository
                .findByUnidade_IdUnidadeAndProduto_IdProduto(
                        idUnidade, idProduto)
                .orElseThrow(() ->
                        new ResponseStatusException(
                                HttpStatus.NOT_FOUND,
                                "Produto não encontrado no cardápio da unidade."));

        if (!"ATIVO".equalsIgnoreCase(item.getStatus())) {
            throw new IllegalArgumentException(
                    "Produto inativo no cardápio da unidade.");
        }

        buscarProdutoAtivo(idProduto);

        return item;
    }

    // Cria pedido, calcula o valor e registra o pagamento para simulação
    
    @Transactional
    public PedidoResponse criarPedido(CriarPedidoRequest request) {

        Cliente cliente = buscarClienteAtivo(request.getIdCliente());

        Unidade unidade = buscarUnidadeAtiva(request.getIdUnidade());

        Pedido pedido = new Pedido();
        pedido.setCliente(cliente);
        pedido.setUnidade(unidade);
        pedido.setCanalPedido(request.getCanalPedido());
        pedido.setStatus("AGUARDANDO_PAGAMENTO");

        BigDecimal valorTotal = BigDecimal.ZERO;

        // Valida os produtos e monta os itens do pedido
        
        for (ItemPedidoRequest itemRequest : request.getItens()) {

            CardapioItem cardapioItem = validarProdutoNoCardapio(
                    unidade.getIdUnidade(),
                    itemRequest.getIdProduto());

            Produto produto = cardapioItem.getProduto();

            ItemPedido item = new ItemPedido();
            item.setPedido(pedido);
            item.setProduto(produto);
            item.setQuantidade(itemRequest.getQuantidade());
            item.setPrecoUnitario(produto.getPreco());

            pedido.getItens().add(item);

            BigDecimal subtotal = produto.getPreco().multiply(
                    BigDecimal.valueOf(itemRequest.getQuantidade()));

            valorTotal = valorTotal.add(subtotal);
        }

        pedido.setValorTotal(valorTotal);

        // Salva o pedido e seus itens relacionados
        
        Pedido pedidoSalvo = pedidoRepository.save(pedido);

        // Prepara e processa o pagamento simulado
        
        Pagamento pagamento = new Pagamento();
        pagamento.setPedido(pedidoSalvo);
        pagamento.setFormaPagamento(request.getFormaPagamento());
        pagamento.setValor(valorTotal);

        pagamentoMock.processarPagamento(
                pagamento, request.isSimularFalha());

        pagamentoRepository.save(pagamento);

        // Atualiza o pedido de acordo com o resultado do pagamento
        
        if ("APROVADO".equals(pagamento.getStatus())) {
            pedidoSalvo.setStatus("PAGO");
        } else {
            pedidoSalvo.setStatus("PAGAMENTO_RECUSADO");
        }

        pedidoSalvo = pedidoRepository.save(pedidoSalvo);

        return new PedidoResponse(pedidoSalvo, pagamento);
        
    }
    
 // Atualiza o status do pedido na cozinha
    @Transactional
    public PedidoResponse atualizarStatus(
            Integer idPedido, String novoStatus) {

        // Busca o pedido pelo ID
        Pedido pedido = pedidoRepository.findById(idPedido)
                .orElseThrow(() ->
                        new ResponseStatusException(
                                HttpStatus.NOT_FOUND,
                                "Pedido não encontrado."
                        ));

        String statusAtual = pedido.getStatus();

        // Padroniza o status recebido
        novoStatus = novoStatus.trim().toUpperCase();

        // Verifica se a transição de status é permitida
        boolean transicaoValida = switch (statusAtual) {

            case "PAGO" ->
                    "EM_PREPARACAO".equals(novoStatus)
                    || "CANCELADO".equals(novoStatus);

            case "EM_PREPARACAO" ->
                    "PRONTO".equals(novoStatus)
                    || "CANCELADO".equals(novoStatus);

            case "PRONTO" ->
                    "ENTREGUE".equals(novoStatus)
                    || "CANCELADO".equals(novoStatus);

            default -> false;
        };

        if (!transicaoValida) {
            throw new IllegalArgumentException(
                    "Não é permitida a alteração de "
                    + statusAtual + " para " + novoStatus + "."
            );
        }

        // Atualiza e salva o pedido
        pedido.setStatus(novoStatus);

        Pedido pedidoSalvo = pedidoRepository.save(pedido);

        // Recupera o pagamento relacionado ao pedido
        List<Pagamento> pagamentos =
                pagamentoRepository.findByPedido_IdPedido(
                        pedidoSalvo.getIdPedido());

        if (pagamentos.isEmpty()) {
            throw new IllegalStateException(
                    "Pagamento não encontrado para o pedido.");
        }

        return new PedidoResponse(pedidoSalvo, pagamentos.get(0));
    }
    
}
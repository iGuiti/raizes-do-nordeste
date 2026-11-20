
USE raizes_do_nordeste;

-- Cadastrar unidade
INSERT INTO unidade (nome, endereco, telefone, status)
VALUES (
    'Unidade Matriz',
    'Rua Principal, 1 - Centro',
    '1122233333',
    'ATIVO'
);

SET @id_unidade = LAST_INSERT_ID();

-- Cadastrar produto
INSERT INTO produto (nome, descricao, preco, categoria, status)
VALUES (
    'Cuscuz Cremoso',
    'Cuscuz tradicional com acompanhamentos',
    18.90,
    'Prato',
    'ATIVO'
);

SET @id_produto = LAST_INSERT_ID();

-- Vincular produto ao cardapio da unidade
INSERT INTO cardapio_item (
    fk_id_unidade,
    fk_id_produto,
    status
)
VALUES (
    @id_unidade,
    @id_produto,
    'ATIVO'
);

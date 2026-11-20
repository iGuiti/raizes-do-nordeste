CREATE TABLE usuario (
    id_usuario INT NOT NULL AUTO_INCREMENT,
    email VARCHAR(150) NOT NULL,
    senha_hash VARCHAR(255) NOT NULL,
    perfil VARCHAR(30) NOT NULL,
    status VARCHAR(20) NOT NULL DEFAULT 'ATIVO',
    data_cadastro DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    PRIMARY KEY (id_usuario),
    UNIQUE KEY uk_usuario_email (email)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

CREATE TABLE unidade (
    id_unidade INT NOT NULL AUTO_INCREMENT,
    nome VARCHAR(100) NOT NULL,
    endereco VARCHAR(255) NOT NULL,
    telefone VARCHAR(20),
    status VARCHAR(20) NOT NULL DEFAULT 'ATIVO',
    PRIMARY KEY (id_unidade)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

CREATE TABLE produto (
    id_produto INT NOT NULL AUTO_INCREMENT,
    nome VARCHAR(100) NOT NULL,
    descricao VARCHAR(255),
    preco DECIMAL(10,2) NOT NULL,
    categoria VARCHAR(60),
    status VARCHAR(20) NOT NULL DEFAULT 'ATIVO',
    PRIMARY KEY (id_produto),
    CONSTRAINT chk_produto_preco CHECK (preco >= 0)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

CREATE TABLE cliente (
    id_cliente INT NOT NULL AUTO_INCREMENT,
    nome VARCHAR(120) NOT NULL,
    fk_id_usuario INT NOT NULL,
    telefone VARCHAR(20),
    status VARCHAR(20) NOT NULL DEFAULT 'ATIVO',
    PRIMARY KEY (id_cliente),
    UNIQUE KEY uk_cliente_usuario (fk_id_usuario),
    CONSTRAINT fk_cliente_usuario
        FOREIGN KEY (fk_id_usuario)
        REFERENCES usuario (id_usuario)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

CREATE TABLE cardapio_item (
    id_cardapio_item INT NOT NULL AUTO_INCREMENT,
    fk_id_unidade INT NOT NULL,
    fk_id_produto INT NOT NULL,
    status VARCHAR(20) NOT NULL DEFAULT 'ATIVO',
    PRIMARY KEY (id_cardapio_item),
    UNIQUE KEY uk_cardapio_unidade_produto
        (fk_id_unidade, fk_id_produto),
    CONSTRAINT fk_cardapio_unidade
        FOREIGN KEY (fk_id_unidade)
        REFERENCES unidade (id_unidade),
    CONSTRAINT fk_cardapio_produto
        FOREIGN KEY (fk_id_produto)
        REFERENCES produto (id_produto)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

CREATE TABLE estoque (
    id_estoque INT NOT NULL AUTO_INCREMENT,
    fk_id_unidade INT NOT NULL,
    fk_id_produto INT NOT NULL,
    quantidade INT NOT NULL DEFAULT 0,
    PRIMARY KEY (id_estoque),
    UNIQUE KEY uk_estoque_unidade_produto
        (fk_id_unidade, fk_id_produto),
    CONSTRAINT chk_estoque_quantidade CHECK (quantidade >= 0),
    CONSTRAINT fk_estoque_unidade
        FOREIGN KEY (fk_id_unidade)
        REFERENCES unidade (id_unidade),
    CONSTRAINT fk_estoque_produto
        FOREIGN KEY (fk_id_produto)
        REFERENCES produto (id_produto)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

CREATE TABLE pedido (
    id_pedido INT NOT NULL AUTO_INCREMENT,
    fk_id_cliente INT NOT NULL,
    fk_id_unidade INT NOT NULL,
    canal_pedido ENUM('APP', 'TOTEM', 'BALCAO', 'PICKUP', 'WEB') NOT NULL,
    status VARCHAR(30) NOT NULL DEFAULT 'AGUARDANDO_PAGAMENTO',
    valor_total DECIMAL(10,2) NOT NULL,
    data_criacao DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    PRIMARY KEY (id_pedido),
    KEY idx_pedido_cliente (fk_id_cliente),
    KEY idx_pedido_unidade (fk_id_unidade),
    KEY idx_pedido_canal_status (canal_pedido, status),
    CONSTRAINT chk_pedido_valor CHECK (valor_total >= 0),
    CONSTRAINT fk_pedido_cliente
        FOREIGN KEY (fk_id_cliente)
        REFERENCES cliente (id_cliente),
    CONSTRAINT fk_pedido_unidade
        FOREIGN KEY (fk_id_unidade)
        REFERENCES unidade (id_unidade)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

CREATE TABLE item_pedido (
    id_item_pedido INT NOT NULL AUTO_INCREMENT,
    fk_id_pedido INT NOT NULL,
    fk_id_produto INT NOT NULL,
    quantidade INT NOT NULL,
    preco_unitario DECIMAL(10,2) NOT NULL,
    PRIMARY KEY (id_item_pedido),
    KEY idx_item_pedido_pedido (fk_id_pedido),
    CONSTRAINT chk_item_quantidade CHECK (quantidade > 0),
    CONSTRAINT chk_item_preco CHECK (preco_unitario >= 0),
    CONSTRAINT fk_item_pedido_pedido
        FOREIGN KEY (fk_id_pedido)
        REFERENCES pedido (id_pedido),
    CONSTRAINT fk_item_pedido_produto
        FOREIGN KEY (fk_id_produto)
        REFERENCES produto (id_produto)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

CREATE TABLE pagamento (
    id_pagamento INT NOT NULL AUTO_INCREMENT,
    fk_id_pedido INT NOT NULL,
    status VARCHAR(20) NOT NULL,
    forma_pagamento VARCHAR(30) NOT NULL,
    valor DECIMAL(10,2) NOT NULL,
    data_pagamento DATETIME,
    id_transacao VARCHAR(100),
    PRIMARY KEY (id_pagamento),
    KEY idx_pagamento_pedido (fk_id_pedido),
    CONSTRAINT chk_pagamento_valor CHECK (valor >= 0),
    CONSTRAINT fk_pagamento_pedido
        FOREIGN KEY (fk_id_pedido)
        REFERENCES pedido (id_pedido)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

CREATE TABLE fidelidade (
    id_fidelidade INT NOT NULL AUTO_INCREMENT,
    fk_id_cliente INT NOT NULL,
    saldo_pontos INT NOT NULL DEFAULT 0,
    status VARCHAR(20) NOT NULL DEFAULT 'ATIVO',
    PRIMARY KEY (id_fidelidade),
    UNIQUE KEY uk_fidelidade_cliente (fk_id_cliente),
    CONSTRAINT chk_fidelidade_pontos CHECK (saldo_pontos >= 0),
    CONSTRAINT fk_fidelidade_cliente
        FOREIGN KEY (fk_id_cliente)
        REFERENCES cliente (id_cliente)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

CREATE TABLE movimentacao_pontos (
    id_movimentacao INT NOT NULL AUTO_INCREMENT,
    fk_id_fidelidade INT NOT NULL,
    tipo VARCHAR(20) NOT NULL,
    quantidade INT NOT NULL,
    data_movimentacao DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    PRIMARY KEY (id_movimentacao),
    CONSTRAINT chk_movimentacao_pontos CHECK (quantidade > 0),
    CONSTRAINT fk_movimentacao_pontos_fidelidade
        FOREIGN KEY (fk_id_fidelidade)
        REFERENCES fidelidade (id_fidelidade)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

CREATE TABLE movimentacao_estoque (
    id_movimentacao INT NOT NULL AUTO_INCREMENT,
    fk_id_estoque INT NOT NULL,
    fk_id_usuario INT NOT NULL,
    quantidade INT NOT NULL,
    tipo VARCHAR(20) NOT NULL,
    data_movimentacao DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    PRIMARY KEY (id_movimentacao),
    CONSTRAINT chk_movimentacao_estoque_quantidade
        CHECK (quantidade > 0),
    CONSTRAINT fk_movimentacao_estoque_estoque
        FOREIGN KEY (fk_id_estoque)
        REFERENCES estoque (id_estoque),
    CONSTRAINT fk_movimentacao_estoque_usuario
        FOREIGN KEY (fk_id_usuario)
        REFERENCES usuario (id_usuario)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

CREATE TABLE auditoria (
    id_auditoria INT NOT NULL AUTO_INCREMENT,
    fk_id_usuario INT NOT NULL,
    fk_id_pedido INT,
    acao VARCHAR(100) NOT NULL,
    descricao TEXT,
    data_hora DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    PRIMARY KEY (id_auditoria),
    KEY idx_auditoria_usuario (fk_id_usuario),
    KEY idx_auditoria_pedido (fk_id_pedido),
    CONSTRAINT fk_auditoria_usuario
        FOREIGN KEY (fk_id_usuario)
        REFERENCES usuario (id_usuario),
    CONSTRAINT fk_auditoria_pedido
        FOREIGN KEY (fk_id_pedido)
        REFERENCES pedido (id_pedido)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;
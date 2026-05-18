CREATE TABLE IF NOT EXISTS pedidos (
    id             BIGSERIAL PRIMARY KEY,
    fecha_pedido   DATE NOT NULL,
    estado         VARCHAR(50) NOT NULL,
    proveedor_id   BIGINT NOT NULL,
    cliente_id     BIGINT NOT NULL,
    items          TEXT NOT NULL
);
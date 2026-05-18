CREATE TABLE IF NOT EXISTS inventario (
                                          id          BIGSERIAL PRIMARY KEY,
                                          stock_actual INT NOT NULL,
                                          stock_minimo INT NOT NULL,
                                          producto_id  BIGINT NOT NULL
);
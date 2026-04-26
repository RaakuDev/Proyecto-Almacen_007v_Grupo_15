CREATE TABLE IF NOT EXISTS productos (
                                         id           BIGSERIAL PRIMARY KEY,
                                         nombre       VARCHAR(255) NOT NULL UNIQUE,
    precio       BIGINT NOT NULL,
    stock        INT NOT NULL,
    categoria_id BIGINT NOT NULL
    );
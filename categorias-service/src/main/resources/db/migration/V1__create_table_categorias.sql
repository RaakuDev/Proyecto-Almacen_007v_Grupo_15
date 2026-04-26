CREATE TABLE IF NOT EXISTS categorias (
                                          id          BIGSERIAL PRIMARY KEY,
                                          nombre      VARCHAR(255) NOT NULL UNIQUE,
    descripcion VARCHAR(255) NOT NULL
    );
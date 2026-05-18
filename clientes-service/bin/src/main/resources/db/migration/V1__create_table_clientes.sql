CREATE TABLE clientes (
                          id BIGSERIAL PRIMARY KEY,
                          nombre VARCHAR(255) NOT NULL,
                          rut VARCHAR(50) NOT NULL UNIQUE,
                          direccion VARCHAR(255) NOT NULL,
                          telefono VARCHAR(50) NOT NULL,
                          email VARCHAR(255) NOT NULL
);
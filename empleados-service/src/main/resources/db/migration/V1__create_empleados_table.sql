CREATE TABLE empleados (
    id BIGSERIAL PRIMARY KEY,
    nombre VARCHAR(255) NOT NULL,
    rut VARCHAR(50) NOT NULL UNIQUE,
    cargo VARCHAR(255) NOT NULL,
    turno VARCHAR(255) NOT NULL,
    telefono VARCHAR(50) NOT NULL,
    email VARCHAR(255) NOT NULL,
    fecha_inicio_contrato DATE NOT NULL,
    activo BOOLEAN NOT NULL,
    usuario_id BIGINT
);
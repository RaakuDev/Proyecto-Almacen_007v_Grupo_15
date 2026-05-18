CREATE TABLE detalle_ventas (
                                id_detalle_venta BIGSERIAL PRIMARY KEY,
                                venta_id BIGINT NOT NULL,
                                producto_id BIGINT NOT NULL,
                                cantidad INTEGER NOT NULL,
                                precio_unitario NUMERIC(10,2) NOT NULL,
                                sub_total NUMERIC(10,2) NOT NULL
);
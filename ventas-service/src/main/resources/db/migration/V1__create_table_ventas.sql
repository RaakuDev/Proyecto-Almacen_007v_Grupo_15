CREATE TABLE ventas (
                        id_venta BIGSERIAL PRIMARY KEY,
                        fecha_venta TIMESTAMP NOT NULL,

                        sub_total NUMERIC(12,2) NOT NULL,
                        descuento_total NUMERIC(12,2) NOT NULL,
                        impuesto_total NUMERIC(12,2) NOT NULL,
                        total NUMERIC(12,2) NOT NULL,

                        metodo_pago VARCHAR(30) NOT NULL,
                        tipo_comprobante VARCHAR(20) NOT NULL,

                        monto_pagado NUMERIC(12,2) NOT NULL,
                        vuelto NUMERIC(12,2) NOT NULL,

                        estado_venta VARCHAR(20) NOT NULL,

                        cliente_id BIGINT NOT NULL,
                        empleado_id BIGINT NOT NULL,

                        numero_comprobante VARCHAR(50) NOT NULL UNIQUE,
                        observaciones VARCHAR(500)
);
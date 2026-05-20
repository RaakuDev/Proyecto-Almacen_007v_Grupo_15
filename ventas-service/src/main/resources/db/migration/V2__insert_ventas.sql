INSERT INTO ventas (
    fecha_venta,
    sub_total,
    descuento_total,
    impuesto_total,
    total,
    metodo_pago,
    tipo_comprobante,
    monto_pagado,
    vuelto,
    estado_venta,
    cliente_id,
    empleado_id,
    numero_comprobante,
    observaciones
) VALUES

('2026-05-18 10:15:00', 5000, 0, 950, 5950, 'EFECTIVO', 'BOLETA', 6000, 50, 'COMPLETADA', 1, 1, 'BOL-0001', 'Venta normal'),

('2026-05-18 11:00:00', 12000, 1000, 2090, 13090, 'T_DEBITO', 'FACTURA', 13090, 0, 'COMPLETADA', 2, 1, 'FAC-0001', 'Cliente empresa'),

('2026-05-18 11:45:00', 8000, 0, 1520, 9520, 'TRANSFERENCIA', 'BOLETA', 10000, 480, 'COMPLETADA', 3, 2, 'BOL-0002', 'Pago transferencia'),

('2026-05-18 12:30:00', 3500, 0, 665, 4165, 'EFECTIVO', 'BOLETA', 5000, 835, 'COMPLETADA', 1, 2, 'BOL-0003', 'Compra rápida'),

('2026-05-18 13:15:00', 15000, 2000, 2470, 15470, 'T_CREDITO', 'FACTURA', 15470, 0, 'COMPLETADA', 4, 1, 'FAC-0002', 'Descuento aplicado'),

('2026-05-18 14:00:00', 4200, 0, 798, 4998, 'EFECTIVO', 'BOLETA', 5000, 2, 'COMPLETADA', 2, 2, 'BOL-0004', 'Venta mostrador'),

('2026-05-18 15:10:00', 9800, 500, 1767, 11067, 'TRANSFERENCIA', 'BOLETA', 12000, 933, 'COMPLETADA', 5, 1, 'BOL-0005', 'Cliente frecuente'),

('2026-05-18 16:20:00', 22000, 0, 4180, 26180, 'T_DEBITO', 'FACTURA', 26180, 0, 'COMPLETADA', 3, 2, 'FAC-0003', 'Compra grande'),

('2026-05-18 17:00:00', 6700, 0, 1273, 7973, 'EFECTIVO', 'BOLETA', 8000, 27, 'COMPLETADA', 4, 1, 'BOL-0006', 'Sin observaciones'),

('2026-05-18 18:10:00', 11000, 1000, 1900, 11900, 'TRANSFERENCIA', 'FACTURA', 12000, 100, 'COMPLETADA', 5, 2, 'FAC-0004', 'Venta final del día');
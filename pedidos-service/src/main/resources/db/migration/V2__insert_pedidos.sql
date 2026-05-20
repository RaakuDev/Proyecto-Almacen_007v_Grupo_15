INSERT INTO pedidos (
    fecha_pedido,
    estado,
    proveedor_id,
    cliente_id,
    items
) VALUES

('2026-05-18', 'PENDIENTE', 1, 1,
'Arroz 1kg x10, Fideos 400g x20'),

('2026-05-18', 'EN_PROCESO', 2, 2,
'Aceite 1L x15, Azúcar 1kg x30'),

('2026-05-18', 'COMPLETADO', 3, 3,
'Leche 1L x25, Yogurt 1L x12'),

('2026-05-18', 'PENDIENTE', 4, 4,
'Queso 500g x10, Papel Higiénico x8'),

('2026-05-18', 'COMPLETADO', 5, 5,
'Detergente 1L x18, Sal 1kg x40'),

('2026-05-19', 'EN_PROCESO', 1, 2,
'Arroz 1kg x5, Aceite 1L x6'),

('2026-05-19', 'PENDIENTE', 2, 3,
'Yogurt 1L x20, Leche 1L x20'),

('2026-05-19', 'COMPLETADO', 3, 4,
'Fideos 400g x50'),

('2026-05-19', 'EN_PROCESO', 4, 5,
'Papel Higiénico x15, Detergente 1L x10'),

('2026-05-19', 'PENDIENTE', 5, 1,
'Queso 500g x8, Yogurt 1L x6');
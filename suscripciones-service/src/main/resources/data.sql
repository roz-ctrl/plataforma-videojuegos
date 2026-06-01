-- Migracion inicial / datos semilla del microservicio SUSCRIPCIONES

INSERT IGNORE INTO planes (id, nombre, precio_mensual, duracion_meses, descripcion, activo) VALUES
(1, 'Pase Mensual', 5990.00,  1,  'Acceso al catalogo por 1 mes',  TRUE),
(2, 'Pase Trimestral', 14990.00, 3, 'Acceso al catalogo por 3 meses', TRUE),
(3, 'Pase Anual', 49990.00, 12, 'Acceso al catalogo por 12 meses con descuento', TRUE);

ALTER TABLE planes AUTO_INCREMENT = 4;

INSERT IGNORE INTO suscripciones (id, usuario_id, plan_id, fecha_inicio, fecha_fin, estado, renovacion_automatica) VALUES
(1, 1, 3, '2026-01-01', '2027-01-01', 'ACTIVA', TRUE);

ALTER TABLE suscripciones AUTO_INCREMENT = 2;

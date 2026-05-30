-- Migracion inicial / datos semilla del microservicio BIBLIOTECA
-- usuario_id y juego_id referencian datos sembrados en usuarios-service y juegos-service.

INSERT IGNORE INTO biblioteca (id, usuario_id, juego_id, titulo_juego, fecha_adquisicion, horas_jugadas, instalado) VALUES
(1, 1, 2, 'The Witcher 3',   CURRENT_TIMESTAMP, 120, TRUE),
(2, 1, 3, 'Hades',           CURRENT_TIMESTAMP, 45,  TRUE),
(3, 2, 2, 'The Witcher 3',   CURRENT_TIMESTAMP, 10,  FALSE);

ALTER TABLE biblioteca AUTO_INCREMENT = 4;

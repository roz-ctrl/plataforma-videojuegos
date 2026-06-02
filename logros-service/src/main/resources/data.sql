-- Migracion inicial / datos semilla del microservicio LOGROS
-- juego_id referencia juegos sembrados en juegos-service.

INSERT IGNORE INTO logros (id, juego_id, nombre, descripcion, puntos) VALUES
(1, 2, 'Carnicero de Grifos', 'Derrota a tu primer grifo en The Witcher 3', 50),
(2, 2, 'Maestro Brujo',        'Alcanza el nivel 50',                       100),
(3, 3, 'Primera Fuga',         'Escapa del inframundo por primera vez en Hades', 80),
(4, 1, 'Bienvenido a City 17', 'Completa el primer capitulo de Half-Life: Alyx', 40);

ALTER TABLE logros AUTO_INCREMENT = 5;

INSERT IGNORE INTO logros_desbloqueados (id, usuario_id, logro_id, fecha_desbloqueo) VALUES
(1, 1, 1, CURRENT_TIMESTAMP);

ALTER TABLE logros_desbloqueados AUTO_INCREMENT = 2;

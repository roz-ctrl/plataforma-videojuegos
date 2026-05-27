-- Migracion inicial / datos semilla del microservicio CATEGORIAS

INSERT IGNORE INTO categorias (id, nombre, descripcion) VALUES
(1, 'RPG',         'Juegos de rol con progresion de personaje e historia profunda'),
(2, 'Shooter',     'Juegos de disparos en primera o tercera persona'),
(3, 'Plataformas', 'Juegos de saltos y desafios de habilidad por niveles'),
(4, 'Aventura',    'Juegos centrados en exploracion y narrativa'),
(5, 'Estrategia',  'Juegos de planificacion y gestion de recursos');

ALTER TABLE categorias AUTO_INCREMENT = 6;

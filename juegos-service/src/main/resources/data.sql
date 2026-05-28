-- Migracion inicial / datos semilla del microservicio JUEGOS
-- desarrolladora_id y categoria_id referencian datos sembrados en sus respectivos servicios.

INSERT IGNORE INTO juegos (id, titulo, descripcion, precio, fecha_lanzamiento, desarrolladora_id, categoria_id, plataforma, requisitos_minimos, requisitos_recomendados, descuento_porcentaje, activo) VALUES
(1, 'Half-Life: Alyx', 'Aventura de realidad virtual ambientada en el universo Half-Life', 29990.00, '2020-03-23', 1, 4, 'PC', '8GB RAM, GTX 1060', '16GB RAM, RTX 2070', 10, TRUE),
(2, 'The Witcher 3',   'RPG de mundo abierto con Geralt de Rivia',                      19990.00, '2015-05-19', 2, 1, 'PC/Consola', '8GB RAM, GTX 660', '16GB RAM, GTX 1070', 50, TRUE),
(3, 'Hades',           'Roguelike de accion sobre escapar del inframundo',             14990.00, '2020-09-17', 4, 1, 'PC/Consola', '4GB RAM, GTX 950', '8GB RAM, GTX 1060', 20, TRUE),
(4, 'Super Mario Bros','Clasico juego de plataformas',                                 24990.00, '1985-09-13', 3, 3, 'Consola', 'Consola Nintendo', 'Consola Nintendo', 0, TRUE);

ALTER TABLE juegos AUTO_INCREMENT = 5;

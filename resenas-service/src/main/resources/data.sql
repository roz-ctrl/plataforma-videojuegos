-- Migracion inicial / datos semilla del microservicio RESENAS
-- usuario_id y juego_id referencian datos de usuarios-service y juegos-service.
-- Coherente con la biblioteca sembrada: el usuario 1 posee los juegos 2 y 3.

INSERT IGNORE INTO resenas (id, usuario_id, juego_id, calificacion, comentario, recomendado, fecha) VALUES
(1, 1, 2, 5, 'Una obra maestra del genero RPG, horas y horas de contenido.', TRUE, CURRENT_TIMESTAMP),
(2, 1, 3, 4, 'Adictivo y rejugable, la banda sonora es excelente.',          TRUE, CURRENT_TIMESTAMP);

ALTER TABLE resenas AUTO_INCREMENT = 3;

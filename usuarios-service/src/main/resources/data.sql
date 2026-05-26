-- Migracion inicial / datos semilla del microservicio USUARIOS
-- Se ejecuta automaticamente al iniciar (spring.sql.init.mode=always).
-- MERGE evita duplicados si la BD en archivo ya contiene los registros.

INSERT IGNORE INTO usuarios (id, nombre_usuario, email, password, pais, saldo, fecha_registro, activo) VALUES
(1, 'gabe_player',   'gabe@steamclone.com',  'pass1234', 'Chile',         50000.00, CURRENT_TIMESTAMP, TRUE),
(2, 'shadow_gamer',  'shadow@steamclone.com','pass1234', 'Argentina',     12000.00, CURRENT_TIMESTAMP, TRUE),
(3, 'pixel_queen',   'pixel@steamclone.com', 'pass1234', 'Mexico',         8000.00, CURRENT_TIMESTAMP, TRUE),
(4, 'noob_master',   'noob@steamclone.com',  'pass1234', 'Colombia',          0.00, CURRENT_TIMESTAMP, TRUE);

ALTER TABLE usuarios AUTO_INCREMENT = 5;

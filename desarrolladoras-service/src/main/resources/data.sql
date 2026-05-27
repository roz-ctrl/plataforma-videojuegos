-- Migracion inicial / datos semilla del microservicio DESARROLLADORAS

INSERT IGNORE INTO desarrolladoras (id, nombre, pais_origen, sitio_web, fecha_fundacion, activa) VALUES
(1, 'Valve',          'Estados Unidos', 'https://www.valvesoftware.com', '1996-08-24', TRUE),
(2, 'CD Projekt Red', 'Polonia',        'https://www.cdprojektred.com',  '2002-02-01', TRUE),
(3, 'Nintendo',       'Japon',          'https://www.nintendo.com',      '1889-09-23', TRUE),
(4, 'Supergiant Games','Estados Unidos','https://www.supergiantgames.com','2009-01-01', TRUE);

ALTER TABLE desarrolladoras AUTO_INCREMENT = 5;

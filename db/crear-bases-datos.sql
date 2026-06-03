-- ============================================================
--  GameVault - Creacion de las bases de datos (MySQL / Laragon)
-- ============================================================
-- Ejecutar en HeidiSQL (o en la consola mysql de Laragon).
-- NOTA: en realidad NO es obligatorio ejecutar esto, porque cada
-- microservicio usa createDatabaseIfNotExist=true y crea su base al
-- arrancar. Este script sirve si prefieres crearlas manualmente.

CREATE DATABASE IF NOT EXISTS usuarios_db        CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
CREATE DATABASE IF NOT EXISTS desarrolladoras_db CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
CREATE DATABASE IF NOT EXISTS categorias_db      CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
CREATE DATABASE IF NOT EXISTS juegos_db          CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
CREATE DATABASE IF NOT EXISTS carrito_db         CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
CREATE DATABASE IF NOT EXISTS pagos_db           CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
CREATE DATABASE IF NOT EXISTS biblioteca_db      CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
CREATE DATABASE IF NOT EXISTS suscripciones_db   CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
CREATE DATABASE IF NOT EXISTS logros_db          CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
CREATE DATABASE IF NOT EXISTS resenas_db         CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;

-- Verificar
SHOW DATABASES LIKE '%_db';

-- ====================================================================
-- INICIALIZACIÓN DE BASE DE DATOS
-- ====================================================================
-- Este archivo se ejecuta automáticamente cuando se inicia PostgreSQL
-- por primera vez con Docker Compose
--
-- Crea extensiones y esquemas iniciales
-- ====================================================================

-- Habilitar extensiones útiles
CREATE EXTENSION IF NOT EXISTS "uuid-ossp";
CREATE EXTENSION IF NOT EXISTS "pgcrypto";

-- Revisar que se creó la BD schedulingdb
\c schedulingdb

-- Crear esquema (opcional, para organización)
CREATE SCHEMA IF NOT EXISTS scheduling;

-- Comentario de documentación
COMMENT ON SCHEMA scheduling IS 'Esquema para Sistema de Gestión de Horarios Laborales';

-- Crear tabla de auditoría global (opcional pero recomendado)
CREATE TABLE IF NOT EXISTS scheduling.database_log (
    id SERIAL PRIMARY KEY,
    event VARCHAR(50),
    event_time TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    details TEXT
);

-- Log de inicialización
INSERT INTO scheduling.database_log (event, details) 
VALUES ('INITIALIZATION', 'Base de datos inicializada correctamente');

-- Confirmar
SELECT 'Base de datos lista para usar' AS status;

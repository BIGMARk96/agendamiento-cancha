-- Crear la base de datos si no existe
CREATE DATABASE IF NOT EXISTS agendamiento_canchas;

USE agendamiento_canchas;

-- Crear la tabla de usuarios si no existe
CREATE TABLE IF NOT EXISTS usuarios (
    id INT AUTO_INCREMENT PRIMARY KEY,
    usuario VARCHAR(50) NOT NULL UNIQUE,
    password VARCHAR(100) NOT NULL,
    nombre VARCHAR(100),
    email VARCHAR(100),
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- Insertar un usuario de prueba si la tabla está vacía
INSERT INTO usuarios (usuario, password, nombre, email)
SELECT 'admin', 'admin123', 'Administrador', 'admin@example.com'
WHERE NOT EXISTS (SELECT 1 FROM usuarios WHERE usuario = 'admin'); 
-- Crear la base de datos
CREATE DATABASE IF NOT EXISTS agendamiento_canchas;
USE agendamiento_canchas;

-- Tabla de usuarios
CREATE TABLE IF NOT EXISTS usuarios (
    id INT PRIMARY KEY AUTO_INCREMENT,
    usuario VARCHAR(50) NOT NULL UNIQUE,
    password VARCHAR(255) NOT NULL,
    nombre VARCHAR(100) NOT NULL,
    rut VARCHAR(12) NOT NULL UNIQUE,
    telefono VARCHAR(15) NOT NULL,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- Tabla de canchas
CREATE TABLE IF NOT EXISTS canchas (
    id INT PRIMARY KEY AUTO_INCREMENT,
    nombre VARCHAR(50) NOT NULL,
    tipo ENUM('baby futbol', 'futsal', 'futbol') NOT NULL,
    precio_hora DECIMAL(10,2) NOT NULL
);

-- Tabla de agendamientos
CREATE TABLE IF NOT EXISTS agendamientos (
    id INT PRIMARY KEY AUTO_INCREMENT,
    usuario_id INT NOT NULL,
    cancha_id INT NOT NULL,
    fecha DATE NOT NULL,
    hora_inicio TIME NOT NULL,
    hora_fin TIME NOT NULL,
    nombre_cliente VARCHAR(100) NOT NULL,
    rut_cliente VARCHAR(20) NOT NULL,
    telefono_cliente VARCHAR(20) NOT NULL,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (usuario_id) REFERENCES usuarios(id),
    FOREIGN KEY (cancha_id) REFERENCES canchas(id)
);

-- Insertar canchas por defecto
INSERT INTO canchas (nombre, tipo, precio_hora) VALUES
('Cancha 1', 'baby futbol', 15000),
('Cancha 2', 'futsal', 20000),
('Cancha 3', 'futbol', 25000);

-- Insertar usuario administrador por defecto (password: admin123)
INSERT INTO usuarios (usuario, password, nombre, rut, telefono) VALUES
('admin', 'admin123', 'Administrador', '11111111-1', '912345678'); 
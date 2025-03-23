# Sistema de Agendamiento de Canchas

Este es un sistema de escritorio desarrollado en Java Swing para la gestión de agendamientos de canchas deportivas.

## Requisitos

- Java JDK 11 o superior
- MySQL 8.0 o superior
- Maven 3.6 o superior

## Configuración de la Base de Datos

1. Crear una base de datos MySQL:
```sql
mysql -u root -p
```

2. Ejecutar el script SQL proporcionado en `database.sql`:
```sql
source ruta/al/archivo/database.sql
```

## Configuración del Proyecto

1. Clonar el repositorio:
```bash
git clone [URL_DEL_REPOSITORIO]
cd agendamiento-cancha
```

2. Compilar el proyecto con Maven:
```bash
mvn clean install
```

## Ejecutar el Proyecto

1. Asegúrate de que MySQL esté corriendo y la base de datos esté creada.

2. Ejecutar la aplicación:
```bash
mvn exec:java -Dexec.mainClass="com.agendamiento.view.LoginFrame"
```

## Credenciales por Defecto

- Usuario: admin
- Contraseña: admin123

## Funcionalidades

- Registro de usuarios
- Login de usuarios
- Agendamiento de canchas
- Visualización de agendamientos
- Gestión de canchas (baby futbol, futsal, futbol)

## Estructura del Proyecto

```
src/
├── main/
│   └── java/
│       └── com/
│           └── agendamiento/
│               ├── util/
│               │   └── DatabaseConnection.java
│               └── view/
│                   ├── LoginFrame.java
│                   ├── RegistroFrame.java
│                   └── AgendamientoFrame.java
```

## Notas

- Asegúrate de tener MySQL instalado y corriendo antes de ejecutar la aplicación
- El usuario y contraseña de la base de datos están configurados en `DatabaseConnection.java`
- Por defecto, se incluyen 3 canchas en la base de datos 
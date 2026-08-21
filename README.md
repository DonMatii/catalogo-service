# 🍰 Catálogo Service - Pastelería My Dreams

Microservicio backend encargado de gestionar el inventario y catálogo de productos para el sistema **Pastelería My Dreams**.

## 🏢 Equipo de Desarrollo
Diseñado y construido por **8 Digital**.

## 🛠️ Stack Tecnológico
* **Lenguaje:** Java 21
* **Framework:** Spring Boot (LTS)
* **Persistencia:** Spring Data JPA / Hibernate & Amazon RDS (MySQL Cloud)
* **Gestor de dependencias:** Maven
* **Seguridad:** Spring Security + OAuth2 Resource Server (Validación JWT)
* **Estructura de datos:** JSON

## 🚀 Endpoints Disponibles

| Método HTTP | Ruta | Descripción |
| :--- | :--- | :--- |
| `GET` | `/api/productos` | Retorna el inventario completo de la pastelería agrupado por categorías (tortas, queques, tartas, personales). |

## ⚙️ Configuración y Despliegue
Para ejecutar este microservicio conectado a la infraestructura cloud:
1. Asegurarse de tener el JDK 21 instalado en el entorno de desarrollo.
2. Verificar las credenciales y el endpoint de conexión a la base de datos en el archivo `src/main/resources/application.properties` (AWS RDS).
3. Abrir el proyecto en IntelliJ IDEA, actualizar las dependencias de Maven y ejecutar la clase principal `CatalogoServiceApplication.java`.
4. El servidor se inicializará por defecto en el puerto `8080`.

> **Nota de Seguridad:** El microservicio implementa un filtro de seguridad personalizado (`JwtUniversalAuthFilter`) que valida tokens JWT de forma unificada. Todas las rutas de gestión y el consumo del catálogo de productos (`/api/productos`) requieren obligatoriamente que el usuario haya iniciado sesión (mediante Google OAuth2 o credenciales de Administrador), enviando el token en el encabezado `Authorization: Bearer <token>`.
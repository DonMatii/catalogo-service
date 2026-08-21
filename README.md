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

> **Nota de Seguridad:** El microservicio implementa validación de tokens JWT mediante Spring Security OAuth2 Resource Server. Las rutas protegidas (como administración) requieren un token válido emitido por Google (IDaaS) en el encabezado `Authorization: Bearer <token>`, mientras que el catálogo de productos es de acceso público (`permitAll`).
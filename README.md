# 🍰 Catálogo Service - Pastelería My Dreams

Microservicio backend encargado de gestionar el inventario y catálogo de productos para el sistema **Pastelería My Dreams**.

## 🏢 Equipo de Desarrollo
Diseñado y construido por **8 Digital**.

## 🛠️ Stack Tecnológico
* **Lenguaje:** Java 21
* **Framework:** Spring Boot 4.1.0 (LTS)
* **Gestor de dependencias:** Maven
* **Seguridad:** Spring Security + OAuth2 Resource Server
* **Estructura de datos:** JSON

## 🚀 Endpoints Disponibles

| Método HTTP | Ruta | Descripción |
| :--- | :--- | :--- |
| `GET` | `/api/productos` | Retorna el inventario completo de la pastelería agrupado por categorías (tortas, queques, tartas, personales). |

## ⚙️ Despliegue Local
Para ejecutar este microservicio en un entorno local:
1. Asegurarse de tener el JDK 21 instalado.
2. Abrir el proyecto en IntelliJ IDEA o Eclipse.
3. Actualizar dependencias de Maven.
4. Ejecutar la clase principal `CatalogoServiceApplication.java`.
5. El servidor se inicializará por defecto en el puerto `8080`.

> **Nota de Seguridad:** Para facilitar las pruebas de integración iniciales con el frontend en React, las rutas están temporalmente expuestas (`permitAll`). En fases posteriores, se implementará la validación estricta del token JWT contra el IDaaS.
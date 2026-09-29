# 🍰 Catálogo Service - Pastelería My Dreams

Microservicio backend encargado de gestionar el inventario y catálogo de productos para el sistema **Pastelería My Dreams**.

## 📌 Versiones del proyecto

| Rama | Versión | Contenido |
| :--- | :--- | :--- |
| `version-1` | **Entrega 1** | CRUD de catálogo con seguridad JWT detrás de AWS API Gateway. |
| `version-2` | **Entrega 2** | Pendiente. |
| `version-3` | **Unidad 3** | Pendiente. |

`main` siempre lleva el último avance del desarrollo.

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
| `GET` | `/api/productos` | Retorna el inventario completo de la pastelería agrupado por categorías. |
| `POST` | `/api/productos` | Registra y guarda un nuevo producto en la base de datos cloud (AWS RDS). |
| `PUT` | `/api/productos/{id}` | Actualiza los datos de un producto existente buscando por su ID único. |
| `DELETE` | `/api/productos/{id}` | Elimina de forma lógica o física un producto del inventario mediante su ID. |

## ⚙️ Configuración y Despliegue Cloud (EC2)
Para el entorno de producción, este microservicio se encuentra desplegado sobre instancias **Amazon EC2** y su acceso está centralizado y protegido a través de **AWS API Gateway** con enrutamiento de proxy (`/{proxy+}`).

Para ejecutar este microservicio en un entorno de desarrollo conectado a la infraestructura cloud:
1. Asegurarse de tener el JDK 21 instalado.
2. Verificar las credenciales y el endpoint de conexión a la base de datos en el archivo `src/main/resources/application.properties` (AWS RDS).
3. Abrir el proyecto en IntelliJ IDEA, actualizar las dependencias de Maven y ejecutar la clase principal `CatalogoServiceApplication.java`.
4. El servidor se inicializará por defecto en el puerto `8080`.
5. **Despliegue en Producción:** Para levantar el servicio en la instancia EC2 de forma segura, aislada y en segundo plano, se utiliza el siguiente comando, el cual también genera un registro de eventos (`app.log`):

```bash
sudo fuser -k 8080/tcp
sudo pkill -f java
nohup java -jar catalogo-service-0.0.1-SNAPSHOT.jar > app.log 2>&1 &
```

## Nota de Arquitectura y Seguridad: El microservicio implementa un filtro personalizado compatible con Spring Security 7 (JwtUniversalAuthFilter) diseñado para procesar el tráfico proveniente de AWS API Gateway. Todas las operaciones del CRUD (GET, POST, PUT, DELETE) operan de forma fluida y sincronizada con el frontend en React, permitiendo una gestión completa del catálogo sin bloqueos de CORS.
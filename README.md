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

## ⚙️ Configuración y Despliegue Cloud (EC2)
Para el entorno de producción, este microservicio se encuentra desplegado sobre instancias **Amazon EC2** y su acceso está centralizado y protegido a través de **AWS API Gateway**.

Para ejecutar este microservicio en un entorno de desarrollo conectado a la infraestructura cloud:
1. Asegurarse de tener el JDK 21 instalado.
2. Verificar las credenciales y el endpoint de conexión a la base de datos en el archivo `src/main/resources/application.properties` (AWS RDS).
3. Abrir el proyecto en IntelliJ IDEA, actualizar las dependencias de Maven y ejecutar la clase principal `CatalogoServiceApplication.java`.
4. El servidor se inicializará por defecto en el puerto `8080`.
5. **Despliegue en Producción:** Para levantar el servicio en la instancia EC2 de forma segura, aislada y en segundo plano, se utiliza el siguiente comando, el cual también genera un registro de eventos (`app.log`):
   
6. ```bash
   sudo fuser -k 8080/tcp
   sudo pkill -f java
   nohup java -jar catalogo-service-0.0.1-SNAPSHOT.jar > app.log 2>&1 &
   ```

> **Nota de Seguridad:** El microservicio implementa un filtro de seguridad universal (`JwtUniversalAuthFilter`) diseñado específicamente para convivir con AWS API Gateway. Todas las rutas de gestión y el consumo del catálogo (`/api/productos`) validan de forma transparente las credenciales (ya sea token de Google o sesión de Administrador), permitiendo el enrutamiento directo desde S3 sin bloqueos de CORS.
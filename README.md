# Prueba Técnica

Este repositorio contiene la solución a la prueba técnica de arquitectura de microservicios para el sistema bancario. La solución ha sido implementada siguiendo los requisitos para el perfil **Senior**, utilizando una arquitectura orientada a eventos y despliegue en contenedores.

## 🚀 Tecnologías Utilizadas

*   **Lenguaje**: Java 21
*   **Framework**: Spring Boot 3.3.1 (Spring WebFlux)
*   **Base de Datos**: PostgreSQL 13
*   **Mensajería Asincrónica**: RabbitMQ 3 (Management)
*   **Contenedores**: Docker & Docker Compose
*   **Documentación API**: OpenAPI 3.0 (Swagger)
*   **Pruebas**: JUnit 5, Mockito, Reactor Test
*   **Calidad de Código**: JaCoCo (Cobertura), PITest (Mutación)
*   **Herramientas**: Lombok, Gradle 8.8

##  Arquitectura

La solución se divide en dos microservicios independientes que se comunican de forma asincrónica para garantizar desacoplamiento, resiliencia y escalabilidad.

1.  **ms-clientes**:
    *   Gestiona la información de Clientes y Personas.
    *   Actúa como **Productor** de eventos: Cuando se crea o actualiza un cliente, publica un mensaje en RabbitMQ.
2.  **ms-cuentas**:
    *   Gestiona Cuentas y Movimientos.
    *   Actúa como **Consumidor** de eventos: Escucha los cambios en clientes y mantiene una réplica local de los datos necesarios (ID y Nombre) para optimizar la generación de reportes sin llamadas síncronas.

## ✅ Requisitos Cumplidos

| Requisito | Estado | Detalles |
| :--- | :---: | :--- |
| **Arquitectura Microservicios** | ✅ | Separación en `ms-clientes` y `ms-cuentas`. |
| **Nivel Senior (Comunicación Asíncrona)** | ✅ | Implementado con RabbitMQ (Patrón de Eventos). |
| **Spring WebFlux** | ✅ | Uso de stack reactivo (`Mono`, `Flux`) en toda la aplicación. |
| **Base de Datos Relacional** | ✅ | PostgreSQL configurado para cada microservicio. |
| **JPA** | ✅ | Uso de Hibernate/JPA para la persistencia. |
| **Docker** | ✅ | `Dockerfile` optimizado y `docker-compose.yml` para orquestación completa. |
| **API First (Contract First)** | ✅ | Definición previa en `openapi.yaml` y generación de código. |
| **Pruebas Unitarias e Integración** | ✅ | Cobertura de lógica de negocio y controladores (WebTestClient). |
| **Pruebas de Mutación** | ✅ | Implementado con PITest. |
| **Manejo de Errores** | ✅ | `GlobalExceptionHandler` centralizado. |
| **Funcionalidades F1-F4** | ✅ | CRUDs, Movimientos (Débito/Crédito), Validaciones de Saldo, Reportes. |

## 📂 Entregables

Todos los artefactos solicitados se encuentran en la carpeta `entregables/` de este repositorio:

*   📄 **Especificación OPENAPI**: `ms-clientes-openapi.yaml` y `ms-cuentas-openapi.yaml`.
*   🗄️ **Script Base de Datos**: `BaseDatos.sql` (en la raíz).
*   🧪 **Colección de Pruebas**: La colección de Postman se encuentra en la carpeta `bruno/` (si aplica) o se puede importar desde los archivos OpenAPI.

## 🛠️ Instrucciones de Ejecución

### Pre-requisitos
*   Docker y Docker Compose instalados.
*   Java 21 (Opcional, solo para desarrollo local).

### Despliegue Rápido (Docker)

1.  **Clonar el repositorio**:
    ```bash
    git clone https://github.com/Lgsalgado/banking-system-backend.git
    cd banking-system-backend
    ```

2.  **Construir y Levantar**:
    ```bash
    # Generar los JARs (omitiendo tests para rapidez)
    ./gradlew clean build -x test
    
    # Levantar contenedores
    docker-compose up --build -d
    ```

3.  **Verificar**:
    *   **ms-clientes**: http://localhost:8081
    *   **ms-cuentas**: http://localhost:8082
    *   **RabbitMQ**: http://localhost:15672 (guest/guest)

### Ejecución de Pruebas

*   **Pruebas Unitarias e Integración**:
    ```bash
    ./gradlew test
    ```
*   **Reporte de Cobertura (JaCoCo)**:
    ```bash
    ./gradlew jacocoTestReport
    # Reportes en: ms-*/build/reports/jacoco/test/html/index.html
    ```
*   **Pruebas de Mutación (PITest)**:
    ```bash
    ./gradlew pitest
    # Reportes en: ms-*/build/reports/pitest/index.html
    ```

## 📝 Endpoints Principales

### Clientes (Puerto 8081)
*   `POST /api/v1/customers`: Crear cliente.
*   `GET /api/v1/customers`: Listar clientes.

### Cuentas y Movimientos (Puerto 8082)
*   `POST /api/v1/accounts`: Crear cuenta.
*   `POST /api/v1/movements`: Realizar movimiento (Débito/Crédito).
*   `GET /reports`: Generar reporte de estado de cuenta por rango de fechas.

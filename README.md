# Piedrazul Microservices

Sistema distribuido para la gestion de procesos clinicos, enfocado en usuarios, personas (pacientes y medicos), citas y notificaciones, con una aplicacion de escritorio JavaFX como cliente.

## Vision General

El proyecto implementa una arquitectura de microservicios con responsabilidades separadas:

- `usuarios-service`: autenticacion, gestion de cuentas y roles.
- `personas-service`: gestion de datos de pacientes, medicos y disponibilidad.
- `citas-service`: orquestacion del ciclo de vida de las citas (crear, cancelar, reagendar, historial).
- `notifications-service`: consumo/publicacion de eventos para notificaciones.
- `piedrazul-frontend`: cliente JavaFX que consume los servicios REST.

La comunicacion combina:

- **Sincrona** por HTTP/REST entre frontend y microservicios.
- **Asincrona** con RabbitMQ para eventos de dominio entre servicios.

## Arquitectura del Repositorio

```text
piedrazul_microservices/
|- usuarios-service/
|- personas-service/
|- citas-service/
|- notifications-service/
|- piedrazul-frontend/
`- README.md
```

## Stack Tecnologico

- Java 17/21 (segun modulo)
- Spring Boot (Web, Validation, JPA, Security)
- PostgreSQL (driver presente en servicios principales)
- RabbitMQ (mensajeria AMQP)
- OpenAPI/Swagger UI (`springdoc-openapi`)
- JavaFX (cliente de escritorio)
- Maven (build y ejecucion)

## Servicios y Responsabilidades

### `usuarios-service`

Responsable de autenticacion y gestion de usuarios.

Endpoints principales:

- `POST /api/auth/login`
- `GET /api/usuarios/{id}`
- `POST /api/usuarios/{id}/roles`

### `personas-service`

Responsable de informacion clinica base de personas.

Endpoints principales:

- `GET /api/personas/{id}`
- `GET /api/pacientes/{personaId}`
- `GET /api/medicos/{personaId}`
- `GET /api/medicos/activos`
- Base de disponibilidad en `/api/disponibilidad`

### `citas-service`

Responsable del flujo de citas y configuracion.

Endpoints principales:

- `POST /api/citas/manual`
- `POST /api/citas/autonoma`
- `PUT /api/citas/{citaId}/cancelar`
- `PUT /api/citas/reagendar`
- `PUT /api/citas/asistencia`
- `GET /api/citas/medicos/{medicoId}/slots`
- `GET /api/citas/historial`
- Base de configuracion en `/api/configuracion`

### `notifications-service`

Responsable de notificaciones por eventos.

Endpoint visible:

- `POST /api/notificaciones/cita-agendada`

Adicionalmente consume/publica mensajes en RabbitMQ para eventos de citas.

### `piedrazul-frontend`

Cliente de escritorio JavaFX con vistas FXML para:

- Login y registro
- Dashboard por perfil
- Historial y gestion de citas
- Integracion con APIs de usuarios, personas y citas

## Requisitos Previos

- JDK 21 recomendado (algunos modulos usan Java 17; Java 21 es compatible para desarrollo moderno)
- Maven 3.9+
- PostgreSQL
- RabbitMQ
- Git

## Configuracion de Entorno

En el repositorio no se incluyen archivos `application.yml`/`application.properties` versionados para los microservicios, por lo que debes configurar cada modulo segun tu entorno local.

El frontend referencia por defecto:

- `usuarios-service` en `http://localhost:8081`
- `personas-service` en `http://localhost:8082`
- `citas-service` en `http://localhost:8083`

Recomendacion:

1. Define puertos unicos por microservicio.
2. Configura `datasource` de PostgreSQL por servicio.
3. Configura exchange/queues/routing keys de RabbitMQ.
4. Mantiene consistencia entre URLs del frontend y puertos backend.

## Ejecucion Local (Desarrollo)

Levanta dependencias primero:

1. Inicia PostgreSQL.
2. Inicia RabbitMQ.
3. Crea las bases de datos necesarias para cada servicio.

Luego inicia servicios backend (cada uno en su carpeta):

```bash
mvn clean spring-boot:run
```

Orden recomendado:

1. `usuarios-service`
2. `personas-service`
3. `citas-service`
4. `notifications-service`

Finalmente inicia el frontend:

```bash
cd piedrazul-frontend
mvn clean javafx:run
```

## Swagger / OpenAPI

Cada microservicio expone documentacion OpenAPI. Una vez levantado el servicio:

- `http://localhost:<PUERTO>/swagger-ui/index.html`
- `http://localhost:<PUERTO>/v3/api-docs`

## Flujo Funcional Resumido

1. Usuario se autentica en `usuarios-service`.
2. Frontend consulta/gestiona perfiles en `personas-service`.
3. Se crean y gestionan citas en `citas-service`.
4. Eventos de citas y cambios de disponibilidad viajan por RabbitMQ.
5. `notifications-service` procesa eventos para notificar.

## Buenas Practicas Recomendadas

- Mantener contratos REST estables entre frontend y servicios.
- Versionar archivos de configuracion de ejemplo (`application-example.yml`).
- Centralizar credenciales en variables de entorno.
- Agregar pruebas de integracion para flujos entre microservicios.
- Incorporar `docker-compose` para levantar infraestructura local de forma reproducible.

## Estado del Proyecto

Proyecto activo en desarrollo. La base esta orientada a evolucionar hacia un entorno mas automatizado (configuracion unificada, pruebas end-to-end y despliegue estandarizado).

## Autores

- Carlos Eduardo Dorado Joaqui
- Rhony Daniel Martinez Benavides
- Juan Esteban Moscoso Salazar
- Andres Felipe Obando Quintero
- Ronal Santiago Valdez Jurado

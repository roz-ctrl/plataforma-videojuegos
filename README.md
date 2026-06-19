# Plataforma de Venta de Videojuegos (Microservicios)

Proyecto de la asignatura DSY1103 - Desarrollo FullStack 1 (Evaluacion Parcial 3).

Es una tienda de videojuegos digitales parecida a Steam, hecha con Spring Boot.
La aplicacion esta dividida en 10 microservicios y cada uno tiene su propia base
de datos en MySQL.

La explicacion del cambio de monolito a microservicios esta en CASO-DE-ESTUDIO.md
y los diagramas estan en ARQUITECTURA.md.

## Integrantes
- Rafael Miotta
- Pedro Ponce R 


## Tecnologias usadas

- Java 17
- Spring Boot 3.3.4
- Spring Data JPA + Hibernate
- MySQL (servidor de Laragon)
- Spring Cloud OpenFeign (comunicacion entre servicios)
- Spring Cloud Gateway (API Gateway, puerto 8080)
- Swagger / OpenAPI (springdoc) para documentacion
- JUnit 5 + Mockito + JaCoCo para pruebas unitarias
- Bean Validation y SLF4J
- Maven

## Microservicios

| Servicio | Puerto | Para que sirve |
|---|---|---|
| usuarios-service | 8081 | Jugadores registrados y su saldo |
| desarrolladoras-service | 8082 | Empresas que crean los juegos |
| categorias-service | 8083 | Generos (RPG, Shooter, etc.) |
| juegos-service | 8084 | Catalogo de juegos |
| carrito-service | 8085 | Carrito de compras |
| pagos-service | 8086 | Pagos y transacciones |
| biblioteca-service | 8087 | Juegos que ya compro el usuario |
| suscripciones-service | 8088 | Pases mensuales |
| logros-service | 8089 | Logros desbloqueables |
| resenas-service | 8090 | Opiniones y calificaciones |

Todos estan organizados con el patron CSR (controller, service, repository/model),
mas los paquetes dto y exception.

## API Gateway

El `gateway-service` (puerto 8080) es el punto unico de entrada: recibe todas las
peticiones y las reenvia al microservicio correspondiente segun la ruta. Esta
configurado con Spring Cloud Gateway en
`gateway-service/src/main/resources/application.yml` (archivo YAML).

Rutas principales (todo entra por el puerto 8080):

| Ruta en el Gateway | Microservicio destino |
|---|---|
| /api/usuarios/** | usuarios (8081) |
| /api/desarrolladoras/** | desarrolladoras (8082) |
| /api/categorias/** | categorias (8083) |
| /api/juegos/** | juegos (8084) |
| /api/carrito/** | carrito (8085) |
| /api/pagos/** | pagos (8086) |
| /api/biblioteca/** | biblioteca (8087) |
| /api/planes/** y /api/suscripciones/** | suscripciones (8088) |
| /api/logros/** | logros (8089) |
| /api/resenas/** | resenas (8090) |

Ejemplo: en vez de `http://localhost:8084/api/juegos` puedes pedir
`http://localhost:8080/api/juegos` y el Gateway lo enruta a juegos-service.

Para levantarlo: `cd gateway-service` y luego `mvn spring-boot:run` (con los
microservicios que vayas a consumir ya encendidos).

## Como ejecutar

Requisitos: tener Java 17 y Laragon corriendo con MySQL (boton Start All).

Cada microservicio es un proyecto Spring Boot aparte. Se pueden abrir en IntelliJ
o ejecutar desde la terminal:

```
cd usuarios-service
mvn spring-boot:run
```

No hace falta crear las bases de datos a mano: cada servicio las crea solo al
arrancar (createDatabaseIfNotExist=true). Si se quieren crear manualmente esta el
script db/crear-bases-datos.sql. El usuario por defecto de Laragon es root sin
contrasena.

Conviene levantar primero los servicios base (usuarios, desarrolladoras,
categorias y juegos) y despues los que dependen de ellos (carrito, biblioteca,
pagos, suscripciones, logros y resenas).

Los datos de ejemplo se cargan desde el archivo data.sql de cada servicio. Para
verlos se puede abrir HeidiSQL desde Laragon.

## Docker

Todo el sistema (MySQL + los 10 microservicios + el gateway) se levanta con Docker
Compose. Requisito: Docker Desktop funcionando.

1. Generar los .jar de cada servicio:
   ```
   .\construir-jars.ps1
   ```
2. Levantar todo:
   ```
   docker compose up --build
   ```

Se crea un contenedor MySQL (con las 10 bases) y un contenedor por cada
microservicio + el gateway. La configuracion (host de la BD y URLs entre
servicios) se inyecta por variables de entorno en `docker-compose.yml`, sin tocar
el codigo. Una vez arriba, todo se consume por el gateway en
`http://localhost:8080`.

Comandos utiles:
- `docker compose ps` - ver contenedores
- `docker compose logs -f gateway-service` - ver logs de un servicio
- `docker compose down` - detener todo

## Funcionalidades

- CRUD completo con JpaRepository en cada servicio.
- Validaciones con Bean Validation en los DTOs.
- Manejo de errores con @RestControllerAdvice y codigos HTTP.
- Logs con SLF4J.
- Comunicacion entre microservicios con OpenFeign.
- Reglas de negocio del dominio (estan explicadas en CASO-DE-ESTUDIO.md).

## Pendiente

- Tablero Trello del equipo.
- (Opcional) Despliegue remoto en Railway / Render.

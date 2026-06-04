# Plataforma de Venta de Videojuegos (Microservicios)

Proyecto de la asignatura DSY1103 - Desarrollo FullStack 1 (Evaluacion Parcial 2).

Es una tienda de videojuegos digitales parecida a Steam, hecha con Spring Boot.
La aplicacion esta dividida en 10 microservicios y cada uno tiene su propia base
de datos en MySQL.

La explicacion del cambio de monolito a microservicios esta en CASO-DE-ESTUDIO.md
y los diagramas estan en ARQUITECTURA.md.

## Integrantes

- (completar nombre) - Equipo (completar)
- (completar nombre)
- (completar nombre)

## Tecnologias usadas

- Java 17
- Spring Boot 3.3.4
- Spring Data JPA + Hibernate
- MySQL (servidor de Laragon)
- Spring Cloud OpenFeign (comunicacion entre servicios)
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

## Funcionalidades

- CRUD completo con JpaRepository en cada servicio.
- Validaciones con Bean Validation en los DTOs.
- Manejo de errores con @RestControllerAdvice y codigos HTTP.
- Logs con SLF4J.
- Comunicacion entre microservicios con OpenFeign.
- Reglas de negocio del dominio (estan explicadas en CASO-DE-ESTUDIO.md).

## Pendiente

- Documentar los endpoints con Swagger (Springdoc OpenAPI).

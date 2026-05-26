# GameVault — Plataforma de Venta de Videojuegos (Microservicios)

Proyecto Semestral — **DSY1103 Desarrollo FullStack 1** — Evaluación Parcial 2.
Arquitectura de **microservicios** con Spring Boot, que migra una tienda digital de
videojuegos (estilo Steam / eShop) desde un monolito hacia 10 servicios independientes.

> 📄 La justificación del cambio monolito → microservicios está en
> [CASO-DE-ESTUDIO.md](CASO-DE-ESTUDIO.md).
> 🗺️ Los diagramas para la defensa están en [ARQUITECTURA.md](ARQUITECTURA.md).
> 📮 Colección de Postman: [GameVault.postman_collection.json](GameVault.postman_collection.json)
> (impórtala con *Import → File*; las URLs ya vienen como variables de colección).

## 👥 Integrantes del equipo

| Nombre completo | Número de equipo |
|---|---|
| _(completar)_ | _(completar)_ |
| _(completar)_ | |
| _(completar)_ | |

## 🧱 Stack tecnológico

- **Java 17** + **Spring Boot 3.3.4**
- **Spring Data JPA + Hibernate** (persistencia real)
- **MySQL / MariaDB** (servidor de Laragon; una base de datos por microservicio)
- **Spring Cloud OpenFeign** (comunicación entre microservicios)
- **Bean Validation (JSR 380)**, **SLF4J** (logs), **Maven**

## 🗂️ Microservicios

| # | Servicio | Puerto | Descripción |
|---|---|---|---|
| 1 | usuarios-service        | 8081 | Jugadores registrados + billetera |
| 2 | desarrolladoras-service | 8082 | Publishers |
| 3 | categorias-service      | 8083 | Géneros |
| 4 | juegos-service          | 8084 | Catálogo (Feign → desarrolladoras, categorias) |
| 5 | carrito-service         | 8085 | Carrito (Feign → usuarios, juegos) |
| 6 | pagos-service           | 8086 | Transacciones (Feign → carrito, usuarios, biblioteca) |
| 7 | biblioteca-service      | 8087 | Juegos comprados (Feign → usuarios, juegos) |
| 8 | suscripciones-service   | 8088 | Pases mensuales (Feign → usuarios) |
| 9 | logros-service          | 8089 | Trofeos (Feign → usuarios, juegos) |
| 10| resenas-service         | 8090 | Reseñas (Feign → usuarios, juegos, biblioteca) |

Todos siguen el **patrón CSR**: `controller` → `service` → `repository`/`model`,
con `dto` y `exception` (manejo centralizado con `@ControllerAdvice`).

## ▶️ Cómo ejecutar

> **Requisitos:** Java 17 instalado y **Laragon corriendo con MySQL** (botón
> *Start All*). Maven viene incluido en IntelliJ IDEA; si usas la terminal y no
> tienes Maven, puedes ejecutar desde IntelliJ.

### Base de datos (Laragon)

No necesitas crear las bases manualmente: cada servicio usa
`createDatabaseIfNotExist=true`, así que MySQL las crea solas al arrancar.
Si prefieres crearlas a mano, ejecuta [db/crear-bases-datos.sql](db/crear-bases-datos.sql)
en HeidiSQL. Conexión por defecto de Laragon: host `localhost`, puerto `3306`,
usuario `root`, **sin contraseña**.

Cada microservicio es un proyecto Spring Boot independiente. Ábrelos en IntelliJ
(File → Open → carpeta del servicio) o ejecútalos por terminal:

```bash
# Ejemplo para un servicio (repetir en cada carpeta)
cd usuarios-service
mvn spring-boot:run
```

### Orden de arranque recomendado

Levanta primero los servicios base (sin dependencias) y luego los que los consumen:

```
1) usuarios-service        (8081)
2) desarrolladoras-service (8082)
3) categorias-service      (8083)
4) juegos-service          (8084)
5) biblioteca-service      (8087)
6) carrito-service         (8085)
7) suscripciones-service   (8088)
8) logros-service          (8089)
9) pagos-service           (8086)
10) resenas-service        (8090)
```

Cada servicio crea su base de datos en MySQL (ej. `usuarios_db`, `juegos_db`, …)
y carga datos de ejemplo desde `data.sql` al iniciar. Para ver los datos abre
**HeidiSQL** desde Laragon (botón *Database*) y revisa cada base, o ejecuta
`SELECT * FROM usuarios;` etc.

## 🔌 Endpoints principales

### usuarios-service (8081)
- `GET    /api/usuarios` · `GET /api/usuarios/{id}`
- `POST   /api/usuarios` · `PUT /api/usuarios/{id}` · `DELETE /api/usuarios/{id}`
- `PUT    /api/usuarios/{id}/recargar?monto=10000`
- `PUT    /api/usuarios/{id}/debitar?monto=5000`

### desarrolladoras-service (8082) / categorias-service (8083)
- CRUD en `/api/desarrolladoras` y `/api/categorias`

### juegos-service (8084)
- CRUD en `/api/juegos` (valida desarrolladora y categoría vía Feign;
  devuelve `precioFinal` con descuento y nombres remotos)

### carrito-service (8085)
- `GET    /api/carrito/usuario/{usuarioId}`
- `POST   /api/carrito/items`  body: `{ "usuarioId":1, "juegoId":1 }`
- `DELETE /api/carrito/usuario/{usuarioId}/juego/{juegoId}`
- `DELETE /api/carrito/usuario/{usuarioId}` (vaciar)
- `PUT    /api/carrito/usuario/{usuarioId}/pagar`

### pagos-service (8086)
- `POST   /api/pagos`  body: `{ "usuarioId":1, "metodoPago":"SALDO" }`
- `GET    /api/pagos/{id}` · `GET /api/pagos/usuario/{usuarioId}`

### biblioteca-service (8087)
- `GET    /api/biblioteca/usuario/{usuarioId}`
- `GET    /api/biblioteca/usuario/{usuarioId}/posee/{juegoId}`
- `POST   /api/biblioteca`  body: `{ "usuarioId":1, "juegoId":1 }`
- `PATCH  /api/biblioteca/{id}/horas?horas=3` · `PATCH /api/biblioteca/{id}/instalado?valor=true`

### suscripciones-service (8088)
- CRUD planes en `/api/planes`
- `POST   /api/suscripciones`  body: `{ "usuarioId":1, "planId":1, "renovacionAutomatica":true }`
- `GET    /api/suscripciones/usuario/{usuarioId}` · `PUT /api/suscripciones/{id}/cancelar`

### logros-service (8089)
- `GET    /api/logros/juego/{juegoId}`
- `POST   /api/logros`  body: `{ "juegoId":2, "nombre":"...", "puntos":50 }`
- `POST   /api/logros/desbloquear`  body: `{ "usuarioId":1, "logroId":1 }`
- `GET    /api/logros/usuario/{usuarioId}/desbloqueados`

### resenas-service (8090)
- `GET    /api/resenas/juego/{juegoId}` · `GET /api/resenas/juego/{juegoId}/resumen`
- `POST   /api/resenas`  body: `{ "usuarioId":1, "juegoId":2, "calificacion":5, "comentario":"...", "recomendado":true }`
- `PUT /api/resenas/{id}` · `DELETE /api/resenas/{id}`

## ✅ Funcionalidades implementadas

- Persistencia real JPA + Hibernate con relaciones `@OneToMany` / `@ManyToOne`.
- CRUD completo con `JpaRepository` y retornos JSON con `ResponseEntity`.
- Validaciones Bean Validation en DTOs (separados de las entidades).
- Manejo centralizado de errores (`@RestControllerAdvice`) con códigos HTTP correctos.
- Logs SLF4J en cada capa de servicio.
- Comunicación entre microservicios con OpenFeign (timeouts y manejo de errores).
- Reglas de negocio del dominio (ver [CASO-DE-ESTUDIO.md](CASO-DE-ESTUDIO.md)).

## 🧪 Prueba rápida del flujo de compra (Postman / curl)

```bash
# 1. El usuario 1 agrega el juego 1 al carrito
curl -X POST http://localhost:8085/api/carrito/items -H "Content-Type: application/json" -d "{\"usuarioId\":1,\"juegoId\":1}"

# 2. Procesa el pago con su saldo
curl -X POST http://localhost:8086/api/pagos -H "Content-Type: application/json" -d "{\"usuarioId\":1,\"metodoPago\":\"SALDO\"}"

# 3. Verifica que el juego quedó en su biblioteca
curl http://localhost:8087/api/biblioteca/usuario/1
```

# Caso de Estudio: De Monolito a Microservicios

## Plataforma de Venta de Videojuegos "GameVault" (estilo Steam / eShop)

---

## 1. Contexto del negocio

**GameVault** es una tienda digital de videojuegos. Vende productos **100% digitales**:
el cliente compra un juego, este queda asociado a su cuenta y puede descargarlo cuantas
veces quiera. Al ser digital, no hay logística de envío ni control de stock físico
complejo, pero sí una gran cantidad de operaciones simultáneas: catálogo, compras,
pagos, bibliotecas personales, suscripciones, logros y reseñas de la comunidad.

## 2. El problema: la arquitectura monolítica actual

La primera versión de GameVault se construyó como una **aplicación monolítica**: un único
proyecto Spring Boot, con una sola base de datos y un solo despliegue, donde **todo el
código vivía junto** (usuarios, juegos, pagos, reseñas, etc.).

Al crecer la plataforma, este enfoque empezó a generar problemas serios:

| Problema del monolito | Impacto real en GameVault |
|---|---|
| **Acoplamiento total** | Un cambio en el módulo de Reseñas obligaba a recompilar y volver a desplegar **toda** la aplicación, incluyendo Pagos. |
| **Escalabilidad "todo o nada"** | En un lanzamiento (ej. una oferta), el cuello de botella era el Catálogo y el Carrito, pero había que escalar **toda** la app —incluyendo Logros o Suscripciones que no recibían carga—, desperdiciando recursos. |
| **Fragilidad / falta de resiliencia** | Un error de memoria en el módulo de Reseñas tumbaba **todo el sistema**: nadie podía siquiera iniciar sesión o comprar. |
| **Base de datos compartida** | Todas las tablas en un mismo esquema; un bloqueo o una migración pesada afectaba a módulos no relacionados. |
| **Despliegues lentos y riesgosos** | Equipos distintos pisándose en el mismo código; cada release era un evento de alto riesgo. |

## 3. La solución: arquitectura de microservicios

La plataforma es **grande y compleja** y necesita **escalabilidad, flexibilidad y
resiliencia**. Por eso se migra a una **arquitectura de microservicios**, donde cada
capacidad de negocio se convierte en un servicio independiente, desplegable por separado
y con **su propia base de datos** (patrón *database per service*).

### Beneficios que resuelve cada problema

- **Escalabilidad selectiva:** se pueden levantar más instancias **solo** del catálogo
  (`juegos-service`) y del `carrito-service` durante una oferta, sin tocar el resto.
- **Flexibilidad:** cada equipo despliega su microservicio de forma independiente; un
  cambio en Reseñas no recompila Pagos.
- **Resiliencia:** si `logros-service` se cae, el usuario igual puede navegar el catálogo,
  comprar y pagar. El fallo queda **aislado** (lo vemos en el manejo de `FeignException`
  que devuelve 503 sin propagar la caída).
- **Aislamiento de datos:** cada servicio es dueño de sus tablas; nadie accede a la BD de
  otro directamente, solo mediante su API REST.

## 4. Descomposición por capacidades de negocio (10 microservicios)

| # | Microservicio | Puerto | Responsabilidad | Entidades |
|---|---|---|---|---|
| 1 | `usuarios-service`        | 8081 | Jugadores registrados + billetera (saldo) | Usuario |
| 2 | `desarrolladoras-service` | 8082 | Publishers / empresas creadoras | Desarrolladora |
| 3 | `categorias-service`      | 8083 | Géneros (RPG, Shooter, etc.) | Categoria |
| 4 | `juegos-service`          | 8084 | Catálogo, precios, requisitos, descuentos | Juego |
| 5 | `carrito-service`         | 8085 | Carrito temporal antes del pago | Carrito, ItemCarrito |
| 6 | `pagos-service`           | 8086 | Transacciones (orquesta la compra) | Pago, DetallePago |
| 7 | `biblioteca-service`      | 8087 | Juegos comprados por el usuario | EntradaBiblioteca |
| 8 | `suscripciones-service`   | 8088 | Pases mensuales/anuales | Plan, Suscripcion |
| 9 | `logros-service`          | 8089 | Trofeos por juego y desbloqueos | Logro, LogroDesbloqueado |
| 10| `resenas-service`         | 8090 | Opiniones y calificaciones | Resena |

## 5. Mapa de comunicación entre servicios (Feign)

La comunicación es **síncrona vía REST** usando **OpenFeign**. Cada flecha es una llamada
remota real (con manejo de timeouts y errores):

```
juegos-service        ──> desarrolladoras-service   (validar/obtener publisher)
juegos-service        ──> categorias-service         (validar/obtener género)

carrito-service       ──> usuarios-service           (validar usuario)
carrito-service       ──> juegos-service             (precio y disponibilidad)

biblioteca-service    ──> usuarios-service           (validar usuario)
biblioteca-service    ──> juegos-service             (título del juego)

pagos-service         ──> carrito-service            (leer carrito y cerrarlo)
pagos-service         ──> usuarios-service           (debitar saldo)
pagos-service         ──> biblioteca-service         (registrar juegos comprados)

suscripciones-service ──> usuarios-service           (validar usuario)

logros-service        ──> usuarios-service           (validar usuario)
logros-service        ──> juegos-service             (validar juego)

resenas-service       ──> usuarios-service           (validar usuario)
resenas-service       ──> juegos-service             (validar juego)
resenas-service       ──> biblioteca-service         (¿el usuario POSEE el juego?)
```

## 6. Flujo de negocio estrella: la compra (orquestación de `pagos-service`)

Este es el flujo que mejor demuestra la interoperabilidad. Cuando un usuario paga su
carrito, `pagos-service` coordina **cuatro** microservicios en una sola operación:

1. **GET** carrito del usuario → `carrito-service`.
2. Valida que el carrito **no esté vacío** (regla de negocio).
3. Si paga con **SALDO**, **debita** la billetera → `usuarios-service`
   (que aplica la regla *saldo suficiente* y responde 409 si no alcanza).
4. **Registra cada juego** en la biblioteca → `biblioteca-service`.
5. **Marca el carrito como PAGADO** → `carrito-service`.
6. **Persiste** la transacción con su detalle en su propia BD.

Si cualquier paso remoto falla, el manejo de excepciones devuelve un código HTTP
coherente (404, 409 o 503) en lugar de dejar el sistema en un estado ambiguo.

## 7. Reglas de negocio implementadas (resumen)

- Email y nombre de usuario **únicos** (`usuarios-service`).
- No se puede comprar con **saldo insuficiente** (`usuarios` + `pagos`).
- Un juego digital **no se agrega dos veces** al carrito (`carrito-service`).
- Un usuario **no puede poseer dos veces** el mismo juego (`biblioteca-service`).
- Solo se puede **reseñar un juego que se posee** y **una sola reseña por juego**
  (`resenas-service` consultando a `biblioteca-service`).
- **Una sola suscripción activa** por usuario a la vez (`suscripciones-service`).
- Un **logro no se desbloquea dos veces** para el mismo usuario (`logros-service`).
- Un juego solo se publica si su **desarrolladora y categoría existen** (`juegos-service`).

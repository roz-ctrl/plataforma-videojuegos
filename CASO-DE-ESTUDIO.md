# Caso de estudio: de monolito a microservicios

## Plataforma de venta de videojuegos (estilo Steam / eShop)

## Contexto

Es una tienda de videojuegos digitales. El cliente compra un juego, este queda
asociado a su cuenta y lo puede descargar las veces que quiera. Al ser digital no
hay envios ni control de stock fisico, pero si hay muchas operaciones: catalogo,
compras, pagos, biblioteca de cada usuario, suscripciones, logros y resenas.

## El problema: la aplicacion monolitica

La primera version se hizo como un monolito: un solo proyecto Spring Boot, una
sola base de datos y un solo despliegue, con todo el codigo junto (usuarios,
juegos, pagos, resenas, etc.).

Al ir creciendo, esto empezo a dar problemas:

- Todo estaba acoplado. Un cambio en el modulo de resenas obligaba a recompilar y
  volver a desplegar toda la aplicacion, incluso pagos.
- La escalabilidad era de "todo o nada". En una oferta, los que recibian carga
  eran el catalogo y el carrito, pero habia que escalar toda la app, gastando
  recursos en modulos que no los necesitaban.
- Poca resiliencia. Si fallaba un modulo (por ejemplo resenas), se caia todo el
  sistema y nadie podia ni iniciar sesion ni comprar.
- Base de datos compartida. Todas las tablas en un mismo esquema, asi que un
  problema en una tabla afectaba a modulos que no tenian relacion.

## La solucion: microservicios

Como la aplicacion es grande y compleja, y necesita escalabilidad, flexibilidad y
resiliencia, se decidio dividirla en microservicios. Cada parte del negocio pasa a
ser un servicio independiente, que se despliega por separado y tiene su propia base
de datos (una base por servicio).

Asi se resuelven los problemas anteriores:

- Escalabilidad: se pueden levantar mas instancias solo del catalogo y del carrito
  durante una oferta, sin tocar el resto.
- Flexibilidad: cada servicio se puede actualizar sin recompilar los demas.
- Resiliencia: si se cae logros-service, el usuario igual puede ver el catalogo,
  comprar y pagar. El fallo queda aislado (se maneja con FeignException y se
  devuelve un 503 sin tumbar todo).
- Datos aislados: cada servicio es dueño de sus tablas y los demas solo lo
  consultan a traves de su API REST.

## Los 10 microservicios

| Microservicio | Puerto | Responsabilidad | Entidades |
|---|---|---|---|
| usuarios-service | 8081 | Jugadores y su saldo | Usuario |
| desarrolladoras-service | 8082 | Empresas creadoras | Desarrolladora |
| categorias-service | 8083 | Generos | Categoria |
| juegos-service | 8084 | Catalogo, precios, descuentos | Juego |
| carrito-service | 8085 | Carrito antes del pago | Carrito, ItemCarrito |
| pagos-service | 8086 | Transacciones | Pago, DetallePago |
| biblioteca-service | 8087 | Juegos comprados | EntradaBiblioteca |
| suscripciones-service | 8088 | Pases mensuales | Plan, Suscripcion |
| logros-service | 8089 | Logros y desbloqueos | Logro, LogroDesbloqueado |
| resenas-service | 8090 | Opiniones y notas | Resena |

## Comunicacion entre servicios

Los servicios se comunican por REST usando OpenFeign. Estas son las llamadas:

```
juegos-service        -> desarrolladoras-service   (validar el publisher)
juegos-service        -> categorias-service        (validar el genero)

carrito-service       -> usuarios-service          (validar usuario)
carrito-service       -> juegos-service            (precio y disponibilidad)

biblioteca-service    -> usuarios-service          (validar usuario)
biblioteca-service    -> juegos-service            (titulo del juego)

pagos-service         -> carrito-service           (leer el carrito y cerrarlo)
pagos-service         -> usuarios-service          (descontar saldo)
pagos-service         -> biblioteca-service        (registrar los juegos comprados)

suscripciones-service -> usuarios-service          (validar usuario)

logros-service        -> usuarios-service          (validar usuario)
logros-service        -> juegos-service            (validar juego)

resenas-service       -> usuarios-service          (validar usuario)
resenas-service       -> juegos-service            (validar juego)
resenas-service       -> biblioteca-service        (revisar si el usuario tiene el juego)
```

## Flujo de compra

Es el flujo que mejor muestra como trabajan juntos los servicios. Cuando un usuario
paga su carrito, pagos-service coordina cuatro servicios en una sola operacion:

1. Pide el carrito del usuario a carrito-service.
2. Revisa que el carrito no este vacio.
3. Si paga con saldo, descuenta el saldo en usuarios-service (que devuelve 409 si
   no le alcanza).
4. Registra cada juego en la biblioteca del usuario (biblioteca-service).
5. Marca el carrito como pagado en carrito-service.
6. Guarda la transaccion con su detalle en su propia base de datos.

Si alguno de esos pasos falla, el manejo de excepciones devuelve un codigo HTTP
correcto (404, 409 o 503) en vez de dejar el sistema en un estado raro.

## Reglas de negocio

- El email y el nombre de usuario no se pueden repetir (usuarios-service).
- No se puede comprar si el saldo no alcanza (usuarios + pagos).
- Un juego no se puede agregar dos veces al carrito (carrito-service).
- Un usuario no puede tener dos veces el mismo juego (biblioteca-service).
- Solo se puede resenar un juego que se tiene, y una sola resena por juego
  (resenas-service consultando a biblioteca-service).
- Un usuario solo puede tener una suscripcion activa a la vez (suscripciones-service).
- Un logro no se puede desbloquear dos veces (logros-service).
- Un juego solo se publica si su desarrolladora y su categoria existen
  (juegos-service).

# Diagramas de arquitectura

Documento de apoyo para la defensa. Tiene los diagramas hechos en Mermaid, que
GitHub muestra solo. Tambien se pueden pegar en https://mermaid.live para verlos.

## 1. Arquitectura general

Cada microservicio es independiente, tiene su propia base de datos en MySQL y su
propio puerto. El cliente consume cada API REST por separado.

```mermaid
flowchart TB
    Cliente([Cliente])

    subgraph Microservicios
        U[usuarios-service 8081]
        D[desarrolladoras-service 8082]
        C[categorias-service 8083]
        J[juegos-service 8084]
        CA[carrito-service 8085]
        P[pagos-service 8086]
        B[biblioteca-service 8087]
        S[suscripciones-service 8088]
        L[logros-service 8089]
        R[resenas-service 8090]
    end

    Cliente --> U & D & C & J & CA & P & B & S & L & R

    U -.-> DBU[(usuarios_db)]
    D -.-> DBD[(desarrolladoras_db)]
    C -.-> DBC[(categorias_db)]
    J -.-> DBJ[(juegos_db)]
    CA -.-> DBCA[(carrito_db)]
    P -.-> DBP[(pagos_db)]
    B -.-> DBB[(biblioteca_db)]
    S -.-> DBS[(suscripciones_db)]
    L -.-> DBL[(logros_db)]
    R -.-> DBR[(resenas_db)]
```

## 2. Comunicacion entre servicios (Feign)

Las flechas son llamadas REST entre servicios usando OpenFeign.

```mermaid
flowchart LR
    J[juegos] --> D[desarrolladoras]
    J --> C[categorias]

    CA[carrito] --> U[usuarios]
    CA --> J

    B[biblioteca] --> U
    B --> J

    P[pagos] --> CA
    P --> U
    P --> B

    S[suscripciones] --> U

    L[logros] --> U
    L --> J

    R[resenas] --> U
    R --> J
    R --> B
```

## 3. Flujo de compra (pagos-service)

Un solo POST /api/pagos coordina cuatro servicios.

```mermaid
sequenceDiagram
    actor Cliente
    participant P as pagos
    participant CA as carrito
    participant U as usuarios
    participant B as biblioteca

    Cliente->>P: POST /api/pagos (usuarioId, metodoPago SALDO)
    P->>CA: GET carrito del usuario
    CA-->>P: carrito con total e items
    Note over P: revisa que no este vacio

    P->>U: PUT debitar saldo
    alt saldo insuficiente
        U-->>P: 409 Conflict
        P-->>Cliente: 409 saldo insuficiente
    else saldo ok
        U-->>P: 200 saldo descontado
        loop por cada juego
            P->>B: POST agregar a biblioteca
            B-->>P: 201 agregado
        end
        P->>CA: PUT marcar carrito como pagado
        CA-->>P: 200 pagado
        Note over P: guarda el pago como COMPLETADO
        P-->>Cliente: 201 pago completado
    end
```

## 4. Estructura interna de un microservicio (patron CSR)

Todos los servicios estan organizados igual:

```mermaid
flowchart TB
    REQ([Request]) --> CO[Controller]
    CO --> SE[Service]
    SE --> RE[Repository]
    SE -.-> FC[Feign Client]
    RE --> MO[(Base de datos)]
    RE --> RESP([Response JSON])
```

El controller recibe la peticion, el service tiene la logica de negocio, el
repository accede a la base de datos y el Feign Client se usa cuando hay que
pedir datos a otro microservicio. Ademas se usan DTOs para entrar y salir datos,
y un GlobalExceptionHandler para los errores.

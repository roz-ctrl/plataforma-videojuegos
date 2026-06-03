# Diagrama de Arquitectura — GameVault

Documento de apoyo para la **defensa técnica**. Contiene tres diagramas:
1. Arquitectura general de microservicios.
2. Mapa de comunicación (Feign) entre servicios.
3. Diagrama de secuencia del flujo de compra.

> Los diagramas están en **Mermaid**: GitHub los renderiza automáticamente.
> Para proyectarlos también puedes pegarlos en https://mermaid.live

---

## 1. Arquitectura general

Cada microservicio es **independiente**, con su **propia base de datos H2**
(patrón *database per service*) y su propio puerto. El cliente (Postman / front)
consume cada API REST por separado.

```mermaid
flowchart TB
    Cliente([Cliente / Postman])

    subgraph Plataforma GameVault - Microservicios
        U[usuarios-service<br/>:8081]
        D[desarrolladoras-service<br/>:8082]
        C[categorias-service<br/>:8083]
        J[juegos-service<br/>:8084]
        CA[carrito-service<br/>:8085]
        P[pagos-service<br/>:8086]
        B[biblioteca-service<br/>:8087]
        S[suscripciones-service<br/>:8088]
        L[logros-service<br/>:8089]
        R[resenas-service<br/>:8090]
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

---

## 2. Mapa de comunicación entre microservicios (Feign)

Las flechas representan **llamadas REST síncronas** vía OpenFeign. Nótese cómo
`usuarios`, `juegos` y `biblioteca` son los servicios más consumidos.

```mermaid
flowchart LR
    J[juegos-service] --> D[desarrolladoras-service]
    J --> C[categorias-service]

    CA[carrito-service] --> U[usuarios-service]
    CA --> J

    B[biblioteca-service] --> U
    B --> J

    P[pagos-service] --> CA
    P --> U
    P --> B

    S[suscripciones-service] --> U

    L[logros-service] --> U
    L --> J

    R[resenas-service] --> U
    R --> J
    R --> B

    classDef base fill:#e8f5e9,stroke:#2e7d32;
    classDef consumidor fill:#e3f2fd,stroke:#1565c0;
    class U,D,C base;
    class J,CA,P,B,S,L,R consumidor;
```

---

## 3. Flujo de compra (secuencia) — orquestado por `pagos-service`

Es el flujo más importante para demostrar la interoperabilidad: un solo
`POST /api/pagos` coordina **cuatro** microservicios.

```mermaid
sequenceDiagram
    actor Cliente
    participant P as pagos-service
    participant CA as carrito-service
    participant U as usuarios-service
    participant B as biblioteca-service

    Cliente->>P: POST /api/pagos { usuarioId, metodoPago: SALDO }
    P->>CA: GET /api/carrito/usuario/{id}
    CA-->>P: carrito + total + items
    Note over P: Valida que el carrito no este vacio

    P->>U: PUT /api/usuarios/{id}/debitar?monto=total
    alt Saldo insuficiente
        U-->>P: 409 Conflict
        P-->>Cliente: 409 "Saldo insuficiente"
    else Saldo OK
        U-->>P: 200 saldo descontado
        loop por cada juego del carrito
            P->>B: POST /api/biblioteca { usuarioId, juegoId }
            B-->>P: 201 juego agregado a la biblioteca
        end
        P->>CA: PUT /api/carrito/usuario/{id}/pagar
        CA-->>P: 200 carrito marcado PAGADO
        Note over P: Guarda el Pago (estado COMPLETADO)
        P-->>Cliente: 201 Pago COMPLETADO + detalle
    end
```

---

## 4. Estructura interna de cada microservicio (patrón CSR)

Todos los servicios siguen la misma organización por capas:

```mermaid
flowchart TB
    REQ([HTTP Request]) --> CO

    subgraph Microservicio
        CO[Controller<br/>recibe la peticion REST] --> SE[Service<br/>logica de negocio]
        SE --> RE[Repository<br/>JpaRepository]
        SE -. valida/enriquece .-> FC[Feign Client<br/>llamada remota]
        RE --> MO[(Entidad JPA / BD)]
        DTO[DTOs + Bean Validation]:::aux
        EX[GlobalExceptionHandler<br/>@RestControllerAdvice]:::aux
    end

    CO -. usa .-> DTO
    CO -. errores .-> EX
    RE --> RESP([JSON Response])

    classDef aux fill:#fff3e0,stroke:#e65100;
```

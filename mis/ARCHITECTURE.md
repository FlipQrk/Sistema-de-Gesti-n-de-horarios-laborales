# 📊 ARQUITECTURA - DOCKER + SPRING BOOT

## Diagrama de Componentes

```
┌─────────────────────────────────────────────────────────────────┐
│                    TU MÁQUINA LOCAL                             │
├─────────────────────────────────────────────────────────────────┤
│                                                                 │
│  ┌─────────────────────────────────────────────────────────┐   │
│  │              IDE (VS Code / IntelliJ)                   │   │
│  │  - Editar código                                        │   │
│  │  - Hot reload automático                               │   │
│  └─────────────┬───────────────────────────────────────────┘   │
│                │                                                 │
│       ┌────────▼────────┐                                       │
│       │ Maven (mvn)     │                                       │
│       │ Compila código  │                                       │
│       └────────┬────────┘                                       │
│                │                                                 │
│  ┌─────────────▼────────────────────────────────────────────┐   │
│  │      Spring Boot - Puerto 8080                           │   │
│  │  http://localhost:8080/api                              │   │
│  │      │                                                  │   │
│  │      ├─ REST Controllers                                │   │
│  │      ├─ Services (lógica de negocio)                    │   │
│  │      ├─ Repositories (JPA)                              │   │
│  │      └─ Entities (objetos del dominio)                  │   │
│  └──────────┬──────────────────────────────────────────────┘   │
│             │ JDBC/JPA Connection                               │
│             │ (localhost:5432)                                  │
│  ┌──────────▼──────────────────────────────────────────────┐   │
│  │          🐳 DOCKER  (Contenedores)                      │   │
│  │ ┌──────────────────────────────────────────────────┐   │   │
│  │ │  PostgreSQL 15 Container                        │   │   │
│  │ │  - puerto:5432                                  │   │   │
│  │ │  - BD: schedulingdb                             │   │   │
│  │ │  - user: postgres / pass: postgres123          │   │   │
│  │ │  - volumen: postgres_data (persistente)        │   │   │
│  │ └──────────────────────────────────────────────────┘   │   │
│  │                                                       │   │
│  │ ┌──────────────────────────────────────────────────┐   │   │
│  │ │  pgAdmin 4 Container (Interfaz gráfica)         │   │   │
│  │ │  http://localhost:5050                          │   │   │
│  │ │  - admin@schedulingdb.com / admin123            │   │   │
│  │ └──────────────────────────────────────────────────┘   │   │
│  └──────────────────────────────────────────────────────────┘   │
│             ▲                                                    │
│             └─ docker-compose up -d                             │
│                                                                 │
└─────────────────────────────────────────────────────────────────┘
```

---

## Flujo de Desarrollo Típico

### 1️⃣ SETUP (Una sola vez)

```bash
# Clonar proyecto
git clone <url>
cd mis/

# Setup automático (Docker + Maven)
bash setup.sh
# O manual:
docker-compose up -d
mvn clean install
```

**Estado después:**
- ✅ PostgreSQL corriendo en Docker
- ✅ pgAdmin disponible
- ✅ Dependencias Maven descargadas
- ✅ Base de datos lista

---

### 2️⃣ DESARROLLO DIARIO

**Terminal 1: Aplicación**
```bash
mvn spring-boot:run
# Escucha cambios automáticamente
# Hot reload en segundos
```

**Terminal 2: Logs de BD (opcional)**
```bash
docker-compose logs -f postgres
```

**Terminal 3: Otras operaciones**
```bash
# Ver swagger
https://localhost:8080/api/swagger-ui.html

# Conectar a BD
docker exec -it scheduling_postgres psql -U postgres

# O usar pgAdmin
http://localhost:5050
```

---

### 3️⃣ PAUSAR (Sin perder datos)

```bash
# Detener Docker
docker-compose stop

# Los datos persisten en volúmenes
# Continuarás mañana desde donde dejaste
```

**Luego para reanudar:**
```bash
docker-compose start
mvn spring-boot:run
```

---

### 4️⃣ LIMPIAR (Borrar todo)

```bash
# Eliminar contenedores pero GUARDAR datos
docker-compose down

# Eliminar TODO incluyendo volúmenes
docker-compose down -v
```

---

## Ventajas de Esta Arquitectura

| Aspecto | Ventaja |
|--------|---------|
| **Desarrollo** | Hot reload en local, sin reinicios de BD |
| **Portabilidad** | BD igual en tu PC, QA, producción |
| **Aislamiento** | BD en contenedor, no interfiere con sistema |
| **Reversibilidad** | `docker-compose down -v` limpia todo |
| **Escalabilidad** | Fácil agregar más servicios (Redis, Kafka, etc.) |
| **Consistencia** | Todos los devs usan exactamente el mismo ambiente |

---

## Flujo de Requests HTTP

```
Usuario/Cliente
      │
      │ HTTP GET /api/employees
      │
      ▼
┌──────────────────────────┐
│   Spring Boot (8080)     │
├──────────────────────────┤
│ 1. EmployeeController    │
│    (recibe request)      │
│          ↓               │
│ 2. EmployeeService      │
│    (lógica negocio)      │
│          ↓               │
│ 3. EmployeeRepository    │
│    (consulta BD)         │
│          ↓               │
└──────────────────────────┘
      │ JDBC/JPA
      │ SELECT * FROM employees
      │
      ▼
┌──────────────────────────┐
│  PostgreSQL (Docker)     │
│   (datos persistentes)   │
└──────────────────────────┘
      │
      │ Resultados
      ▼
Spring Boot
      │ JSON
      │
      ▼
Cliente Web
```

---

## Rutas de Datos

### Crear Empleado (POST)
```
Cliente → Spring Boot → Service → Repository → PostgreSQL (volumen persistente)
                                               ↓
                                              Datos guardados
```

### Leer Empleados (GET)
```
Cliente → Spring Boot → Service → Repository → PostgreSQL (desde volumen)
                                               ↓
                                              Returned to Client
```

---

## Configuración por Capas

```
┌─────────────────────────────────────────────┐
│  MVC Controllers (REST Endpoints)           │ ← /api/employees
├─────────────────────────────────────────────┤
│  Service Layer (Lógica de Negocio)          │ ← Reglas de negocio
├─────────────────────────────────────────────┤
│  Repository Layer (JPA/Hibernate)           │ ← Queries a BD
├─────────────────────────────────────────────┤
│  Entity Layer (JPA Mapped Objects)          │ ← Tablas BD
├─────────────────────────────────────────────┤
│  Database (PostgreSQL en Docker)            │ ← Datos persistentes
└─────────────────────────────────────────────┘
```

---

## Datos Persistentes

### Volúmenes Docker
```
┌──────────────────────────────┐
│   postgres_data (volumen)    │
│                              │
│  ├─ 16384/        ← archivos BD
│  │  ├─ base/                │
│  │  ├─ pg_WAL/              │
│  │  └─ pg_tblspc/           │
│  │                           │
│  └─ No se elimina con:      │
│     docker-compose down     │
│                              │
│  Se elimina solo con:        │
│  docker-compose down -v     │
└──────────────────────────────┘
```

### Dónde están los datos
```bash
# Linux/Mac
$HOME/.docker/volumes/postgres_data/

# Windows (WSL)
\\wsl$\docker-desktop\mnt\wsl
```

---

## Próxima Arquitectura (Cuando agregues más)

```
┌────────────────────┐
│  Nginx (Reverse    │ ← Frontend + Proxy
│  Proxy)            │
└────────┬───────────┘
         │
    ┌────▼────────────┐
    │ Spring Boot      │ ← Backend API
    │ (puerto 8080)    │
    └────┬─────────────┘
         │
    ┌────▼────────────┐
    │ PostgreSQL      │ ← Datos
    └─────────────────┘
         │
    ┌────▼────────────┐
    │ Redis           │ ← Cache (futuro)
    └─────────────────┘
         │
    ┌────▼────────────┐
    │ Elasticsearch   │ ← Búsquedas (futuro)
    └─────────────────┘
```

---

## Seguridad en Capas

```
Public Internet
      │
      ├─ ❌ No acceso directo a BD (PostgreSQL)
      │
      ├─ ✅ Solo acceso a API (REST)
      │
Spring Boot (Validación JPA + Spring Security)
      │
      ├─ Validaciones @NotNull, @Email, etc.
      ├─ Autenticación JWT
      ├─ Autorización por roles
      │
PostgreSQL (Validaciones BD)
      │
      ├─ Constraints (UNIQUE, NOT NULL)
      ├─ Índices para performance
      │
Volúmenes Docker (Datos encriptados en BD)
```

---

**¿Listo para empezar a codificar? 🚀**

Próximo paso: Crear Repositorios JPA

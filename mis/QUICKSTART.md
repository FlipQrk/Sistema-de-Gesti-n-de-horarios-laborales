# ⚡ QUICK START - DOCKER SETUP

## Opción 1: Setup Automático (RECOMENDADO)

```bash
cd mis/
bash setup.sh
```

✅ Esto hace:
- Verifica Docker, Java, Maven
- Inicia PostgreSQL en Docker
- Descarga dependencias Maven
- Muestra información de conexión

## Opción 2: Setup Manual

```bash
# 1. Iniciar BD
docker-compose up -d

# 2. Compilar
mvn clean install

# 3. Ejecutar
mvn spring-boot:run
```

## Resultados Esperados

### Docker Servicios Corriendo
```
NAME                 STATUS
scheduling_postgres RUNNING (healthy)
scheduling_pgadmin  RUNNING
```

### Acceso

| Componente | URL | Usuario | Contraseña |
|-----------|-----|---------|-----------|
| **API** | http://localhost:8080/api | - | - |
| **Swagger** | http://localhost:8080/api/swagger-ui.html | - | - |
| **pgAdmin** | http://localhost:5050 | admin@schedulingdb.com | admin123 |
| **PostgreSQL** | localhost:5432 | postgres | postgres123 |

## 🔍 Verificar Funcionamiento

### 1. Revisar logs de Spring Boot
```
Buscar línea: "Started MisApplication in X seconds"
```

### 2. Acceder a Swagger
```
http://localhost:8080/api/swagger-ui.html
```
Debería mostrar endpoints disponibles (vacíos por ahora)

### 3. Conectar a BD con pgAdmin
```
http://localhost:5050
Email: admin@schedulingdb.com
Password: admin123

Luego:
- Servers → Register → Server
- Name: LocalPostgreSQL
- Host: postgres (importante!)
- Port: 5432
- User: postgres
- Pass: postgres123
- Database: schedulingdb
```

### 4. Verificar directamente con SQL
```bash
docker exec -it scheduling_postgres psql \
  -U postgres \
  -d schedulingdb \
  -c "SELECT * FROM information_schema.tables;"
```

## ⚠️ Problemas Comunes

| Error | Solución |
|-------|----------|
| "Port 5432 already in use" | `docker-compose down -v` y reintenta |
| "Cannot connect to Docker daemon" | Abre Docker Desktop |
| "psql: FATAL - password authentication failed" | Verifica credenciales (postgres123) |
| No ve tablas en pgAdmin | Espera 30 segundos después de `docker-compose up` |

## 🎯 Próximos Pasos

```
✅ Setup completado
  ↓
  Crear Repositorios JPA
  ↓
  Crear DTOs
  ↓
  Crear Servicios
  ↓
  Crear Controladores REST
  ↓
  API funcional
```

## 📚 Documentación Relacionada

- **DOCKER.md** - Guía completa de Docker
- **CONFIGURATION.md** - Perfiles y configuración
- **README.md** - Descripción general del proyecto

---

**¿Todo listo? Ejecuta:** `mvn spring-boot:run` en una nueva terminal

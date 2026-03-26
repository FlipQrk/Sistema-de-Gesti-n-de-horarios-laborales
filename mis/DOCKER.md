# 🐳 GUÍA DOCKER - SISTEMA DE HORARIOS LABORALES

## Descripción General

Este proyecto usa Docker para ejecutar **PostgreSQL**, permitiendo desarrollo consistente sin instalar bases de datos localmente.

```
┌─────────────────────────────────────────┐
│      Docker Desktop / Docker Engine      │
├─────────────────────────────────────────┤
│  Container PostgreSQL 15                │
│  - Port: 5432                           │
│  - User: postgres                       │
│  - Password: postgres123                │
│  - Database: schedulingdb               │
├─────────────────────────────────────────┤
│  Container pgAdmin 4 (Opcional)         │
│  - URL: http://localhost:5050           │
│  - User: admin@schedulingdb.com         │
│  - Password: admin123                   │
└─────────────────────────────────────────┘
        ↕️ Conexión desde Spring Boot
    ┌──────────────────────────────────┐
    │ Spring Boot en tu máquina local   │
    │ http://localhost:8080/api        │
    └──────────────────────────────────┘
```

---

## 📋 Requisitos Previos

### Docker Desktop (Recomendado)
- **Windows/Mac**: Descargar desde https://www.docker.com/products/docker-desktop
- **Linux**: Instalar docker y docker-compose
  ```bash
  sudo apt-get install docker.io docker-compose
  sudo usermod -aG docker $USER  # Ejecutar docker sin sudo
  ```

### Verificar Instalación
```bash
docker --version
docker-compose --version
```

---

## 🚀 Inicio Rápido

### Paso 1: Iniciar PostgreSQL
```bash
cd mis/
docker-compose up -d
```

✅ PostgreSQL está corriendo en `localhost:5432`

### Paso 2: Verificar que están activos
```bash
docker-compose ps

# Esperado:
# NAME                  STATUS
# scheduling_postgres   Up (healthy)
# scheduling_pgadmin    Up
```

### Paso 3: Ejecutar la aplicación
```bash
# En otra terminal
mvn spring-boot:run
```

✅ Aplicación conectada automáticamente a PostgreSQL en Docker

### Paso 4: Detener servicios
```bash
docker-compose down
```

---

## 🛠️ Comandos Útiles

### Ver logs en tiempo real
```bash
docker-compose logs -f postgres
docker-compose logs -f pgadmin
docker-compose logs -f  # Todos
```

### Conectar directamente a PostgreSQL
```bash
# Usando psql (si está instalado localmente)
psql -h localhost -U postgres -d schedulingdb
# Password: postgres123

# O usando Docker
docker exec -it scheduling_postgres psql -U postgres -d schedulingdb
```

### Reiniciar servicios
```bash
docker-compose restart
```

### Limpiar volúmenes (CUIDADO: borra datos)
```bash
docker-compose down -v
```

---

## 🖥️ pgAdmin (Interfaz Gráfica)

### Acceso
```
URL: http://localhost:5050
Email: admin@schedulingdb.com
Password: admin123
```

### Agregar servidor (primera vez)

1. Click en "Servers" → "Register" → "Server"
2. Tab "General":
   - Name: `LocalPostgreSQL`
3. Tab "Connection":
   - Host: `postgres` (nombre del contenedor, no localhost)
   - Port: `5432`
   - Username: `postgres`
   - Password: `postgres123`
   - Database: `schedulingdb`
4. Click "Save"

✅ Ahora puedes administrar la BD gráficamente

---

## 🔧 Personalizar Configuración

### Cambiar contraseña de PostgreSQL
Editar `docker-compose.yml`:
```yaml
environment:
  POSTGRES_PASSWORD: tu_nueva_contraseña
```
Luego recrear:
```bash
docker-compose down -v
docker-compose up -d
```

### Cambiar puerto de PostgreSQL
Editar `docker-compose.yml`:
```yaml
ports:
  - "5433:5432"  # Escucha en puerto 5433 localmente
```
Y actualizar `application.properties`:
```properties
spring.datasource.url=jdbc:postgresql://localhost:5433/schedulingdb
```

### Cambiar puerto de pgAdmin
Editar `docker-compose.yml`:
```yaml
ports:
  - "5051:80"  # Acceder a http://localhost:5051
```

---

## 🐛 Troubleshooting

### Error: "port 5432 is already allocated"
Otro servicio usa el puerto 5432:
```bash
# Opción 1: Cambiar puerto en docker-compose.yml
# Opción 2: Matar proceso en puerto 5432
# Linux/Mac:
lsof -i :5432
kill -9 <PID>

# Windows:
netstat -ano | findstr :5432
taskkill /PID <PID> /F
```

### Error: "Cannot connect to Docker daemon"
Docker no está corriendo:
```bash
# Windows/Mac: Abre Docker Desktop
# Linux: Inicia el servicio
sudo systemctl start docker
```

### Error: "psql: unable to connect"
PostgreSQL aún se está iniciando:
```bash
# Espera 20 segundos y reintenta
# O verifica logs:
docker-compose logs postgres
```

### Error: "role postgres does not exist"
Reinicializar base de datos:
```bash
docker-compose down -v
docker-compose up -d
# Esperar ~30 segundos
```

---

## 🌍 Conexión desde aplicación

### Configuración Automática
La aplicación usa estas variables en `application.properties`:
```properties
spring.datasource.url=jdbc:postgresql://localhost:5432/schedulingdb
spring.datasource.username=postgres
spring.datasource.password=postgres123
```

### Override con Variables de Entorno
```bash
export DB_URL=jdbc:postgresql://host:5432/db
export DB_USERNAME=user
export DB_PASSWORD=pass

java -jar app.jar
```

---

## 📊 Monitorear Base de Datos

### Con pgAdmin (GUI)
```
http://localhost:5050 → Ver queries, estructuras, datos
```

### Con SQL directo
```bash
# Conectar
docker exec -it scheduling_postgres psql -U postgres -d schedulingdb

# Comandos útiles
\dt                    # Listar tablas
\d table_name         # Describir tabla
SELECT * FROM table;  # Ver datos
\q                    # Salir
```

### Ver logs de PostgreSQL
```bash
docker-compose logs postgres
```

---

## 🔒 Seguridad (IMPORTANTE para Producción)

### NO usar credenciales hardcoding en Producción
En `docker-compose.yml` usa:
```yaml
environment:
  POSTGRES_PASSWORD: ${DB_PASSWORD}  # Variable de entorno
```

### Usar secrets de Docker
Para producción con Swarm/Kubernetes:
```yaml
environment:
  POSTGRES_PASSWORD_FILE: /run/secrets/db_password
secrets:
  db_password:
    external: true
```

### Cambiar contraseña por defecto
```bash
# Conectar a BD
docker exec -it scheduling_postgres psql -U postgres

# Cambiar contraseña del usuario postgres
ALTER USER postgres WITH PASSWORD 'nueva_contraseña_segura';
\q
```

---

## 🔄 Ciclo de Vida del Desarrollo

```
1. Iniciar Docker
   docker-compose up -d

2. Verificar que PostgreSQL esté listo (healthy)
   docker-compose ps

3. Ejecutar aplicación Spring Boot
   mvn spring-boot:run

4. Desarrollar normalmente

5. Detener todo (los datos persisten)
   docker-compose down

6. Siguiente día: reiniciar
   docker-compose up -d
   mvn spring-boot:run
```

---

## 📚 Archivos Relevantes

| Archivo | Propósito |
|---------|-----------|
| `docker-compose.yml` | Define servicios PostgreSQL y pgAdmin |
| `init-db.sql` | Script inicial de BD (ejecuta al crear contenedor) |
| `.dockerignore` | Archivos a ignorar en imágenes Docker |
| `application.properties` | Configuración de conexión (PostgreSQL) |

---

## 🎓 Próximos Pasos

Una vez PostgreSQL está corriendo:

1. **Verificar conexión**
   ```bash
   mvn spring-boot:run
   # Observar logs: "Hibernate: create table departments..."
   ```

2. **Crear Repositorios JPA**
   - Queries eficientes a la BD

3. **Crear DTOs y Servicios**
   - Lógica de negocio

4. **Crear Controladores REST**
   - Endpoints de la API

---

## 💡 Pro Tips

### Backup de BD
```bash
docker exec scheduling_postgres pg_dump -U postgres schedulingdb > backup.sql
```

### Restaurar desde backup
```bash
docker exec -i scheduling_postgres psql -U postgres schedulingdb < backup.sql
```

### Ejecutar SQL personalizado
```bash
docker exec scheduling_postgres psql -U postgres -d schedulingdb -c "SELECT * FROM employees;"
```

### Detener sin eliminar datos
```bash
docker-compose stop  # Pausar contenedores, datos persisten
docker-compose start # Reanudar
```

---

## ❓ Preguntas Frecuentes

**P: ¿Los datos persisten si hago `docker-compose down`?**
R: Sí, los volúmenes persisten los datos. Solo se pierden con `docker-compose down -v`.

**P: ¿Puedo acceder a PostgreSQL desde otra máquina en la red?**
R: Cambiar en docker-compose.yml: `- "0.0.0.0:5432:5432"` (cuidado con seguridad).

**P: ¿Cómo uso PostgreSQL remoto en lugar de Docker?**
R: Cambiar en `application.properties`:
```properties
spring.datasource.url=jdbc:postgresql://servidor-remoto:5432/db
```

**P: ¿Puedo usar MariaDB/MySQL en lugar de PostgreSQL?**
R: Sí, cambiar `docker-compose.yml` y dialect en properties.

---

## 📞 Soporte

Si Docker no funciona:
1. Verificar instalación: `docker --version`
2. Ver logs: `docker-compose logs`
3. Revisar puertos disponibles: `docker-compose ps`
4. Buscar documentación: https://docs.docker.com/

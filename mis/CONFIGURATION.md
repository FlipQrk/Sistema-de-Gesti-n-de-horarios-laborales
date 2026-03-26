# 📝 DOCUMENTACIÓN DE CONFIGURACIÓN - SISTEMA DE HORARIOS LABORALES

## Descripción General

El sistema está configurado con **3 perfiles** que coexisten:
- **application.properties** → Desarrollo (default)
- **application-production.properties** → Producción
- **application-test.properties** → Testing

---

## 🔧 CONFIGURACIÓN ACTUALIZADA

### Base de Datos

#### Desarrollo (H2)
```properties
spring.datasource.url=jdbc:h2:mem:testdb
```
- **Ventaja**: No necesita instalación, rápido para testing
- **Acceso**: http://localhost:8080/api/h2-console
- **User**: sa
- **Password**: (vacío)

#### Producción (PostgreSQL)
```properties
spring.datasource.url=jdbc:postgresql://localhost:5432/schedulingdb
```
- **Requiere**: PostgreSQL instalado
- **Variables de entorno**:
  - `DB_URL` → jdbc:postgresql://host:port/database
  - `DB_USERNAME` → usuario
  - `DB_PASSWORD` → contraseña
  - `JWT_SECRET` → clave JWT (mínimo 32 caracteres)

---

### JPA / Hibernate

#### Estrategia DDL

| Perfil | Valor | Comportamiento |
|--------|-------|---|
| Desarrollo | `create-drop` | Crea tablas al iniciar, las elimina al cerrar |
| Producción | `validate` | Solo valida la estructura, sin cambios |
| Testing | `create-drop` | Crea tablas limpias para cada test |

#### Configuración

```properties
# Mostrar SQL procesado (útil para debugging)
spring.jpa.show-sql=true
spring.jpa.properties.hibernate.format_sql=true

# Batch processing para mejor rendimiento
spring.jpa.properties.hibernate.jdbc.batch_size=10
spring.jpa.properties.hibernate.order_inserts=true
```

---

### 🔐 Seguridad - JWT

#### Configuración Actual

```properties
app.jwt.secret=mi_clave_secreta_super_segura_para_jwt_desarrollo_cambiar_en_produccion
app.jwt.expiration=86400000  # 24 horas en milisegundos
```

#### Para Producción

```bash
# Generar clave segura (Linux/Mac)
openssl rand -hex 32

# Luego usar como variable de entorno
export JWT_SECRET=<clave_generada>
java -jar app.jar --spring.profiles.active=production
```

---

### 📊 Logging

#### Niveles por Perfil

| Perfil | Nivel | Ubicación |
|--------|-------|-----------|
| Desarrollo | DEBUG | Console + `logs/application.log` |
| Producción | INFO/WARN | `/var/log/scheduling-system/application.log` |
| Testing | DEBUG | Console only |

#### Parciales por Paquete

**Desarrollo (application.properties):**
```properties
logging.level.com.mis.mis=DEBUG                     # Mi código
logging.level.org.springframework.web=DEBUG         # Spring Web
logging.level.org.springframework.security=DEBUG    # Spring Security
logging.level.org.hibernate.SQL=DEBUG              # Queries SQL
```

---

### 🔗 Swagger / OpenAPI

#### Acceso

```
URL: http://localhost:8080/api/swagger-ui.html
Docs: http://localhost:8080/api/v3/api-docs
```

#### Estados

- **Desarrollo**: ✅ Habilitado
- **Producción**: ❌ Deshabilitado (seguridad)
- **Testing**: ❌ Deshabilitado

---

## 🚀 Cómo Usar

### Opción 1: Ejecutar en Desarrollo (Recomendado para aprender)

```bash
cd mis/
mvn spring-boot:run
```

Acceder a:
- API: http://localhost:8080/api
- Swagger: http://localhost:8080/api/swagger-ui.html
- H2 Console: http://localhost:8080/api/h2-console

---

### Opción 2: Ejecutar en Producción

**Prerequisito**: PostgreSQL instalado y configurado

```bash
# 1. Crear base de datos
sudo -u postgres createdb schedulingdb

# 2. Compilar (Maven)
cd mis/
mvn clean package -DskipTests

# 3. Ejecutar
export DB_URL=jdbc:postgresql://localhost:5432/schedulingdb
export DB_USERNAME=postgres
export DB_PASSWORD=tu_contraseña
export JWT_SECRET=$(openssl rand -hex 32)

java -jar target/mis-0.0.1-SNAPSHOT.jar \
  --spring.profiles.active=production
```

---

### Opción 3: Testing

```bash
# Correr todos los tests
mvn test

# Correr un test específico
mvn test -Dtest=EmployeeServiceTest
```

El perfil **test** se activa automáticamente.

---

## 🎯 Validaciones Personalizadas

```properties
# Minutos de tolerancia para retrasos
app.lateness.tolerance.minutes=5

# Máximo de horas por día (para detectar errores)
app.max.work.hours.per.day=12
```

---

## 📋 Checklist de Configuración

- ✅ H2 Console habilitada en desarrollo
- ✅ JPA DDL configurado por perfil
- ✅ Logging con niveles apropiados
- ✅ JWT configurado
- ✅ Swagger habilitado en desarrollo
- ✅ Connection pool optimizado
- ✅ Batch processing configurado
- ✅ Validaciones de negocio

---

## 🔄 Siguientes Pasos

1. **Repositorios JPA** - Crear queries en Repository/
2. **DTOs** - Mapeo de entidades en DTO/
3. **Servicios** - Lógica de negocio en Services/
4. **Controladores** - Endpoints REST en Controller/
5. **Excepciones** - Manejo centralizado en Exception/

---

## 📞 Notas Importantes

### Para Desarrollo
- La BD se recrea cada vez que inicia (create-drop)
- SQL queries visibles en logs (para debugging)
- Swagger accesible para probar endpoints

### Para Producción
- NO cambiar `ddl-auto` a `update` sin backups
- Usar SIEMPRE variables de entorno para credenciales
- Cambiar `JWT_SECRET` a valor seguro
- Configurar HTTPS
- Monitorear `/var/log/scheduling-system/`

### Para Testing
- BD en memoria (muy rápido)
- Aislada de otras instancias
- Limpia automáticamente entre tests

# 📅 SISTEMA DE GESTIÓN DE HORARIOS LABORALES

[![Java](https://img.shields.io/badge/Java-21-orange.svg)](https://www.oracle.com/java/)
[![Spring Boot](https://img.shields.io/badge/Spring%20Boot-4.0.4-brightgreen.svg)](https://spring.io/projects/spring-boot)
[![PostgreSQL](https://img.shields.io/badge/PostgreSQL-Ready-blue.svg)](https://www.postgresql.org/)
[![License](https://img.shields.io/badge/License-MIT-yellow.svg)](LICENSE)

Sistema profesional para la gestión de horarios laborales, registros de asistencia, turnos y análisis de jornadas de trabajo.

---

## 🎯 Descripción del Proyecto

Un aplicativo Spring Boot que permite:

✅ **Gestión de Empleados** - Datos personales y laborales  
✅ **Gestión de Turnos** - Definición flexible de horarios  
✅ **Asignación de Horarios** - Asignar turnos a empleados por períodos  
✅ **Registro de Asistencia** - Control de entrada/salida diaria  
✅ **Cálculo de Horas** - Automático con detección de extras  
✅ **Reportes Analíticos** - Asistencia, horas, departamentos  
✅ **Auditoría Completa** - Rastreo de todos los cambios  
✅ **Seguridad JWT** - Autenticación y autorización  

---

## 🛠️ Stack Tecnológico

- **Backend**: Spring Boot 4.0.4
- **Lenguaje**: Java 21
- **BD (Dev)**: H2 (embebida)
- **BD (Prod)**: PostgreSQL
- **ORM**: Hibernate JPA
- **Seguridad**: Spring Security + JWT
- **Validación**: Jakarta Validation
- **Mapeo DTOs**: MapStruct
- **Documentación**: Swagger/OpenAPI 3.0
- **Build**: Maven
- **Logging**: SLF4J + Logback

---

## 📋 Requisitos Previos

### Mínimos (para Desarrollo)
- Java 21 JDK
- Maven 3.8+
- Git

### Para Producción
- Java 21 JDK
- PostgreSQL 12+
- Maven 3.8+

---

## ⚡ Inicio Rápido

### 1️⃣ Clonar y Preparar

```bash
# Clonar el repositorio
git clone <url-repo>
cd Manegement_System/mis

# Compilar dependencias
mvn clean install
```

### 2️⃣ Ejecutar en Desarrollo

```bash
# Opción A: Con Maven (incluye hot reload)
mvn spring-boot:run

# Opción B: Compilar y ejecutar JAR
mvn clean package -DskipTests
java -jar target/mis-0.0.1-SNAPSHOT.jar
```

### 3️⃣ Acceder

```
🌐 API:        http://localhost:8080/api
📚 Swagger:    http://localhost:8080/api/swagger-ui.html
🗄️  H2 Console: http://localhost:8080/api/h2-console (User: sa, Pass: vacío)
```

---

## 📁 Estructura del Proyecto

```
mis/
├── src/
│   ├── main/
│   │   ├── java/com/mis/mis/
│   │   │   ├── Entity/           # Entidades JPA (7 clases)
│   │   │   ├── Repository/       # JpaRepository interfaces
│   │   │   ├── Services/         # Lógica de negocio
│   │   │   ├── Controller/       # REST Controllers
│   │   │   ├── DTO/             # Data Transfer Objects
│   │   │   ├── Exception/       # Excepciones personalizadas
│   │   │   ├── Config/          # Configuraciones Spring
│   │   │   ├── Responses/       # Respuestas estándar
│   │   │   └── MisApplication.java
│   │   └── resources/
│   │       ├── application.properties           # Dev
│   │       ├── application-production.properties # Prod
│   │       └── application-test.properties      # Test
│   └── test/
│       └── java/
├── CONFIGURATION.md              # Guía detallada de configuración
├── RUN.sh                       # Scripts de ejecución
└── pom.xml                      # Dependencias Maven
```

---

## 🏗️ Arquitectura de Entidades

```
┌──────────────────────────────────────────────────┐
│                   DEPARTMENT                      │
│  (Departamentos)                                  │
└────────────────┬─────────────────────────────────┘
                 │ 1:*
                 ├──────────────────────┐
                 │                      │
        ┌────────▼────────┐      SHIFT  │
        │    EMPLOYEE     │    (Turnos) │
        │  (Empleados)    │             │
        ├────────┬────────┤      /      │
        │ 1:*    │ 1:*    │     /       │
        │        │        │    /        │
  ┌─────▼──┐   ┌─┴──────┐  ◄──────────┘
  │ TIME   │   │WORK    │
  │ENTRY  │   │SCHEDULE│
  │(Reg.) │   │(Asign.)│
  └────────┘   └────────┘

  ┌──────────────────────┐
  │ SCHEDULE_TEMPLATE    │
  │ (Plantillas M:N)     │ ◄─── SHIFT
  └──────────────────────┘

  ┌──────────────────────────┐
  │     AUDIT_LOG            │
  │  (Rastreo de cambios)    │
  └──────────────────────────┘
```

---

## 🔐 Seguridad

### Autenticación JWT
- Token de 24 horas (configurable)
- Renovación automática
- Almacenamiento seguro

### Roles
- `ROLE_ADMIN` - Administrador del sistema
- `ROLE_MANAGER` - Gerente de recursos humanos
- `ROLE_EMPLOYEE` - Empleado

### Validación
- Datos con Jakarta Validation
- Restricciones a nivel BD
- Índices para performance

---

## 📊 Base de Datos

### Desarrollo (H2)
- Sin instalación requerida
- En memoria o archivo local
- Perfecto para testing

### Producción (PostgreSQL)
- Configuración en `application-production.properties`
- Variables de entorno para credenciales
- Backups automáticos recomendados

---

## 📖 Documentación

| Archivo | Contenido |
|---------|-----------|
| [CONFIGURATION.md](CONFIGURATION.md) | Guía detallada de configuración por perfil |
| [RUN.sh](RUN.sh) | Scripts de ejecución (copy-paste ready) |
| Código fuente | Comentarios extensos en cada clase |

---

## 🧪 Testing

```bash
# Ejecutar todos los tests
mvn test

# Test específico
mvn test -Dtest=EmployeeServiceTest

# Con cobertura
mvn test jacoco:report
# Reporte: target/site/jacoco/index.html
```

---

## 🔄 Próximos Pasos

Las siguientes capas están listadas para implementación:

- [ ] **Repository Layer** - JPA queries personalizadas
- [ ] **DTO Layer** - Mapeo de entidades
- [ ] **Service Layer** - Lógica de negocio
- [ ] **Controller Layer** - REST endpoints
- [ ] **Exception Handling** - Excepciones personalizadas
- [ ] **Unit Tests** - Cobertura de servicios
- [ ] **Integration Tests** - Pruebas end-to-end

---

## 💡 Configuraciones por Perfil

```bash
# Desarrollo (default)
mvn spring-boot:run

# Producción
java -jar app.jar --spring.profiles.active=production

# Testing (automático en @SpringBootTest)
mvn test
```

---

## 📝 Variables de Entorno (Producción)

```bash
DB_URL=jdbc:postgresql://localhost:5432/schedulingdb
DB_USERNAME=postgres
DB_PASSWORD=tu_contraseña
JWT_SECRET=<clave_generada_con_openssl>
SPRING_PROFILES_ACTIVE=production
```

Generar JWT_SECRET:
```bash
openssl rand -hex 32
```

---

## 🐛 Troubleshooting

### Error: "Port 8080 already in use"
```bash
java -jar app.jar --server.port=8081
```

### Error: "No datasource configured"
Verificar `application.properties` - asegurar que está en `src/main/resources/`

### Error: "JWT secret not configured"
En producción, establece: `export JWT_SECRET=<valor>`

---

## 📞 Contacto & Soporte

Para dudas sobre la arquitectura o configuración, revisar:
- Los comentarios en el código fuente
- [CONFIGURATION.md](CONFIGURATION.md)
- [RUN.sh](RUN.sh)

---

## 📄 Licencia

MIT License - Ver [LICENSE](LICENSE) para detalles

---

## 🎓 Año de Desarrollo

2026 - Sistema de Gestión de Horarios Laborales v1.0

# ====================================================================
# DOCKERFILE - Spring Boot Application
# ====================================================================
# Construye una imagen Docker de la aplicación Spring Boot
# 
# Uso:
#   docker build -t scheduling-app:1.0 .
#   docker run -p 8080:8080 \
#     -e DB_URL=jdbc:postgresql://postgres:5432/schedulingdb \
#     -e DB_USERNAME=postgres \
#     -e DB_PASSWORD=postgres123 \
#     scheduling-app:1.0
#
# O usar con docker-compose extendido
# ====================================================================

# Stage 1: BUILD
# Compilar la aplicación con Maven en un contenedor build
FROM maven:3.9.0-eclipse-temurin-21-alpine AS builder

WORKDIR /app

# Copiar pom.xml y descargar dependencias
# (esto se cachea si no cambia pom.xml)
COPY pom.xml .
RUN mvn dependency:go-offline

# Copiar código fuente
COPY src ./src

# Compilar aplicación
RUN mvn clean package -DskipTests

# ====================================================================

# Stage 2: RUNTIME
# Imagen pequeña solo con la app y JVM
FROM eclipse-temurin:21-jdk-alpine

WORKDIR /app

# Copiar JAR desde stage anterior
COPY --from=builder /app/target/*.jar app.jar

# Usuario no-root por seguridad
RUN addgroup -g 1001 appuser && \
    adduser -D -u 1001 -G appuser appuser
USER appuser

# Puerto donde escucha la app
EXPOSE 8080

# Health check
HEALTHCHECK --interval=30s --timeout=10s --start-period=40s --retries=3 \
  CMD java -cp app.jar -Dloader.main=com.mis.mis.MisApplication \
    org.springframework.boot.loader.PropertiesLauncher || exit 1

# Comando para ejecutar
ENTRYPOINT ["java", "-jar", "app.jar"]

# ====================================================================
# NOTAS:
# ====================================================================
# 1. Multi-stage: Reduce tamaño de imagen (builder no se incluye)
# 2. Alpine: Imagen de Linux ultra pequeña (~5MB base)
# 3. Non-root user: Mejor seguridad
# 4. Health check: Docker puede reiniciar si app falla
# 5. Env variables: Se pasan al ejecutar contenedor
# ====================================================================

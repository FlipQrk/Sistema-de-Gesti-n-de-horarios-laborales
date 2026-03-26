#!/bin/bash

# ====================================================================
# SCRIPTS DE EJECUCIÓN - SISTEMA DE HORARIOS LABORALES
# ====================================================================
# Nota: Este archivo es de referencia. Personalizar según tu sistema.
# ====================================================================

# ====================================================================
# 1. DESARROLLO - Con Maven (más común)
# ====================================================================
echo \"=== EJECUTAR EN DESARROLLO CON MAVEN ===\"
echo \"Comando:\"
echo \"cd mis/ && mvn spring-boot:run\"
echo \"\"
echo \"Luego acceder a:\"
echo \"  - API:        http://localhost:8080/api\"
echo \"  - Swagger:    http://localhost:8080/api/swagger-ui.html\"
echo \"  - H2 Console: http://localhost:8080/api/h2-console\"
echo \"\"

# ====================================================================
# 2. DESARROLLO - Compilar y ejecutar JAR
# ====================================================================
echo \"=== COMPILAR A JAR ===\"
echo \"Comando:\"
echo \"mvn clean package -DskipTests\"
echo \"\"
echo \"Luego ejecutar:\"
echo \"java -jar target/mis-0.0.1-SNAPSHOT.jar\"
echo \"\"

# ====================================================================
# 3. PRODUCCIÓN - Con PostgreSQL
# ====================================================================
echo \"=== EJECUTAR EN PRODUCCIÓN ===\"
echo \"\"
echo \"Paso 1: Crear base de datos (si no existe)\"
echo \"  sudo -u postgres createdb schedulingdb\"
echo \"\"
echo \"Paso 2: Compilar\"
echo \"  mvn clean package -DskipTests\"
echo \"\"
echo \"Paso 3: Generar clave JWT segura\"
echo \"  openssl rand -hex 32\"
echo \"\"
echo \"Paso 4: Establecer variables de entorno\"
cat << 'ENV'
  export DB_URL=jdbc:postgresql://localhost:5432/schedulingdb
  export DB_USERNAME=postgres
  export DB_PASSWORD=tu_contraseña_aqui
  export JWT_SECRET=<resultado_del_openssl>
  export SPRING_PROFILES_ACTIVE=production
ENV
echo \"\"
echo \"Paso 5: Ejecutar aplicación\"
echo \"  java -jar target/mis-0.0.1-SNAPSHOT.jar\"
echo \"\"
echo \"O todo junto:\"
cat << 'PROD'
  export DB_URL=jdbc:postgresql://localhost:5432/schedulingdb && \
  export DB_USERNAME=postgres && \
  export DB_PASSWORD=password && \
  export JWT_SECRET=$(openssl rand -hex 32) && \
  java -jar target/mis-0.0.1-SNAPSHOT.jar --spring.profiles.active=production
PROD
echo \"\"

# ====================================================================
# 4. TESTING
# ====================================================================
echo \"=== EJECUTAR TESTS ===\"
echo \"\"
echo \"Todos los tests:\"
echo \"  mvn test\"
echo \"\"
echo \"Test específico:\"
echo \"  mvn test -Dtest=EmployeeServiceTest\"
echo \"\"
echo \"Con cobertura:\"
echo \"  mvn test jacoco:report\"
echo \"\"

# ====================================================================
# 5. DESARROLLO CON PERFIL EXPLÍCITO
# ====================================================================
echo \"=== ESPECIFICAR PERFIL EXPLÍCITAMENTE ===\"
echo \"\"
echo \"Desarrollo (default):\"
echo \"  java -jar target/mis-0.0.1-SNAPSHOT.jar\"
echo \"\"
echo \"Con perfil desarrollo explicit:\"
echo \"  java -jar target/mis-0.0.1-SNAPSHOT.jar \\\"
echo \"    --spring.profiles.active=development\"
echo \"\"
echo \"Con perfil test:\"
echo \"  java -jar target/mis-0.0.1-SNAPSHOT.jar \\\"
echo \"    --spring.profiles.active=test\"
echo \"\"

# ====================================================================
# 6. VARIABLES DE ENTORNO (REFERENCE)
# ====================================================================
echo \"=== VARIABLES DE ENTORNO DISPONIBLES ===\"
echo \"\"
echo \"Base de Datos:\"
echo \"  - DB_URL\"
echo \"  - DB_USERNAME\"
echo \"  - DB_PASSWORD\"
echo \"\"
echo \"Seguridad:\"
echo \"  - JWT_SECRET (mínimo 32 caracteres)\"
echo \"\"
echo \"Spring Boot:\"
echo \"  - SPRING_PROFILES_ACTIVE=production|test\"
echo \"  - SERVER_PORT=8080\"
echo \"\"
echo \"Logging:\"
echo \"  - LOGGING_LEVEL_COM_MIS_MIS=DEBUG|INFO|WARN|ERROR\"
echo \"\"

# ====================================================================
# 7. ÚTILES PARA TESTING DE ENDPOINTS
# ====================================================================
echo \"=== TESTING DE ENDPOINTS CON CURL ===\"
echo \"\"
echo \"Obtener todos los empleados:\"
echo \"  curl http://localhost:8080/api/employees -H 'Authorization: Bearer <token>'\"
echo \"\"
echo \"Crear nuevo empleado:\"
echo \"  curl -X POST http://localhost:8080/api/employees \\\"
echo \"    -H 'Content-Type: application/json' \\\"
echo \"    -d '{\\\"firstName\\\":\\\"Juan\\\",\\\"lastName\\\":\\\"Perez\\\",\\\"email\\\":\\\"juan@example.com\\\"}'\"
echo \"\"

# ====================================================================
# 8. DEBUGGING
# ====================================================================
echo \"=== DEBUG MODE ===\"
echo \"\"
echo \"Con Maven (incluye hot reload):\"
echo \"  mvn -Dspring-boot.run.jvmArguments=\\'-Xdebug -Xrunjdwp:transport=dt_socket,server=y,suspend=y,address=5005\\' spring-boot:run\"
echo \"\"
echo \"Ver logs en tiempo real:\"
echo \"  tail -f logs/application.log\"
echo \"\"

echo \"=====================================================================\"
echo \"Para más información, ver CONFIGURATION.md\"
echo \"=====================================================================\"\n
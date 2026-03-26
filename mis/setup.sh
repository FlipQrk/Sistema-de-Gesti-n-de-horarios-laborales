#!/bin/bash

# ====================================================================
# SETUP.SH - Setup inicial con Docker
# ====================================================================
# Script para automatizar el setup inicial del proyecto
# Ejecutar una sola vez: bash setup.sh
# ====================================================================

set -e  # Salir si hay error

clear

echo "╔════════════════════════════════════════════════════════════════╗"
echo "║  SISTEMA DE GESTIÓN DE HORARIOS LABORALES - SETUP DOCKER      ║"
echo "╚════════════════════════════════════════════════════════════════╝"
echo ""

# ====================================================================
# 1. VERIFICAR REQUISITOS
# ====================================================================
echo "📋 Verificando requisitos..."
echo ""

# Docker
if ! command -v docker &> /dev/null; then
    echo "❌ Docker no está instalado"
    echo "   Descargar desde: https://www.docker.com/products/docker-desktop"
    exit 1
fi
echo "✅ Docker: $(docker --version)"

# Docker Compose
if ! command -v docker-compose &> /dev/null; then
    echo "❌ Docker Compose no está instalado"
    echo "   Instalarlo con: sudo apt-get install docker-compose (Linux)"
    exit 1
fi
echo "✅ Docker Compose: $(docker-compose --version)"

# Java
if ! command -v java &> /dev/null; then
    echo "❌ Java no está instalado"
    echo "   Descargar JDK 21 desde: https://www.oracle.com/java/"
    exit 1
fi
JAVA_VERSION=$(java -version 2>&1 | grep -oP '(?<=")[^"]*(?=")' | head -1)
echo "✅ Java: $JAVA_VERSION"

# Maven
if ! command -v mvn &> /dev/null; then
    echo "❌ Maven no está instalado"
    echo "   Instalarlo: sudo apt-get install maven (Linux)"
    exit 1
fi
MAVEN_VERSION=$(mvn -v 2>&1 | head -1)
echo "✅ Maven: $MAVEN_VERSION"

echo ""

# ====================================================================
# 2. INICIAR DOCKER
# ====================================================================
echo "🐳 Iniciando servicios Docker..."
echo ""

docker-compose up -d

echo ""
echo "⏳ Esperando a que PostgreSQL esté listo..."
sleep 5

# Verificar health
while ! docker exec scheduling_postgres pg_isready -U postgres > /dev/null 2>&1; do
    echo "   PostgreSQL iniciando... (esperando)"
    sleep 2
done

echo "✅ PostgreSQL está listo"
echo ""

# ====================================================================
# 3. MOSTRAR INFO DE CONEXIÓN
# ====================================================================
echo "╔════════════════════════════════════════════════════════════════╗"
echo "║  BASE DE DATOS - INFORMACIÓN DE CONEXIÓN                      ║"
echo "╚════════════════════════════════════════════════════════════════╝"
echo ""
echo "  Host:     localhost"
echo "  Port:     5432"
echo "  Database: schedulingdb"
echo "  User:     postgres"
echo "  Password: postgres123"
echo ""
echo "  URL: jdbc:postgresql://localhost:5432/schedulingdb"
echo ""

# ====================================================================
# 4. COMPILAR DEPENDENCIAS
# ====================================================================
if [ "$1" != "--skip-build" ]; then
    echo "📦 Compilando dependencias Maven..."
    echo ""
    mvn clean install -q
    echo "✅ Dependencias compiladas"
    echo ""
fi

# ====================================================================
# 5. MOSTRAR PASOS SIGUIENTES
# ====================================================================
echo "╔════════════════════════════════════════════════════════════════╗"
echo "║  PRÓXIMOS PASOS                                               ║"
echo "╚════════════════════════════════════════════════════════════════╝"
echo ""
echo "1️⃣  Ejecutar la aplicación (en una nueva terminal):"
echo "    mvn spring-boot:run"
echo ""
echo "2️⃣  Acceder a la aplicación:"
echo "    API:        http://localhost:8080/api"
echo "    Swagger:    http://localhost:8080/api/swagger-ui.html"
echo "    pgAdmin:    http://localhost:5050"
echo "              (User: admin@schedulingdb.com, Pass: admin123)"
echo ""
echo "3️⃣  Ver logs de PostgreSQL:"
echo "    docker-compose logs -f postgres"
echo ""
echo "4️⃣  Detener servicios cuando termines:"
echo "    docker-compose down"
echo ""
echo "5️⃣  Para revisar la documentación:"
echo "    - DOCKER.md       → Guía completa de Docker"
echo "    - CONFIGURATION.md → Perfiles y configuración"
echo "    - README.md       → Descripción general"
echo ""

# ====================================================================
# 6. VERIFICAR SERVICIOS
# ====================================================================
echo "╔════════════════════════════════════════════════════════════════╗"
echo "║  ESTADO DE SERVICIOS                                          ║"
echo "╚════════════════════════════════════════════════════════════════╝"
echo ""
docker-compose ps
echo ""

# ====================================================================
# 7. RESUMEN FINAL
# ====================================================================
echo "╔════════════════════════════════════════════════════════════════╗"
echo "║  ✅ SETUP COMPLETADO EXITOSAMENTE                            ║"
echo "╚════════════════════════════════════════════════════════════════╝"
echo ""
echo "PostgreSQL está corriendo y listo para conexiones."
echo ""
echo "Para más ayuda, revisar DOCKER.md"
echo ""

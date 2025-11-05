#!/bin/bash

# Script para iniciar el backend con las variables de entorno

# Cargar variables del archivo .env
if [ -f .env ]; then
    export $(cat .env | grep -v '^#' | xargs)
fi

# Configurar Java 21
export JAVA_HOME=$(/usr/libexec/java_home -v 21)

# Iniciar la aplicación con el perfil de producción
./gradlew bootRun --args='--spring.profiles.active=prod'

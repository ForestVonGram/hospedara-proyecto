# syntax=docker/dockerfile:1

############################
# Stage 1: Build con Gradle
############################
FROM gradle:8.7-jdk21-alpine AS builder

WORKDIR /workspace

# Copiamos configuración de Gradle
COPY build.gradle settings.gradle gradlew gradlew.bat ./
COPY gradle ./gradle

# Copiamos el código fuente
COPY src ./src

# Construimos el JAR ejecutable de Spring Boot
RUN ./gradlew --no-daemon clean bootJar

############################
# Stage 2: Runtime con JRE
############################
FROM eclipse-temurin:21-jre-alpine

WORKDIR /app

# Copiamos el JAR construido desde el stage anterior
COPY --from=builder /workspace/build/libs/*.jar app.jar

# Perfil por defecto y opciones de Java
ENV SPRING_PROFILES_ACTIVE=prod \
    JAVA_OPTS=""

EXPOSE 8080

# Ejecutamos la aplicación; la configuración de DB vendrá por variables de entorno
ENTRYPOINT ["sh", "-c", "java $JAVA_OPTS -jar app.jar"]
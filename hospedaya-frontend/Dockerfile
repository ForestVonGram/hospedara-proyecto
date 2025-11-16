# syntax=docker/dockerfile:1

############################
# Stage 1: Build Angular app
############################
FROM node:20-alpine AS builder

WORKDIR /app

# Instalar dependencias
COPY package.json package-lock.json ./
RUN npm ci

# Copiar código fuente y configuración
COPY angular.json tsconfig*.json ./
COPY src ./src
COPY public ./public

# Build en modo producción (por defecto en angular.json)
RUN npm run build

############################
# Stage 2: Servir con Nginx
############################
FROM nginx:alpine

# Eliminar configuración por defecto
RUN rm /etc/nginx/conf.d/default.conf

# Copiar configuración personalizada para SPA
COPY nginx.conf /etc/nginx/conf.d/default.conf

# Copiar artefactos construidos
COPY --from=builder /app/dist/hospedaya-frontend/browser /usr/share/nginx/html

EXPOSE 80

CMD ["nginx", "-g", "daemon off;"]

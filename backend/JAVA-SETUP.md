# ☕ Configuración de Java para HospedaYa Backend

## Problema Actual

El proyecto requiere **Java 21** pero tienes **Java 24** instalado.

## ✅ Solución Rápida

### Opción 1: Instalar Java 21 (Recomendado)

```bash
# Instalar Java 21 con Homebrew
brew install openjdk@21

# Vincular Java 21
sudo ln -sfn /opt/homebrew/opt/openjdk@21/libexec/openjdk.jdk /Library/Java/JavaVirtualMachines/openjdk-21.jdk

# Agregar a tu shell profile (~/.zshrc o ~/.bash_profile)
echo 'export JAVA_HOME=/Library/Java/JavaVirtualMachines/openjdk-21.jdk/Contents/Home' >> ~/.zshrc
echo 'export PATH="$JAVA_HOME/bin:$PATH"' >> ~/.zshrc

# Recargar configuración
source ~/.zshrc

# Verificar
java -version
# Debería mostrar: openjdk version "21..."
```

### Opción 2: Usar Java 24 (Modificar proyecto)

Si prefieres usar Java 24, edita `backend/build.gradle`:

```gradle
java {
    toolchain {
        languageVersion = JavaLanguageVersion.of(24)  // Cambiar de 21 a 24
    }
}
```

Luego compila:
```bash
cd backend
./gradlew clean build
```

### Opción 3: Configurar JAVA_HOME temporalmente

Para una sesión específica:

```bash
# Verificar dónde está Java 21 (si lo tienes)
/usr/libexec/java_home -V

# Usar Java 21 para esta sesión
export JAVA_HOME=$(/usr/libexec/java_home -v 21)

# Compilar
cd backend
./gradlew clean build
```

## 🚀 Iniciar el Backend

Una vez configurado Java 21:

```bash
cd backend
./gradlew bootRun
```

## 🔍 Verificar Configuración

```bash
# Ver todas las versiones de Java instaladas
/usr/libexec/java_home -V

# Ver versión actual
java -version

# Ver JAVA_HOME actual
echo $JAVA_HOME
```

## 📝 Configuración Permanente

Agrega estas líneas a tu `~/.zshrc` (o `~/.bash_profile` si usas bash):

```bash
# Java 21
export JAVA_HOME=/Library/Java/JavaVirtualMachines/openjdk-21.jdk/Contents/Home
export PATH="$JAVA_HOME/bin:$PATH"
```

Luego ejecuta:
```bash
source ~/.zshrc
```

## ⚠️ Nota

El proyecto fue desarrollado y probado con **Java 21**. Se recomienda usar esta versión para evitar problemas de compatibilidad.

# =========================================================
# ETAPA 1: El "Builder" - Construir la aplicación con Maven
# =========================================================
# Usamos una imagen oficial de Maven que ya tiene JDK 17 y Maven instalados.
FROM maven:3.8-openjdk-17 AS builder

# Establecemos el directorio de trabajo dentro del contenedor.
WORKDIR /app

# Optimizacion de capas de Docker:
# 1. Copiamos solo el pom.xml primero.
COPY pom.xml .
# 2. Descargamos todas las dependencias. Si el pom.xml no cambia,
#    Docker reutilizará esta capa cacheada, haciendo los builds futuros mucho más rápidos.
RUN mvn dependency:go-offline

# 3. Copiamos el resto del código fuente de la aplicación.
COPY src ./src

# 4. Construimos el proyecto. Esto compila, corre las pruebas y empaqueta el .jar.
#    Usamos -DskipTests para no correr las pruebas aquí, ya que GitHub Actions lo hace.
RUN mvn package -DskipTests

# =========================================================
# ETAPA 2: El "Runner" - La imagen final y ligera
# =========================================================
# Usamos una imagen muy pequeña que solo tiene el Java Runtime Environment (JRE).
FROM eclipse-temurin:17-jre-jammy

# Establecemos el directorio de trabajo.
WORKDIR /app

# Copiamos ÚNICAMENTE el .jar compilado desde la etapa 'builder'.
# Fíjate en el nombre del archivo, debe coincidir con el de tu pom.xml.
COPY --from=builder /app/target/toke-0.0.1-SNAPSHOT.jar app.jar

# Exponemos el puerto en el que correrá la aplicación. Render usará esto.
EXPOSE 8176

# El comando que se ejecutará cuando el contenedor se inicie.
ENTRYPOINT ["java", "-jar", "app.jar"]
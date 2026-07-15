# ETAPA 1: BUILD
FROM gradle:jdk25 AS builder
WORKDIR /build

COPY build.gradle settings.gradle* gradle.properties* ./
COPY gradle ./gradle
RUN gradle dependencies --no-daemon

COPY src ./src
# 1. Forzamos la creación exclusiva del JAR de Spring Boot
RUN gradle bootJar --no-daemon
# 2. Destruimos cualquier archivo 'plain' generado para evitar confusiones al copiar
RUN find build/libs -name "*-plain.jar" -type f -delete

# ETAPA 2: RUNTIME
FROM eclipse-temurin:25-jre-alpine
RUN addgroup -S spring && adduser -S spring -G spring
WORKDIR /app

COPY --from=builder /build/build/libs/*.jar app.jar
RUN chown spring:spring app.jar
USER spring:spring
EXPOSE 8080

HEALTHCHECK --interval=30s --timeout=3s --start-period=60s --retries=3 \
  CMD wget --no-verbose --tries=1 --spider http://localhost:8080/api/actuator/health || exit 1

ENV SPRING_PROFILES_ACTIVE=prod

ENTRYPOINT ["java", \
    "-Djava.security.egd=file:/dev/./urandom", \
    "-Xms256m", \
    "-Xmx512m", \
    "-jar", \
    "app.jar"]
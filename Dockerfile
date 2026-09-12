# ---------- build stage ----------
FROM maven:3.9-eclipse-temurin-17 AS builder

WORKDIR /build

# Resolve dependencies first so this layer stays cached while only sources change.
COPY pom.xml checkstyle.xml ./
RUN mvn -B dependency:go-offline

COPY src ./src
RUN mvn -B clean package -DskipTests

# ---------- runtime stage ----------
FROM eclipse-temurin:17-jre-alpine

RUN addgroup -S spring && adduser -S spring -G spring

WORKDIR /app

COPY --from=builder /build/target/*.jar app.jar

USER spring

EXPOSE 8080

ENTRYPOINT ["java", "-jar", "app.jar"]

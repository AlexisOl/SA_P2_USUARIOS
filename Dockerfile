# syntax=docker/dockerfile:1

# ---------- Build ----------
FROM maven:3.9.8-eclipse-temurin-21 AS build
WORKDIR /app
COPY pom.xml .
RUN mvn -q -B -DskipTests dependency:go-offline
COPY src src
RUN mvn -q -B -DskipTests package

# ---------- Runtime ----------
FROM eclipse-temurin:21-jre
WORKDIR /opt/app
RUN useradd -r -u 1001 spring
COPY --from=build /app/target/*.jar app.jar
ENV JAVA_OPTS="" SPRING_PROFILES_ACTIVE=docker TZ=UTC
EXPOSE 8080
USER spring
ENTRYPOINT ["sh","-c","java $JAVA_OPTS -jar app.jar"]

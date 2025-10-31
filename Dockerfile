FROM bellsoft/liberica-openjdk-alpine:21.0.3

WORKDIR /app

COPY target/mcsv-usuarios.jar /app/mcsv-usuarios.jar

EXPOSE 8083

ENTRYPOINT ["java", "-jar", "mcsv-usuarios.jar"]

# Imagen de ejecucion con Java 17 (liviana)
FROM eclipse-temurin:17-jre-alpine
WORKDIR /app
# Copia el .jar generado por Maven (mvn package)
COPY target/*.jar app.jar
EXPOSE 8082
ENTRYPOINT ["java", "-jar", "app.jar"]
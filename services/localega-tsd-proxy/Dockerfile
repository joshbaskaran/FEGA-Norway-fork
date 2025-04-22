FROM eclipse-temurin:21-jre-alpine
COPY build/libs/localega-tsd-proxy*.jar /app/app.jar
WORKDIR /app
EXPOSE 8080
CMD ["java","-XX:+UseG1GC", "-jar","app.jar"]

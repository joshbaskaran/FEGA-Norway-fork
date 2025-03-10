# Use Temurin 21 as the base image for Java 21
FROM eclipse-temurin:21-jdk-alpine

# Set the working directory in the container
WORKDIR /fega-norway

# Copy the compiled binaries and dependencies from your local machine into the container
COPY /build/libs/e2eTests.jar /fega-norway/e2eTests.jar
COPY .env /fega-norway/.env
COPY entrypoint.sh /entrypoint.sh

RUN chmod +x /entrypoint.sh

CMD ["/entrypoint.sh"]

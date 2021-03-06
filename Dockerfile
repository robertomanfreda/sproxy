FROM adoptopenjdk/openjdk14:latest
RUN mkdir -p /var/log/sproxy
ARG JAR_FILE=target/*.jar
COPY ${JAR_FILE} app.jar
ENTRYPOINT ["java", "--enable-preview", "-jar","/app.jar"]
FROM openjdk:17-jdk-slim
VOLUME /tmp
COPY target/spring2-1.0-SNAPSHOT.jar app.jar
EXPOSE 8080
ENTRYPOINT ["java","-jar","/app.jar"]
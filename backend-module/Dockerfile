FROM openjdk:17-jdk-slim
WORKDIR /backendmodule
COPY ./build/libs/moum-0.0.1-SNAPSHOT.jar backendapp.jar
EXPOSE 8080
ENTRYPOINT ["java", "-jar", "backendapp.jar"]
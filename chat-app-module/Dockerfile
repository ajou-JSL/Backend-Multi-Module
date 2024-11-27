FROM openjdk:17-jdk-slim
WORKDIR /chatmodule
COPY ./build/libs/chat-app-module-0.0.1-SNAPSHOT.jar chatapp.jar
EXPOSE 8070
ENTRYPOINT ["java", "-jar", "chatapp.jar"]
FROM openjdk:17-jdk-alpine

WORKDIR /app

COPY target/*.jar addressbookservice.jar

EXPOSE 8080

ENTRYPOINT ["java", "-jar", "addressbookservice.jar"]

FROM openjdk:17-alpine
ARG JAR_FILE=build/libs/*.jar
COPY ${JAR_FILE} container-platform-common-api.jar
RUN apk update && apk upgrade && apk add --no-cache bash
ENTRYPOINT ["java","-jar","-Dspring.profiles.active=prod","/container-platform-common-api.jar"]
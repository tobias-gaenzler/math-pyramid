FROM adoptopenjdk/openjdk16:alpine-jre
ARG JAR_FILE=target/*.jar
COPY ${JAR_FILE} application.jar
EXPOSE 80
ENTRYPOINT ["java","-jar","/application.jar"]
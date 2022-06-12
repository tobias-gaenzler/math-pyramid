FROM maven:3.8.5-openjdk-18 as build

WORKDIR /usr/src/app
COPY . .

# TODO: install chrome driver to run integration tests
RUN mvn -B clean package -DskipTests

FROM openjdk:18-slim

RUN useradd docker_user
USER docker_user

WORKDIR /usr/src/app
COPY --from=build /usr/src/app/target/*.jar ./application.jar
EXPOSE 80

ENTRYPOINT ["java","-jar","/usr/src/app/application.jar"]
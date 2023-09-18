FROM maven:3.8.5-openjdk-18 as build

WORKDIR /usr/src/app
COPY . .

# TODO: install chrome driver to run integration tests
RUN mvn -B clean package -DskipTests


FROM openjdk:18-slim

RUN addgroup --system --gid 1001 appuser
RUN adduser --system --uid  1001 --group appuser
RUN mkdir -p /usr/src/app
RUN chown -R appuser:appuser /usr/src/app

USER appuser
WORKDIR /usr/src/app

COPY --from=build /usr/src/app/target/*.jar ./application.jar

ENTRYPOINT ["java","-jar","/usr/src/app/application.jar"]
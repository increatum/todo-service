FROM maven:3.9.6-eclipse-temurin-17-alpine as mavenBuild
WORKDIR /tmp/build
COPY pom.xml .
COPY ./src ./src
RUN mvn clean install

FROM eclipse-temurin:17-jdk-alpine 
COPY --from=mavenBuild /tmp/build/target/*.jar app.jar
ENTRYPOINT ["java","-jar","/app.jar"]

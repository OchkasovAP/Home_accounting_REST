FROM eclipse-temurin:17-jdk-alpine
VOLUME /tmp
ARG JAR_FILE
RUN ./mvnw install
COPY ${JAR_FILE} homeAccountingRest-0.0.1-SNAPSHOT.jar
ENTRYPOINT ["java","-jar","/homeAccountingRest-0.0.1-SNAPSHOT.jar"]

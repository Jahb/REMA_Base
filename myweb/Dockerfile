FROM openjdk:11-jre-slim

WORKDIR /usr/local/runtime
COPY target/*.jar webapp.jar

EXPOSE 8080

ENTRYPOINT ["java"]
CMD ["-jar", "webapp.jar"]

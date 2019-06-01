FROM openjdk:11.0.3-jre-slim-stretch

COPY    service /opt/service
WORKDIR /opt/service/
CMD     ["java", "$JAVA_OPTS", "-jar", "service.jar"]
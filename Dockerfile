FROM openjdk:11
ADD target/iot_data-0.0.1-SNAPSHOT.jar .
ENTRYPOINT ["java", "-jar","iot_data-0.0.1-SNAPSHOT.jar"]
EXPOSE 9090
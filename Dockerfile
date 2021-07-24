FROM openjdk:8
COPY target/filestorage-*.jar filestorage.jar
EXPOSE 8080
ENTRYPOINT ["java", "-jar", "filestorage.jar"]
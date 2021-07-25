FROM openjdk:8
COPY target/filestorage-*.jar filestorage.jar
COPY wait-for-it.sh wait-for-it.sh
EXPOSE 8080
ENTRYPOINT ["./wait-for-it.sh", "mysql_db:3306", "--", "java", "-jar", "filestorage.jar"]
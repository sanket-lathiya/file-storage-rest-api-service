# file-storage-rest-api-service
File storage rest api service design and implementations

## Prerequisites

**1. Install Java JDK 1.8 from https://www.oracle.com/in/java/technologies/javase/javase-jdk8-downloads.html, for setup refer https://docs.oracle.com/cd/E19182-01/820-7851/inst_cli_jdk_javahome_t/** 

**2. Install Maven from https://maven.apache.org/download.cgi, for setup refer https://maven.apache.org/install.html**

**3. Install Docker from https://docs.docker.com/get-docker/**

**4. Install Postman tool from https://www.postman.com/downloads/**

**5. Install Git tool from https://git-scm.com/downloads**

## Project setup setps
1. git clone https://github.com/sanket-lathiya/file-storage-rest-api-service.git      **(Clone project from github)**
2. cd cd file-storage-rest-api-service      **(Go to project's root directory)**
3. mvn clean install **(Build the project)**
4. Start docker engine in your system
5. docker-compose up -d --build      **(Start filestorage-rest-api-service using docker compose)**
6. docker-compose down -v      **(Stop filestorage-rest-api-service)**

## How to test rest apis

Download testing document from **(https://drive.google.com/file/d/1QcVATTrTgr3F17cHj3PmrqWaPYZm5Qo0/view?usp=sharing)** and follow along.

## Troubleshooting
1. Please do not bind port 3306 to any other service because it will be bound to mysql_db container from docker. Otherwise mysql_db service will not come up when we run application using docker compose.
2. Please do not bind port 8080 to any other service because it will be bound to filestorage_api container from docker. Otherwise filestorage_api service will not come up when we run application using docker compose.

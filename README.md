# Highload Software Architecture 8 Lesson 16 Homework

Logging
---

## Test project setup

The demo is written in Kotlin and uses docker-compose to run MySQL and loggigng services.

The `com.example.DemoKt.runDemo` function is used to run load against the MySQL database while applying different slow query log settings.

The summary of the results is located in the [REPORT.md](reports/REPORT.md) file.

## How to build and run

Start up Docker containers

```shell script
docker-compose up -d
```

Build and run demo application (Requires Java 17+)

```shell script
./gradlew build && \
java -jar build/quarkus-app/quarkus-run.jar
```

You can also run application in dev mode that enables live coding using:

```shell script
./gradlew quarkusDev
```

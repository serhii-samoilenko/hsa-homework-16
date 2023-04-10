# Highload Software Architecture 8 Lesson 16 Homework

Logging
---

## Test project setup

The demo is written in Kotlin and uses docker-compose to run MySQL and logging services.

The `com.example.DemoKt.runDemo` function is used to run load against the MySQL database while applying different slow query log settings.

The [docker-compose.yaml](docker-compose.yaml) file contains two setups for logging: ELK and Graylog.

There are two filebeat services that are used to send logs to the logging services. Their configurations are located in the [config](config) directory, as well as the configuration for the MySQL service.

The MySQL service writes logs into mounted volume, which is then read by the filebeat service.

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

## Results and Conclusions

As seen in the [REPORT.md](reports/REPORT.md), in my setup there was no significant impact on the performance of the database when the slow query log was enabled and even when the threshold was set to 0.

Only a very minor impact was observed.

### Graylog dashboard

![Graylog dashboard](reports/graylog.png)

### ELK dashboard

![ELK dashboard](reports/elk.png)

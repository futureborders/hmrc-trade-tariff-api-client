# "Check how to import or export goods" service - hmrc-trade-tariff-api-client

## About the service

Use this service to get information about importing and exporting goods for your business, including:

- how to register your business for trading
- which licences and certificates you need for your goods
- paying the right VAT and duties for your goods
- how to make declarations for your goods to clear the UK border
- which commodity codes you'll need to classify your goods

The live service is accessed via ```https://www.gov.uk/check-how-to-import-export```

## About this repository
- this is a library to call OTT api for the signposting service
- it's a Java application
- Source code formatted using the [Google Java formatting standards](https://google.github.io/styleguide/javaguide.html). There are plugins available for both IntelliJ and Eclipse which can be found [here](https://github.com/google/google-java-format).

## Installation

The following steps will enable you to setup your development environment:

* Set JAVA_HOME, PATH and MVN_HOME env variables
* Build the project : ```mvn clean compile```
* Compile and install the library into local maven repository: ```mvn clean install```
* Use this library as maven dependency in stw-trade-tariff-api application.

## Dependencies

* jdk - https://adoptium.net/en-GB/temurin/releases/?version=11
* mvn - https://maven.apache.org/download.cgi

## Structure

| Directory                  | Description                   |
|----------------------------|-------------------------------|
| `src/test/resources/`      | Stub OTT response files       |
| `src/main/java`            | Contains all the source code. |
| `src/test/java/`           | Contains test code.           |

## Licence

This application is made available under the [Apache 2.0 licence](/LICENSE).

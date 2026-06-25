# gh4ecore-commits

Empty Java project for downloading Ecore model file history versions from GitHub commits for Model-Driven Engineering research projects.

## Requirements

- JDK 21
- Maven 3.9 or newer

## Build

```bash
mvn test
```

## Run

```bash
mvn exec:java
```

## Package

```bash
mvn package
```

## Project Coordinates

- Group ID: `com.fadihania`
- Artifact ID: `gh4ecore-commits`
- Main package: `com.fadihania`

## GitHub API

This project includes the Hub4J GitHub API for Java Maven dependency:

```xml
<dependency>
    <groupId>org.kohsuke</groupId>
    <artifactId>github-api</artifactId>
    <version>2.0-rc.6</version>
</dependency>
```

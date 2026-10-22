# gh4ecore-commits

Java CLI for downloading Ecore model file history versions from GitHub commits for Model-Driven Engineering research projects.

## Requirements

- JDK 21
- Maven 3.9 or newer

## Build

```bash
mvn test
```

## Run

Set a GitHub token first:

```bash
export GITHUB_TOKEN=ghp_your_token
```

```bash
mvn exec:java
```

The default input file is `models/ecore-model.txt`, and the default output directory is `downloads/`.

You can override both:

```bash
mvn exec:java -Dexec.args="--input models/ecore-model.txt --output downloads"
```

Input rows must use this CSV format:

```csv
model_file_name,repository_full_name,repository_url,file_url,commit_count
```

Example:

```csv
models.ecore,opensourceBIM/BIMserver,https://github.com/opensourceBIM/BIMserver,https://github.com/opensourceBIM/BIMserver/blob/master/BimServer/models/models.ecore,241
```

Each repository is written to `downloads/<owner-repo>/`, for example `downloads/opensourceBIM-BIMserver/`.
Downloaded versions are named `<model-base-name>_<yyyy-MM-dd>_<shortsha>.ecore`, and repository metadata is written to `metadata.json`.

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

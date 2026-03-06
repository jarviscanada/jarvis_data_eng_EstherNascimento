# Java Grep Application

## Introduction

The ***Java Grep App*** is a tool that helps users search for specific words or patterns inside many files at once, similar to the Linux grep command. For example, if you have hundreds of documents and need to find every line that contains a specific name, this app will find them and save them into a single report file.

I built this project using Java 8, focusing on Lambdas and Streams. These features allow the app to process data efficiently without using too much computer memory. To make the app easy to share and run on any computer, I used Docker. This means you don't have to worry about installing specific versions of Java or setting up complex environments?it works perfectly right out of the box.

## Quick Start

1.  **Build the project** using Maven to create the executable JAR:
    ```bash
    mvn clean package
    ```
2.  **Run the application locally**:
    ```bash
    java -jar target/grep-1.0-SNAPSHOT.jar [regex] [rootPath] [outFile]
    ```
    *Example:* `java -jar target/grep-1.0-SNAPSHOT.jar .*Romeo.*Juliet.* ./data ./log/grep.out`
3.  **Run via Docker**:
    ```bash
    docker run --rm -v $(pwd)/data:/data -v $(pwd)/log:/log tehlaran/grep .*Romeo.*Juliet.* /data /log/grep.out
    ```

## Implementation
### Pseudocode
The core logic resides in the `process` method, which orchestrates the file traversal and filtering using a functional approach:
```java
process() {
    listAllFiles(rootPath)
        .flatMap(file -> readLines(file))
        .filter(line -> containsPattern(line))
        .collect(toList())
        .writeTo(outFile)
}
```

## Performance Issue
In a traditional implementation, loading all lines from a large file into an ArrayList could cause an OutOfMemoryError if the file size exceeded the JVM heap memory. I resolved this by using Java Streams to process files lazily. By returning a Stream<String> from the file reader, the application processes one line at a time, keeping the memory footprint constant regardless of the input file size.

## Test

I tested the application manually by:

**Sample Data:** Using a collection of text files (e.g., Shakespearean plays) in the /data directory.

**Test Cases:** Running various regex patterns ranging from simple strings to complex phrases.

**Validation:** Comparing the output in grep.out with the results of the actual Linux grep command to ensure 100% accuracy in the filtered data.

## Deployment

I dockerized the application to simplify distribution and remove the "it works on my machine" problem.

**Base Image:** Used amazoncorretto:8-alpine for a lightweight and secure Java 8 environment.

**Packaging:** Integrated the maven-shade-plugin to bundle dependencies into a "Fat JAR" with a defined Main Manifest attribute.

**Registry:** The image is hosted on Docker Hub at tehlaran/grep, making it accessible for deployment on any system with Docker installed.

## Improvement

**Parallel Streams:** Implement .parallelStream() to leverage multi-core processors for faster file traversal in massive directories.

**Enhanced Logging:** Integrate a logging framework like SLF4J to replace System.out for better production-level monitoring.

**Unit Testing:** Implement JUnit test suites to automatically verify edge cases, such as empty directories or malformed regex strings.


---



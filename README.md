# spring-grpc
A simple Spring Boot and gRPC application with the sole purpose off getting started with gRPC and learning more about it

## Requirements

- [Java 17](https://adoptium.net/)
- [Maven](https://maven.apache.org/)
- [Gradle](https://gradle.org/)

## Getting Started

1. Clone the repository
2. Navigate to the project directory
3. Build the project using Maven:

```
./mvnw clean install
```

4. Build protos schemas:

```
cd proto && ./mvnw compile 
```

4. Run the project using Maven wrapper:

```
./mvnw spring-boot:run
```

5. The application will start on http://localhost:8080

## Usage

- You will also need the database installed on your machine and set the connection vars on the application.properties or application.yml

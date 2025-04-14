# Recruitment Task API

REST API for asynchronous Tasks Processing.

## Prerequisites
- Java 17+
- Maven 3.8+
- Docker + Docker Compose

## How to run locally

1. Clone the repository:
    ```bash
    git clone https://github.com/Paullo99/cdq-recruitment-task.git
    cd cdq-recruitment-task
    ```

2. Create a `.env` file in the root directory of the project (automatically loaded by Docker Compose).

    Example `.env` file:
    ```dotenv
    POSTGRES_DB=recruitment-task
    POSTGRES_USER=postgres
    POSTGRES_PASSWORD=postgres
    ```

3. Build and run the application using Docker Compose:
    ```bash
    docker-compose up --build
    ```
   
> **Note:**  
> The application will be available at [http://localhost:8080](http://localhost:8080).


## API Documentation
The API documentation is available at [http://localhost:8080/swagger-ui/index.html](http://localhost:8080/swagger-ui/index.html) after starting the application.

## Task overview
//TODO

## Development & Testing
- Running the project locally without Docker:
    ```bash
    mvn clean install
    mvn spring-boot:run
    ```
- Running tests:
    ```bash
    mvn test
    ```
- Checking the test coverage:
    ```bash
    mvn test jacoco:report
    ```
  The coverage report will be generated in `target/site/jacoco/index.html`.
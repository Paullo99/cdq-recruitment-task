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

## Implementation details
### Purpose
The API allows creating and updating persons. 
After each operation, an asynchronous task is created, which compares the new person data with the previous version (if any).  
It checks each field individually and classifies the difference.

### What happens when a person is created or updated?
1. A new person is created in the database — or updated, if it already exists.
2. A background task is triggered to compare the new state with the previous one.
3. The task runs asynchronously in a single-threaded queue (non-blocking, FIFO).
4. You can check the task's progress and result using its ID.
5. The final result contains a list of fields with their old and new values and a classification.

### How does the classification work?
Each field (name, surname, birthDate, company) is compared using text similarity. The similarity is calculated using [Levenshtein distance](https://en.wikipedia.org/wiki/Levenshtein_distance):
```
similarity = 1 - (Levenshtein distance / max string length)
```
#### Types of classification:
- **ADDED** - new value is not null or empty, old value is null or empty.
- **DELETED** - new value is null or empty, old value is not null or empty.
- **LOW** - similarity < 0.4.
- **MEDIUM** - 0.4 <= similarity < 0.9.
- **HIGH** - similarity >= 0.9 or the values are equal.

#### Edge cases
- `null → null` is treated as **HIGH** (no change)
- `null → ""` is treated as **HIGH** (no change)
- `"" → ""` is treated as **HIGH** (no change)
- `"" → "null` is treated as **HIGH** (no change)

### Cache
Task results are cached in memory only when the task is **DONE**.

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

## Ideas for improvements
- Replace raw JSON task result with a structured DB model.
- Replace simple in-memory cache with **Redis**.
- Use **Apache Kafka** for task processing.
- Add **CI/CD** pipeline with GitHub Actions (build, test, Docker image)


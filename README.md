# Product Service

## Overview

The Product Service is a Spring Boot application designed to perform CRUD operations for a 'Product' entity. The 'Product' entity includes fields such as 'id', 'name', 'description', and 'price'.

## Prerequisites

Before running the application, ensure you have the following installed:

- [Java 17](https://www.oracle.com/java/technologies/javase-jdk17-downloads.html)
- [PostgreSQL](https://www.postgresql.org/download/)
- [Maven](https://maven.apache.org/install.html) (for building the application)

## Installation

### PostgreSQL Setup

1. **Install PostgreSQL**:
   - Download and install PostgreSQL from the [official website](https://www.postgresql.org/download/).
   - Ensure PostgreSQL is running locally on your machine.

2. **Create Database**:
   - Open your PostgreSQL client (e.g., pgAdmin or psql).
   - Create a new database named `product_db`:

     ```sql
     CREATE DATABASE product_db;
     ```
   - Ensure you have a PostgreSQL user with the necessary permissions to access `product_db`.

### Application Setup

1. **Clone the Repository**:

    ```sh
    git clone https://github.com/konstantinbojchevski/product-service.git
    cd product-service
    ```

### Open in IntelliJ

1. Open IntelliJ IDEA and select "Open" to open the project directory.

### Configure Database Credentials

1. In IntelliJ, go to `File > Project Structure > Project Settings > Modules > Dependencies`.
2. Set the PostgreSQL database credentials (user and password) in IntelliJ's database tool.

### Build the Application

```sh
mvn clean install
```

### Run the application

```sh
mvn spring-boot:run
```

### API Documentation

Once the application is running, you can access the Swagger UI to explore and test the RESTful APIs:

[Swagger UI](http://localhost:8080/swagger-ui/index.html)

### Features

- Create Product: Add a new product to the database.
- Retrieve Products: Get details of all products or a specific product by ID.
- Update Product: Update details of an existing product.
- Delete Product: Remove a product from the database.
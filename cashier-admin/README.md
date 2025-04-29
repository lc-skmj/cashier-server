# Cashier System Server

A Spring Boot 3 based cashier system server implementation.

## Requirements

- JDK 17
- Maven 3.8+
- MySQL 8.0+

## Setup

1. Clone the repository
2. Create a MySQL database named `cashier_db`
3. Update the database credentials in `application.yml` if needed
4. Run the application:
   ```bash
   mvn spring-boot:run
   ```

## Project Structure

```
src/main/java/com/cashier/
├── config/         # Configuration classes
├── controller/     # REST controllers
├── model/          # Entity classes
├── repository/     # JPA repositories
├── service/        # Business logic
└── util/           # Utility classes
```

## Features

- User Management
- Product Management
- Inventory Management
- Order Management
- Payment Processing
- Reporting
- System Settings

## API Documentation

API documentation will be available at `http://localhost:8080/swagger-ui.html` after starting the application.

## License

This project is licensed under the MIT License. 
Z# Calculator Application with Database History

This is a simple calculator application built with Java Swing and connected to a MySQL database. It allows users to perform basic calculations and stores a history of results in a MySQL table.

## Project Structure

- **Main.java**: Entry point of the application. Initializes the calculator.
- **Calc.java**: Contains the GUI and logic for the calculator. Manages database connections, performs calculations, and stores history.

## Features

- **Basic Arithmetic**: Addition, Subtraction, Multiplication, Division, and Power.
- **Trigonometric Functions**: Sine, Cosine, and Tangent.
- **Database Integration**: Stores the history of calculations in a MySQL database and allows for clearing the history.
- **Theming**: Users can switch between light and dark themes.

## Requirements

- **Java 17 or later**
- **MySQL Database**: Ensure a database is set up with the following credentials and structure:
  - Database: `practice`
  - Table: `calc_hist` with columns:
    ```sql
    CREATE TABLE calc_hist (
        id INT AUTO_INCREMENT PRIMARY KEY,
        Number1 FLOAT,
        sign VARCHAR(10),
        Number2 FLOAT,
        Answer FLOAT
    );
    ```
  - Update `url`, `user`, and `pass` in `Calc.java` with your MySQL credentials.
- **MySQL JDBC Driver**: The MySQL connector JAR file (`mysql-connector-java-x.x.x.jar`) must be added to the project's build path.
  - In your IDE, add the MySQL connector JAR to the project’s build path by going to **Project Properties > Java Build Path > Libraries** and adding the JAR file.
  - You can download the latest MySQL connector JAR from [MySQL's official site](https://dev.mysql.com/downloads/connector/j/).

## How to Run

1. Clone the repository:
   ```bash
   git clone https://github.com/your-username/your-repository-name.git
   ```
2. Open the project in your preferred Java IDE
3. Add the MYSQL Connector JAR to the project(as mentioned in requirement)
4. Run Main.java to start the application

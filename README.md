# Java Banking System

A simple command-line banking system built using **Java**, **JDBC**, and **MySQL**. This application supports basic banking operations such as creating accounts, depositing/withdrawing money, and viewing account details. It is designed with thread safety to handle concurrent operations efficiently.

## Features

1. **Create Account**: Add a new account with an initial deposit.
2. **View Account**: Retrieve details of an account using its account number.
3. **Deposit Money**: Add funds to an account.
4. **Withdraw Money**: Deduct funds from an account, ensuring sufficient balance.
5. **Delete Account**: Remove an account from the system.
6. **List All Accounts**: View all existing accounts.

## Prerequisites

1. **Java Development Kit (JDK)**: Version 8 or higher.
2. **MySQL Server**: Installed and running.
3. **JDBC Driver**: Ensure the MySQL JDBC driver (e.g., `mysql-connector-java`) is added to your project.

## Setup

### Database Configuration
1. Start your MySQL server.
2. Create a database named `banking`:
   ```sql
   CREATE DATABASE banking;
   ```
3. Update the following variables in the code with your MySQL credentials:
   ```java
   String url = "jdbc:mysql://localhost:3306/banking";
   String user = "root";
   String password = "password";
   ```

### Create Table
The application will automatically create the `accounts` table when it runs if the table does not exist.

```sql
CREATE TABLE accounts (
    accountNumber INT PRIMARY KEY AUTO_INCREMENT,
    accountHolderName VARCHAR(255),
    balance DOUBLE
);
```

## How to Run

1. Compile the program:
   ```bash
   javac Main.java
   ```

2. Run the program:
   ```bash
   java Main
   ```

3. Follow the menu options to interact with the banking system.

## Example Menu

```
Welcome to the Banking System
1. Create Account
2. View Account
3. Deposit Money
4. Withdraw Money
5. Delete Account
6. List All Accounts
7. Exit
Enter your choice: 
```

## Thread Safety
All database operations are synchronized to ensure thread-safe access in concurrent environments.

## Error Handling
- Handles invalid account numbers.
- Ensures withdrawals do not exceed the account balance.
- Provides detailed messages for user actions.

## Dependencies
- **Java SE Development Kit**
- **MySQL Server**
- **MySQL JDBC Driver**

## Future Enhancements
- Add user authentication.
- Implement transaction history.
- Create a graphical user interface (GUI).
- Enable remote database connectivity.

## License
This project is licensed under the MIT License. Feel free to use, modify, and distribute it.

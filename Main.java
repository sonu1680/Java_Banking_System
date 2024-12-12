import java.sql.*;
import java.util.*;

class BankAccount {
    private int accountNumber;
    private String accountHolderName;
    private double balance;

    public BankAccount(int accountNumber, String accountHolderName, double balance) {
        this.accountNumber = accountNumber;
        this.accountHolderName = accountHolderName;
        this.balance = balance;
    }

    public int getAccountNumber() {
        return accountNumber;
    }

    public String getAccountHolderName() {
        return accountHolderName;
    }

    public double getBalance() {
        return balance;
    }

    @Override
    public String toString() {
        return "Account Number: " + accountNumber + ", Holder: " + accountHolderName + ", Balance: " + balance;
    }
}

class BankingSystem {
    private final Connection connection;

    public BankingSystem() throws SQLException {
        String url = "jdbc:mysql://localhost:3306/banking";
        String user = "root";
        String password = "123456789";
        connection = DriverManager.getConnection(url, user, password);
        initializeDatabase();
    }

    private void initializeDatabase() throws SQLException {
        String createTableQuery = "CREATE TABLE IF NOT EXISTS accounts (" +
                "accountNumber INT PRIMARY KEY AUTO_INCREMENT," +
                "accountHolderName VARCHAR(255)," +
                "balance DOUBLE" +
                ")";
        try (Statement statement = connection.createStatement()) {
            statement.execute(createTableQuery);
        }
    }

    public synchronized void createAccount(String name, double initialDeposit) throws SQLException {
        String query = "INSERT INTO accounts (accountHolderName, balance) VALUES (?, ?)";
        try (PreparedStatement pstmt = connection.prepareStatement(query)) {
            pstmt.setString(1, name);
            pstmt.setDouble(2, initialDeposit);
            pstmt.executeUpdate();
            System.out.println("Account created successfully.");
        }
    }

    public synchronized void viewAccount(int accountNumber) throws SQLException {
        String query = "SELECT * FROM accounts WHERE accountNumber = ?";
        try (PreparedStatement pstmt = connection.prepareStatement(query)) {
            pstmt.setInt(1, accountNumber);
            ResultSet rs = pstmt.executeQuery();
            if (rs.next()) {
                BankAccount account = new BankAccount(rs.getInt("accountNumber"), rs.getString("accountHolderName"),
                        rs.getDouble("balance"));
                System.out.println(account);
            } else {
                System.out.println("Account not found.");
            }
        }
    }

    public synchronized void deposit(int accountNumber, double amount) throws SQLException {
        String query = "UPDATE accounts SET balance = balance + ? WHERE accountNumber = ?";
        try (PreparedStatement pstmt = connection.prepareStatement(query)) {
            pstmt.setDouble(1, amount);
            pstmt.setInt(2, accountNumber);
            int rowsAffected = pstmt.executeUpdate();
            if (rowsAffected > 0) {
                System.out.println("Deposit successful.");
            } else {
                System.out.println("Account not found.");
            }
        }
    }

    public synchronized void withdraw(int accountNumber, double amount) throws SQLException {
        connection.setAutoCommit(false);
        try {
            String selectQuery = "SELECT balance FROM accounts WHERE accountNumber = ?";
            try (PreparedStatement selectStmt = connection.prepareStatement(selectQuery)) {
                selectStmt.setInt(1, accountNumber);
                ResultSet rs = selectStmt.executeQuery();
                if (rs.next()) {
                    double currentBalance = rs.getDouble("balance");
                    if (currentBalance >= amount) {
                        String updateQuery = "UPDATE accounts SET balance = balance - ? WHERE accountNumber = ?";
                        try (PreparedStatement updateStmt = connection.prepareStatement(updateQuery)) {
                            updateStmt.setDouble(1, amount);
                            updateStmt.setInt(2, accountNumber);
                            updateStmt.executeUpdate();
                            connection.commit();
                            System.out.println("Withdrawal successful.");
                        }
                    } else {
                        System.out.println("Insufficient balance.");
                    }
                } else {
                    System.out.println("Account not found.");
                }
            }
        } catch (SQLException e) {
            connection.rollback();
            throw e;
        } finally {
            connection.setAutoCommit(true);
        }
    }

    public synchronized void deleteAccount(int accountNumber) throws SQLException {
        String query = "DELETE FROM accounts WHERE accountNumber = ?";
        try (PreparedStatement pstmt = connection.prepareStatement(query)) {
            pstmt.setInt(1, accountNumber);
            int rowsAffected = pstmt.executeUpdate();
            if (rowsAffected > 0) {
                System.out.println("Account deleted successfully.");
            } else {
                System.out.println("Account not found.");
            }
        }
    }

    public synchronized void listAllAccounts() throws SQLException {
        String query = "SELECT * FROM accounts";
        try (Statement stmt = connection.createStatement()) {
            ResultSet rs = stmt.executeQuery(query);
            while (rs.next()) {
                BankAccount account = new BankAccount(rs.getInt("accountNumber"), rs.getString("accountHolderName"),
                        rs.getDouble("balance"));
                System.out.println(account);
            }
        }
    }
}

public class Main {
    public static void main(String[] args) {
        try (Scanner scanner = new Scanner(System.in)) {
            BankingSystem bankingSystem = new BankingSystem();
            int choice;

            do {
                System.out.println("\nWelcome to the Banking System");
                System.out.println("1. Create Account");
                System.out.println("2. View Account");
                System.out.println("3. Deposit Money");
                System.out.println("4. Withdraw Money");
                System.out.println("5. Delete Account");
                System.out.println("6. List All Accounts");
                System.out.println("7. Exit");
                System.out.print("Enter your choice: ");
                choice = scanner.nextInt();
                scanner.nextLine(); 

                switch (choice) {
                    case 1:
                        System.out.print("Enter account holder name: ");
                        String name = scanner.nextLine();
                        System.out.print("Enter initial deposit: ");
                        double deposit = scanner.nextDouble();
                        bankingSystem.createAccount(name, deposit);
                        break;

                    case 2:
                        System.out.print("Enter account number: ");
                        int viewAccNum = scanner.nextInt();
                        bankingSystem.viewAccount(viewAccNum);
                        break;

                    case 3:
                        System.out.print("Enter account number: ");
                        int depositAccNum = scanner.nextInt();
                        System.out.print("Enter amount to deposit: ");
                        double depositAmount = scanner.nextDouble();
                        bankingSystem.deposit(depositAccNum, depositAmount);
                        break;

                    case 4:
                        System.out.print("Enter account number: ");
                        int withdrawAccNum = scanner.nextInt();
                        System.out.print("Enter amount to withdraw: ");
                        double withdrawAmount = scanner.nextDouble();
                        bankingSystem.withdraw(withdrawAccNum, withdrawAmount);
                        break;

                    case 5:
                        System.out.print("Enter account number: ");
                        int deleteAccNum = scanner.nextInt();
                        bankingSystem.deleteAccount(deleteAccNum);
                        break;

                    case 6:
                        bankingSystem.listAllAccounts();
                        break;

                    case 7:
                        System.out.println("Thank you for using the Banking System.");
                        break;

                    default:
                        System.out.println("Invalid choice. Please try again.");
                }
            } while (choice != 7);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}

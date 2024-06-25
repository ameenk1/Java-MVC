import java.sql.*;
import java.util.Scanner;

public class CustomerManager {

    public void menu(Scanner scanner) {
        while (true) {
            System.out.println("Manage Customers:");
            System.out.println("1. Add Customer");
            System.out.println("2. Update Customer");
            System.out.println("3. Remove Customer");
            System.out.println("4. Search Customer");
            System.out.println("5. Display All Customers");
            System.out.println("6. Back to Main Menu");

            System.out.print("Choose an option: ");
            int choice = scanner.nextInt();
            scanner.nextLine(); // consume newline

            switch (choice) {
                case 1:
                    addCustomer(scanner);
                    break;
                case 2:
                    updateCustomer(scanner);
                    break;
                case 3:
                    removeCustomer(scanner);
                    break;
                case 4:
                    searchCustomer(scanner);
                    break;
                case 5:
                    displayAllCustomers();
                    break;
                case 6:
                    return;
                default:
                    System.out.println("Invalid choice. Try again.");
            }
        }
    }

    private void addCustomer(Scanner scanner) {
        System.out.print("Enter Customer ID: ");
        String id = scanner.nextLine();
        System.out.print("Enter Customer Name: ");
        String name = scanner.nextLine();
        System.out.print("Enter Email: ");
        String email = scanner.nextLine();
        System.out.print("Enter Address: ");
        String address = scanner.nextLine();
        System.out.print("Enter Contact Number: ");
        String contactNumber = scanner.nextLine();
        System.out.print("Enter Date of Birth (yyyy-mm-dd): ");
        String dob = scanner.nextLine();
        System.out.print("Enter Gender: ");
        String gender = scanner.nextLine();

        try (Connection con = DBConnector.getCon()) {
            String query = "INSERT INTO customers (id, name, email, address, contact_number, dob, gender) VALUES (?, ?, ?, ?, ?, ?, ?)";
            try (PreparedStatement ps = con.prepareStatement(query)) {
                ps.setString(1, id);
                ps.setString(2, name);
                ps.setString(3, email);
                ps.setString(4, address);
                ps.setString(5, contactNumber);
                ps.setString(6, dob);
                ps.setString(7, gender);
                ps.executeUpdate();
                System.out.println("Customer added successfully.");
            }
        } catch (ClassNotFoundException | SQLException e) {
            System.out.println("Error: " + e.getMessage());
        }
    }

    private void updateCustomer(Scanner scanner) {
        System.out.print("Enter Customer ID to update: ");
        String id = scanner.nextLine();

        System.out.print("Enter New Customer Name: ");
        String name = scanner.nextLine();
        System.out.print("Enter New Email: ");
        String email = scanner.nextLine();
        System.out.print("Enter New Address: ");
        String address = scanner.nextLine();
        System.out.print("Enter New Contact Number: ");
        String contactNumber = scanner.nextLine();
        System.out.print("Enter New Date of Birth (yyyy-mm-dd): ");
        String dob = scanner.nextLine();
        System.out.print("Enter New Gender: ");
        String gender = scanner.nextLine();

        try (Connection con = DBConnector.getCon()) {
            String query = "UPDATE customers SET name = ?, email = ?, address = ?, contact_number = ?, dob = ?, gender = ? WHERE id = ?";
            try (PreparedStatement ps = con.prepareStatement(query)) {
                ps.setString(1, name);
                ps.setString(2, email);
                ps.setString(3, address);
                ps.setString(4, contactNumber);
                ps.setString(5, dob);
                ps.setString(6, gender);
                ps.setString(7, id);
                int rowsUpdated = ps.executeUpdate();
                if (rowsUpdated > 0) {
                    System.out.println("Customer updated successfully.");
                } else {
                    System.out.println("Customer not found.");
                }
            }
        } catch (ClassNotFoundException | SQLException e) {
            System.out.println("Error: " + e.getMessage());
        }
    }

    private void removeCustomer(Scanner scanner) {
        System.out.print("Enter Customer ID to remove: ");
        String id = scanner.nextLine();

        try (Connection con = DBConnector.getCon()) {
            String query = "DELETE FROM customers WHERE id = ?";
            try (PreparedStatement ps = con.prepareStatement(query)) {
                ps.setString(1, id);
                int rowsDeleted = ps.executeUpdate();
                if (rowsDeleted > 0) {
                    System.out.println("Customer removed successfully.");
                } else {
                    System.out.println("Customer not found.");
                }
            }
        } catch (ClassNotFoundException | SQLException e) {
            System.out.println("Error: " + e.getMessage());
        }
    }

    private void searchCustomer(Scanner scanner) {
        System.out.print("Enter Customer ID to search: ");
        String id = scanner.nextLine();

        try (Connection con = DBConnector.getCon()) {
            String query = "SELECT * FROM customers WHERE id = ?";
            try (PreparedStatement ps = con.prepareStatement(query)) {
                ps.setString(1, id);
                try (ResultSet rs = ps.executeQuery()) {
                    if (rs.next()) {
                        String name = rs.getString("name");
                        String email = rs.getString("email");
                        String address = rs.getString("address");
                        String contactNumber = rs.getString("contact_number");
                        String dob = rs.getString("dob");
                        String gender = rs.getString("gender");
                        System.out.println("Customer ID: " + id);
                        System.out.println("Name: " + name);
                        System.out.println("Email: " + email);
                        System.out.println("Address: " + address);
                        System.out.println("Contact Number: " + contactNumber);
                        System.out.println("Date of Birth: " + dob);
                        System.out.println("Gender: " + gender);
                    } else {
                        System.out.println("Customer not found.");
                    }
                }
            }
        } catch (ClassNotFoundException | SQLException e) {
            System.out.println("Error: " + e.getMessage());
        }
    }

    private void displayAllCustomers() {
        try (Connection con = DBConnector.getCon()) {
            String query = "SELECT * FROM customers";
            try (Statement stmt = con.createStatement();
                 ResultSet rs = stmt.executeQuery(query)) {
                while (rs.next()) {
                    String id = rs.getString("id");
                    String name = rs.getString("name");
                    String email = rs.getString("email");
                    String address = rs.getString("address");
                    String contactNumber = rs.getString("contact_number");
                    String dob = rs.getString("dob");
                    String gender = rs.getString("gender");
                    System.out.println("Customer ID: " + id);
                    System.out.println("Name: " + name);
                    System.out.println("Email: " + email);
                    System.out.println("Address: " + address);
                    System.out.println("Contact Number: " + contactNumber);
                    System.out.println("Date of Birth: " + dob);
                    System.out.println("Gender: " + gender);
                    System.out.println("-------------------------------");
                }
            }
        } catch (ClassNotFoundException | SQLException e) {
            System.out.println("Error: " + e.getMessage());
        }
    }
}

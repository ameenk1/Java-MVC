import java.sql.*;
import java.util.Scanner;

public class ProductManager {

    public void menu(Scanner scanner) {
        while (true) {
            System.out.println("Manage Products:");
            System.out.println("1. Add Product");
            System.out.println("2. Update Product");
            System.out.println("3. Remove Product");
            System.out.println("4. Search Product");
            System.out.println("5. Display All Products");
            System.out.println("6. Back to Main Menu");

            System.out.print("Choose an option: ");
            int choice = scanner.nextInt();
            scanner.nextLine(); // consume newline

            switch (choice) {
                case 1:
                    addProduct(scanner);
                    break;
                case 2:
                    updateProduct(scanner);
                    break;
                case 3:
                    removeProduct(scanner);
                    break;
                case 4:
                    searchProduct(scanner);
                    break;
                case 5:
                    displayAllProducts();
                    break;
                case 6:
                    return;
                default:
                    System.out.println("Invalid choice. Try again.");
            }
        }
    }

    private void addProduct(Scanner scanner) {
        System.out.print("Enter Product ID: ");
        String id = scanner.nextLine();
        System.out.print("Enter Product Name: ");
        String name = scanner.nextLine();
        System.out.print("Enter Description: ");
        String description = scanner.nextLine();
        System.out.print("Enter Purchase Price: ");
        double purchasePrice = scanner.nextDouble();
        System.out.print("Enter Selling Price: ");
        double sellingPrice = scanner.nextDouble();
        System.out.print("Enter Quantity: ");
        int quantity = scanner.nextInt();
        scanner.nextLine(); // consume newline

        try (Connection con = DBConnector.getCon()) {
            String query = "INSERT INTO product (id, name, description, purchase_price, selling_price, quantity) VALUES (?, ?, ?, ?, ?, ?)";
            try (PreparedStatement ps = con.prepareStatement(query)) {
                ps.setString(1, id);
                ps.setString(2, name);
                ps.setString(3, description);
                ps.setDouble(4, purchasePrice);
                ps.setDouble(5, sellingPrice);
                ps.setInt(6, quantity);
                ps.executeUpdate();
                System.out.println("Product added successfully.");
            }
        } catch (ClassNotFoundException | SQLException e) {
            System.out.println("Error: " + e.getMessage());
        }
    }

    private void updateProduct(Scanner scanner) {
        System.out.print("Enter Product ID to update: ");
        String id = scanner.nextLine();

        System.out.print("Enter New Product Name: ");
        String name = scanner.nextLine();
        System.out.print("Enter New Description: ");
        String description = scanner.nextLine();
        System.out.print("Enter New Purchase Price: ");
        double purchasePrice = scanner.nextDouble();
        System.out.print("Enter New Selling Price: ");
        double sellingPrice = scanner.nextDouble();
        System.out.print("Enter New Quantity: ");
        int quantity = scanner.nextInt();
        scanner.nextLine(); // consume newline

        try (Connection con = DBConnector.getCon()) {
            String query = "UPDATE product SET name = ?, description = ?, purchase_price = ?, selling_price = ?, quantity = ? WHERE id = ?";
            try (PreparedStatement ps = con.prepareStatement(query)) {
                ps.setString(1, name);
                ps.setString(2, description);
                ps.setDouble(3, purchasePrice);
                ps.setDouble(4, sellingPrice);
                ps.setInt(5, quantity);
                ps.setString(6, id);
                int rowsUpdated = ps.executeUpdate();
                if (rowsUpdated > 0) {
                    System.out.println("Product updated successfully.");
                } else {
                    System.out.println("Product not found.");
                }
            }
        } catch (ClassNotFoundException | SQLException e) {
            System.out.println("Error: " + e.getMessage());
        }
    }

    private void removeProduct(Scanner scanner) {
        System.out.print("Enter Product ID to remove: ");
        String id = scanner.nextLine();

        try (Connection con = DBConnector.getCon()) {
            String query = "DELETE FROM product WHERE id = ?";
            try (PreparedStatement ps = con.prepareStatement(query)) {
                ps.setString(1, id);
                int rowsDeleted = ps.executeUpdate();
                if (rowsDeleted > 0) {
                    System.out.println("Product removed successfully.");
                } else {
                    System.out.println("Product not found.");
                }
            }
        } catch (ClassNotFoundException | SQLException e) {
            System.out.println("Error: " + e.getMessage());
        }
    }

    private void searchProduct(Scanner scanner) {
        System.out.print("Enter Product ID to search: ");
        String id = scanner.nextLine();

        try (Connection con = DBConnector.getCon()) {
            String query = "SELECT * FROM product WHERE id = ?";
            try (PreparedStatement ps = con.prepareStatement(query)) {
                ps.setString(1, id);
                try (ResultSet rs = ps.executeQuery()) {
                    if (rs.next()) {
                        String name = rs.getString("name");
                        String description = rs.getString("description");
                        double purchasePrice = rs.getDouble("purchase_price");
                        double sellingPrice = rs.getDouble("selling_price");
                        int quantity = rs.getInt("quantity");
                        System.out.println("Product ID: " + id);
                        System.out.println("Name: " + name);
                        System.out.println("Description: " + description);
                        System.out.println("Purchase Price: " + purchasePrice);
                        System.out.println("Selling Price: " + sellingPrice);
                        System.out.println("Quantity: " + quantity);
                    } else {
                        System.out.println("Product not found.");
                    }
                }
            }
        } catch (ClassNotFoundException | SQLException e) {
            System.out.println("Error: " + e.getMessage());
        }
    }

    private void displayAllProducts() {
        try (Connection con = DBConnector.getCon()) {
            String query = "SELECT * FROM product";
            try (Statement stmt = con.createStatement();
                 ResultSet rs = stmt.executeQuery(query)) {
                while (rs.next()) {
                    String id = rs.getString("id");
                    String name = rs.getString("name");
                    String description = rs.getString("description");
                    double purchasePrice = rs.getDouble("purchase_price");
                    double sellingPrice = rs.getDouble("selling_price");
                    int quantity = rs.getInt("quantity");
                    System.out.println("Product ID: " + id);
                    System.out.println("Name: " + name);
                    System.out.println("Description: " + description);
                    System.out.println("Purchase Price: " + purchasePrice);
                    System.out.println("Selling Price: " + sellingPrice);
                    System.out.println("Quantity: " + quantity);
                    System.out.println("-------------------------------");
                }
            }
        } catch (ClassNotFoundException | SQLException e) {
            System.out.println("Error: " + e.getMessage());
        }
    }
}

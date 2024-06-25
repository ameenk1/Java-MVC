import java.sql.*;
import java.util.*;
import java.util.Date;

public class InvoiceManager {

    public void menu(Scanner scanner, ProductManager productManager, CustomerManager customerManager) {
        while (true) {
            System.out.println("Invoice Generation:");
            System.out.println("1. Generate Invoice");
            System.out.println("2. View Invoices");
            System.out.println("3. Back to Main Menu");

            System.out.print("Choose an option: ");
            int choice = scanner.nextInt();
            scanner.nextLine(); // consume newline

            switch (choice) {
                case 1:
                    generateInvoice(scanner, productManager, customerManager);
                    break;
                case 2:
                    viewInvoices(scanner);
                    break;
                case 3:
                    return;
                default:
                    System.out.println("Invalid choice. Try again.");
            }
        }
    }

    private void generateInvoice(Scanner scanner, ProductManager productManager, CustomerManager customerManager) {
        System.out.print("Enter Customer ID: ");
        String customerId = scanner.nextLine();

        try (Connection con = DBConnector.getCon()) {
            String customerQuery = "SELECT * FROM customers WHERE id = ?";
            try (PreparedStatement ps = con.prepareStatement(customerQuery)) {
                ps.setString(1, customerId);
                try (ResultSet rs = ps.executeQuery()) {
                    if (!rs.next()) {
                        System.out.println("Customer not found.");
                        return;
                    }
                }
            }

            List<InvoiceItem> items = new ArrayList<>();
            while (true) {
                System.out.print("Enter Product ID (or 'done' to finish): ");
                String productId = scanner.nextLine();
                if ("done".equalsIgnoreCase(productId)) break;

                System.out.print("Enter Quantity: ");
                int quantity = scanner.nextInt();
                scanner.nextLine(); // consume newline

                String productQuery = "SELECT * FROM product WHERE id = ?";
                try (PreparedStatement ps = con.prepareStatement(productQuery)) {
                    ps.setString(1, productId);
                    try (ResultSet rs = ps.executeQuery()) {
                        if (!rs.next()) {
                            System.out.println("Product not found.");
                            continue;
                        }

                        int availableQuantity = rs.getInt("quantity");
                        if (quantity > availableQuantity) {
                            System.out.println("Insufficient stock. Available quantity: " + availableQuantity);
                            continue;
                        }

                        String productName = rs.getString("name");
                        double unitPrice = rs.getDouble("selling_price");
                        double totalPrice = unitPrice * quantity;
                        double discount = calculateDiscount(quantity, unitPrice);

                        items.add(new InvoiceItem(productId, productName, quantity, unitPrice, totalPrice, discount));

                        // Update product quantity
                        String updateProductQuery = "UPDATE product SET quantity = ? WHERE id = ?";
                        try (PreparedStatement updatePs = con.prepareStatement(updateProductQuery)) {
                            updatePs.setInt(1, availableQuantity - quantity);
                            updatePs.setString(2, productId);
                            updatePs.executeUpdate();
                        }
                    }
                }
            }

            if (items.isEmpty()) {
                System.out.println("No items added to the invoice.");
                return;
            }

            // Generate and save the invoice
            String invoiceId = UUID.randomUUID().toString();
            Date invoiceDate = new Date();
            double totalAmount = items.stream().mapToDouble(item -> item.totalPrice - item.discount).sum();

            String insertInvoiceQuery = "INSERT INTO invoices (id, customer_id, date, total_amount) VALUES (?, ?, ?, ?)";
            try (PreparedStatement ps = con.prepareStatement(insertInvoiceQuery)) {
                ps.setString(1, invoiceId);
                ps.setString(2, customerId);
                ps.setDate(3, new java.sql.Date(invoiceDate.getTime()));
                ps.setDouble(4, totalAmount);
                ps.executeUpdate();
            }

            String insertInvoiceItemQuery = "INSERT INTO invoice_items (invoice_id, product_id, quantity, unit_price, total_price, discount) VALUES (?, ?, ?, ?, ?, ?)";
            try (PreparedStatement ps = con.prepareStatement(insertInvoiceItemQuery)) {
                for (InvoiceItem item : items) {
                    ps.setString(1, invoiceId);
                    ps.setString(2, item.productId);
                    ps.setInt(3, item.quantity);
                    ps.setDouble(4, item.unitPrice);
                    ps.setDouble(5, item.totalPrice);
                    ps.setDouble(6, item.discount);
                    ps.addBatch();
                }
                ps.executeBatch();
            }

            System.out.println("Invoice generated successfully.");
        } catch (ClassNotFoundException | SQLException e) {
            System.out.println("Error: " + e.getMessage());
        }
    }

    private void viewInvoices(Scanner scanner) {
        System.out.print("Enter Customer ID (or 'all' to view all invoices): ");
        String customerId = scanner.nextLine();

        String query = "SELECT i.id, i.date, i.total_amount, c.name AS customer_name " +
                "FROM invoices i " +
                "JOIN customers c ON i.customer_id = c.id";

        if (!"all".equalsIgnoreCase(customerId)) {
            query += " WHERE i.customer_id = ?";
        }

        try (Connection con = DBConnector.getCon();
             PreparedStatement ps = con.prepareStatement(query)) {

            if (!"all".equalsIgnoreCase(customerId)) {
                ps.setString(1, customerId);
            }

            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    String invoiceId = rs.getString("id");
                    Date invoiceDate = rs.getDate("date");
                    double totalAmount = rs.getDouble("total_amount");
                    String customerName = rs.getString("customer_name");

                    System.out.println("Invoice ID: " + invoiceId);
                    System.out.println("Date: " + invoiceDate);
                    System.out.println("Customer: " + customerName);
                    System.out.println("Total Amount: " + totalAmount);
                    System.out.println("-------------------------------");

                    displayInvoiceItems(invoiceId);
                }
            }
        } catch (ClassNotFoundException | SQLException e) {
            System.out.println("Error: " + e.getMessage());
        }
    }

    private void displayInvoiceItems(String invoiceId) {
        String query = "SELECT * FROM invoice_items WHERE invoice_id = ?";

        try (Connection con = DBConnector.getCon();
             PreparedStatement ps = con.prepareStatement(query)) {
            ps.setString(1, invoiceId);

            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    String productId = rs.getString("product_id");
                    int quantity = rs.getInt("quantity");
                    double unitPrice = rs.getDouble("unit_price");
                    double totalPrice = rs.getDouble("total_price");
                    double discount = rs.getDouble("discount");

                    System.out.println("Product ID: " + productId);
                    System.out.println("Quantity: " + quantity);
                    System.out.println("Unit Price: " + unitPrice);
                    System.out.println("Total Price: " + totalPrice);
                    System.out.println("Discount: " + discount);
                    System.out.println("-------------------------------");
                }
            }
        } catch (ClassNotFoundException | SQLException e) {
            System.out.println("Error: " + e.getMessage());
        }
    }

    private double calculateDiscount(int quantity, double unitPrice) {
        // Implement your discount logic here
        return 0; // Placeholder, no discount applied
    }

    private static class InvoiceItem {
        String productId;
        String productName;
        int quantity;
        double unitPrice;
        double totalPrice;
        double discount;

        public InvoiceItem(String productId, String productName, int quantity, double unitPrice, double totalPrice, double discount) {
            this.productId = productId;
            this.productName = productName;
            this.quantity = quantity;
            this.unitPrice = unitPrice;
            this.totalPrice = totalPrice;
            this.discount = discount;
        }
    }
}


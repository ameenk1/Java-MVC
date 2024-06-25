import java.util.Scanner;

public class Main {
    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        ProductManager productManager = new ProductManager();
        CustomerManager customerManager = new CustomerManager();
        InvoiceManager invoiceManager = new InvoiceManager();

        while (true) {
            System.out.println("Main Menu:");
            System.out.println("1. Manage Products");
            System.out.println("2. Manage Customers");
            System.out.println("3. Invoice Generation");
            System.out.println("4. Admin Tasks");
            System.out.println("5. Exit");

            System.out.print("Choose an option: ");
            int choice = scanner.nextInt();
            scanner.nextLine(); // consume newline

            switch (choice) {
                case 1:
                    productManager.menu(scanner);
                    break;
                case 2:
                    customerManager.menu(scanner);
                    break;
                case 3:
                    invoiceManager.menu(scanner, productManager, customerManager);
                    break;
                case 4:
                    adminTasks();
                    break;
                case 5:
                    System.exit(0);
                    break;
                default:
                    System.out.println("Invalid choice. Try again.");
            }
        }
    }

    private static void adminTasks() {
        // Implement admin tasks here
        System.out.println("Admin tasks not implemented yet.");
    }
}
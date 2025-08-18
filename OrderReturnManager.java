package Order_return_manager;

import java.sql.*;
import java.util.*;
import java.io.*;

public class OrderReturnManager {
    private static final Scanner scanner = new Scanner(System.in);
    private static final String ADMIN_USER = "admin";
    private static final String ADMIN_PASS = "admin123";

    public static void main(String[] args) {
        if (!authenticateAdmin()) return;

        int choice;
        do {
            System.out.println("\n==== Order Return Manager ====");
            System.out.println("1. Add Return Request");
            System.out.println("2. View All Returns");
            System.out.println("3. Filter by Status");
            System.out.println("4. Update Return Status");
            System.out.println("5. Delete Return Request");
            System.out.println("6. Export to CSV");
            System.out.println("7. Exit");
            System.out.print("Enter your choice: ");
            choice = validateIntInput();

            switch (choice) {
                case 1: addReturn(); break;
                case 2: viewReturns(); break;
                case 3: filterByStatus(); break;
                case 4: updateReturn(); break;
                case 5: deleteReturn(); break;
                case 6: exportToCSV(); break;
                case 7: System.out.println("Exiting..."); break;
                default: System.out.println("Invalid choice.");
            }
        } while (choice != 7);
    }

    private static boolean authenticateAdmin() {
        System.out.println("=== Admin Login ===");
        System.out.print("Username: ");
        String username = scanner.nextLine().trim();
        System.out.print("Password: ");
        String password = scanner.nextLine().trim();

        if (username.equals(ADMIN_USER) && password.equals(ADMIN_PASS)) {
            System.out.println("Login successful.\n");
            return true;
        } else {
            System.out.println("Authentication failed. Exiting...");
            return false;
        }
    }

    private static void addReturn() {
        System.out.print("Enter Order ID: ");
        String orderId = scanner.nextLine().trim();
        if (orderId.isEmpty()) {
            System.out.println("Order ID cannot be empty.");
            return;
        }

        System.out.print("Enter Customer Name: ");
        String name = scanner.nextLine().trim();
        if (name.isEmpty()) {
            System.out.println("Customer name cannot be empty.");
            return;
        }

        System.out.print("Enter Reason for Return: ");
        String reason = scanner.nextLine().trim();
        if (reason.isEmpty()) {
            System.out.println("Reason cannot be empty.");
            return;
        }

        String sql = "INSERT INTO return_requests (order_id, customer_name, reason) VALUES (?, ?, ?)";

        try (Connection conn = DBConnection.getConnection1();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, orderId);
            stmt.setString(2, name);
            stmt.setString(3, reason);
            int rows = stmt.executeUpdate();

            System.out.println(rows > 0 ? "Return request added." : "Failed to add return.");
        } catch (SQLException e) {
            System.out.println("Database error: " + e.getMessage());
        }
    }

    private static void viewReturns() {
        String sql = "SELECT * FROM return_requests";
        try (Connection conn = DBConnection.getConnection1();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            List<ReturnRequest> requests = new ArrayList<>();
            while (rs.next()) {
                requests.add(new ReturnRequest(
                        rs.getInt("id"),
                        rs.getString("order_id"),
                        rs.getString("customer_name"),
                        rs.getString("reason"),
                        rs.getString("status")
                ));
            }

            if (requests.isEmpty()) {
                System.out.println("No return requests found.");
            } else {
                requests.forEach(System.out::println);
            }
        } catch (SQLException e) {
            System.out.println("Database error: " + e.getMessage());
        }
    }

    private static void filterByStatus() {
        System.out.print("Enter status to filter (Pending/Approved/Rejected): ");
        String status = scanner.nextLine().trim();

        String sql = "SELECT * FROM return_requests WHERE status = ?";
        try (Connection conn = DBConnection.getConnection1();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, status);
            ResultSet rs = stmt.executeQuery();

            List<ReturnRequest> filtered = new ArrayList<>();
            while (rs.next()) {
                filtered.add(new ReturnRequest(
                        rs.getInt("id"),
                        rs.getString("order_id"),
                        rs.getString("customer_name"),
                        rs.getString("reason"),
                        rs.getString("status")
                ));
            }

            if (filtered.isEmpty()) {
                System.out.println("No requests with status: " + status);
            } else {
                filtered.forEach(System.out::println);
            }
        } catch (SQLException e) {
            System.out.println("Error: " + e.getMessage());
        }
    }

    private static void updateReturn() {
        System.out.print("Enter Return ID to update: ");
        int id = validateIntInput();

        System.out.print("Enter new status (Pending/Approved/Rejected): ");
        String status = scanner.nextLine().trim();

        String sql = "UPDATE return_requests SET status = ? WHERE id = ?";
        try (Connection conn = DBConnection.getConnection1();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, status);
            stmt.setInt(2, id);
            int rows = stmt.executeUpdate();

            System.out.println(rows > 0 ? "Status updated." : "Return ID not found.");
        } catch (SQLException e) {
            System.out.println("Error: " + e.getMessage());
        }
    }

    private static void deleteReturn() {
        System.out.print("Enter Return ID to delete: ");
        int id = validateIntInput();

        String sql = "DELETE FROM return_requests WHERE id = ?";
        try (Connection conn = DBConnection.getConnection1();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, id);
            int rows = stmt.executeUpdate();

            System.out.println(rows > 0 ? "Request deleted." : "ID not found.");
        } catch (SQLException e) {
            System.out.println("Error: " + e.getMessage());
        }
    }

    private static void exportToCSV() {
        String sql = "SELECT * FROM return_requests";
        try (Connection conn = DBConnection.getConnection1();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql);
             PrintWriter writer = new PrintWriter("return_requests.csv")) {

            writer.println("ID,OrderID,CustomerName,Reason,Status");

            while (rs.next()) {
                writer.printf("%d,%s,%s,%s,%s%n",
                        rs.getInt("id"),
                        rs.getString("order_id"),
                        rs.getString("customer_name"),
                        rs.getString("reason").replace(",", " "),
                        rs.getString("status"));
            }
            System.out.println("Data exported to return_requests.csv");
        } catch (Exception e) {
            System.out.println("Export error: " + e.getMessage());
        }
    }

    private static int validateIntInput() {
        while (true) {
            try {
                return Integer.parseInt(scanner.nextLine().trim());
            } catch (NumberFormatException e) {
                System.out.print("Invalid input. Enter a number: ");
            }
        }


    }
}

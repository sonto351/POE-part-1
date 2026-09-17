import java.util.Scanner;

public class Main {

    public static String storedFirstName;
    public static String storedLastName;
    public static String storedUsername;
    public static String storedPassword;
    public static String storedCellNumber;

    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);

        // REGISTRATION
        System.out.println("=== REGISTER ===");

        System.out.print("Enter first name: ");
        storedFirstName = scanner.next();

        System.out.print("Enter Last name: ");
        storedLastName = scanner.next();

        // Username
        while (true) {
            System.out.print("Create a username (must contain '_' and max 5 chars): ");
            String username = scanner.next();
            if (username.length() <= 5 && username.contains("_")) {
                storedUsername = username;
                System.out.println("Username captured");
                break;
            } else {
                System.out.println("Username is not correctly formatted. Must contain _ and be max 5 chars.");
            }
        }

        // Password
        while (true) {
            System.out.print("Create a password (8+ chars, upper, lower, digit, special): ");
            String password = scanner.next();
            if (Login.checkPasswordComplexity(password)) {
                storedPassword = password;
                System.out.println("Password captured");
                break;
            } else {
                System.out.println("Password is not correctly formatted.");
            }
        }

        // Cell number
        while (true) {
            System.out.print("Enter cell number (+27 and max 12 chars): ");
            String cell = scanner.next();
            if (cell.startsWith("+27") && cell.length() <= 12) {
                storedCellNumber = cell;
                System.out.println("Cell number captured");
                break;
            } else {
                System.out.println("Cell number is not correctly formatted. Must start with +27.");
            }
        }

        System.out.println("\nRegistration Successful!\n");

        // Go to login page
        Login loginPage = new Login();
        loginPage.loginUser(scanner);

        scanner.close();
    }
}
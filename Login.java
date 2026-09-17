public class Login {

    // Password validation logic lives here now
    public static boolean checkPasswordComplexity(String password) {
        if (password == null || password.length() < 8) {
            return false;
        }
        boolean hasUpper = !password.equals(password.toLowerCase());
        boolean hasLower = !password.equals(password.toUpperCase());
        boolean hasDigit = password.matches(".*\\d.*");
        boolean hasSpecial = password.matches(".*[^a-zA-Z0-9].*");

        return hasUpper && hasLower && hasDigit && hasSpecial;
    }

    public void loginUser(java.util.Scanner scanner) {
        System.out.println("=== LOGIN ===");
        boolean loggedIn = false;

        while (!loggedIn) {
            System.out.print("Enter username to login: ");
            String userLogin = scanner.next();

            System.out.print("Enter password to login: ");
            String passLogin = scanner.next();

            if (userLogin.equals(Main.storedUsername) && passLogin.equals(Main.storedPassword)) {
                System.out.println("Login successful");
                System.out.println("Welcome back " + Main.storedFirstName +" "+Main.storedLastName +"It's nice to see you again");
                loggedIn = true;
            } else {
                System.out.println("Login incorrect - username or password wrong, try again");
            }
        }
    }
}
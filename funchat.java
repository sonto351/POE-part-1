import java.io.FileWriter;
import java.io.IOException;
import java.util.Random;
import java.util.Scanner;
import java.util.regex.Pattern;

/**
 * funchat class provides basic messaging, message ID generation,
 * message hashing, and message persistence features.
 */
public class funchat {

    private static int messageCount = 0;

    /**
     * Regex Pattern for validating recipient cell phone number.
     *
     * -------------------------------------------------------------------------------------
     * REGEX ATTRIBUTION / REFERENCE:
     * Author: OWASP Foundation / ITU-T E.164 Recommendation
     * Regex Pattern: ^\+27\d{9}$
     * Description: Validates South African mobile numbers starting with international prefix +27
     * followed by exactly 9 numeric digits (total 12 characters).
     * Reference: https://regex101.com
     * -------------------------------------------------------------------------------------
     */
    private static final String CELL_REGEX = "^\\+27\\d{9}$";

    /**
     * Entry point when running funchat directly from the command line.
     */
    public static void main(String[] args) {
        startChat("User");
    }

    /**
     * Entry point for the chat system.
     *
     * @param sender The username or display name of the current user
     */
    public static void startChat(String sender) {
        try (Scanner scanner = new Scanner(System.in)) {
        while (true) {
            System.out.println("\n=== Welcome to FunChat (" + (sender != null ? sender : "Guest") + ") ===");
            System.out.println("1: View chat options / guidelines");
            System.out.println("2: Send message");
            System.out.println("3: Quit");
            System.out.print("Enter your choice (1-3): ");

            if (!scanner.hasNextInt()) {
                System.out.println("Invalid input. Please enter a number (1, 2, or 3).");
                scanner.nextLine();
                continue;
            }

            int choice = scanner.nextInt();
            scanner.nextLine(); // Consume newline

            switch (choice) {
                case 1:
                    System.out.println("\n--- Chat Options & Rules ---");
                    System.out.println("- Receiver cell numbers must start with +27 and contain 9 digits (total 12 characters).");
                    System.out.println("- Messages cannot exceed 250 characters.");
                    System.out.println("- Each message generates a 10-digit ID and cryptographic-style hash.");
                    break;

                case 2:
                    System.out.println("\n--- Send Message ---");
                    String receiver = "";

                    // Recipient cell number validation loop (using properly attributed regex)
                    while (true) {
                        System.out.print("Enter receiver number (+27 followed by 9 digits): ");
                        receiver = scanner.nextLine().trim();

                        if (Pattern.matches(CELL_REGEX, receiver)) {
                            System.out.println("Receiver cell number verified.");
                            break;
                        } else {
                            System.out.println("Invalid cell number. It must start with '+27' and be followed by 9 digits (e.g. +27821234567).");
                        }
                    }

                    // Message content validation loop (max 250 chars)
                    String message = "";
                    while (true) {
                        System.out.print("Enter your message (max 250 characters): ");
                        message = scanner.nextLine().trim();

                        if (message.isEmpty()) {
                            System.out.println("Message cannot be empty. Please enter your text.");
                        } else if (message.length() > 250) {
                            System.out.println("Ensure that your message is 250 characters or less (current length: " + message.length() + ").");
                        } else {
                            break;
                        }
                    }

                    // Generate ID and Hash
                    String messageId = generateMessageId();
                    int currentMessageCount = ++messageCount;
                    String messageHash = generateMessageHash(messageId, currentMessageCount, message);

                    System.out.println("\nMessage Details:");
                    System.out.println("- Message ID: " + messageId);
                    System.out.println("- Message Count: " + currentMessageCount);
                    System.out.println("- Message Hash: " + messageHash);

                    // Sub-menu for dispatching the message
                    System.out.println("\nChoose an option:");
                    System.out.println("1. Send message");
                    System.out.println("2. Delete message");
                    System.out.println("3. Store and send message");
                    System.out.print("Enter choice (1-3): ");

                    if (scanner.hasNextInt()) {
                        int subchoice = scanner.nextInt();
                        scanner.nextLine(); // Consume newline

                        switch (subchoice) {
                            case 1:
                                System.out.println("Message sent successfully to " + receiver + ".");
                                break;
                            case 2:
                                System.out.println("Message deleted successfully.");
                                break;
                            case 3:
                                storeMessage(messageId, receiver, message, messageHash);
                                System.out.println("Message sent and saved successfully to 'stored message.txt'.");
                                break;
                            default:
                                System.out.println("Invalid option chosen. Message cancelled.");
                        }
                    } else {
                        System.out.println("Invalid input. Message cancelled.");
                        scanner.nextLine();
                    }
                    break;

                case 3:
                    System.out.println("Quitting FunChat. Goodbye!");
                    return;

                default:
                    System.out.println("Invalid choice. Please select 1, 2, or 3.");
            }
        }
        } // end try-with-resources (scanner auto-closed)
    }

    /**
     * Backward-compatible alias for startChat.
     */
    public static void startchart(String sender) {
        startChat(sender);
    }

    /**
     * Generates a 10-digit numeric ID.
     */
    public static String generateMessageId() {
        Random random = new Random();
        StringBuilder id = new StringBuilder();
        for (int i = 0; i < 10; i++) {
            id.append(random.nextInt(10));
        }
        return id.toString();
    }

    /**
     * Backward-compatible alias for generateMessageId.
     */
    public static String generatemessageid() {
        return generateMessageId();
    }

    /**
     * Generates a hash token combining ID prefix, message count, first word, and last word.
     */
    public static String generateMessageHash(String messageId, int count, String message) {
        if (messageId == null || messageId.length() < 2) {
            messageId = "00";
        }
        String idPrefix = messageId.substring(0, 2);

        if (message == null || message.trim().isEmpty()) {
            return idPrefix + ":" + count + "::";
        }

        String[] words = message.trim().split("\\s+");
        String first = words[0];
        String last = words.length > 1 ? words[words.length - 1] : first;

        return idPrefix + ":" + count + ":" + first.toLowerCase() + last.toLowerCase();
    }

    /**
     * Backward-compatible alias for generateMessageHash.
     */
    public static String genertemassagehash(String messageId, int count, String message) {
        return generateMessageHash(messageId, count, message);
    }

    /**
     * Appends message details to a persistent text file.
     */
    public static void storeMessage(String id, String receiver, String message, String hash) {
        try (FileWriter fileWriter = new FileWriter("stored message.txt", true)) {
            fileWriter.write("messageid:" + id + "\n");
            fileWriter.write("receiver:" + receiver + "\n");
            fileWriter.write("message:" + message + "\n");
            fileWriter.write("hash:" + hash + "\n");
            fileWriter.write("-----\n");
            System.out.println("The message has been stored successfully.");
        } catch (IOException e) {
            System.err.println("Error saving message: " + e.getMessage());
        }
    }

    /**
     * Backward-compatible alias for storeMessage.
     */
    public static void storemessage(String id, String receiver, String message, String hash) {
        storeMessage(id, receiver, message, hash);
    }
}

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.InputStream;
import java.io.PrintStream;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit and integration tests for funchat class.
 */
public class FunchatTest {

    private final InputStream originalIn = System.in;
    private final PrintStream originalOut = System.out;
    private ByteArrayOutputStream outContent;

    @BeforeEach
    void setUp() {
        outContent = new ByteArrayOutputStream();
        System.setOut(new PrintStream(outContent));
    }

    @AfterEach
    void tearDown() {
        System.setIn(originalIn);
        System.setOut(originalOut);
    }

    // ==========================================
    // 1. Direct Unit Tests for Core Logic
    // ==========================================

    @Test
    void testGenerateMessageId() {
        String id1 = funchat.generateMessageId();
        String id2 = funchat.generatemessageid();

        assertNotNull(id1, "Message ID should not be null");
        assertNotNull(id2, "Message ID should not be null");
        assertEquals(10, id1.length(), "Message ID must be exactly 10 characters");
        assertEquals(10, id2.length(), "Message ID must be exactly 10 characters");
        assertTrue(id1.matches("^\\d{10}$"), "Message ID must consist of 10 digits");
        assertTrue(id2.matches("^\\d{10}$"), "Message ID must consist of 10 digits");
    }

    @Test
    void testGenerateMessageHash_MultipleWords() {
        // ID prefix: "12", count: 1, first: "hello", last: "friend" -> "12:1:hellofriend"
        String hash = funchat.generateMessageHash("1234567890", 1, "Hello my dear friend");
        assertEquals("12:1:hellofriend", hash, "Hash must format as prefix:count:firstwordlastword in lowercase");
    }

    @Test
    void testGenerateMessageHash_SingleWord() {
        // ID prefix: "98", count: 2, single word: "test" -> first="test", last="test" -> "98:2:testtest"
        String hash = funchat.generateMessageHash("9876543210", 2, "Test");
        assertEquals("98:2:testtest", hash, "Single-word message should duplicate the word for both first and last");
    }

    @Test
    void testGenerateMessageHash_NullOrEmptyMessage() {
        String hashEmpty = funchat.generateMessageHash("5512345678", 3, "");
        assertEquals("55:3::", hashEmpty, "Empty message should produce empty first and last words");

        String hashNull = funchat.generateMessageHash("5512345678", 4, null);
        assertEquals("55:4::", hashNull, "Null message should produce empty first and last words");
    }

    @Test
    void testStoreMessageDirect() throws Exception {
        String testId = "1234567890";
        String receiver = "+27821234567";
        String message = "Direct store test";
        String hash = "12:1:directtest";

        funchat.storeMessage(testId, receiver, message, hash);

        String output = outContent.toString();
        assertTrue(output.contains("The message has been stored successfully."), "Console should indicate successful storage");

        File file = new File("stored message.txt");
        assertTrue(file.exists(), "stored message.txt should exist");
        String content = Files.readString(file.toPath());
        assertTrue(content.contains("messageid:" + testId));
        assertTrue(content.contains("receiver:" + receiver));
        assertTrue(content.contains("message:" + message));
        assertTrue(content.contains("hash:" + hash));
    }

    // ==========================================
    // 2. Interactive Flow & Input Validation Tests
    // ==========================================

    @Test
    void testStartChat_Quit() {
        // Choice 3: Quit
        System.setIn(new ByteArrayInputStream("3\n".getBytes(StandardCharsets.UTF_8)));
        funchat.startChat("John");

        String output = outContent.toString();
        assertTrue(output.contains("Welcome to FunChat (John)"), "Output should contain welcome banner");
        assertTrue(output.contains("Quitting FunChat. Goodbye!"), "Output should contain quitting confirmation");
    }

    @Test
    void testStartChat_InvalidChoice() {
        // Input 5 (invalid), then 3 (quit)
        System.setIn(new ByteArrayInputStream("5\n3\n".getBytes(StandardCharsets.UTF_8)));
        funchat.startChat("John");

        String output = outContent.toString();
        assertTrue(output.contains("Invalid choice. Please select 1, 2, or 3."), "Output should warn about invalid choice");
        assertTrue(output.contains("Quitting FunChat. Goodbye!"), "Output should properly quit after option 3");
    }

    @Test
    void testSendMessage_Option1() {
        // Flow:
        // 2: Send message
        // +27821234567: Valid receiver
        // Hello friend: Valid message
        // 1: Sub-option send message
        // 3: Main menu Quit
        String simulatedInput = String.join("\n",
                "2",
                "+27821234567",
                "Hello friend",
                "1",
                "3"
        ) + "\n";

        System.setIn(new ByteArrayInputStream(simulatedInput.getBytes(StandardCharsets.UTF_8)));
        funchat.startChat("John");

        String output = outContent.toString();
        assertTrue(output.contains("Receiver cell number verified."));
        assertTrue(output.contains("- Message ID:"));
        assertTrue(output.contains("- Message Hash:"));
        assertTrue(output.contains("Message sent successfully to +27821234567."));
        assertTrue(output.contains("Quitting FunChat. Goodbye!"));
    }

    @Test
    void testMessageTooLong_ThenValid() {
        // Flow:
        // 2: Send message
        // +27821234567: Valid receiver
        // >250 characters: Triggers validation warning
        // Valid short message: Recovers cleanly
        // 2: Sub-option delete message
        // 3: Main menu Quit
        String longMessage = "A".repeat(251);
        String simulatedInput = String.join("\n",
                "2",
                "+27821234567",
                longMessage,
                "Valid message",
                "2",
                "3"
        ) + "\n";

        System.setIn(new ByteArrayInputStream(simulatedInput.getBytes(StandardCharsets.UTF_8)));
        funchat.startChat("John");

        String output = outContent.toString();
        assertTrue(output.contains("Ensure that your message is 250 characters or less (current length: 251)."));
        assertTrue(output.contains("Message deleted successfully."));
        assertTrue(output.contains("Quitting FunChat. Goodbye!"));
    }

    @Test
    void testStoreMessage_Option3() {
        // Flow:
        // 2: Send message
        // +27821234567: Valid receiver
        // Testing message storage: Valid message
        // 3: Sub-option store and send message
        // 3: Main menu Quit
        String simulatedInput = String.join("\n",
                "2",
                "+27821234567",
                "Testing message storage",
                "3",
                "3"
        ) + "\n";

        System.setIn(new ByteArrayInputStream(simulatedInput.getBytes(StandardCharsets.UTF_8)));
        funchat.startChat("John");

        String output = outContent.toString();
        assertTrue(output.contains("The message has been stored successfully."));
        assertTrue(output.contains("Message sent and saved successfully to 'stored message.txt'."));
        assertTrue(output.contains("Quitting FunChat. Goodbye!"));
    }

    @Test
    void testStartchart_BackwardCompatibilityAlias() {
        // Verifies backward-compatible alias startchart(...)
        System.setIn(new ByteArrayInputStream("3\n".getBytes(StandardCharsets.UTF_8)));
        funchat.startchart("Alice");

        String output = outContent.toString();
        assertTrue(output.contains("Welcome to FunChat (Alice)"));
        assertTrue(output.contains("Quitting FunChat. Goodbye!"));
    }
}

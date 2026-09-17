import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.io.PrintStream;
import java.util.Scanner;

import static org.junit.jupiter.api.Assertions.*;

class LoginTest {

    private final InputStream originalIn = System.in;
    private final PrintStream originalOut = System.out;
    private ByteArrayOutputStream outContent;

    @BeforeEach
    void setUp() {
        outContent = new ByteArrayOutputStream();
        System.setOut(new PrintStream(outContent));

        // Initialize static variables in Main expected by Login
        Main.storedFirstName = "John";
        Main.storedLastName = "Doe";
        Main.storedUsername = "j_doe";
        Main.storedPassword = "Ch@ngeM3!";
        Main.storedCellNumber = "+27821234567";
    }

    @AfterEach
    void tearDown() {
        System.setIn(originalIn);
        System.setOut(originalOut);
    }

    @Test
    void testCheckPasswordComplexity_Valid() {
        assertTrue(Login.checkPasswordComplexity("Ch@ngeM3!"));
    }

    @ParameterizedTest
    @ValueSource(strings = {
            "Short1!",         // Less than 8 characters
            "lowercase1!",     // Missing uppercase
            "UPPERCASE1!",     // Missing lowercase
            "NoDigitsHere!",   // Missing digit
            "NoSpecialChar1"   // Missing special character
    })
    void testCheckPasswordComplexity_Invalid(String invalidPassword) {
        assertFalse(Login.checkPasswordComplexity(invalidPassword));
    }

    @Test
    void testCheckPasswordComplexity_Null() {
        assertFalse(Login.checkPasswordComplexity(null));
    }

    @Test
    void testLoginUser_SuccessOnFirstAttempt() {
        String simulatedInput = "j_doe\nCh@ngeM3!\n";
        Scanner scanner = new Scanner(new ByteArrayInputStream(simulatedInput.getBytes()));

        Login login = new Login();
        login.loginUser(scanner);

        String output = outContent.toString();
        assertTrue(output.contains("Login successful"));
        assertTrue(output.contains("Welcome back John DoeIt's nice to see you again"));
    }

    @Test
    void testLoginUser_RetryAfterFailedAttempt() {
        String simulatedInput = "wrongUser\nwrongPass\nj_doe\nCh@ngeM3!\n";
        Scanner scanner = new Scanner(new ByteArrayInputStream(simulatedInput.getBytes()));

        Login login = new Login();
        login.loginUser(scanner);

        String output = outContent.toString();
        assertTrue(output.contains("Login incorrect - username or password wrong, try again"));
        assertTrue(output.contains("Login successful"));
    }
}
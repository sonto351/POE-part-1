import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.io.PrintStream;

import static org.junit.jupiter.api.Assertions.*;

class MainTest {

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

    @Test
    void testMain_FullRegistrationAndLoginFlow() {
        // Sequence: First, Last, Bad User, Good User, Bad Pass, Good Pass, Bad Cell, Good Cell, Login User, Login Pass
        String simulatedInputs = String.join("\n",
                "Jane",
                "Smith",
                "invalidusername", // Fails username format (no '_')
                "j_smi",           // Valid username
                "weakpass",        // Fails password complexity
                "P@ssw0rd1",       // Valid password
                "0821234567",      // Fails cell format (no +27)
                "+27821234567",    // Valid cell
                "j_smi",           // Login username
                "P@ssw0rd1"        // Login password
        ) + "\n";

        System.setIn(new ByteArrayInputStream(simulatedInputs.getBytes()));

        Main.main(new String[]{});

        assertEquals("Jane", Main.storedFirstName);
        assertEquals("Smith", Main.storedLastName);
        assertEquals("j_smi", Main.storedUsername);
        assertEquals("P@ssw0rd1", Main.storedPassword);
        assertEquals("+27821234567", Main.storedCellNumber);

        String output = outContent.toString();
        assertTrue(output.contains("Username is not correctly formatted."));
        assertTrue(output.contains("Password is not correctly formatted."));
        assertTrue(output.contains("Cell number is not correctly formatted."));
        assertTrue(output.contains("Registration Successful!"));
        assertTrue(output.contains("Login successful"));
    }
}
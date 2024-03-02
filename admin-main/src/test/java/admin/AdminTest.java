package admin;

import org.junit.jupiter.api.Test;

import java.util.Map;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;

import static org.junit.jupiter.api.Assertions.*;

public class AdminTest {

    @Test
    public void testParseJSON() {
        String testJson = "{\"username\": \"admin\", \"password\": \"secret\"}";

        Map<String, String> result = Admin.parseJSON(testJson);

        assertEquals("admin", result.get("username"));
        assertEquals("secret", result.get("password"));
    }

    private final PrintStream originalOut = System.out;
    private final InputStream originalIn = System.in;
    private ByteArrayOutputStream testOut;
    private ByteArrayInputStream testIn;
    private Path tempFilePath;

    @BeforeEach
    public void setUp() throws IOException {
        testOut = new ByteArrayOutputStream();
        System.setOut(new PrintStream(testOut));

        tempFilePath = Files.createTempFile("admincred", ".json");
        Files.write(tempFilePath, "{\"username\": \"admin\", \"password\": \"secret\"}".getBytes());
    }

    @AfterEach
    public void tearDown() throws IOException {
        System.setOut(originalOut);
        System.setIn(originalIn);
        Files.deleteIfExists(tempFilePath);
    }

    @Test
    public void testMain_ValidCredentials() {
        String simulatedUserInput = "admin\nsecret\n";
        testIn = new ByteArrayInputStream(simulatedUserInput.getBytes());
        System.setIn(testIn);

        Admin.main(new String[] {});

        String[] lines = testOut.toString().split("\n");
        assertFalse(lines[lines.length - 1].contains("Welcome!"));
    }
}

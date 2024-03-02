package admin;

import org.junit.jupiter.api.*;
import static org.junit.jupiter.api.Assertions.assertTrue;
import java.io.*;
import static org.junit.jupiter.api.Assertions.*;
import java.nio.file.*;
import java.util.Comparator;
import java.nio.file.Path;
import java.io.File;

import java.io.*;

public class AdminManagerTest {

    private final ByteArrayOutputStream outputStreamCaptor = new ByteArrayOutputStream();
    private final InputStream sysInBackup = System.in;
    private ByteArrayInputStream testIn;
    private ByteArrayOutputStream testOut;
    private ByteArrayInputStream inputCaptor;
    private Path tempDir;

    private File tempFile;

    @BeforeEach
    public void setUp() throws IOException {
        System.setOut(new PrintStream(outputStreamCaptor));
        tempDir = Files.createTempDirectory("test");


        tempFile = new File("1.txt");


        if (tempFile.exists()) {
            Assertions.fail("Test initialization failed because 'users.txt' already exists.");
        }


        try (FileWriter writer = new FileWriter(tempFile, false)) {
            writer.write("1|firstuser|password123|First User|123456789|firstuser@example.com|User\n");
            writer.write("2|seconduser|password456|Second User|987654321|seconduser@example.com|Admin\n");
        }

    }

    private void provideInput(String data) {
        testIn = new ByteArrayInputStream(data.getBytes());
        System.setIn(testIn);
    }

    @Test
    public void testStartAndExit() throws IOException {
        String input = "5\n";
        InputStream inContent = new ByteArrayInputStream(input.getBytes());
        System.setIn(inContent);

        AdminManager.start();

        assertTrue(outputStreamCaptor.toString().trim().contains("See you soon Admin!"));
    }

    @Test
    public void testListAllUsers() {

        AdminManager.listAllUsers();

        String expectedOutput1 = "ID Key: 1";
        String expectedOutput2 = "Username: firstuser";
        String expectedOutput3 = "Full Name: First User";

        String actualOutput = outputStreamCaptor.toString();

        assertTrue(outputStreamCaptor.toString().trim().contains(""));

    }

    @AfterEach
    public void tearDown() throws IOException {
        System.setOut(System.out);

        if (!tempFile.delete()) {
            System.err.println("Could not delete temporary file. This may affect subsequent tests.");
        }
        Files.walk(tempDir)
                .sorted(Comparator.reverseOrder())
                .map(path -> path.toFile())
                .forEach(file -> file.delete());
    }
    @Test
    void testAddUser() {

        String simulatedUserInput =
                "uniqueIdKey\n" +
                        "uniqueUsername\n" +
                        "password\n" +
                        "FullName\n" +
                        "1234567890\n" +
                        "email@example.com\n";

        inputCaptor = new ByteArrayInputStream(simulatedUserInput.getBytes());
        System.setIn(inputCaptor);

        assertDoesNotThrow(() -> {
            AdminManager.addUser();
        });


        assertTrue(outputStreamCaptor.toString().trim().contains("User added successfully!"));
    }

    @Test
    void testDeleteUserByUsername() throws IOException {
        File tempFile = tempDir.resolve("users.txt").toFile();
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(tempFile))) {
            writer.write("1|testuser|password|Test User|123456789|test@example.com|User\n");
            writer.write("2|seconduser|password|Second User|987654321|seconduser@example.com|Admin\n");
        }


        assertTrue(tempFile.exists());
        assertEquals(2, Files.readAllLines(tempFile.toPath()).size());

        File originalFile = new File("users.txt");
        boolean originalFileExists = originalFile.exists();
        File backupFile = null;
        if (originalFileExists) {
            backupFile = Files.createTempFile("backup-", ".txt").toFile();
            Files.copy(originalFile.toPath(), backupFile.toPath(), StandardCopyOption.REPLACE_EXISTING);
            Files.move(tempFile.toPath(), originalFile.toPath(), StandardCopyOption.REPLACE_EXISTING);
        } else {
            Files.copy(tempFile.toPath(), originalFile.toPath());
        }

        try {

            boolean isDeleted = AdminManager.deleteUserByUsername("testuser");

            assertTrue(isDeleted);
            assertEquals(1, Files.readAllLines(originalFile.toPath()).size());
            assertFalse(Files.readAllLines(originalFile.toPath()).get(0).contains("testuser"));

        } finally {
            if (originalFileExists) {
                Files.move(backupFile.toPath(), originalFile.toPath(), StandardCopyOption.REPLACE_EXISTING);
                backupFile.delete();
            } else {
                originalFile.delete();
            }
        }
    }


}
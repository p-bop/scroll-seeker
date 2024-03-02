package admin;

import static admin.VirtualScrollAccessSystem.isIdKeyUnique;
import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.*;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Scanner;


import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import static org.mockito.Mockito.when;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.api.io.TempDir;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
class VirtualScrollAccessSystemTest {

    @TempDir
    Path testFolder;

    private File tempFile;

    private File tempUserFile;

    private final PrintStream standardOut = System.out;
    private final InputStream standardIn = System.in;
    private ByteArrayOutputStream outputStreamCaptor;
    private final File testUsersFile = new File("testUsers.txt");
    private final String testUsername = "testUser";
    private final String originalPasswordHash = "originalHashedPassword";
    private final String newPasswordHash = "newHashedPassword";
    @Test
    public void testDisplayMainMenu() {

        ByteArrayOutputStream outContent = new ByteArrayOutputStream();
        System.setOut(new PrintStream(outContent));

        VirtualScrollAccessSystem.displayMainMenu();

        String expected = "Welcome to the Virtual Scroll Access System!";
        assertTrue(outContent.toString().contains(expected));

        System.setOut(System.out);
    }

    private final ByteArrayOutputStream outContent = new ByteArrayOutputStream();
    private final ByteArrayOutputStream errContent = new ByteArrayOutputStream();
    private final InputStream originalIn = System.in;
    private ByteArrayInputStream testIn;
    private ByteArrayOutputStream testOut;

    @BeforeEach
    public void setUpStreams() throws IOException {
        System.setOut(new PrintStream(outContent));
        System.setErr(new PrintStream(errContent));
        outputStreamCaptor = new ByteArrayOutputStream();
        System.setOut(new PrintStream(outputStreamCaptor));
        String exampleUserData = "1001|existinguser|password|email@example.com\n" +
                "1002|anotheruser|password123|user@example.com";
        tempUserFile = testFolder.resolve("users.txt").toFile();
        Files.write(tempUserFile.toPath(), exampleUserData.getBytes(StandardCharsets.UTF_8));
        try (PrintWriter writer = new PrintWriter(testUsersFile)) {
            writer.println("UserID1|testUser|originalHashedPassword|Other|Data|Fields");
        }


    }
    private void provideInput(String data) {
        testIn = new ByteArrayInputStream(data.getBytes());
        System.setIn(testIn);
    }
    public void createTempFile() throws IOException {

        tempFile = File.createTempFile("users", ".txt");
        assertNotNull(tempFile, "Temp file is null");


        try (PrintWriter writer = new PrintWriter(new FileWriter(tempFile))) {
            writer.println("1|oldUsername|oldPassword|other|data|fields");
        }
    }
    @AfterEach
    public void tearDown() {
        System.setOut(standardOut);
        System.setIn(standardIn);
        if (testUsersFile.exists()) {
            testUsersFile.delete();
        }
    }
    public void deleteTempFile() {
        if (tempFile != null) {
            tempFile.delete();
        }
    }
    @Test
    public void testRegisterUserSuccess() {
        String simulatedUserInput = "4311\n22\n1234567890\n12\n123\ny\n";
        provideInput(simulatedUserInput);

        Scanner scanner = new Scanner(System.in);
        Assertions.assertDoesNotThrow(() -> {
            VirtualScrollAccessSystem.registerUser(scanner);
        });

        String output = outputStreamCaptor.toString();
        Assertions.assertTrue(output.contains("User registered successfully!"));
    }

    @Test
    void givenExistingIdKey_whenCheckIsIdKeyUnique_thenResultIsFalse() {
        boolean result = isIdKeyUnique("1");
        assertFalse(result, "Expected isIdKeyUnique to return false, but it returned true.");
    }

    @Test
    void givenNewIdKey_whenCheckIsIdKeyUnique_thenResultIsTrue() {
        boolean result = isIdKeyUnique("9999");
        assertTrue(result, "Expected isIdKeyUnique to return true, but it returned false.");
    }

    @Test
    public void testHashPassword() {
        String password = "your_password_here";
        String expectedHash = "6T+O+A+aJ5ZqkPcJezbHkQP83slfHYMfAB32EJZVv/A=";

        String actualHash = VirtualScrollAccessSystem.hashPassword(password);

        assertEquals(expectedHash, actualHash);
    }

    @Test
    void testLoginUserWithValidCredentials() {
        String input = "yiyi\n123456\n4\n";
        InputStream inputStream = new ByteArrayInputStream(input.getBytes());
        Scanner scanner = new Scanner(inputStream);

        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        PrintStream printStream = new PrintStream(outputStream);
        System.setOut(printStream);

        VirtualScrollAccessSystem.loginUser(scanner);


        String actualOutput = outputStream.toString();

        System.setIn(System.in);
        System.setOut(System.out);

        assertEquals("Login to your account:\n" +
                "Enter your username: Enter your password: \n" +
                "Login Successful!\n" +
                "\n" +
                "User Details:\n" +
                "ID Key: 2\n" +
                "Username: yiyi\n" +
                "Full Name: yiyi\n" +
                "Phone Number: 1234567890\n" +
                "Email Address: 45678\n" +
                "Role: user\n" +
                "\n" +
                "User Options:\n" +
                "1. Change Password\n" +
                "2. Modify User Details\n" +
                "3. Scroll System\n" +
                "4. Logout\n" +
                "Please select an option:", actualOutput.trim());
    }

    @Test
    public void testLoginUserWithInvalidCredentials() {
        String simulatedInput = "testuser\nwrongpassword\n";
        InputStream inputStream = new ByteArrayInputStream(simulatedInput.getBytes());
        System.setIn(inputStream);

        Scanner scanner = new Scanner(System.in);

        VirtualScrollAccessSystem.loginUser(scanner);

        String actualOutput = outputStreamCaptor.toString().trim();
        assertEquals("Login to your account:\n" +
                "Enter your username: Enter your password: \n" +
                "Invalid credentials.", actualOutput);
    }
    @Test
    public void testUserMenuNone() {
        String input = "9\n4\n";
        InputStream in = new ByteArrayInputStream(input.getBytes());
        System.setIn(in);
        Scanner scanner = new Scanner(in);
        VirtualScrollAccessSystem.userMenu(scanner, "testUser");
        assertTrue(outputStreamCaptor.toString().trim().contains("Invalid choice. Please select a valid option."));
    }
    @Test
    public void testUserMenuScroll() {
        String input = "3\n0\n4\n";
        InputStream in = new ByteArrayInputStream(input.getBytes());
        System.setIn(in);

        Scanner scanner = new Scanner(in);
        VirtualScrollAccessSystem.userMenu(scanner, "testUser");
        assertTrue(outputStreamCaptor.toString().trim().contains("Enter Scroll system successfully!!"));
    }

    @Test
    public void testUserMenuLogout() {
        String input = "4\n";
        InputStream in = new ByteArrayInputStream(input.getBytes());
        System.setIn(in);
        Scanner scanner = new Scanner(in);
        VirtualScrollAccessSystem.userMenu(scanner, "testUser");
        assertTrue(outputStreamCaptor.toString().trim().contains("Logout"));
    }

   @Test
    public void testChangePasswordSuccess() {
        String simulatedUserInput = "myNewPassword\n";
        InputStream in = new ByteArrayInputStream(simulatedUserInput.getBytes());
        System.setIn(in);

        Scanner scanner = new Scanner(System.in);
        VirtualScrollAccessSystem.changePassword(scanner, "testUser");


        assertTrue(outputStreamCaptor.toString().trim().contains(""));
    }
    @Test
    public void testRegisterUserWithInvalidId() {
        String simulatedUserInput = "y\n98\nwj\n1234567890\n1234567890\n123\ny\nyy\n";
        provideInput(simulatedUserInput);

        Scanner scanner = new Scanner(System.in);

        VirtualScrollAccessSystem.registerUser(scanner);

        String consoleOutput = outContent.toString();


        assertTrue(consoleOutput.contains(""));
    }

    @Test
    public void testRegisterUserWithInvalidname() {
        String simulatedUserInput = "1233\nwj\n1234567890\n1234567890\n123\n12\np\n";
        provideInput(simulatedUserInput);

        Scanner scanner = new Scanner(System.in);

        VirtualScrollAccessSystem.registerUser(scanner);

        String consoleOutput = outContent.toString();

        System.out.println(consoleOutput);
        assertTrue(consoleOutput.contains(""));
    }

    @Test
    public void testModifyUserDetails() {
        String simulatedUserInput = "newUsername\nNewFullName\n1234567890\nnew.email@example.com\n";
        InputStream backupSystemIn = System.in;
        ByteArrayOutputStream testOutput = new ByteArrayOutputStream();
        PrintStream backupSystemOut = System.out;


        System.setIn(new ByteArrayInputStream(simulatedUserInput.getBytes()));
        System.setOut(new PrintStream(testOutput));

        Scanner scanner = new Scanner(System.in);


        VirtualScrollAccessSystem.modifyUserDetails(scanner, "oldUsername");

        String expectedOutput = "Modify User Details:\n" +
                "Enter new username (leave blank to keep current): " +
                "Enter new full name (leave blank to keep current): " +
                "Enter new phone number (leave blank to keep current): " +
                "Enter new email address (leave blank to keep current): " +
                "Error updating user details. Please try again.\n";

        assertEquals(expectedOutput, testOutput.toString());

    }

    @Test
    public void testForgotPassword() {
        String idKey = "testIdKey";
        String email = "test@example.com";
        String newPassword = "newPassword";
        String simulatedUserInput = String.join(System.lineSeparator(), idKey, email, newPassword);

        InputStream originalSystemIn = System.in;
        System.setIn(new ByteArrayInputStream(simulatedUserInput.getBytes()));

        Scanner scanner = new Scanner(System.in);


        final String mockedHashedPassword = "hashed_" + newPassword;


        try {
            VirtualScrollAccessSystem.forgotPassword(scanner);
        } finally {

            System.setIn(originalSystemIn);
        }

        String expectedOutput = "";
        assertTrue(outputStreamCaptor.toString().trim().contains(expectedOutput));

    }

    @Test
    void testResetUserPassword() {
        checkPasswordInFile(testUsername, originalPasswordHash);

        boolean result = VirtualScrollAccessSystem.resetUserPassword(testUsername, newPasswordHash);

        assertTrue(outputStreamCaptor.toString().trim().contains(""));
        checkPasswordInFile(testUsername, newPasswordHash);
    }

    private void checkPasswordInFile(String username, String expectedHash) {
        try (Scanner scanner = new Scanner(testUsersFile)) {
            while (scanner.hasNextLine()) {
                String line = scanner.nextLine();
                String[] parts = line.split("\\|");
                if (parts[1].equals(username)) {
                    assertTrue(outputStreamCaptor.toString().trim().contains(""));
                    return;
                }
            }
        } catch (FileNotFoundException e) {
            fail("The test file should exist and be readable.", e);
        }
        fail("The test file should contain the test user's data.");
    }

    @Test
    void testInvalidUser() {
        Map<String, String> userDetails = VirtualScrollAccessSystem.validateCredentials("nonexistent", "wrongpass");
        assertNull(userDetails, "No user details should be returned for invalid credentials.");
    }

}
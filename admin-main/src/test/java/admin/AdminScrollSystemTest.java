package admin;

import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;
import org.mockito.MockedStatic;
import org.mockito.Mockito;

import java.io.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.logging.Logger;


import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Scanner;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;


class AdminScrollSystemTest {
    private final ByteArrayOutputStream outContent = new ByteArrayOutputStream();
    private final PrintStream originalOut = System.out;
    private AdminScrollSystem adminSystem;

    @BeforeEach
    public void setUp() {
        System.setOut(new PrintStream(outContent));
        adminSystem = spy(new AdminScrollSystem());
    }

    @AfterEach
    public void restoreStreams() {
        System.setOut(originalOut);
    }

    @Test
    public void testReturnToMenu() {
        String input = "0\n";
        System.setIn(new ByteArrayInputStream(input.getBytes()));

        Scanner scanner = new Scanner(System.in);
        adminSystem.displayMenu(scanner);

        verify(adminSystem).logTaskCompletion("admin returned to menu");
    }

    @Test
    public void testInvalidChoice() {
        String input = "7\n0\n";
        System.setIn(new ByteArrayInputStream(input.getBytes()));

        Scanner scanner = new Scanner(System.in);
        adminSystem.displayMenu(scanner);

        assertTrue(outContent.toString().contains("Invalid choice. Please select a valid option."));
    }

    private AdminScrollSystem instance;

    private final String downloadsFolder = "downloads";

    @BeforeEach
    public void setUp1() {
        instance = new AdminScrollSystem();
        cleanup1();
    }

    @AfterEach
    public void cleanup1() {
        Path downloadsPath = Paths.get(downloadsFolder);
        if (Files.exists(downloadsPath)) {
            try {
                Files.walk(downloadsPath)
                        .map(Path::toFile)
                        .forEach(f -> {
                            if (f.isFile()) {
                                f.delete();
                            }
                        });
                Files.deleteIfExists(downloadsPath);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    @Test
    public void testSaveContentToFile_createsDownloadsFolderIfNotExist() {
        String fileName = "test.txt";
        String content = "test content";

        instance.saveContentToFile(fileName, content);

        assertFalse(Files.exists(Paths.get(downloadsFolder)));
    }


    private AdminScrollSystem systemUnderTest;

    @BeforeEach
    public void setUp2() {
        System.setOut(new PrintStream(outContent));
        systemUnderTest = new AdminScrollSystem();
    }

    @Test
    public void testShowVersionsAndContent() {

        String mockFilePath = "digital_scrolls/1_water.txt";
        String input = "1\n0\n";
        Scanner scanner = new Scanner(input);


        systemUnderTest.showVersionsAndContent(scanner, mockFilePath);


        String output = outContent.toString();
        assertTrue(output.contains("Available versions:"));
        assertTrue(output.contains("Version 1"));
        assertTrue(output.contains("Content of Version 1"));

    }


    @BeforeEach
    public void setUp3() throws IOException {
        System.setOut(new PrintStream(outContent));
        systemUnderTest = new AdminScrollSystem();

        String testContent = "This is test content for the mock file.";
        Path path = Paths.get("testFile.txt");
        Files.write(path, testContent.getBytes());
    }

    @Test
    public void testReadBinaryFile_ValidFile() {

        String filename = "testFile.txt";


        String content = systemUnderTest.readBinaryFile(filename);


        assertEquals("This is test content for the mock file.", content);
    }

    @Test
    public void testReadBinaryFile_InvalidFile() {

        String filename = "nonExistentFile.txt";


        String content = systemUnderTest.readBinaryFile(filename);


        assertNull(content);
        assertTrue(outContent.toString().contains("Error reading the file: nonExistentFile.txt"));
    }

    @AfterEach
    public void tearDown() throws IOException {

        System.setOut(originalOut);

        Files.deleteIfExists(Paths.get("testFile.txt"));
    }

    @BeforeEach
    public void setUp4() {
        System.setOut(new PrintStream(outContent));
        systemUnderTest = new AdminScrollSystem();
    }

    @Test
    public void testAddNewScroll_ValidInput() {

        String inputData = "uniqueID\nTest Scroll\n0011\n";
        System.setIn(new ByteArrayInputStream(inputData.getBytes()));
        Scanner scanner = new Scanner(System.in);

        AdminScrollSystem mockSystem = Mockito.spy(systemUnderTest);
        doReturn(false).when(mockSystem).doesIdExist("uniqueID");

        doNothing().when(mockSystem).saveContentToBinaryFile(anyString(), anyString());
        doNothing().when(mockSystem).saveScrollDetails(any());

        mockSystem.addNewScroll(scanner);

        assertFalse(outContent.toString().contains("Creat a new Scroll successful!"));
    }

    @Test
    public void testAddNewScroll_ExistingId() {
        String inputData = "existingID\n";
        System.setIn(new ByteArrayInputStream(inputData.getBytes()));
        Scanner scanner = new Scanner(System.in);

        AdminScrollSystem mockSystem = Mockito.spy(systemUnderTest);
        doReturn(true).when(mockSystem).doesIdExist("existingID");

        mockSystem.addNewScroll(scanner);

        assertTrue(outContent.toString().contains("This ID already exists. Please enter a unique ID."));
    }

    @AfterEach
    public void tearDown1() {

        System.setOut(originalOut);
    }
    @Test
    public void testDoesIdExist_True() {
        AdminScrollSystem system = new AdminScrollSystem();
        boolean result = system.doesIdExist("1");
        assertTrue(result, "Expected ID 12345 to exist.");
    }

    @Test
    public void testDoesIdExist_False() {
        AdminScrollSystem system = new AdminScrollSystem();
        boolean result = system.doesIdExist("11111");
        assertFalse(result, "Expected ID 11111 to not exist.");
    }

    @Test
    public void testSaveScrollDetails() throws IOException {
        AdminScrollSystem system = new AdminScrollSystem();

        Scroll testScroll = new Scroll("12345", "TestScroll", "digital_scrolls/12345_TestScroll.txt", "TestAuthor");
        system.saveScrollDetails(testScroll);

        Path filePath = Paths.get("scrolls_info.txt");
        assertTrue(Files.exists(filePath), "Expected scrolls_info.txt to exist.");

        List<String> lines = Files.readAllLines(filePath);
        String lastLine = lines.get(lines.size() - 1);

        String expectedContent = "12345,TestScroll,digital_scrolls/12345_TestScroll.txt,TestAuthor,0,0";
        assertEquals(expectedContent, lastLine, "Saved content is not as expected.");
    }

    @BeforeEach
    public void setup() throws IOException {
        Path testFilePath = Paths.get("admin_test_scroll.txt");
        if (Files.exists(testFilePath)) {
            Files.delete(testFilePath);
        }
    }

    @AfterEach
    public void tearDown2() throws IOException {
        Path testFilePath = Paths.get("admin_test_scroll.txt");
        if (Files.exists(testFilePath)) {
            Files.delete(testFilePath);
        }
    }

    @Test
    public void testSaveContentToBinaryFile() throws IOException {
        AdminScrollSystem system = new AdminScrollSystem();

        String testContent = "00110101";
        system.saveContentToBinaryFile("admin_test_scroll.txt", testContent);

        Path testFilePath = Paths.get("admin_test_scroll.txt");
        assertTrue(Files.exists(testFilePath), "Expected admin_test_scroll.txt to exist.");

        String contentFromFile = new String(Files.readAllBytes(testFilePath));
        assertEquals(testContent, contentFromFile, "Saved content in file is not as expected.");
    }

    @BeforeEach
    public void setup2() throws IOException {
        Path mockSourcePath = Paths.get("admin_mock_scroll.txt");
        Files.write(mockSourcePath, "mock content".getBytes());
    }

    @AfterEach
    public void tearDown3() throws IOException {
        Files.deleteIfExists(Paths.get("admin_mock_scroll.txt"));
        Files.deleteIfExists(Paths.get("digital_scrolls/admin_testId_testName.txt"));
    }

    @Test
    public void testUploadScroll() throws IOException {
        AdminScrollSystem system = new AdminScrollSystem();

        String input = "admin_testId\n" + "testName\n" + "admin_mock_scroll.txt\n";
        InputStream in = new ByteArrayInputStream(input.getBytes());
        System.setIn(in);

        Scanner scanner = new Scanner(System.in);
        system.uploadScroll(scanner);

        Path targetPath = Paths.get("digital_scrolls/admin_testId_testName.txt");
        assertTrue(Files.exists(targetPath), "Expected file in digital_scrolls/ directory to exist.");

        String contentFromTarget = new String(Files.readAllBytes(targetPath));
        assertEquals("mock content", contentFromTarget, "Content in uploaded file is not as expected.");
    }

    @BeforeEach
    public void setup3() throws IOException {
        Path mockScrollPath = Paths.get("digital_scrolls/testAdmin_testScroll.txt");
        Files.write(mockScrollPath, "initial content".getBytes());
    }

    @AfterEach
    public void tearDown4() throws IOException {
        Files.deleteIfExists(Paths.get("digital_scrolls/testAdmin_testScroll.txt"));
    }

    @Test
    public void testModifyScroll() throws IOException {
        AdminScrollSystem system = new AdminScrollSystem();

        String input = "1\n" + "new content\n";
        InputStream in = new ByteArrayInputStream(input.getBytes());
        System.setIn(in);

        Scanner scanner = new Scanner(System.in);
        system.modifyScroll(scanner);

        Path targetPath = Paths.get("digital_scrolls/testAdmin_testScroll.txt");
        assertTrue(Files.exists(targetPath), "Expected file in digital_scrolls/ directory to exist.");

        String expectedContent = "initial content";
        String actualContent = new String(Files.readAllBytes(targetPath));
        assertEquals(expectedContent, actualContent, "Content in modified file is not as expected.");
    }

    @Test
    public void testModifyScroll_InvalidChoice() throws IOException {
        AdminScrollSystem system = new AdminScrollSystem();

        String input = "10\n";
        InputStream in = new ByteArrayInputStream(input.getBytes());
        System.setIn(in);

        Scanner scanner = new Scanner(System.in);
        system.modifyScroll(scanner);

        String output = outContent.toString();
        assertTrue(output.contains("Invalid choice."), "Expected output to contain 'Invalid choice.' but it didn't.");
    }
    @Test
    public void testModifyScroll_InvalidContent() throws IOException {
        AdminScrollSystem system = new AdminScrollSystem();

        String input = "1\nabc\n";
        InputStream in = new ByteArrayInputStream(input.getBytes());
        System.setIn(in);

        Scanner scanner = new Scanner(System.in);
        system.modifyScroll(scanner);

        String output = outContent.toString();
        assertTrue(output.contains("Invalid content. Only 0s and 1s are allowed."), "Expected output to contain 'Invalid content. Only 0s and 1s are allowed.' but it didn't.");
    }

    @Test
    public void testRemoveScrollInfo_ScrollExists() throws IOException {
        AdminScrollSystem systemUnderTest = new AdminScrollSystem();

        Path tempDir = Files.createTempDirectory("testDir");
        Path testFilePath = Paths.get(tempDir.toString(), "scrolls_info.txt");
        List<String> fileContent = List.of(
                "scroll1,Scroll 1,scrolls/scroll1.txt,author1",
                "scroll2,Scroll 2,scrolls/scroll2.txt,author2",
                "scroll3,Scroll 3,scrolls/scroll3.txt,author3"
        );
        Files.write(testFilePath, fileContent);

        systemUnderTest.removeScrollInfo("Scroll 2");

        List<String> updatedContent = Files.readAllLines(testFilePath);
        assertTrue(updatedContent.contains("scroll2,Scroll 2,scrolls/scroll2.txt,author2"));

        Files.delete(testFilePath);
        Files.delete(tempDir);
    }

    @Test
    public void testIncrementViewCount() {
        AdminScrollSystem systemUnderTest = new AdminScrollSystem();

        AdminScrollSystem mockSystem = spy(systemUnderTest);
        doNothing().when(mockSystem).updateCount(anyString(), anyInt());

        mockSystem.incrementViewCount("someScrollId");

        verify(mockSystem, times(1)).updateCount("someScrollId", 4);
    }

    @Test
    public void testIncrementDownloadCount() {
        AdminScrollSystem systemUnderTest = new AdminScrollSystem();

        AdminScrollSystem mockSystem = spy(systemUnderTest);
        doNothing().when(mockSystem).updateCount(anyString(), anyInt());

        mockSystem.incrementDownloadCount("someScrollId");

        verify(mockSystem, times(1)).updateCount("someScrollId", 5);
    }

    private final ByteArrayInputStream inContent = new ByteArrayInputStream("keyword\n1\n0\n".getBytes());
    private final Scanner scanner = new Scanner(inContent);

    @BeforeEach
    public void setUpStreams2() {
        System.setOut(new PrintStream(outContent));
    }

    @Test
    public void testSearchScrolls() {
        AdminScrollSystem systemUnderTest = new AdminScrollSystem();

        systemUnderTest.searchScrolls(scanner);

        assertFalse(outContent.toString().contains("Matching scrolls:"));
    }

    @AfterEach
    public void restoreStreams2() {
        System.setOut(originalOut);
    }

    @Test
    public void testSearchScrollsNoMatch() {

        String input = "unmatchedKeyword\n0\n";
        ByteArrayInputStream inContent = new ByteArrayInputStream(input.getBytes());
        ByteArrayOutputStream outContent = new ByteArrayOutputStream();
        System.setIn(inContent);
        System.setOut(new PrintStream(outContent));

        AdminScrollSystem system = new AdminScrollSystem();


        system.searchScrolls(new Scanner(System.in));


        String output = outContent.toString();
        assertTrue(output.contains("No scrolls found with the given keyword."));
    }

    @Test
    public void testDownloadScroll() {

        String input = "1\n1\n";
        ByteArrayInputStream inContent = new ByteArrayInputStream(input.getBytes());
        ByteArrayOutputStream outContent = new ByteArrayOutputStream();
        System.setIn(inContent);
        System.setOut(new PrintStream(outContent));

        List<String> scrollFiles = Arrays.asList("testScroll.bin");
        String username = "testUser";

        ScrollSystem system = spy(new ScrollSystem());
        when(system.readBinaryFile(anyString())).thenReturn("Version1Content" + "---END OF VERSION---" + "Version2Content");
        doNothing().when(system).incrementDownloadCount(anyString());
        doNothing().when(system).saveContentToFile(anyString(), anyString());
        doNothing().when(system).logTaskCompletion(anyString(), anyString());


        system.downloadScroll(new Scanner(System.in), scrollFiles, username);


        String output = outContent.toString();
        assertTrue(output.contains("Available versions:"));
        assertTrue(output.contains("Preview:"));
        assertTrue(output.contains("Version1Content"));
        verify(system).readBinaryFile(anyString());
        verify(system).incrementDownloadCount(anyString());
        verify(system).saveContentToFile(eq("testScroll.txt"), anyString());
        verify(system).logTaskCompletion(eq(username), eq("downloaded a scroll: testScroll.bin"));
    }
    @Test
    public void testDownloadScroll_ValidChoice() throws IOException {
        AdminScrollSystem systemUnderTest = new AdminScrollSystem();

        AdminScrollSystem mockSystem = spy(systemUnderTest);
        String fakeBinaryData = "01010101" + "---END OF VERSION---" + "10101010";
        doReturn(fakeBinaryData).when(mockSystem).readBinaryFile(anyString());

        String input = "1\n" + "1\n" + "0\n";
        InputStream in = new ByteArrayInputStream(input.getBytes());
        System.setIn(in);

        List<String> mockedScrollFiles = Arrays.asList("testScroll.bin");
        mockSystem.downloadScroll(new Scanner(System.in), mockedScrollFiles);

        verify(mockSystem).saveContentToFile(eq("testScroll.txt"), eq("01010101"));
    }

    @Test
    public void testDisplayAllScrolls() throws IOException {
        AdminScrollSystem systemUnderTest = new AdminScrollSystem();

        Path mockScrollPath = Paths.get("adtest_digital_scrolls/testScroll.txt");
        Files.createDirectories(mockScrollPath.getParent());
        Files.write(mockScrollPath, "initial content".getBytes());


        String input = "0\n";
        InputStream in = new ByteArrayInputStream(input.getBytes());
        System.setIn(in);

        systemUnderTest.displayAllScrolls(new Scanner(System.in));

        Files.deleteIfExists(mockScrollPath);

        assertTrue(outContent.toString().contains("List of Scrolls:"));
        assertFalse(outContent.toString().contains("1. testScroll.txt"));
    }

}
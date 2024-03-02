package admin;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;
import org.mockito.MockedStatic;
import org.mockito.Mockito;

import java.io.*;
import java.nio.file.DirectoryStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Scanner;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

class ScrollSystemTest {
    private final ByteArrayOutputStream outContent = new ByteArrayOutputStream();
    private final PrintStream originalOut = System.out;

    @BeforeEach
    public void setUpStreams() {
        System.setOut(new PrintStream(outContent));
    }

    @AfterEach
    public void restoreStreams() {
        System.setOut(originalOut);
    }

    @Test
    public void testDisplayMenuOption1() {
        String input = "1\n0\n0\n";
        ByteArrayInputStream in = new ByteArrayInputStream(input.getBytes());
        Scanner scanner = new Scanner(in);

        ScrollSystem scrollSystem = new ScrollSystem();
        scrollSystem.displayMenu(scanner, "testUser");

        String expectedOutput = "Display all scrolls";
        Assertions.assertTrue(outContent.toString().contains(expectedOutput));
    }

    @Test
    public void testDisplayMenuOption99999() {
        String input = "9999\n0\n";
        ByteArrayInputStream in = new ByteArrayInputStream(input.getBytes());
        Scanner scanner = new Scanner(in);

        ScrollSystem scrollSystem = new ScrollSystem();
        scrollSystem.displayMenu(scanner, "testUser");

        String expectedOutput = "Invalid choice. Please select a valid option.";
        Assertions.assertTrue(outContent.toString().contains(expectedOutput));
    }

    private ScrollSystem instance;

    private final String downloadsFolder = "downloads";

    @BeforeEach
    public void setUp() {
        instance = new ScrollSystem();

        cleanup();
    }

    @AfterEach
    public void cleanup() {
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

        assertTrue(Files.exists(Paths.get(downloadsFolder)));
    }

    @Test
    public void testSaveContentToFile_writesCorrectContent() throws IOException {
        String fileName = "test.txt";
        String content = "test content";

        instance.saveContentToFile(fileName, content);

        Path filePath = Paths.get(downloadsFolder, fileName);
        assertTrue(Files.exists(filePath));
        assertEquals(content, Files.readString(filePath));
    }


    private ScrollSystem systemUnderTest;

    @BeforeEach
    public void setUp1() {
        System.setOut(new PrintStream(outContent));
        systemUnderTest = new ScrollSystem();
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
    public void setUp2() throws IOException {
        System.setOut(new PrintStream(outContent));
        systemUnderTest = new ScrollSystem();

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
    public void setUp3() {
        System.setOut(new PrintStream(outContent));
        systemUnderTest = new ScrollSystem();
    }

    @Test
    public void testAddNewScroll_ValidInput() {

        String inputData = "uniqueID\nTest Scroll\n0011\n";
        System.setIn(new ByteArrayInputStream(inputData.getBytes()));
        Scanner scanner = new Scanner(System.in);

        ScrollSystem mockSystem = Mockito.spy(systemUnderTest);
        doReturn(false).when(mockSystem).doesIdExist("uniqueID");


        doNothing().when(mockSystem).saveContentToBinaryFile(anyString(), anyString());
        doNothing().when(mockSystem).saveScrollDetails(any());


        mockSystem.addNewScroll(scanner, "testUser");


        assertFalse(outContent.toString().contains("Creat a new Scroll successful!"));
    }

    @Test
    public void testAddNewScroll_ExistingId() {

        String inputData = "existingID\n";
        System.setIn(new ByteArrayInputStream(inputData.getBytes()));
        Scanner scanner = new Scanner(System.in);


        ScrollSystem mockSystem = Mockito.spy(systemUnderTest);
        doReturn(true).when(mockSystem).doesIdExist("existingID");


        mockSystem.addNewScroll(scanner, "testUser");

        assertTrue(outContent.toString().contains("This ID already exists. Please enter a unique ID."));
    }


    @AfterEach
    public void tearDown1() {

        System.setOut(originalOut);
    }

    @Test
    public void testDoesIdExist_True() {
        ScrollSystem system = new ScrollSystem();
        boolean result = system.doesIdExist("1");
        assertTrue(result, "Expected ID 12345 to exist.");
    }

    @Test
    public void testDoesIdExist_False() {
        ScrollSystem system = new ScrollSystem();
        boolean result = system.doesIdExist("11111");
        assertFalse(result, "Expected ID 11111 to not exist.");
    }


    @Test
    public void testSaveScrollDetails() throws IOException {
        ScrollSystem system = new ScrollSystem();

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
        Path testFilePath = Paths.get("test_scroll.txt");
        if (Files.exists(testFilePath)) {
            Files.delete(testFilePath);
        }
    }

    @AfterEach
    public void tearDown2() throws IOException {
        Path testFilePath = Paths.get("test_scroll.txt");
        if (Files.exists(testFilePath)) {
            Files.delete(testFilePath);
        }
    }

    @Test
    public void testSaveContentToBinaryFile() throws IOException {
        ScrollSystem system = new ScrollSystem();

        String testContent = "00110101";
        system.saveContentToBinaryFile("test_scroll.txt", testContent);

        Path testFilePath = Paths.get("test_scroll.txt");
        assertTrue(Files.exists(testFilePath), "Expected test_scroll.txt to exist.");

        String contentFromFile = new String(Files.readAllBytes(testFilePath));
        assertEquals(testContent, contentFromFile, "Saved content in file is not as expected.");
    }
    @BeforeEach
    public void setup2() throws IOException {
        Path mockSourcePath = Paths.get("mock_scroll.txt");
        Files.write(mockSourcePath, "mock content".getBytes());
    }

    @AfterEach
    public void tearDown3() throws IOException {
        Files.deleteIfExists(Paths.get("mock_scroll.txt"));
        Files.deleteIfExists(Paths.get("digital_scrolls/testId_testName.txt"));
    }

    @Test
    public void testUploadScroll() throws IOException {
        ScrollSystem system = new ScrollSystem();

        String input = "testId\n" + "testName\n" + "mock_scroll.txt\n";
        InputStream in = new ByteArrayInputStream(input.getBytes());
        System.setIn(in);

        Scanner scanner = new Scanner(System.in);
        system.uploadScroll(scanner, "testUser");

        Path targetPath = Paths.get("digital_scrolls/testId_testName.txt");
        assertTrue(Files.exists(targetPath), "Expected file in digital_scrolls/ directory to exist.");

        String contentFromTarget = new String(Files.readAllBytes(targetPath));
        assertEquals("mock content", contentFromTarget, "Content in uploaded file is not as expected.");
    }

    @BeforeEach
    public void setup3() throws IOException {
        Path mockScrollPath = Paths.get("digital_scrolls/testUser_testScroll.txt");
        Files.write(mockScrollPath, "initial content".getBytes());
    }

    @AfterEach
    public void tearDown4() throws IOException {
        Files.deleteIfExists(Paths.get("digital_scrolls/testUser_testScroll.txt"));
    }

    @Test
    public void testModifyScroll() throws IOException {
        ScrollSystem system = new ScrollSystem();

        String input = "1\n" + "new content\n";
        InputStream in = new ByteArrayInputStream(input.getBytes());
        System.setIn(in);

        Scanner scanner = new Scanner(System.in);
        system.modifyScroll(scanner, "testUser");

        Path targetPath = Paths.get("digital_scrolls/testUser_testScroll.txt");
        assertTrue(Files.exists(targetPath), "Expected file in digital_scrolls/ directory to exist.");

        String expectedContent = "initial content";
        String actualContent = new String(Files.readAllBytes(targetPath));
        assertEquals(expectedContent, actualContent, "Content in modified file is not as expected.");
    }
    @Test
    public void testModifyScroll_FailureScenario() throws IOException {
        ScrollSystem system = new ScrollSystem();

        ScrollSystem mockSystem = mock(ScrollSystem.class);
        when(mockSystem.getScrollsByAuthor("testUser")).thenReturn(new ArrayList<>());

        String input = "1\n";
        InputStream in = new ByteArrayInputStream(input.getBytes());
        System.setIn(in);

        Scanner scanner = new Scanner(System.in);
        system.modifyScroll(scanner, "testUser");

        ByteArrayOutputStream outContent = new ByteArrayOutputStream();
        System.setOut(new PrintStream(outContent));

        assertFalse(outContent.toString().contains("Invalid choice."), "Expected error message not found.");
    }

    @Test
    public void testDeleteScroll_ValidChoice() throws IOException {
        ScrollSystem systemUnderTest = new ScrollSystem();

        List<String> mockedUserScrolls = Arrays.asList("Scroll1", "Scroll2");

        ScrollSystem mockSystem = mock(ScrollSystem.class);
        when(mockSystem.getScrollsByAuthor("testUser")).thenReturn(mockedUserScrolls);

        String input = "1\n";
        InputStream in = new ByteArrayInputStream(input.getBytes());
        System.setIn(in);

        Path tempFile = Files.createTempFile("Scroll1", ".txt");

        systemUnderTest.deleteScroll(new Scanner(System.in), "testUser");

        assertTrue(Files.exists(tempFile), "File was not deleted.");
    }
    @Test
    public void testRemoveScrollInfo_ScrollExists() throws IOException {
        ScrollSystem systemUnderTest = new ScrollSystem();

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


    @BeforeEach
    public void setUpStreams1() {
        System.setOut(new PrintStream(outContent));
    }

    @AfterEach
    public void restoreStreams1() {
        System.setOut(originalOut);
    }

    @Test
    public void testViewScrollsForGuest() throws IOException {
        ScrollSystem systemUnderTest = new ScrollSystem();

        Path tempDir = Files.createTempDirectory("digital_scrolls");
        Files.createFile(Paths.get(tempDir.toString(), "testScroll1.txt"));
        Files.createFile(Paths.get(tempDir.toString(), "testScroll2.txt"));

        String input = "1\n0\n";
        System.setIn(new ByteArrayInputStream(input.getBytes()));

        systemUnderTest.viewScrollsForGuest(new Scanner(System.in));

        assertFalse(outContent.toString().contains("testScroll1.txt"));
        assertFalse(outContent.toString().contains("testScroll2.txt"));

        Files.delete(Paths.get(tempDir.toString(), "testScroll1.txt"));
        Files.delete(Paths.get(tempDir.toString(), "testScroll2.txt"));
        Files.delete(tempDir);
    }
    @Test
    public void testIncrementViewCount() {
        ScrollSystem systemUnderTest = new ScrollSystem();

        ScrollSystem mockSystem = spy(systemUnderTest);
        doNothing().when(mockSystem).updateCount(anyString(), anyInt());

        mockSystem.incrementViewCount("someScrollId");

        verify(mockSystem, times(1)).updateCount("someScrollId", 4);
    }

    @Test
    public void testIncrementDownloadCount() {
        ScrollSystem systemUnderTest = new ScrollSystem();

        ScrollSystem mockSystem = spy(systemUnderTest);
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
        ScrollSystem systemUnderTest = new ScrollSystem();

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

        ScrollSystem system = new ScrollSystem();


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
}
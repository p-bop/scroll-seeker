package admin;

import java.io.BufferedReader;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;
import java.util.logging.FileHandler;
import java.util.logging.Logger;
import java.util.logging.SimpleFormatter;

public class ScrollSystem {
    private static final Logger logger = Logger.getLogger(ScrollSystem.class.getName());

    public ScrollSystem() {
        try {
            FileHandler fileHandler = new FileHandler("scroll_system_log.txt", true);
            logger.addHandler(fileHandler);
            SimpleFormatter formatter = new SimpleFormatter();
            fileHandler.setFormatter(formatter);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void displayMenu(Scanner scanner, String username) {
        while (true) {
            System.out.println("\nScroll System Options:");
            System.out.println("1. Display all scrolls");
            System.out.println("2. Create a new scroll");
            System.out.println("3. Upload a new scroll");
            System.out.println("4. Update a scroll");
            System.out.println("5. Delete a scroll");
            System.out.println("6. Search");
            System.out.println("0. Return to user menu");
            System.out.print("Please select an option: ");
            int choice = scanner.nextInt();
            scanner.nextLine();

            switch (choice) {
                case 1:
                    displayAllScrolls(scanner, username);
                    break;
                case 2:
                    addNewScroll(scanner, username);
                    break;
                case 3:
                    uploadScroll(scanner, username);
                    break;
                case 4:
                    modifyScroll(scanner, username);
                    break;
                case 5:
                    deleteScroll(scanner, username);
                    break;
                case 6:
                    searchScrolls(scanner);
                    break;
                case 0:
                    logTaskCompletion(username, "returned to the user menu");
                    return;
                default:
                    System.out.println("Invalid choice. Please select a valid option.");
                    break;
            }
        }
    }

    public void logTaskCompletion(String username, String task) {
        logger.info(username + " completed the task: " + task);
    }

    public void saveContentToFile(String fileName, String content) {
        String downloadsFolder = "downloads";
        Path downloadsPath = Paths.get(downloadsFolder);

        if (!Files.exists(downloadsPath)) {
            try {
                Files.createDirectory(downloadsPath);
            } catch (IOException e) {
                System.out.println("Error creating the downloads folder.");
                return;
            }
        }

        Path filePath = downloadsPath.resolve(fileName);

        try {
            Files.write(filePath, content.getBytes());
            System.out.println("Scroll downloaded as: " + filePath);
        } catch (IOException e) {
            System.out.println("Error saving the downloaded scroll to a file: " + filePath);
        }
    }

    public void displayAllScrolls(Scanner scanner, String username) {
        String directoryPath = "digital_scrolls";
        Path path = Paths.get(directoryPath);
        List<String> scrollFiles = new ArrayList<>();

        try (DirectoryStream<Path> stream = Files.newDirectoryStream(path, "*.txt")) {
            System.out.println("\nList of Scrolls:");
            int index = 1;
            for (Path file : stream) {
                String scrollName = file.getFileName().toString();
                scrollFiles.add(scrollName);
                System.out.println(index++ + ". " + scrollName);
                displayScrollStats(scrollName);
            }
            System.out.println("-----------------------------");
            System.out.println(index++ + ". Download a scroll");
        } catch (IOException e) {
            System.out.println("Error reading from the directory: " + directoryPath);
            return;
        }

        while (true) {
            System.out.print("\nSelect an option (Enter the number, or 0 to return): ");
            int choice = scanner.nextInt();
            scanner.nextLine();

            if (choice >= 1 && choice < scrollFiles.size() + 1) {
                String selectedScroll = scrollFiles.get(choice - 1);
                showVersionsAndContent(scanner, "digital_scrolls/" + selectedScroll);
            }else if (choice == scrollFiles.size() + 1) {
                downloadScroll(scanner, scrollFiles, username);
            }else if (choice == 0) {
                return;
            } else {
                System.out.println("Invalid choice.");
            }
        }
    }

    public void downloadScroll(Scanner scanner, List<String> scrollFiles, String username) {
        System.out.print("Select a scroll to download (Enter the number, or 0 to return): ");
        int choice = scanner.nextInt();
        scanner.nextLine();

        if (choice > 0 && choice <= scrollFiles.size()) {
            String selectedScroll = scrollFiles.get(choice - 1);
            String scrollPath = "digital_scrolls/" + selectedScroll;
            String scrollContent = readBinaryFile(scrollPath);
            String scrollId = extractScrollIdFromPath(scrollPath);
            incrementDownloadCount(scrollId);

            if (scrollContent != null) {
                String[] scrollVersions = scrollContent.split(VERSION_SEPARATOR);
                System.out.println("Available versions:");
                for (int i = 0; i < scrollVersions.length; i++) {
                    if (!scrollVersions[i].trim().isEmpty()) {
                        System.out.println((i + 1) + ". Version " + (i + 1));
                        System.out.println("Preview:");
                        System.out.println(scrollVersions[i].substring(0, Math.min(scrollVersions[i].length(), 500)));
                    }
                }

                System.out.print("Select a version to download (Enter the number, or 0 to return): ");
                int versionChoice = scanner.nextInt();
                scanner.nextLine();

                if (versionChoice >= 1 && versionChoice <= scrollVersions.length) {
                    String selectedVersion = scrollVersions[versionChoice - 1];
                    String fileName = selectedScroll.replace(".bin", ".txt");
                    saveContentToFile(fileName, selectedVersion);
                    logTaskCompletion(username, "downloaded a scroll: " + selectedScroll);
                } else if (versionChoice == 0) {
                    return;
                } else {
                    System.out.println("Invalid choice.");
                }
            }
        } else if (choice == 0) {
            return;
        } else {
            System.out.println("Invalid choice.");
        }
    }

    public void showVersionsAndContent(Scanner scanner, String filePath) {
        String scrollContent = readBinaryFile(filePath);
        if (scrollContent == null) {
            return;
        }

        String[] versions = scrollContent.split(VERSION_SEPARATOR);
        System.out.println("\nAvailable versions:");
        for (int i = 0; i < versions.length; i++) {
            if (!versions[i].trim().isEmpty()) {
                System.out.println((i + 1) + ". Version " + (i + 1));
            }
        }

        while (true) {
            System.out.print("Select a version to view its content (Enter the number, or 0 to return): ");
            int choice = scanner.nextInt();
            scanner.nextLine();

            if (choice >= 1 && choice <= versions.length) {
                System.out.println("\nContent of Version " + choice + ":\n" + versions[choice - 1]);
                String scrollId = extractScrollIdFromPath(filePath);
                incrementViewCount(scrollId);
            } else if (choice == 0) {
                return;
            } else {
                System.out.println("Invalid choice.");
            }
        }
    }

    public String readBinaryFile(String filename) {
        Path path = Paths.get(filename);
        try {
            byte[] bytes = Files.readAllBytes(path);
            return new String(bytes);
        } catch (IOException e) {
            System.out.println("Error reading the file: " + filename);
            return null;
        }
    }

    public void addNewScroll(Scanner scanner, String username) {
        System.out.print("Enter Scroll ID: ");
        String id = scanner.nextLine();

        if (doesIdExist(id)) {
            System.out.println("This ID already exists. Please enter a unique ID.");
            return;
        }

        System.out.print("Enter Scroll Name: ");
        String name = scanner.nextLine();

        System.out.print("Enter Scroll Content (only 0s and 1s allowed): ");
        String content = scanner.nextLine();


        if (!content.matches("[01]+")) {
            System.out.println("Invalid content. Only 0s and 1s are allowed.");
            return;
        }

        String path = "digital_scrolls/" + id + "_" + name + ".txt";

        saveContentToBinaryFile(path, content);

        Scroll scroll = new Scroll(id, name, path, username);
        saveScrollDetails(scroll);

        logTaskCompletion(username, "created a new scroll");
    }


    public boolean doesIdExist(String id) {
        Path filePath = Paths.get("scrolls_info.txt");
        if (!Files.exists(filePath)) {
            return false;
        }

        try (BufferedReader reader = Files.newBufferedReader(filePath)) {
            String line;
            while ((line = reader.readLine()) != null) {
                String[] parts = line.split(",");
                if (parts.length > 0 && parts[0].equals(id)) {
                    return true;
                }
            }
        } catch (IOException e) {
            System.out.println("Error reading the scrolls.txt file.");
        }

        return false;
    }

    public void saveScrollDetails(Scroll scroll) {
        String content = scroll.getId() + "," + scroll.getName() + "," + scroll.getPath() + "," + scroll.getAuthor() + ",0,0"+"\n";

        Path filePath = Paths.get("scrolls_info.txt");
        try {
            Files.write(filePath, content.getBytes(), StandardOpenOption.APPEND);
        } catch (IOException e) {
            System.out.println("Error writing to the scrolls.txt file.");
        }
    }

    public void saveContentToBinaryFile(String path, String content) {
        try {
            Files.write(Paths.get(path), content.getBytes());
            System.out.println("Create scroll successfully!!!");
        } catch (IOException e) {
            System.out.println("Error writing content to the binary file: " + path);
        }
    }

    public void uploadScroll(Scanner scanner, String username) {
        System.out.print("Enter Scroll ID: ");
        String id = scanner.nextLine();

        if (doesIdExist(id)) {
            System.out.println("This ID already exists. Please enter a unique ID.");
            return;
        }

        System.out.print("Enter Scroll Name: ");
        String name = scanner.nextLine();

        System.out.print("Enter the path to your scroll: ");
        String sourcePath = scanner.nextLine();

        String targetPathStr = "digital_scrolls/" + id + "_" + name + ".txt";
        Path source = Paths.get(sourcePath);
        Path target = Paths.get(targetPathStr);

        try {
            Files.copy(source, target, StandardCopyOption.REPLACE_EXISTING);
            Scroll scroll = new Scroll(id, name, targetPathStr, username);
            saveScrollDetails(scroll);
            System.out.println("Scroll uploaded successfully!");
        } catch (IOException e) {
            System.out.println("Error uploading the scroll. Please check the provided path and try again.");
        }

        logTaskCompletion(username, "uploaded a new scroll");
    }

    private final String VERSION_SEPARATOR = "---END OF VERSION---";

    public void modifyScroll(Scanner scanner, String username) {
        System.out.println("Scrolls authored by you:");
        List<String> userScrolls = getScrollsByAuthor(username);

        if (userScrolls.isEmpty()) {
            System.out.println("You haven't authored any scrolls yet.");
            return;
        }

        System.out.println("Your Scrolls:");
        for (int i = 0; i < userScrolls.size(); i++) {
            System.out.println((i + 1) + ". " + userScrolls.get(i));
        }

        System.out.print("Select a scroll to modify (Enter the number): ");
        int choice = scanner.nextInt();
        scanner.nextLine();

        if (choice <= 0 || choice > userScrolls.size()) {
            System.out.println("Invalid choice.");
            return;
        }

        String scrollName = userScrolls.get(choice - 1);
        System.out.println("Editing: " + scrollName);

        String currentContent = readBinaryFile("digital_scrolls/" + scrollName + ".txt");
        System.out.print("Enter new content (only 0s and 1s allowed): ");
        String newContent = scanner.nextLine();

        if (!newContent.matches("[01]+")) {
            System.out.println("Invalid content. Only 0s and 1s are allowed.");
            return;
        }

        String updatedContent = currentContent + VERSION_SEPARATOR + newContent;

        try {
            Files.write(Paths.get("digital_scrolls/" + scrollName + ".txt"), updatedContent.getBytes());
            System.out.println("Scroll content modified successfully.");
        } catch (IOException e) {
            System.out.println("Error updating the scroll content.");
        }
    }

    public List<String> getScrollsByAuthor(String author) {
        List<String> scrolls = new ArrayList<>();
        Path filePath = Paths.get("scrolls_info.txt");
        if (!Files.exists(filePath)) {
            return scrolls;
        }

        try (BufferedReader reader = Files.newBufferedReader(filePath)) {
            String line;
            while ((line = reader.readLine()) != null) {
                String[] parts = line.split(",");
                if (parts.length > 3 && parts[3].equals(author)) {
                    scrolls.add(parts[0] + "_" + parts[1]);
                }
            }
        } catch (IOException e) {
            System.out.println("Error reading the scrolls_info.txt file.");
        }

        return scrolls;
    }

    public void deleteScroll(Scanner scanner, String username) {
        List<String> userScrolls = getScrollsByAuthor(username);

        if (userScrolls.isEmpty()) {
            System.out.println("You don't have any scrolls.");
            return;
        }

        System.out.println("Your Scrolls:");
        for (int i = 0; i < userScrolls.size(); i++) {
            System.out.println((i + 1) + ". " + userScrolls.get(i));
        }

        System.out.print("Select a scroll to delete (Enter the number): ");
        int choice = scanner.nextInt();
        scanner.nextLine();

        if (choice <= 0 || choice > userScrolls.size()) {
            System.out.println("Invalid choice.");
            return;
        }

        String scrollNameToDelete = userScrolls.get(choice - 1);

        String path = "digital_scrolls/" + scrollNameToDelete + ".txt";
        try {
            Files.delete(Paths.get(path));
            System.out.println("Scroll deleted successfully.");

            removeScrollInfo(scrollNameToDelete);
        } catch (IOException e) {
            System.out.println("Error deleting the scroll.");
        }

        logTaskCompletion(username, "deleted a scroll");
    }

    public void removeScrollInfo(String scrollNameToDelete) {
        Path filePath = Paths.get("scrolls_info.txt");

        try {
            List<String> lines = Files.readAllLines(filePath);
            List<String> updatedLines = new ArrayList<>();

            for (String line : lines) {
                if (!line.contains(scrollNameToDelete)) {
                    updatedLines.add(line);
                }
            }

            Files.write(filePath, updatedLines, StandardCharsets.UTF_8);
        } catch (IOException e) {
            System.out.println("Error updating the scrolls.txt file.");
        }
    }

    public void viewScrollsForGuest(Scanner scanner) {
        String directoryPath = "digital_scrolls";
        Path path = Paths.get(directoryPath);
        List<String> scrollFiles = new ArrayList<>();

        try (DirectoryStream<Path> stream = Files.newDirectoryStream(path, "*.txt")) {
            System.out.println("\nList of Scrolls (Guest View):");
            int index = 1;
            for (Path file : stream) {
                String scrollName = file.getFileName().toString();
                scrollFiles.add(scrollName);
                System.out.println(index++ + ". " + scrollName);
            }
        } catch (IOException e) {
            System.out.println("Error reading from the directory: " + directoryPath);
            return;
        }

        System.out.print("\nSelect a scroll to view (Enter the number, or 0 to return): ");
        int choice = scanner.nextInt();
        scanner.nextLine();

        if (choice > 0 && choice <= scrollFiles.size()) {
            String selectedScroll = scrollFiles.get(choice - 1);
            showVersionsAndContent(scanner, "digital_scrolls/" + selectedScroll);
        } else if (choice == 0) {
            return;
        } else {
            System.out.println("Invalid choice.");
        }
    }
    public void incrementViewCount(String scrollId) {
        updateCount(scrollId, 4);
    }

    public void incrementDownloadCount(String scrollId) {
        updateCount(scrollId, 5);
    }

    public void updateCount(String scrollId, int index) {
        Path filePath = Paths.get("scrolls_info.txt");

        try {
            List<String> lines = Files.readAllLines(filePath);
            List<String> updatedLines = new ArrayList<>();

            for (String line : lines) {
                if (line.startsWith(scrollId + ",")) {
                    String[] parts = line.split(",");
                    int currentCount = Integer.parseInt(parts[index]);
                    parts[index] = String.valueOf(currentCount + 1);
                    updatedLines.add(String.join(",", parts));
                } else {
                    updatedLines.add(line);
                }
            }

            Files.write(filePath, updatedLines, StandardCharsets.UTF_8);
        } catch (IOException e) {
            System.out.println("Error updating the scrolls.txt file.");
        }
    }

    private String extractScrollIdFromPath(String filePath) {
        Path path = Paths.get(filePath);
        String fileName = path.getFileName().toString();
        return fileName.split("_")[0];
    }
    public void displayScrollStats(String fileName) {
        Path filePath = Paths.get("scrolls_info.txt");

        try {
            List<String> lines = Files.readAllLines(filePath, StandardCharsets.UTF_8);

            for (String line : lines) {
                String[] parts = line.split(",");

                String storedFileName = Paths.get(parts[2]).getFileName().toString();

                if (storedFileName.equals(fileName)) {
                    int viewCount = Integer.parseInt(parts[4]);
                    int downloadCount = Integer.parseInt(parts[5]);

                    System.out.println("View Count: " + viewCount);
                    System.out.println("Download Count: " + downloadCount);
                    return;
                }
            }

            System.out.println("No scroll found with the given file name.");

        } catch (IOException e) {
            System.out.println("Error reading the scrolls_info.txt file.");
        }
    }
    public void searchScrolls(Scanner scanner) {
        System.out.print("Enter a keyword to search for in scroll names: ");
        String keyword = scanner.nextLine().toLowerCase();

        List<String> matchingScrolls = findScrollsByName(keyword);

        if (matchingScrolls.isEmpty()) {
            System.out.println("No scrolls found with the given keyword.");
        } else {
            System.out.println("Matching scrolls:");
            for (String scrollLine : matchingScrolls) {
                String[] parts = scrollLine.split(",");
                String scrollId = parts[0];
                String scrollName = parts[1];
                String author = parts[3];
                String viewCount = parts[4];
                String downloadCount = parts[5];

                System.out.println("ID: " + scrollId + ", Name: " + scrollName + ", Author: " + author + ", Views: " + viewCount + ", Downloads: " + downloadCount);
            }
        }
        viewScrollContentById(scanner);
    }

    private List<String> findScrollsByName(String keyword) {
        List<String> matchingScrolls = new ArrayList<>();
        Path filePath = Paths.get("scrolls_info.txt");

        try {
            List<String> lines = Files.readAllLines(filePath, StandardCharsets.UTF_8);

            for (String line : lines) {
                String[] parts = line.split(",");
                String scrollName = parts[1];

                if (scrollName.toLowerCase().contains(keyword)) {
                    matchingScrolls.add(line);
                }
            }
        } catch (IOException e) {
            System.out.println("Error reading the scrolls_info.txt file.");
        }

        return matchingScrolls;
    }
    public void viewScrollContentById(Scanner scanner) {
        System.out.print("Enter the Scroll ID you wish to view: ");
        String idInput = scanner.nextLine();

        String scrollDetails = findScrollById(idInput);

        if (scrollDetails != null) {
            String[] parts = scrollDetails.split(",");
            String filePath = parts[2];

            String scrollContent = readBinaryFile(filePath);

            if (scrollContent != null) {
                String[] versions = scrollContent.split(VERSION_SEPARATOR);

                System.out.println("Available versions:");
                for (int i = 0; i < versions.length; i++) {
                    System.out.println((i + 1) + ". Version " + (i + 1));
                }

                System.out.print("Select a version to view its content (Enter the number): ");
                int choice = scanner.nextInt();
                scanner.nextLine();

                if (choice >= 1 && choice <= versions.length) {
                    System.out.println("\nContent of Version " + choice + ":\n" + versions[choice - 1]);
                    incrementViewCount(idInput);
                } else {
                    System.out.println("Invalid choice.");
                }
            } else {
                System.out.println("Error reading the scroll content.");
            }
        } else {
            System.out.println("No scroll found with the given ID.");
        }
    }


    private String findScrollById(String id) {
        Path filePath = Paths.get("scrolls_info.txt");
        try {
            List<String> lines = Files.readAllLines(filePath, StandardCharsets.UTF_8);
            for (String line : lines) {
                String[] parts = line.split(",");
                String scrollId = parts[0];
                if (scrollId.equals(id)) {
                    return line;
                }
            }
        } catch (IOException e) {
            System.out.println("Error reading the scrolls_info.txt file.");
        }
        return null;
    }

}
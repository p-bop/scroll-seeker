package admin;

import java.io.IOException;
import java.util.List;
import java.util.ArrayList;
import java.io.File;
import java.io.PrintWriter;
import java.util.Scanner;

public class AdminManager {
    private static Scanner scanner = new Scanner(System.in);

    public static void start() throws IOException {
        AdminInterface ai = new AdminInterface();

        while (true) {
            int choice = ai.promptOptions();
            if (choice >= 1 && choice <= 6) {
                switch (choice) {
                    case 1:
                        System.out.println("View a list of all users and their profiles");
                        listAllUsers();
                        break;
                    case 2:
                        System.out.println("Add a user");
                        addUser();
                        break;
                    case 3:
                        System.out.println("Delete a user");
                        deleteUser();
                        break;
                    case 4:
                        System.out.println("Welcome to Scroll System");
                        AdminScrollSystem adminscrollSystem = new AdminScrollSystem();
                        Scanner scanner = new Scanner(System.in);
                        adminscrollSystem.displayMenu(scanner);
                        break;
                    case 5:
                        System.out.println("See you soon Admin!");
                        return;
                    default:
                }
            }else{
                System.out.println("\nInvalid choice. Please select a valid option.\n");
            }
        }
    }
    public static void listAllUsers() {
        try (Scanner fileScanner = new Scanner(new File("users.txt"))) {
            while (fileScanner.hasNextLine()) {
                String line = fileScanner.nextLine();
                String[] parts = line.split("\\|");
                String idKey = parts[0];
                String username = parts[1];
                String fullName = parts[3];
                String phoneNumber = parts[4];
                String emailAddress = parts[5];
                String role = parts[6];

                System.out.println("ID Key: " + idKey);
                System.out.println("Username: " + username);
                System.out.println("Full Name: " + fullName);
                System.out.println("Phone Number: " + phoneNumber);
                System.out.println("Email Address: " + emailAddress);
                System.out.println("Role: " + role);
                System.out.println("------------------------------");
            }
        } catch (IOException e) {
            System.err.println("Error: 'users.txt' file not found.");
        }
    }

    public static void addUser() {
        String idKey;
        String username;
        String password;
        String fullName;
        String phoneNumber;
        String emailAddress;
        String role;

        while (true) {
            System.out.print("Enter a unique ID key: ");
            idKey = scanner.nextLine();

            if (!VirtualScrollAccessSystem.isIdKeyUnique(idKey)) {
                System.out.println("ID key is not unique. Please choose a different one.");
            } else {
                break;
            }
        }


        while (true) {
            System.out.print("Enter a username: ");
            username = scanner.nextLine();

            if (!VirtualScrollAccessSystem.isUsernameUnique(username)) {
                System.out.println("Username is not unique. Please choose a different one.");
            } else {
                break;
            }
        }


        System.out.print("Enter a password: ");
        password = VirtualScrollAccessSystem.hashPassword(scanner.nextLine());

        while (true) {
            System.out.print("Enter the user's full name (cannot include numbers): ");
            fullName = scanner.nextLine();
            if (!fullName.matches(".*\\d+.*")) {
                break;
            } else {
                System.out.println("Full name cannot contain digits. Please try again.");
            }
        }


        while (true) {
            System.out.print("Enter the user's phone number (10 digits): ");
            phoneNumber = scanner.nextLine();

            if (phoneNumber.matches("\\d{10}")) {
                break;
            } else {
                System.out.println("Phone number must be exactly 10 digits long. Please try again.");
            }
        }

        while (true) {
            System.out.print("Enter the user's email address (320 characters or less): ");
            emailAddress = scanner.nextLine();

            if (emailAddress.length() <= 320) {
                break;
            } else {
                System.out.println("Email address must be 320 characters or less. Please try again.");
            }
        }

        role = "user";

        VirtualScrollAccessSystem.saveUserDetails(idKey, username, password, fullName, phoneNumber, emailAddress, role);
        System.out.println("User added successfully!");
    }


    public static void deleteUser() {
        System.out.print("Enter the username of the user to delete: ");
        String usernameToDelete = scanner.nextLine();

        if (deleteUserByUsername(usernameToDelete)) {
            System.out.println("User deleted successfully.");
        } else {
            System.out.println("User not found or error deleting the user.");
        }
    }

    public static boolean deleteUserByUsername(String username) {
        File usersFile = new File("users.txt");
        List<String> newLines = new ArrayList<>();
        boolean isDeleted = false;

        try (Scanner fileScanner = new Scanner(usersFile)) {
            while (fileScanner.hasNextLine()) {
                String line = fileScanner.nextLine();
                String[] parts = line.split("\\|");
                String existingUsername = parts[1];

                if (!existingUsername.equals(username)) {
                    newLines.add(line);
                } else {
                    isDeleted = true;
                }
            }
        } catch (IOException e) {
            System.err.println("Error: 'users.txt' file not found.");
            return false;
        }

        if (isDeleted) {
            try (PrintWriter writer = new PrintWriter(usersFile)) {
                for (String line : newLines) {
                    writer.println(line);
                }
                return true;
            } catch (IOException e) {
                System.err.println("Error writing to users.txt");
                return false;
            }
        } else {
            return false;
        }
    }
}
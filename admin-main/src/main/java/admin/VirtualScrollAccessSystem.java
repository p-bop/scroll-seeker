package admin;

import java.util.*;
import java.io.*;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Base64;


public class VirtualScrollAccessSystem {


    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);


        while (true) {
            displayMainMenu();
            if (scanner.hasNextInt()) {
                int choice = scanner.nextInt();
                scanner.nextLine();
                if (choice >= 1 && choice <= 6) {
                    switch (choice) {
                        case 1:
                            System.out.println("\nYou are using the application as a guest. You can only view scrolls.\n");
                            ScrollSystem scrollSystem = new ScrollSystem();
                            scrollSystem.viewScrollsForGuest(scanner);
                            break;
                        case 2:
                            registerUser(scanner);
                            break;
                        case 3:
                            loginUser(scanner);
                            break;
                        case 4:
                            Admin.main(new String[0]);
                            break;
                        case 5:
                            forgotPassword(scanner);
                            break;
                        case 6:
                            System.out.println("\nThank you for using the Virtual Scroll Access System. Goodbye!\n");
                            System.exit(0);
                            break;
                        default:
                            System.out.println("\nInvalid choice. Please select a valid option.\n");
                            break;
                    }
                }else{
                    System.out.println("\nInvalid choice. Please select a valid option.\n");
                }
            }else {
                System.out.println("\nOnly appropriate numbers are allowed. Please enter a valid option.\n");
                scanner.nextLine();
            }
        }
    }


    public static void displayMainMenu() {
        System.out.println("Welcome to the Virtual Scroll Access System!");
        System.out.println("1. Continue as guest");
        System.out.println("2. Register new user");
        System.out.println("3. Login");
        System.out.println("4. Admin login");
        System.out.println("5. Forgot Password");
        System.out.println("6. Exit");
        System.out.print("Please select an option: ");
    }


    public static void registerUser(Scanner scanner) {
        System.out.println("\nRegister a new user:");

        String idKey;
        String username;
        String phoneNumber;
        String emailAddress;

        while (true) {
            System.out.print("Enter a unique ID key (integer): ");
            if (scanner.hasNextInt()) {
                idKey = scanner.nextLine();
                if (isIdKeyUnique(idKey)) {
                    break;
                } else {
                    System.out.println("ID key is not unique. Please choose a different one.");
                }
            } else {
                System.out.println("ID key must be an integer. Please enter a valid integer.");
                scanner.nextLine();
            }
        }

        while (true) {
            System.out.print("Enter a username (not more than 10 characters): ");
            username = scanner.nextLine();

            if (username.length() <= 10) {
                if (isUsernameUnique(username)) {
                    break;
                } else {
                    System.out.println("Username is not unique. Please choose a different one.");
                }
            } else {
                System.out.println("Username must be 10 characters or less. Please try again.");
            }
        }

        while (true) {
            System.out.print("Enter a phone number (10 digits): ");
            phoneNumber = scanner.nextLine();

            if (phoneNumber.matches("\\d{10}")) {
                break;
            } else {
                System.out.println("Phone number must be exactly 10 digits long. Please try again.");
            }
        }

        while (true) {
            System.out.print("Enter an email address (not more than 320 characters): ");
            emailAddress = scanner.nextLine();

            if (emailAddress.length() <= 320) {
                break;
            } else {
                System.out.println("Email address must be 320 characters or less. Please try again.");
            }
        }

        System.out.print("Enter a password: ");
        String password = scanner.nextLine();
        String hashedPassword = hashPassword(password);

        String fullName;
        while (true) {
            System.out.print("Enter your full name (Cannot include numbers): ");
            fullName = scanner.nextLine();
            if (!fullName.matches(".*\\d+.*")) {
                break;
            } else {
                System.out.println("Full name cannot contain digits. Please try again.");
            }
        }


        saveUserDetails(idKey, username, hashedPassword, fullName, phoneNumber, emailAddress, "user");

        System.out.println("\nUser registered successfully!\n");
    }


    public static boolean isIdKeyUnique(String idKey) {
        try (Scanner scanner = new Scanner(new File("users.txt"))) {
            while (scanner.hasNextLine()) {
                String line = scanner.nextLine();
                String[] parts = line.split("\\|");
                String existingIdKey = parts[0];
                if (existingIdKey.equals(idKey)) {
                    return false;
                }
            }
        } catch (IOException e) {
            System.err.println("Error: 'users.txt' file not found.");
        }
        return true;
    }


    public static boolean isUsernameUnique(String username) {
        try (Scanner scanner = new Scanner(new File("users.txt"))) {
            while (scanner.hasNextLine()) {
                String line = scanner.nextLine();
                String[] parts = line.split("\\|");
                String existingUsername = parts[1];
                if (existingUsername.equals(username)) {
                    return false;
                }
            }
        } catch (IOException e) {
            System.err.println("Error: 'users.txt' file not found.");
        }
        return true;
    }


    public static void saveUserDetails(String idKey, String username, String hashedPassword, String fullName, String phoneNumber, String emailAddress, String role) {
        try (PrintWriter writer = new PrintWriter(new FileWriter("users.txt", true))) {
            writer.println(idKey + "|" + username + "|" + hashedPassword + "|" + fullName + "|" + phoneNumber + "|" + emailAddress + "|" + role);
        } catch (IOException e) {
            System.err.println("Error writing to users.txt");
        }
    }



    public static String hashPassword(String password) {
        try {
            MessageDigest md = MessageDigest.getInstance("SHA-256");
            byte[] hashedPassword = md.digest(password.getBytes());
            return Base64.getEncoder().encodeToString(hashedPassword);
        } catch (NoSuchAlgorithmException e) {
            System.err.println("Password hashing error: " + e.getMessage());
            return null;
        }
    }

    public static void loginUser(Scanner scanner) {
        System.out.println("\nLogin to your account:");

        System.out.print("Enter your username: ");
        String username = scanner.nextLine();

        System.out.print("Enter your password: ");
        String password = scanner.nextLine();
        String hashedPassword = hashPassword(password);

        User loggedInUser = validateLoginCredentials(username, hashedPassword);

        if (loggedInUser != null) {
            System.out.println("\nLogin Successful!");
            displayUserDetails(loggedInUser);
            userMenu(scanner, username);
        } else {
            System.out.println("\nInvalid credentials.");
        }
    }

    public static User validateLoginCredentials(String username, String hashedPassword) {
        try (Scanner fileScanner = new Scanner(new File("users.txt"))) {
            while (fileScanner.hasNextLine()) {
                String line = fileScanner.nextLine();
                String[] parts = line.split("\\|");

                if (parts[1].equals(username) && parts[2].equals(hashedPassword)) {
                    return new User(parts[0], parts[1], parts[3], parts[4], parts[5], parts[6]);
                }
            }
        } catch (IOException e) {
            System.err.println("Error: 'users.txt' file not found.");
        }
        return null;
    }

    public static void displayUserDetails(User user) {
        System.out.println("\nUser Details:");
        System.out.println("ID Key: " + user.getIdKey());
        System.out.println("Username: " + user.getUsername());
        System.out.println("Full Name: " + user.getFullName());
        System.out.println("Phone Number: " + user.getPhoneNumber());
        System.out.println("Email Address: " + user.getEmailAddress());
        System.out.println("Role: " + user.getRole());
    }


    public static void displayUserInfo(Map<String, String> userDetails) {
        System.out.println("User Details:");
        for (Map.Entry<String, String> entry : userDetails.entrySet()) {
            System.out.println(entry.getKey() + ": " + entry.getValue());
        }
    }


    public static Map<String, String> validateCredentials(String username, String hashedPassword) {
        try (Scanner fileScanner = new Scanner(new File("users.txt"))) {
            while (fileScanner.hasNextLine()) {
                String line = fileScanner.nextLine();
                String[] parts = line.split("\\|");
                String existingIdKey = parts[0];
                String existingUsername = parts[1];
                String existingHashedPassword = parts[2];

                if (existingUsername.equals(username) && existingHashedPassword.equals(hashedPassword)) {
                    Map<String, String> userDetails = new HashMap<>();
                    userDetails.put("ID Key", existingIdKey);
                    userDetails.put("Username", existingUsername);
                    userDetails.put("Full Name", parts[3]);
                    userDetails.put("Phone Number", parts[4]);
                    userDetails.put("Email Address", parts[5]);
                    return userDetails;
                }
            }
        } catch (IOException e) {
            System.err.println("Error: 'users.txt' file not found.");
        }
        return null;
    }

    public static void userMenu(Scanner scanner, String username) {
        while (true) {
            System.out.println("\nUser Options:");
            System.out.println("1. Change Password");
            System.out.println("2. Modify User Details");
            System.out.println("3. Scroll System");
            System.out.println("4. Logout");
            System.out.print("Please select an option: ");
            int choice = scanner.nextInt();
            scanner.nextLine();

            switch (choice) {
                case 1:
                    changePassword(scanner, username);
                    break;
                case 2:
                    modifyUserDetails(scanner, username);
                    break;
                case 3:
                    System.out.println("Enter Scroll system successfully!!");
                    ScrollSystem scrollSystem = new ScrollSystem();
                    scrollSystem.displayMenu(scanner, username);
                    break;
                case 4:
                    return;
                default:
                    System.out.println("Invalid choice. Please select a valid option.");
                    break;
            }
        }
    }


    public static void changePassword(Scanner scanner, String username) {
        System.out.print("Enter your new password: ");
        String newPassword = hashPassword(scanner.nextLine());

        if (updatePassword(username, newPassword)) {
            System.out.println("Password updated successfully!");
        } else {
            System.out.println("Error updating password. Please try again.");
        }
    }

    public static boolean updatePassword(String username, String newHashedPassword) {
        File usersFile = new File("users.txt");
        List<String> newLines = new ArrayList<>();
        boolean isUpdated = false;

        try (Scanner fileScanner = new Scanner(usersFile)) {
            while (fileScanner.hasNextLine()) {
                String line = fileScanner.nextLine();
                String[] parts = line.split("\\|");
                String existingUsername = parts[1];

                if (existingUsername.equals(username)) {
                    parts[2] = newHashedPassword;
                    isUpdated = true;
                }
                newLines.add(String.join("|", parts));
            }
        } catch (IOException e) {
            System.err.println("Error: 'users.txt' file not found.");
            return false;
        }

        if (isUpdated) {
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

    public static void modifyUserDetails(Scanner scanner, String username) {
        System.out.println("Modify User Details:");

        System.out.print("Enter new username (leave blank to keep current): ");
        String newUsername = scanner.nextLine();
        if (newUsername.isEmpty()) {
            newUsername = username;
        }

        System.out.print("Enter new full name (leave blank to keep current): ");
        String newFullName = scanner.nextLine();

        System.out.print("Enter new phone number (leave blank to keep current): ");
        String newPhoneNumber = scanner.nextLine();

        System.out.print("Enter new email address (leave blank to keep current): ");
        String newEmail = scanner.nextLine();

        if (updateUserDetails(username, newUsername, newFullName, newPhoneNumber, newEmail)) {
            System.out.println("User details updated successfully!");
        } else {
            System.out.println("Error updating user details. Please try again.");
        }
    }

    public static boolean updateUserDetails(String oldUsername, String newUsername, String newFullName, String newPhoneNumber, String newEmail) {
        File usersFile = new File("users.txt");
        List<String> newLines = new ArrayList<>();
        boolean isUpdated = false;

        try (Scanner fileScanner = new Scanner(usersFile)) {
            while (fileScanner.hasNextLine()) {
                String line = fileScanner.nextLine();
                String[] parts = line.split("\\|");
                String existingUsername = parts[1];

                if (existingUsername.equals(oldUsername)) {
                    parts[1] = newUsername.isEmpty() ? existingUsername : newUsername;
                    parts[3] = newFullName.isEmpty() ? parts[3] : newFullName;
                    parts[4] = newPhoneNumber.isEmpty() ? parts[4] : newPhoneNumber;
                    parts[5] = newEmail.isEmpty() ? parts[5] : newEmail;
                    isUpdated = true;
                }
                newLines.add(String.join("|", parts));
            }
        } catch (IOException e) {
            System.err.println("Error: 'users.txt' file not found.");
            return false;
        }

        if (isUpdated) {
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
    public static void forgotPassword(Scanner scanner) {
        System.out.println("\nForgot Password Process:");

        System.out.print("Enter your unique ID key: ");
        String idKey = scanner.nextLine();

        System.out.print("Enter your email address: ");
        String emailAddress = scanner.nextLine();

        String username = validateUserByIdAndEmail(idKey, emailAddress);

        if (username != null) {
            System.out.print("Enter a new password: ");
            String newPassword = scanner.nextLine();
            String hashedNewPassword = hashPassword(newPassword);
            if (resetUserPassword(username, hashedNewPassword)) {
                System.out.println("Password reset successfully!");
            } else {
                System.out.println("Error resetting password. Please try again.");
            }
        } else {
            System.out.println("No user found with the provided ID key and email address.");
        }
    }
    public static String validateUserByIdAndEmail(String idKey, String email) {
        try (Scanner fileScanner = new Scanner(new File("users.txt"))) {
            while (fileScanner.hasNextLine()) {
                String line = fileScanner.nextLine();
                String[] parts = line.split("\\|");
                String existingIdKey = parts[0];
                String existingEmail = parts[5];

                if (existingIdKey.equals(idKey) && existingEmail.equals(email)) {
                    return parts[1];
                }
            }
        } catch (IOException e) {
            System.err.println("Error: 'users.txt' file not found.");
        }
        return null;
    }
    public static boolean resetUserPassword(String username, String hashedNewPassword) {
        File usersFile = new File("users.txt");
        List<String> newLines = new ArrayList<>();
        boolean isUpdated = false;

        try (Scanner fileScanner = new Scanner(usersFile)) {
            while (fileScanner.hasNextLine()) {
                String line = fileScanner.nextLine();
                String[] parts = line.split("\\|");
                String existingUsername = parts[1];

                if (existingUsername.equals(username)) {
                    parts[2] = hashedNewPassword;
                    isUpdated = true;
                }
                newLines.add(String.join("|", parts));
            }
        } catch (IOException e) {
            System.err.println("Error: 'users.txt' file not found.");
            return false;
        }

        if (isUpdated) {
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

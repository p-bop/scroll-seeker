package admin;

import java.util.Scanner;
public class AdminInterface {
    private Scanner scanner;
    public AdminInterface() {
        scanner = new Scanner(System.in);
    }
    public int promptOptions() {
        System.out.println("\nWelcome Admin! Please select an option below\n");
        System.out.println("Options:\n1. View all users\n2. Add a new user\n3. Delete an existing user\n4. Scroll System\n5. Logout\n");
        if (scanner.hasNextInt()) {
            int choice = scanner.nextInt();
            scanner.nextLine();
            return choice;
        } else {
            scanner.nextLine();
            System.out.println("\nInvalid input. Please enter a valid option.\n");
        }
        return 0;
    }
}

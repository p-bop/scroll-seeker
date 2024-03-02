package admin;

import java.io.*;
import java.util.Map;
import java.util.HashMap;
import java.util.Scanner;

public class Admin {
    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);

        try {
            File file = new File("admincred.json");
            Scanner jsonScanner = new Scanner(file);
            StringBuilder jsonContent = new StringBuilder();

            while (jsonScanner.hasNextLine()) {
                jsonContent.append(jsonScanner.nextLine());
            }
            jsonScanner.close();
            Map<String, String> credentials = parseJSON(jsonContent.toString());

            System.out.print("Enter username: ");
            String inputUsername = scanner.nextLine();

            System.out.print("Enter password: ");
            String inputPassword = scanner.nextLine();

            if (inputUsername.equals(credentials.get("username")) && inputPassword.equals(credentials.get("password"))) {
                System.out.println("Credentials match! Welcome!");
                AdminManager.start();
            } else {
                System.out.println("Invalid credentials. Access denied.");
            }
        } catch (IOException e) {
            e.printStackTrace();
            System.exit(0);
        }
    }

    public static Map<String, String> parseJSON(String json) {
        Map<String, String> result = new HashMap<>();
        String[] keyValuePairs = json
                .replace("{", "")
                .replace("}", "")
                .split(",");

        for (String pair : keyValuePairs) {
            String[] keyValue = pair.split(":");
            String key = keyValue[0].trim().replace("\"", "");
            String value = keyValue[1].trim().replace("\"", "");
            result.put(key, value);
        }
        return result;
    }
}


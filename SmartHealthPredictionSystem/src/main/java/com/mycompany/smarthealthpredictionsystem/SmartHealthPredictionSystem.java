package com.mycompany.smarthealthpredictionsystem;

import java.sql.*;
import java.util.*;

// Database Connection
public class SmartHealthPredictionSystem {

    private static final String URL = "jdbc:mysql://localhost:3306/SmartHealthDB";
    private static final String USER = "root";
    private static final String PASSWORD = "";
    private static final Scanner scanner = new Scanner(System.in);

    public static Connection getConnection() {
        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
            return DriverManager.getConnection(URL, USER, PASSWORD);
        } catch (ClassNotFoundException | SQLException e) {
            System.out.println("Database connection failed: " + e.getMessage());
            return null;
        }
    }

    // User Registration
    public static void registerUser() {
        try (Connection conn = getConnection()) {
            if (conn == null) {
                System.out.println("Failed to establish a database connection.");
                return;
            }

            System.out.print("Enter Username: ");
            String username = scanner.nextLine().trim();

            System.out.print("Enter Password: ");
            String password = scanner.nextLine().trim();

            System.out.print("Enter Age: ");
            int age = Integer.parseInt(scanner.nextLine().trim());

            System.out.print("Enter Gender (Male/Female): ");
            String gender = scanner.nextLine().trim();

            String sql = "INSERT INTO Users (username, password, age, gender) VALUES (?, ?, ?, ?)";
            try (PreparedStatement stmt = conn.prepareStatement(sql)) {
                stmt.setString(1, username);
                stmt.setString(2, password);
                stmt.setInt(3, age);
                stmt.setString(4, gender);

                int rows = stmt.executeUpdate();
                if (rows > 0) {
                    System.out.println("User registered successfully!");
                }
            }
        } catch (Exception e) {
            System.out.println("Error: " + e.getMessage());
        }
    }

    // Symptom Input and Prediction
    public static void inputSymptoms() {
        try {
            System.out.println("Enter symptoms separated by commas (e.g., fever, cough): ");
            String symptomsInput = scanner.nextLine().toLowerCase().trim();
            String[] inputSymptoms = symptomsInput.split("\\s*,\\s*");
            predictDisease(inputSymptoms);
        } catch (Exception e) {
            System.out.println("Error: " + e.getMessage());
        }
    }

    // Disease Prediction with Flexible Matching
    public static void predictDisease(String[] inputSymptoms) {
        Map<String, String> diseaseDatabase = new HashMap<>();
        diseaseDatabase.put("fever, cough", "Flu");
        diseaseDatabase.put("headache, nausea", "Migraine");
        diseaseDatabase.put("chest pain, shortness of breath", "Heart Disease");

        String bestMatch = null;
        int maxMatchCount = 0;

        for (Map.Entry<String, String> entry : diseaseDatabase.entrySet()) {
            String[] diseaseSymptoms = entry.getKey().split("\\s*,\\s*");
            int matchCount = 0;

            for (String inputSymptom : inputSymptoms) {
                for (String diseaseSymptom : diseaseSymptoms) {
                    if (inputSymptom.equalsIgnoreCase(diseaseSymptom)) {
                        matchCount++;
                    }
                }
            }

            if (matchCount > maxMatchCount) {
                maxMatchCount = matchCount;
                bestMatch = entry.getValue();
            }
        }

        if (maxMatchCount > 0) {
            System.out.println("Based on your symptoms, the predicted disease is: " + bestMatch);
        } else {
            System.out.println("No matching disease found. Please consult a doctor.");
        }
    }

    // Main Method
    public static void main(String[] args) {
        while (true) {
            try {
                System.out.println("\nSmart Health Prediction System");
                System.out.println("1. Register User");
                System.out.println("2. Enter Symptoms for Prediction");
                System.out.println("3. Exit");
                System.out.print("Choose an option: ");

                int choice = Integer.parseInt(scanner.nextLine().trim());

                switch (choice) {
                    case 1:
                        registerUser();
                        break;
                    case 2:
                        inputSymptoms();
                        break;
                    case 3:
                        System.out.println("Exiting the system. Stay healthy!");
                        System.exit(0);
                        break;
                    default:
                        System.out.println("Invalid choice. Please try again.");
                }
            } catch (NumberFormatException e) {
                System.out.println("Invalid input. Please enter a number.");
            }
        }
    }
}

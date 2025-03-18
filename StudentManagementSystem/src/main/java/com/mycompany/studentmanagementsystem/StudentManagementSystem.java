package com.mycompany.studentmanagementsystem;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Scanner;

// Database Connection Class
class DatabaseConnection {
    public static Connection getConnection() throws SQLException {
        try {
            // Load MySQL JDBC Driver
            Class.forName("com.mysql.cj.jdbc.Driver");
        } catch (ClassNotFoundException e) {
            throw new SQLException("MySQL JDBC Driver not found.", e);
        }

        String url = "jdbc:mysql://localhost:3306/StudentDB";
        String user = "root";
        String password = "";

        return DriverManager.getConnection(url, user, password);
    }
}

// Student Class
class Student {
    private int studentId;
    private String firstName;
    private String lastName;
    private int age;

    public Student(String firstName, String lastName, int age) {
        this.firstName = firstName;
        this.lastName = lastName;
        this.age = age;
    }

    public int getStudentId() { return studentId; }
    public void setStudentId(int studentId) { this.studentId = studentId; }
    public String getFirstName() { return firstName; }
    public String getLastName() { return lastName; }
    public int getAge() { return age; }
}

// Data Access Object (DAO) Class
class StudentDAO {
    public void addStudent(Student student) throws SQLException {
        String query = "INSERT INTO Students (FirstName, LastName, Age) VALUES (?, ?, ?)";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(query)) {
            pstmt.setString(1, student.getFirstName());
            pstmt.setString(2, student.getLastName());
            pstmt.setInt(3, student.getAge());
            pstmt.executeUpdate();
            System.out.println("Student added successfully!");
        }
    }

    public void viewStudents() throws SQLException {
        String query = "SELECT * FROM Students";
        try (Connection conn = DatabaseConnection.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(query)) {
            while (rs.next()) {
                System.out.printf("ID: %d, Name: %s %s, Age: %d%n",
                        rs.getInt("StudentID"), rs.getString("FirstName"),
                        rs.getString("LastName"), rs.getInt("Age"));
            }
        }
    }

    public void updateStudent(int id, int age) throws SQLException {
        String query = "UPDATE Students SET Age = ? WHERE StudentID = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(query)) {
            pstmt.setInt(1, age);
            pstmt.setInt(2, id);
            int rowsUpdated = pstmt.executeUpdate();
            if (rowsUpdated > 0) {
                System.out.println("Student updated successfully!");
            } else {
                System.out.println("Student not found.");
            }
        }
    }

    public void deleteStudent(int id) throws SQLException {
        String query = "DELETE FROM Students WHERE StudentID = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(query)) {
            pstmt.setInt(1, id);
            int rowsDeleted = pstmt.executeUpdate();
            if (rowsDeleted > 0) {
                System.out.println("Student deleted successfully!");
            } else {
                System.out.println("Student not found.");
            }
        }
    }
}

// Main Class
public class StudentManagementSystem {

    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        StudentDAO studentDAO = new StudentDAO();

        while (true) {
            System.out.println("1. Add Student");
            System.out.println("2. View Students");
            System.out.println("3. Update Student");
            System.out.println("4. Delete Student");
            System.out.println("5. Exit");
            System.out.print("Choose an option: ");
            int choice = scanner.nextInt();

            try {
                switch (choice) {
                    case 1:
                        System.out.print("First Name: ");
                        String firstName = scanner.next();
                        System.out.print("Last Name: ");
                        String lastName = scanner.next();
                        System.out.print("Age: ");
                        int age = scanner.nextInt();
                        studentDAO.addStudent(new Student(firstName, lastName, age));
                        break;
                    case 2:
                        studentDAO.viewStudents();
                        break;
                    case 3:
                        System.out.print("Student ID: ");
                        int idToUpdate = scanner.nextInt();
                        System.out.print("New Age: ");
                        int newAge = scanner.nextInt();
                        studentDAO.updateStudent(idToUpdate, newAge);
                        break;
                    case 4:
                        System.out.print("Student ID: ");
                        int idToDelete = scanner.nextInt();
                        studentDAO.deleteStudent(idToDelete);
                        break;
                    case 5:
                        System.exit(0);
                    default:
                        System.out.println("Invalid choice.");
                }
            } catch (SQLException e) {
                System.out.println("Error: " + e.getMessage());
            }
        }
    }
}

package boundary;

import adt.*;
import entity.Doctor;
import entity.User;
import utility.MessageUI;

import java.util.Scanner;
import java.util.UUID;

public class DoctorUI {
    private QueueInterface<String> choiceQueue;
    private MessageUI UI;
    private Scanner scanner;

    public DoctorUI() {
        choiceQueue = new LinkedQueue<>();
        UI = new MessageUI();
        scanner = new Scanner(System.in);
    }

    public Integer mainMenu() {
        choiceQueue.clear();
        choiceQueue.enqueue("Add Doctor");
        choiceQueue.enqueue("View Doctors");
        choiceQueue.enqueue("Update Doctor");
        choiceQueue.enqueue("Delete Doctor");
        choiceQueue.enqueue("Search Doctors");
        return UI.mainUI("Doctor Management", choiceQueue);
    }

    public Doctor getDoctorDetails() {
        System.out.println("Enter doctor details (type CANCEL to abort):");
        System.out.println("-".repeat(50));

        String name = getInput("Full Name");
        if (name == null) return null;

        String phone = getInput("Phone Number");
        if (phone == null) return null;

        String email = getInput("Email");
        if (email == null) return null;

        String address = getInput("Address");
        if (address == null) return null;

        String dateOfBirth = getInput("Date of Birth (yyyy-mm-dd)");
        if (dateOfBirth == null) return null;

        User.Gender gender = selectGender();
        if (gender == null) return null;

        String specialization = getInput("Specialization");
        if (specialization == null) return null;

        String licenseNumber = getInput("License Number");
        if (licenseNumber == null) return null;

        return new Doctor(
            UUID.randomUUID(),
            name,
            address,
            gender,
            phone,
            email,
            dateOfBirth,
            specialization,
            licenseNumber
        );
    }

    private String getInput(String fieldName) {
        while (true) {
            System.out.print(fieldName + ": ");
            String input = scanner.nextLine().trim();
            
            if (input.equalsIgnoreCase("CANCEL")) {
                return null;
            }
            
            if (!input.isEmpty()) {
                return input;
            }
            
            System.out.println("ERROR: " + fieldName + " is required.");
        }
    }

    private User.Gender selectGender() {
        while (true) {
            System.out.println("Select Gender:");
            System.out.println("1. MALE");
            System.out.println("2. FEMALE");
            System.out.print("Choice (1-2) or CANCEL: ");
            
            String input = scanner.nextLine().trim();
            
            if (input.equalsIgnoreCase("CANCEL")) {
                return null;
            }
            
            switch (input) {
                case "1": return User.Gender.MALE;
                case "2": return User.Gender.FEMALE;
                default: System.out.println("ERROR: Invalid choice.");
            }
        }
    }

    public void displayDoctorList(ArrayList<Doctor> doctors, int currentPage, int totalPages, String searchQuery, String title) {
        System.out.println("\n" + "=".repeat(90));
        System.out.println(title);
        System.out.println("=".repeat(90));
        
        if (!searchQuery.isEmpty()) {
            System.out.println("Search: \"" + searchQuery + "\"");
            System.out.println("-".repeat(90));
        }
        
        System.out.printf("Page %d of %d | Total: %d doctor(s)\n", currentPage, totalPages, doctors.size());
        System.out.println("-".repeat(90));
        
        if (doctors.isEmpty()) {
            System.out.println("No doctors found.");
            return;
        }

        int startIndex = (currentPage - 1) * 5;
        int endIndex = Math.min(startIndex + 5, doctors.size());
        
        System.out.printf("%-4s %-20s %-20s %-15s %-15s\n", 
                         "No.", "Name", "Specialization", "License", "Phone");
        System.out.println("-".repeat(90));
        
        for (int i = startIndex; i < endIndex; i++) {
            Doctor doctor = doctors.get(i);
            System.out.printf("%-4d %-20s %-20s %-15s %-15s\n",
                            i + 1,
                            truncateString(doctor.getName(), 18),
                            truncateString(doctor.getSpecialization(), 18),
                            truncateString(doctor.getLicenseNumber(), 13),
                            truncateString(doctor.getPhone(), 13));
        }
        System.out.println("-".repeat(90));
    }

    public void displayDoctorSelectionList(ArrayList<Doctor> doctors) {
        System.out.println("\nSelect Doctor:");
        System.out.println("-".repeat(70));
        System.out.printf("%-4s %-25s %-20s %-15s\n", 
                         "No.", "Name", "Specialization", "License");
        System.out.println("-".repeat(70));
        
        for (int i = 0; i < doctors.size(); i++) {
            Doctor doctor = doctors.get(i);
            System.out.printf("%-4d %-25s %-20s %-15s\n",
                            i + 1,
                            truncateString(doctor.getName(), 23),
                            truncateString(doctor.getSpecialization(), 18),
                            truncateString(doctor.getLicenseNumber(), 13));
        }
        System.out.println("-".repeat(70));
    }

    private String truncateString(String str, int maxLength) {
        if (str == null) return "";
        return str.length() > maxLength ? str.substring(0, maxLength - 3) + "..." : str;
    }

    public void displayMessage(String message) {
        System.out.println(message);
    }

    public void displaySuccess(String message) {
        System.out.println("SUCCESS: " + message);
    }

    public void displayError(String message) {
        System.out.println("ERROR: " + message);
    }

    public void pause() {
        System.out.print("Press Enter to continue...");
        scanner.nextLine();
    }
}

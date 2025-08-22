package control;

import adt.ArrayList;
import adt.QueueInterface;
import adt.LinkedQueue;
import boundary.PatientUI;
import entity.Patient;

import java.io.IOException;
import java.util.Scanner;

public class PatientControl {

    private static final int PAGE_SIZE = 5;

    private QueueInterface<Patient> patientQueue;
    private PatientUI ui;
    private Scanner scanner;

    public PatientControl() {
        patientQueue = new LinkedQueue<>();
        ui = new PatientUI();
        scanner = new Scanner(System.in);

        // Load 5 sample patients into the queue at startup
        adt.ArrayList<Patient> samplePatients = utility.GeneratePatientData.createSamplePatients();
        for (int i = 0; i < samplePatients.size(); i++) {
            patientQueue.enqueue(samplePatients.get(i));
        }
    }

    public void main() throws IOException {
        while (true) {
            Integer choice = ui.mainMenu();
            switch (choice) {
                case 1:
                    registerPatient();
                    break;
                case 2:
                    displayPatient();
                    break;
                case 3:
                    editPatient();
                    break;
                case 4:
                    deletePatient();
                    break;
                case 999:
                    return;
                default:
                    System.out.println("Invalid choice.");
            }
        }
    }

    // Register new patient
    public void registerPatient() {
        Patient newPatient = ui.getPatientDetails();
        patientQueue.enqueue(newPatient);
        System.out.println("Patient registered successfully!");
    }

    // Edit patient with pagination & search
    public void editPatient() {
        if (patientQueue.isEmpty()) {
            ui.displayError("No patients available to edit.");
            return;
        }

        ArrayList<Patient> patients = queueToList();

        int choice = selectPatient(patients, "edit");
        if (choice == 0) return;

        Patient p = patients.get(choice - 1);

        System.out.print("New Name (" + p.getName() + "): ");
        String name = scanner.nextLine().trim();
        if (!name.isEmpty()) p.setName(name);

        System.out.print("New Phone (" + p.getPhone() + "): ");
        String phone = scanner.nextLine().trim();
        if (!phone.isEmpty()) p.setPhone(phone);

        System.out.print("New Email (" + p.getEmail() + "): ");
        String email = scanner.nextLine().trim();
        if (!email.isEmpty()) p.setEmail(email);

        ui.displaySuccess("Patient updated successfully!");
    }

    // Delete patient with pagination & search
    public void deletePatient() {
        if (patientQueue.isEmpty()) {
            ui.displayError("No patients available to delete.");
            return;
        }

        ArrayList<Patient> patients = queueToList();

        int choice = selectPatient(patients, "delete");
        if (choice == 0) return;

        Patient p = patients.get(choice - 1);
        System.out.print("Are you sure you want to delete " + p.getName() + "? (Y/N): ");
        String confirm = scanner.nextLine().trim().toUpperCase();

        if (confirm.equals("Y")) {
            patients.remove(choice - 1);
            patientQueue = new LinkedQueue<>();
            for (int i = 0; i < patients.size(); i++) {
                patientQueue.enqueue(patients.get(i));
            }
            ui.displaySuccess("Patient deleted successfully!");
        } else {
            ui.displayMessage("Delete cancelled.");
        }
    }

    // Display patient list with pagination + search
    public void displayPatient() {
        if (patientQueue.isEmpty()) {
            System.out.println("No patients found.");
            return;
        }

        ArrayList<Patient> originalView = queueToList();
        ArrayList<Patient> currentView = new ArrayList<>(originalView);

        int currentPage = 1;
        String searchQuery = "";

        while (true) {
            int totalItems = currentView.size();
            int totalPages = (totalItems + PAGE_SIZE - 1) / PAGE_SIZE;
            if (totalPages == 0) totalPages = 1;

            ui.displayPatientList(currentView, totalItems, currentPage, totalPages, searchQuery);

            System.out.println("Press: [A] Prev | [D] Next | [S] Search | [R] Reset | [Q] Quit");
            System.out.print("Enter choice: ");
            String input = scanner.nextLine().trim().toLowerCase();

            switch (input) {
                case "a":
                    if (currentPage > 1) currentPage--; else System.out.println("This is the first page.");
                    break;
                case "d":
                    if (currentPage < totalPages) currentPage++; else System.out.println("This is the last page.");
                    break;
                case "s":
                    System.out.print("Enter search (Name/IC/Phone): ");
                    searchQuery = scanner.nextLine().trim();
                    ArrayList<Patient> filtered = filterPatients(originalView, searchQuery);
                    if (!filtered.isEmpty()) {
                        currentView = filtered;
                        currentPage = 1;
                    } else {
                        System.out.println("No results found for: " + searchQuery);
                        pause();
                    }
                    break;
                case "r":
                    currentView = originalView;
                    searchQuery = "";
                    currentPage = 1;
                    break;
                case "q":
                    return;
                default:
                    System.out.println("Invalid choice.");
            }
        }
    }

    // Helper: Convert queue -> ArrayList
    private ArrayList<Patient> queueToList() {
        ArrayList<Patient> patients = new ArrayList<>();
        while (!patientQueue.isEmpty()) {
            patients.add(patientQueue.dequeue());
        }
        for (int i = 0; i < patients.size(); i++) {
            patientQueue.enqueue(patients.get(i));
        }
        return patients;
    }

    // Reusable patient selector (pagination + search)
    private int selectPatient(ArrayList<Patient> originalView, String actionLabel) {
        ArrayList<Patient> currentView = new ArrayList<>(originalView);
        int currentPage = 1;
        String searchQuery = "";

        while (true) {
            int totalItems = currentView.size();
            int totalPages = (totalItems + PAGE_SIZE - 1) / PAGE_SIZE;
            if (totalPages == 0) totalPages = 1;

            ui.displayPatientList(currentView, totalItems, currentPage, totalPages, searchQuery);

            System.out.println("Press: [A] Prev | [D] Next | [S] Search | [R] Reset |");
            System.out.println("Or enter patient number to " + actionLabel + " | [Q] Cancel");
            System.out.print("Choice: ");
            String input = scanner.nextLine().trim().toLowerCase();

            switch (input) {
                case "a":
                    if (currentPage > 1) currentPage--; else System.out.println("This is the first page.");
                    break;
                case "d":
                    if (currentPage < totalPages) currentPage++; else System.out.println("This is the last page.");
                    break;
                case "s":
                    System.out.print("Enter search (Name/IC/Phone): ");
                    searchQuery = scanner.nextLine().trim();
                    ArrayList<Patient> filtered = filterPatients(originalView, searchQuery);
                    if (!filtered.isEmpty()) {
                        currentView = filtered;
                        currentPage = 1;
                    } else {
                        System.out.println("No results found for: " + searchQuery);
                        pause();
                    }
                    break;
                case "r":
                    currentView = originalView;
                    searchQuery = "";
                    currentPage = 1;
                    break;
                case "q":
                    return 0; // cancel
                default:
                    try {
                        int choice = Integer.parseInt(input);
                        int index = choice - 1;
                        if (index >= 0 && index < currentView.size()) {
                            return originalView.indexOf(currentView.get(index)) + 1;
                        } else {
                            System.out.println("Invalid choice.");
                        }
                    } catch (NumberFormatException e) {
                        System.out.println("Invalid input.");
                    }
            }
        }
    }

    // Filtering logic
    private ArrayList<Patient> filterPatients(ArrayList<Patient> source, String query) {
        ArrayList<Patient> results = new ArrayList<>();
        if (query == null || query.trim().isEmpty()) return results;

        String q = query.toLowerCase();

        for (int i = 0; i < source.size(); i++) {
            Patient p = source.get(i);
            String name = safeLower(p.getName());
            String ic = safeLower(p.getPatientIC());
            String phone = safeLower(p.getPhone());
            if (name.contains(q) || ic.contains(q) || phone.contains(q)) {
                results.add(p);
            }
        }
        return results;
    }

    private String safeLower(String s) {
        return (s == null) ? "" : s.toLowerCase();
    }

    private void pause() {
        System.out.print("Press Enter to continue...");
        scanner.nextLine();
    }
}
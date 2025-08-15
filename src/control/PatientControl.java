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
    }

    public void main() throws IOException {
        while (true) {
            Integer choice = ui.mainMenu();
            switch (choice) {
                case 1: // View Sales Items
                    registerPatient();
                    break;
                case 2:
                    displayPatient();
                    break;
                case 3:
                    // other features...
                    break;
                case 4:
                    // other features...
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

    // Display patient list with pagination + search
    public void displayPatient() {
        if (patientQueue.isEmpty()) {
            System.out.println("No patients found.");
            return;
        }

        // Convert Queue to ArrayList for easy pagination
        ArrayList<Patient> originalView = new ArrayList<>();
        while (!patientQueue.isEmpty()) {
            Patient p = patientQueue.dequeue();
            originalView.add(p);
        }
        // Refill queue so data isn't lost
        for (int i = 0; i < originalView.size(); i++) {
            patientQueue.enqueue(originalView.get(i));
        }

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
                    if (currentPage > 1) currentPage--;
                    else {
                        System.out.println("This is the first page.");
                        pause();
                    }
                    break;

                case "d":
                    if (currentPage < totalPages) currentPage++;
                    else {
                        System.out.println("This is the last page.");
                        pause();
                    }
                    break;

                case "s":
                    System.out.print("Enter search (Name/IC/Phone): ");
                    searchQuery = scanner.nextLine().trim();
                    ArrayList<Patient> filtered = filterPatients(originalView, searchQuery);

                    if (filtered.isEmpty()) {
                        System.out.println("No results found for: " + searchQuery);
                        pause();
                    } else {
                        currentView = filtered;
                        currentPage = 1;
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

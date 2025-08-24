package control;

import adt.ArrayList;
import adt.ListInterface;
import adt.QueueInterface;
import adt.LinkedQueue;
import boundary.PatientUI;
import entity.Consultation;
import entity.Doctor;
import entity.Patient;

import java.io.IOException;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.Scanner;
import java.util.UUID;

public class PatientControl {

    private static final int PAGE_SIZE = 5;

    private QueueInterface<Patient> patientQueue;
    private ListInterface<Consultation> consultationList;
    private ListInterface<Doctor> doctors;
    private PatientUI ui;
    private Scanner scanner;

    public PatientControl(ArrayList<Patient> patients, ListInterface<Consultation> consultationList, ListInterface<Doctor> doctors) {
        patientQueue = new LinkedQueue<>();
        ui = new PatientUI();
        scanner = new Scanner(System.in);
        this.consultationList = consultationList;
        this.doctors = doctors;

        // Load 5 sample patients into the queue at startup
        for (int i = 0; i < patients.size(); i++) {
            patientQueue.enqueue(patients.get(i));
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
                    registerConsultation();
                    break;
                case 5:
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
    public Patient registerPatient() {
        Patient newPatient = ui.getPatientDetails(); // returns null if user typed CANCEL

        if (newPatient == null) {
            ui.displayMessage("Registration cancelled. Returning to Main Menu...");
            pause();
            return null;
        }

        patientQueue.enqueue(newPatient);
        ui.displaySuccess("Patient registered successfully!");
        pause();
        return newPatient;
    }

    // Edit patient
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

    // Register Consultant
    public void registerConsultation() {
        System.out.println("\n=== REGISTER CONSULTATION LOOKUP ===");

        String key;
        while (true) {
            System.out.print("Enter IC (12 digits) / Passport (min 6 alphanumeric) (or type CANCEL): ");
            key = scanner.nextLine().trim();

            if (key.equalsIgnoreCase("CANCEL")) {
                ui.displayMessage("Cancelled. Returning to Main Menu...");
                pause();
                return;
            }

            // Validation
            boolean isIC = key.matches("\\d{12}");
            boolean isPassport = key.matches("[A-Za-z0-9]{6,}");

            if (!isIC && !isPassport) {
                System.out.println("❌ Invalid input. Please enter a valid IC (12 digits) or Passport (min 6 alphanumeric).");
                continue; // re-prompt
            }

            break; // valid input
        }

        // Lookup patient
        Patient p = findPatientByICOrPassport(key);

        if (p == null) {
            System.out.println("No patient found with that IC/Passport.");
            System.out.print("Create a new patient now? (Y/N): ");
            String ans = scanner.nextLine().trim();
            if (ans.equalsIgnoreCase("Y")) {
                Patient created = registerPatient(); // may return null if cancelled
                if (created == null) return;         // cancelled during registration
                p = created;                         // proceed with new patient
            } else {
                ui.displayMessage("Operation cancelled. Returning to Main Menu...");
                pause();
                return;
            }
        }

        // p is guaranteed non-null here
        showConsultationMenu(p);
    }


    // Helper: Find patient by IC or Passport
    private Patient findPatientByICOrPassport(String keyword) {
        ArrayList<Patient> patients = new ArrayList<>();
        while (!patientQueue.isEmpty()) {
            patients.add(patientQueue.dequeue());
        }
        for (int i = 0; i < patients.size(); i++) {
            patientQueue.enqueue(patients.get(i)); // restore queue
        }

        for (int i = 0; i < patients.size(); i++) {
            Patient p = patients.get(i);
            if ((p.getPatientIC() != null && p.getPatientIC().equals(keyword)) ||
                    (p.getPatientPassport() != null && p.getPatientPassport().equals(keyword))) {
                return p;
            }
        }
        return null;
    }

    private void showConsultationMenu(Patient p) {
        if (p == null) {
            ui.displayError("Internal error: patient is null.");
            pause();
            return;
        }

        while (true) {
            System.out.println("\n----------------------------------------");
            System.out.println("Patient Found:");
            System.out.println("Name       : " + p.getName());
            System.out.println("IC         : " + (p.getPatientIC() != null ? p.getPatientIC() : "-"));
            System.out.println("Passport   : " + (p.getPatientPassport() != null ? p.getPatientPassport() : "-"));
            System.out.println("Student ID : " + (p.getStudentID() != null ? p.getStudentID() : "-"));
            System.out.println("Gender     : " + p.getGender());
            System.out.println("Phone      : " + p.getPhone());
            System.out.println("Email      : " + p.getEmail());
            System.out.println("Address    : " + p.getAddress());
            System.out.println("DOB        : " + p.getDateOfBirth());
            System.out.println("----------------------------------------");

            System.out.println("[1] Register Consultation");
            System.out.println("[2] Edit Patient");
            System.out.println("[3] View Consultation History");
            System.out.println("[4] Delete Patient");
            System.out.println("[Q] Back to Main Menu");
            System.out.print("Choice: ");
            String input = scanner.nextLine().trim().toLowerCase();

            switch (input) {
                case "1":
                    // register consultation
                    registerConsultation(p);
                    break;
                case "2":
                    editPatient();
                    break;
                case "3":
                    System.out.println("View Consultation");
                    pause();
                    break;
                case "4":
                    deletePatient();
                    break;
                case "q":
                    return;
                default:
                    System.out.println("Invalid choice.");
                    pause();
            }
        }
    }

    private void registerConsultation(Patient p) {
        // Step 1: Choose a doctor
        System.out.println("Available Doctors:");
        for (int i = 0; i < doctors.size(); i++) {
            Doctor doctor = doctors.get(i);
            System.out.println("[" + (i + 1) + "] " + doctor.getName());
        }
        System.out.print("Select doctor (1-" + doctors.size() + "): ");
        int doctorChoice = Integer.parseInt(scanner.nextLine().trim());

        if (doctorChoice < 1 || doctorChoice > doctors.size()) {
            System.out.println("Invalid doctor selection.");
            return;
        }
        Doctor selectedDoctor = doctors.get(doctorChoice - 1);

        // Step 2: Enter the consultation note
        System.out.print("Enter the consultation issue/notes: ");
        String consultationNote = scanner.nextLine().trim();

        // Step 3: Create the consultation and add it to the consultation list
        Consultation newConsultation = new Consultation(UUID.randomUUID(), p, selectedDoctor, LocalDate.now(), Consultation.Status.WAITING, consultationNote, LocalTime.now(), null, 0, "", null);
        consultationList.add(newConsultation);
        System.out.println("Consultation registered successfully.");
        pause();
    }

    // Delete patient
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

    // Display patient list
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

    // Reusable patient selector
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
// Ng Zhe Wei
package control;

import adt.ArrayList;
import adt.ListInterface;
import adt.QueueInterface;
import adt.LinkedQueue;
import boundary.PatientUI;
import entity.Consultation;
import entity.Doctor;
import entity.Patient;
import entity.pharmacyManagement.LabTest;
import entity.pharmacyManagement.Medicine;
import entity.pharmacyManagement.Prescription;
import entity.pharmacyManagement.SalesItem;
import entity.DutySchedule;
import control.MainControl;

import java.io.IOException;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.Period;
import java.util.Comparator;
import java.util.Scanner;
import java.util.UUID;

public class PatientControl {

    private static final int PAGE_SIZE = 10;

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
                    editPatient(null);
                    break;
                case 4:
                    registerConsultation();
                    break;
                case 5:
                    deletePatient(null);
                    break;
                case 6:
                    patientReport();
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
    public void editPatient(Patient p) {
        if (patientQueue.isEmpty()) {
            ui.displayError("No patients available to edit.");
            return;
        }
        if (p == null) {
            ArrayList<Patient> patients = queueToList();

            int choice = selectPatient(patients, "edit");
            if (choice == 0) return;

            p = patients.get(choice - 1);
        }



        while (true) {
            System.out.println("\n=== EDIT PATIENT DETAILS ===");
            System.out.println("Editing: " + p.getName());
            System.out.println("1. Name");
            System.out.println("2. Address");
            System.out.println("3. Gender");
            System.out.println("4. Phone");
            System.out.println("5. Email");
            System.out.println("6. Date of Birth");
            System.out.println("7. Patient IC");
            System.out.println("8. Passport");
            System.out.println("0. Done editing");
            System.out.print("Enter choice: ");

            String input = scanner.nextLine().trim();
            if (!input.matches("\\d")) {
                System.out.println("Invalid choice.");
                continue;
            }

            int field = Integer.parseInt(input);
            if (field == 0) break;

            switch (field) {
                case 1:
                    System.out.print("New Name (" + p.getName() + "): ");
                    String name = scanner.nextLine().trim();
                    if (!name.isEmpty()) p.setName(name);
                    break;

                case 2:
                    System.out.print("New Address (" + p.getAddress() + "): ");
                    String address = scanner.nextLine().trim();
                    if (!address.isEmpty()) p.setAddress(address);
                    break;

                case 3:
                    System.out.print("New Gender (" + p.getGender() + ") (M/F): ");
                    String gender = scanner.nextLine().trim().toUpperCase();
                    if (gender.equals("M") || gender.equals("F")) p.setGender(gender);
                    else System.out.println("Invalid gender.");
                    break;

                case 4:
                    System.out.print("New Phone (" + p.getPhone() + "): ");
                    String phone = scanner.nextLine().trim();
                    if (phone.matches("\\d{10,11}")) p.setPhone(phone);
                    else if (!phone.isEmpty()) System.out.println("Invalid phone number.");
                    break;

                case 5:
                    System.out.print("New Email (" + p.getEmail() + "): ");
                    String email = scanner.nextLine().trim();
                    if (email.matches("^[\\w-\\.]+@([\\w-]+\\.)+[\\w-]{2,4}$")) p.setEmail(email);
                    else if (!email.isEmpty()) System.out.println("Invalid email format.");
                    break;

                case 6:
                    System.out.print("New Date of Birth (" + p.getDateOfBirthString() + "): ");
                    String dob = scanner.nextLine().trim();
                    if (!dob.isEmpty()) {
                        try {
                            LocalDate newDob = LocalDate.parse(dob);
                            if (newDob.isAfter(LocalDate.now())) {
                                System.out.println("Error: DOB cannot be in future.");
                            } else {
                                p.setDateOfBirth(newDob);
                            }
                        } catch (Exception e) {
                            System.out.println("Invalid date format.");
                        }
                    }
                    break;

                case 7:
                    System.out.print("New Patient IC (" + (p.getPatientIC() != null ? p.getPatientIC() : "none") + "): ");
                    String ic = scanner.nextLine().trim();
                    if (ic.matches("\\d{12}")) p.setPatientIC(ic);
                    else if (!ic.isEmpty()) System.out.println("Invalid IC format.");pause();
                    break;

                case 8:
                    System.out.print("New Passport (" + (p.getPatientPassport() != null ? p.getPatientPassport() : "none") + "): ");
                    String passport = scanner.nextLine().trim();
                    if (!passport.isEmpty()) p.setPatientPassport(passport);
                    break;

                default:
                    System.out.println("Invalid choice.");
                    pause();
            }
        }

        ui.displaySuccess("Patient updated successfully!");
        pause();
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
                System.out.println("Invalid input. Please enter a valid IC (12 digits) or Passport (min 6 alphanumeric).");
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


    // Find patient by IC or Passport
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
                    registerConsultation(p);
                    break;
                case "2":
                    editPatient(p);
                    break;
                case "3":
                    viewConsultationHistory(p);
                    break;
                case "4":
                    if(deletePatient(p)){
                        return;
                    }
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

        // Step 1: Get doctors currently on duty
        ArrayList<Doctor> doctorsOnDuty = getDoctorsOnDuty();
        
        if (doctorsOnDuty.isEmpty()) {
            System.out.println("No doctors are currently on duty. Please try again later.");
            pause();
            return;
        }
        
        // Step 2: Display available doctors on duty and get valid selection
        Doctor selectedDoctor = null;
        while (selectedDoctor == null) {
            System.out.println("Doctors Currently On Duty:");
            for (int i = 0; i < doctorsOnDuty.size(); i++) {
                Doctor doctor = doctorsOnDuty.get(i);
                System.out.println("[" + (i + 1) + "] " + doctor.getName() + " (" + doctor.getSpecialization() + ")");
            }
            System.out.print("Select doctor (1-" + doctorsOnDuty.size() + ") or type CANCEL: ");
            String input = scanner.nextLine().trim();
            
            if (input.equalsIgnoreCase("CANCEL")) {
                System.out.println("Consultation registration cancelled.");
                pause();
                return;
            }
            
            try {
                int doctorChoice = Integer.parseInt(input);
                if (doctorChoice >= 1 && doctorChoice <= doctorsOnDuty.size()) {
                    selectedDoctor = doctorsOnDuty.get(doctorChoice - 1);
                } else {
                    System.out.println("ERROR: Invalid selection. Please enter a number between 1 and " + doctorsOnDuty.size() + ".");
                }
            } catch (NumberFormatException e) {
                System.out.println("ERROR: Invalid input. Please enter a valid number or type CANCEL.");
            }
        }

        // Enter the consultation note
        System.out.print("Enter the consultation issue/notes: ");
        String consultationNote = scanner.nextLine().trim();

        // Create the consultation and add it to the consultation list
        Consultation newConsultation = new Consultation(UUID.randomUUID(), p, selectedDoctor, LocalDate.now(), Consultation.Status.WAITING, consultationNote, LocalTime.now(), null, 0, "", new ArrayList<Prescription>(), new ArrayList<LabTest>());
        consultationList.add(newConsultation);
        System.out.println("Consultation registered successfully.");
        pause();
    }

    // Delete patient
    public boolean deletePatient(Patient p) {
        if (patientQueue.isEmpty()) {
            ui.displayError("No patients available to delete.");
            return false;
        }

        ArrayList<Patient> patients = queueToList();
        if(p ==  null) {

            int choice = selectPatient(patients, "delete");
            if (choice == 0) return false;

            p = patients.get(choice - 1);
        }


        System.out.print("Are you sure you want to delete " + p.getName() + "? (Y/N): ");
        String confirm = scanner.nextLine().trim().toUpperCase();

        if (confirm.equals("Y")) {
            patients.remove(p);
            patientQueue = new LinkedQueue<>();
            for (int i = 0; i < patients.size(); i++) {
                patientQueue.enqueue(patients.get(i));
            }
            ui.displaySuccess("Patient deleted successfully!");
            return true;
        } else {
            ui.displayMessage("Delete cancelled.");
            return false;
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

    public void patientReport() {
        while (true) {
            System.out.println("\n=== REPORT MENU ===");
            System.out.println("[1] Patient Demographics – Age Distribution");
            System.out.println("[2] Patient Demographics – Identification Type");
            System.out.println("[Q] Back to Main Menu");
            System.out.print("Choice: ");
            String input = scanner.nextLine().trim().toLowerCase();

            switch (input) {
                case "1":
                    reportPatientByAgeGroup();
                    break;
                case "2":
                    reportPatientByIdType();
                    break;
                case "q":
                    return;
                default:
                    System.out.println("Invalid choice.");
            }
        }
    }

    public void reportPatientByAgeGroup() {
        if (patientQueue.isEmpty()) {
            System.out.println("No patients found.");
            pause();
            return;
        }

        ui.printReportHeader("Age Distribution of Patients");

        ArrayList<Patient> patients = queueToList();
        patients.sort(Comparator.comparing(Patient::getDateOfBirth).reversed());

        int under18 = 0, between18And30 = 0, between31And50 = 0, above50 = 0;

        for (Patient p : patients) {
            int age = Period.between(p.getDateOfBirth(), LocalDate.now()).getYears();
            if (age < 18) under18++;
            else if (age <= 30) between18And30++;
            else if (age <= 50) between31And50++;
            else above50++;
        }

        System.out.printf("<18 years      : %d%n", under18);
        System.out.printf("18 - 30 years  : %d%n", between18And30);
        System.out.printf("31 - 50 years  : %d%n", between31And50);
        System.out.printf("51+ years      : %d%n", above50);
        System.out.println("---------------------------------------\n");

        pause();
    }

    public void reportPatientByIdType() {
        if (patientQueue.isEmpty()) {
            System.out.println("No patients found.");
            pause();
            return;
        }

        ui.printReportHeader("Patient Distribution by Identification Type");

        ArrayList<Patient> patients = queueToList();

        int icCount = 0, passportCount = 0;

        for (int i = 0; i < patients.size(); i++) {
            Patient p = patients.get(i);
            if (p.getPatientIC() != null && !p.getPatientIC().isEmpty()) {
                icCount++;
            } else if (p.getPatientPassport() != null && !p.getPatientPassport().isEmpty()) {
                passportCount++;
            }
        }

        System.out.println("--- Patient by Identification Type ---");
        System.out.printf("Malaysian IC : %d%n", icCount);
        System.out.printf("Passport     : %d%n", passportCount);
        System.out.println("--------------------------------------\n");

        pause();
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

    private ArrayList<Doctor> getDoctorsOnDuty() {
        ArrayList<Doctor> doctorsOnDuty = new ArrayList<>();
        LocalDate currentDate = LocalDate.now();
        LocalTime currentTime = LocalTime.now();

        for (int i = 0; i < doctors.size(); i++) {
            Doctor doctor = doctors.get(i);
            UUID doctorId = doctor.getUserID();
            
            ListInterface<DutySchedule> doctorSchedules = MainControl.schedules.getValue(doctorId);
            
            if (doctorSchedules != null) {
                for (int j = 0; j < doctorSchedules.size(); j++) {
                    DutySchedule schedule = doctorSchedules.get(j);
                    
                    if (schedule.isValid() && 
                        schedule.getDateObject().equals(currentDate) &&
                        isWithinTimeRange(currentTime, schedule.getStartTimeObject(), schedule.getEndTimeObject())) {
                        doctorsOnDuty.add(doctor);
                        break;
                    }
                }
            }
        }
        
        return doctorsOnDuty;
    }
    
    private boolean isWithinTimeRange(LocalTime currentTime, LocalTime startTime, LocalTime endTime) {
        return !currentTime.isBefore(startTime) && !currentTime.isAfter(endTime);
    }

    private void viewConsultationHistory(Patient patient) {
        System.out.println("\n=== CONSULTATION HISTORY ===");
        System.out.println("Patient: " + patient.getName());
        
        ArrayList<Consultation> patientConsultations = getPatientConsultations(patient);
        
        if (patientConsultations.isEmpty()) {
            System.out.println("No consultation history found for this patient.");
            pause();
            return;
        }
        
        // Sort consultations by date (newest first)
        patientConsultations.sort((c1, c2) -> {
            int dateCompare = c2.getConsultatonDate().compareTo(c1.getConsultatonDate());
            if (dateCompare == 0) {
                return c2.getStartTime().compareTo(c1.getStartTime());
            }
            return dateCompare;
        });
        
        int currentPage = 1;
        String searchQuery = "";
        ArrayList<Consultation> currentView = new ArrayList<>(patientConsultations);
        
        while (true) {
            int totalItems = currentView.size();
            int totalPages = (totalItems + PAGE_SIZE - 1) / PAGE_SIZE;
            if (totalPages == 0) totalPages = 1;
            
            displayConsultationHistory(currentView, currentPage, totalPages, searchQuery, patient);
            
            System.out.println("Press: [A] Prev | [D] Next | [S] Search | [R] Reset | [Q] Back");
            System.out.print("Enter choice: ");
            String input = scanner.nextLine().trim().toLowerCase();
            
            switch (input) {
                case "a":
                    if (currentPage > 1) {
                        currentPage--;
                    } else {
                        System.out.println("This is the first page.");
                        pause();
                    }
                    break;
                case "d":
                    if (currentPage < totalPages) {
                        currentPage++;
                    } else {
                        System.out.println("This is the last page.");
                        pause();
                    }
                    break;
                case "s":
                    System.out.print("Enter search (Notes/Treatment/Doctor): ");
                    searchQuery = scanner.nextLine().trim();
                    ArrayList<Consultation> filtered = filterConsultations(patientConsultations, searchQuery);
                    if (!filtered.isEmpty()) {
                        currentView = filtered;
                        currentPage = 1;
                    } else {
                        System.out.println("No results found for: " + searchQuery);
                        pause();
                    }
                    break;
                case "r":
                    currentView = new ArrayList<>(patientConsultations);
                    searchQuery = "";
                    currentPage = 1;
                    break;
                case "q":
                    return;
                default:
                    System.out.println("Invalid choice.");
                    pause();
            }
        }
    }
    
    private ArrayList<Consultation> getPatientConsultations(Patient patient) {
        ArrayList<Consultation> patientConsultations = new ArrayList<>();
        UUID patientId = patient.getUserID();
        
        for (int i = 0; i < consultationList.size(); i++) {
            Consultation consultation = consultationList.get(i);
            if (consultation.getPatientId().equals(patientId)) {
                patientConsultations.add(consultation);
            }
        }
        
        return patientConsultations;
    }
    
    private ArrayList<Consultation> filterConsultations(ArrayList<Consultation> source, String query) {
        ArrayList<Consultation> results = new ArrayList<>();
        if (query == null || query.trim().isEmpty()) return results;
        
        String q = query.toLowerCase();
        
        for (int i = 0; i < source.size(); i++) {
            Consultation c = source.get(i);
            String notes = safeLower(c.getNotes());
            String treatment = safeLower(c.getMedicalTreatment());
            String doctorName = safeLower(c.getDoctor().getName());
            
            if (notes.contains(q) || treatment.contains(q) || doctorName.contains(q)) {
                results.add(c);
            }
        }
        return results;
    }
    
    private void displayConsultationHistory(ArrayList<Consultation> consultations, int currentPage, 
                                          int totalPages, String searchQuery, Patient patient) {
        System.out.println("\n" + "=".repeat(80));
        System.out.println("CONSULTATION HISTORY - " + patient.getName());
        System.out.println("=".repeat(80));
        
        if (!searchQuery.isEmpty()) {
            System.out.println("Search: \"" + searchQuery + "\"");
            System.out.println("-".repeat(80));
        }
        
        System.out.printf("Page %d of %d | Total: %d consultation(s)\n", currentPage, totalPages, consultations.size());
        System.out.println("-".repeat(80));
        
        int startIndex = (currentPage - 1) * PAGE_SIZE;
        int endIndex = Math.min(startIndex + PAGE_SIZE, consultations.size());
        
        if (consultations.isEmpty()) {
            System.out.println("No consultations found.");
            return;
        }
        
        for (int i = startIndex; i < endIndex; i++) {
            Consultation c = consultations.get(i);
            System.out.printf("\n[%d] Date: %s | Time: %s", 
                            i + 1, c.getConsultatonDate(), c.getStartTime());
                            
            if (c.getEndTime() != null) {
                System.out.printf(" - %s", c.getEndTime());
            }
            
            // Show status with color indicators
            System.out.printf(" | Status: %s", getStatusDisplay(c.getStatus()));
            
            System.out.printf("\n    Doctor: %s (%s)", 
                            c.getDoctor().getName(), c.getDoctor().getSpecialization());
                            
            if (c.getNotes() != null && !c.getNotes().trim().isEmpty()) {
                System.out.printf("\n    Notes: %s", 
                                truncateString(c.getNotes().replace("\n", " "), 60));
            }
            
            if (c.getMedicalTreatment() != null && !c.getMedicalTreatment().trim().isEmpty()) {
                System.out.printf("\n    Treatment: %s", 
                                truncateString(c.getMedicalTreatment(), 60));
            }
            
            if (c.getStatus() == Consultation.Status.COMPLETED && c.getTotalPayment() > 0) {
                System.out.printf("\n    Total Paid: RM %.2f", c.getTotalPayment());
            }
            
            System.out.println("\n" + "-".repeat(80));
        }
    }
    
    private String getStatusDisplay(Consultation.Status status) {
        switch (status) {
            case WAITING:
                return "WAITING";
            case IN_PROGRESS:
                return "IN PROGRESS";
            case BILLING:
                return "BILLING";
            case COMPLETED:
                return "COMPLETED";
            default:
                return status.toString();
        }
    }
    
    private String truncateString(String str, int maxLength) {
        if (str == null) return "";
        return str.length() > maxLength ? str.substring(0, maxLength - 3) + "..." : str;
    }

    private void pause() {
        System.out.print("Press Enter to continue...");
        scanner.nextLine();
    }
}
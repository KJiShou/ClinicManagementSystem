// Teoh Yong Ming
package control;

import adt.HashedDictionary;
import adt.ListInterface;
import adt.ArrayList;
import boundary.ConsultationUI;
import control.PharmacyControl;
import entity.Consultation;
import entity.Doctor;
import entity.Patient;
import entity.pharmacyManagement.LabTest;
import entity.pharmacyManagement.Medicine;
import entity.pharmacyManagement.Prescription;
import utility.GenerateConsultationData;

import java.io.IOException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.Comparator;
import java.util.Scanner;
import java.util.UUID;

public class ConsultationControl {
    private static final int PAGE_SIZE = 5;
    ListInterface<Consultation> consultationList;
    PrescriptionControl  prescriptionControl;
    DutyScheduleControl scheduleControl;
    PharmacyControl pharmacyControl;
    private ArrayList<Patient> patients;
    private ListInterface<Doctor> doctors;
    ConsultationUI UI;
    Scanner scanner;
    DateTimeFormatter formatter = DateTimeFormatter.ofPattern("HH:mm");

    ConsultationControl(ListInterface<Consultation> consultationList, ArrayList<Patient> patients, ListInterface<Doctor> doctors, PrescriptionControl  prescriptionControl, DutyScheduleControl scheduleControl, PharmacyControl pharmacyControl) {
        try {
            this.consultationList = consultationList;
            scanner = new Scanner(System.in);
            UI = new ConsultationUI(scanner);
            this.prescriptionControl = prescriptionControl;
            this.scheduleControl =  scheduleControl;
            this.pharmacyControl = pharmacyControl;
            this.patients = patients;
            this.doctors = doctors;
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void main() throws IOException {
        while (true) {
            Integer choice = UI.mainMenu();
            switch (choice) {
                case 1:
                    viewConsultation();
                    break;
                case 2:
                    addConsultation();
                    break;
                case 3:
                    removeConsultation();
                    break;
                case 4:
                    revenueReport();
                    break;
                case 5:
                    durationReport();
                    break;
                case 999:
                    return;
                default:
                    System.out.println("Invalid choice.");
            }
        }
    }

    // If you don't already have it:
    private static final Consultation.Status[] STATUS_ORDER = {
            Consultation.Status.WAITING,
            Consultation.Status.IN_PROGRESS,
            Consultation.Status.BILLING,
            Consultation.Status.COMPLETED
    };

    private int compareByStatusThenDate(Consultation a, Consultation b) {
        int sa = indexOfStatus(a.status);
        int sb = indexOfStatus(b.status);
        if (sa != sb) return Integer.compare(sa, sb);
        return getArrival(a).compareTo(getArrival(b));
    }

    private int indexOfStatus(Consultation.Status status) {
        for (int i = 0; i < STATUS_ORDER.length; i++) {
            if (STATUS_ORDER[i] == status) return i;
        }
        return -1;
    }

    private LocalTime getArrival(Consultation c) {
        return c.getStartTime();
    }

    public void viewConsultation() throws IOException {
        while (true) {
            if (consultationList.isEmpty()) {
                System.out.println("No consultations found.");
                UI.pause();
                return;
            }

            // snapshot and sort by status then arrival (for section display)
            ArrayList<Consultation> currentView = new ArrayList<>((ArrayList) consultationList);
            currentView.sort(this::compareByStatusThenDate);

            // -------- Build arrival-order numbering (global) --------
            ArrayList<Consultation> byArrival = new ArrayList<>((ArrayList) consultationList);
            byArrival.sort(Comparator.comparing(this::getArrival));

            // number each consultation by arrival (1..N); use identity to be safe
            HashedDictionary<Consultation, Integer> indexMap = new HashedDictionary<>();
            HashedDictionary<Integer, Consultation> pickMap = new HashedDictionary<>();
            for (int i = 0; i < byArrival.size(); i++) {
                Consultation c = byArrival.get(i);
                int number = i + 1;
                indexMap.add(c, number);
                pickMap.add(number, c);
            }

            // -------- Group into sections (no pagination) --------
            int waitingTotal = 0, inProgressTotal = 0, billingTotal = 0, completedTotal = 0;
            ArrayList<Consultation> waitingList = new ArrayList<>();
            ArrayList<Consultation> inProgressList = new ArrayList<>();
            ArrayList<Consultation> billingList = new ArrayList<>();
            ArrayList<Consultation> completedList = new ArrayList<>();

            for (int i = 0; i < currentView.size(); i++) {
                Consultation c = currentView.get(i);
                switch (c.status) {
                    case WAITING -> { waitingList.add(c); waitingTotal++; }
                    case IN_PROGRESS -> { inProgressList.add(c); inProgressTotal++; }
                    case BILLING -> { billingList.add(c); billingTotal++; }
                    case COMPLETED -> { completedList.add(c); completedTotal++; }
                }
            }

            // -------- Render with arrival numbers + waiting time for WAITING --------
            UI.displayConsultationSectionsWithArrival(
                    waitingList, waitingTotal,
                    inProgressList, inProgressTotal,
                    billingList, billingTotal,
                    completedList, completedTotal,
                    indexMap
            );

            // -------- Selection loop (choose by number) --------

            System.out.print("Enter a number to select a consultation, or [Q] to quit: ");
            String input = scanner.nextLine().trim().toLowerCase();
            if ("q".equals(input)) return;

            int chosen;
            try {
                chosen = Integer.parseInt(input);
            } catch (NumberFormatException e) {
                System.out.println("Invalid input. Type a valid number or Q.");
                UI.pause();
                continue;
            }

            Consultation selected = pickMap.getValue(chosen);
            if (selected == null) {
                System.out.println("No consultation with that number. Try again.");
                UI.pause();
                continue;
            }

            updateConsultation(selected);
        }
    }

    public void addConsultation() throws IOException {
        System.out.println("\n=== ADD NEW CONSULTATION ===");

        UUID id = UUID.randomUUID();

        Patient patient = selectPatient();
        if (patient == null) {
            return;
        }
        Doctor doctor = selectDoctor();
        if (doctor == null) {
            return;
        }

        LocalDate consultationDate = LocalDate.now();

        Consultation.Status status = null;
        do {
            try {
                System.out.println("\nSelect status:");
                System.out.println("1. WAITING");
                System.out.println("2. IN PROGRESS");
                System.out.println("3. BILLING");
                System.out.println("4. COMPLETED");
                System.out.println("0. Cancel");

                System.out.print("Enter status choice: ");
                int statusChoice = Integer.parseInt(scanner.nextLine().trim());
                switch (statusChoice) {
                    case 1:
                        status = Consultation.Status.WAITING;
                        break;
                    case 2:
                        status = Consultation.Status.IN_PROGRESS;
                        break;
                    case 3:
                        status = Consultation.Status.BILLING;
                        break;
                    case 4:
                        status = Consultation.Status.COMPLETED;
                        break;
                    case 0:
                        System.out.println("Operation cancelled.");
                        return;
                    default:
                        System.out.println("Invalid number, try again.");
                        break;
                }
            } catch (Exception e) {
                System.out.println("Invalid input, try again.");
            }
        } while (status == null);

        System.out.print("Enter Notes (or 'cancel' to exit): ");
        String notes = scanner.nextLine();
        if ("cancel".equalsIgnoreCase(notes.trim())) {
            System.out.println("Operation cancelled.");
            return;
        }

        LocalTime startTime;
        do {
            try {
                System.out.print("Enter Start Time (HH:MM, 24H format) or 'cancel' to exit: ");
                String startTimeInput = scanner.nextLine();
                if ("cancel".equalsIgnoreCase(startTimeInput.trim())) {
                    System.out.println("Operation cancelled.");
                    return;
                }
                startTime = LocalTime.parse(startTimeInput);
            } catch (Exception e) {
                System.out.println("Invalid time format, try again.\n");
                startTime = null;
            }
        } while (startTime == null);

        LocalTime endTime;
        do {
            try {
                System.out.print("Enter End Time (HH:MM, 24H format) or 'cancel' to exit: ");
                String endTimeInput = scanner.nextLine();
                if ("cancel".equalsIgnoreCase(endTimeInput.trim())) {
                    System.out.println("Operation cancelled.");
                    return;
                }
                endTime = LocalTime.parse(endTimeInput);
            } catch (Exception e) {
                System.out.println("Invalid time format, try again.\n");
                endTime = null;
            }
        } while (endTime == null);

        float totalPayment = 0;
        do {
            try {
                System.out.print("Enter Total Payment (or 'cancel' to exit) : RM  ");
                String paymentInput = scanner.nextLine();
                if ("cancel".equalsIgnoreCase(paymentInput.trim())) {
                    System.out.println("Operation cancelled.");
                    return;
                }
                totalPayment = Float.parseFloat(paymentInput);
                if (totalPayment <= 0) {
                    System.out.println("Amount must be greater than 0, try again.\n");
                }
            } catch (NumberFormatException e) {
                System.out.println("Invalid amount, try again.\n");
            }
        } while (totalPayment <= 0);

        Consultation newConsultation = new Consultation(
                id, patient, doctor, consultationDate, status,
                notes, startTime, endTime, totalPayment, "medical Treatment",null, null);

        consultationList.add(newConsultation);
        System.out.println("Consultation added successfully!");
        UI.pause();
    }

    private Patient selectPatient() {
        System.out.println("\n=== SELECT PATIENT ===");

        if (patients.isEmpty()) {
            UI.displayError("No patients found in the system.");
            return null;
        }

        ArrayList<Patient> currentView = new ArrayList<>(patients);
        int currentPage = 1;
        String searchQuery = "";

        while (true) {
            int totalItems = currentView.size();
            int totalPages = (totalItems + PAGE_SIZE - 1) / PAGE_SIZE;
            if (totalPages == 0) totalPages = 1;

            displayPatientList(currentView, totalItems, currentPage, totalPages, searchQuery);

            System.out.println("Press: [A] Prev | [D] Next | [S] Search | [R] Reset |");
            System.out.println("Or enter patient number to select | [Q] Cancel");
            System.out.print("Choice: ");
            String input = scanner.nextLine().trim().toLowerCase();

            switch (input) {
                case "a":
                    if (currentPage > 1) {
                        currentPage--;
                    } else {
                        System.out.println("This is the first page.");
                        UI.pause();
                    }
                    break;
                case "d":
                    if (currentPage < totalPages) {
                        currentPage++;
                    } else {
                        System.out.println("This is the last page.");
                        UI.pause();
                    }
                    break;
                case "s":
                    System.out.print("Enter search (Name/IC/Phone): ");
                    searchQuery = scanner.nextLine().trim();
                    ArrayList<Patient> filtered = filterPatients(patients, searchQuery);
                    if (!filtered.isEmpty()) {
                        currentView = filtered;
                        currentPage = 1;
                    } else {
                        System.out.println("No results found for: " + searchQuery);
                        UI.pause();
                    }
                    break;
                case "r":
                    currentView = new ArrayList<>(patients);
                    searchQuery = "";
                    currentPage = 1;
                    break;
                case "q":
                    return null;
                default:
                    try {
                        int choice = Integer.parseInt(input);
                        if (choice >= 1 && choice <= currentView.size()) {
                            return currentView.get(choice - 1);
                        } else {
                            System.out.println("Invalid choice. Please select from the displayed patients.");
                            UI.pause();
                        }
                    } catch (NumberFormatException e) {
                        System.out.println("Please enter a valid number or command.");
                        UI.pause();
                    }
            }
        }
    }

    private void displayPatientList(ArrayList<Patient> patients, int totalItems, int currentPage, int totalPages, String searchQuery) {
        System.out.println("\n" + "=".repeat(80));
        System.out.println("                            PATIENT SELECTION");
        System.out.println("=".repeat(80));
        
        if (!searchQuery.isEmpty()) {
            System.out.printf("Search results for: \"%s\"\n", searchQuery);
            System.out.println("-".repeat(80));
        }
        
        System.out.printf("Page %d of %d | Total patients: %d\n", currentPage, totalPages, totalItems);
        System.out.println("-".repeat(80));
        
        if (patients.isEmpty()) {
            System.out.println("No patients found.");
            System.out.println("=".repeat(80));
            return;
        }
        
        int start = (currentPage - 1) * PAGE_SIZE;
        int end = Math.min(start + PAGE_SIZE, patients.size());
        
        System.out.printf("%-4s | %-25s | %-15s | %-20s\n", "No.", "Name", "IC", "Phone");
        System.out.println("-".repeat(80));
        
        for (int i = start; i < end; i++) {
            Patient patient = patients.get(i);
            System.out.printf("%-4d | %-25s | %-15s | %-20s\n",
                    i + 1,
                    truncateString(patient.getName(), 25),
                    patient.getPatientIC() != null ? patient.getPatientIC() : "N/A",
                    patient.getPhone() != null ? patient.getPhone() : "N/A");
        }
        
        System.out.println("=".repeat(80));
    }

    private ArrayList<Patient> filterPatients(ArrayList<Patient> patients, String searchQuery) {
        if (searchQuery == null || searchQuery.trim().isEmpty()) {
            return new ArrayList<>(patients);
        }
        
        String query = searchQuery.toLowerCase();
        ArrayList<Patient> filtered = new ArrayList<>();
        
        for (int i = 0; i < patients.size(); i++) {
            Patient patient = patients.get(i);
            boolean matches = false;
            
            // Search by name
            if (patient.getName() != null && patient.getName().toLowerCase().contains(query)) {
                matches = true;
            }
            
            // Search by IC
            if (!matches && patient.getPatientIC() != null && patient.getPatientIC().toLowerCase().contains(query)) {
                matches = true;
            }
            
            // Search by phone
            if (!matches && patient.getPhone() != null && patient.getPhone().contains(query)) {
                matches = true;
            }
            
            if (matches) {
                filtered.add(patient);
            }
        }
        
        return filtered;
    }

    private Doctor selectDoctor() {
        System.out.println("\n=== SELECT DOCTOR ===");

        if (doctors.isEmpty()) {
            UI.displayError("No doctors found in the system.");
            return null;
        }

        while (true) {
            System.out.printf("Available Doctors:\n");
            for (int i = 0; i < doctors.size(); i++) {
                Doctor doctor = doctors.get(i);
                System.out.printf("[%d] Dr. %s (%s)\n",
                        i + 1,
                        doctor.getName(),
                        doctor.getSpecialization());
            }

            System.out.printf("Select doctor (1-%d) or 0 to cancel: ", doctors.size());

            try {
                int choice = Integer.parseInt(scanner.nextLine().trim());
                if (choice == 0) {
                    return null;
                }
                if (choice >= 1 && choice <= doctors.size()) {
                    return doctors.get(choice - 1);
                }
                System.out.println("Invalid selection.");
            } catch (NumberFormatException e) {
                System.out.println("Please enter a valid number.");
            }
        }
    }

    public void updateConsultation(Consultation consToUpdate) throws IOException {
        while (true) {
            System.out.println("\n=== UPDATE CONSULTATION ===");
            try {
                if (consToUpdate == null) {
                    System.out.println("Error: Consultation not found.");
                    UI.pause();
                    return;
                }
                UI.displayConsultationDetails(consToUpdate);
                System.out.println("\n--- Select field to update ---");
                switch (consToUpdate.status) {
                    case WAITING:
                        System.out.println("1. Notes");
                        System.out.println("2. Medical Treatment");
                        System.out.println("3. Prescription");
                        System.out.println("4. Lab Test");
                        System.out.println("5. Change Status to IN PROGRESS");
                        System.out.println("9. Exit");
                        break;
                    case IN_PROGRESS:
                        System.out.println("1. Notes");
                        System.out.println("2. Medical Treatment");
                        System.out.println("3. Prescription");
                        System.out.println("4. Lab Test");
                        System.out.println("5. Change Status to BILLING");
                        System.out.println("9. Exit");
                        break;
                    case BILLING:
                        System.out.println("1. Notes");
                        System.out.println("2. Medical Treatment");
                        System.out.println("3. Prescription");
                        System.out.println("4. Lab Test");
                        System.out.println("5. Dispense Bill");
                        System.out.println("9. Exit");
                        break;
                    case COMPLETED:
                        System.out.println("Unable to update completed consultation.");
                        System.out.println("Enter to continue...");
                        scanner.nextLine();
                        return;
                }
                System.out.print("Enter choice: ");
                String input = scanner.nextLine().trim();
                int choice;
                try {
                    choice = Integer.parseInt(input);
                } catch (NumberFormatException e) {
                    System.out.println("ERROR: Please enter a valid number.");
                    continue;
                }
                switch (choice) {
                    case 1:
                        String currentNotes = consToUpdate.getNotes();
                        if (currentNotes == null) currentNotes = "";
                        String updateNotesChoice;
                        String finalNotes = currentNotes;
                        do {
                            System.out.println("\nCurrent Notes:");
                            if (currentNotes.isEmpty() || currentNotes.trim().isEmpty()) {
                                System.out.println("[No current notes]");
                            } else {
                                System.out.println("----------------------------------------------");
                                String[] currentNoteLines = currentNotes.split("\\R");
                                for (String line : currentNoteLines) {
                                    System.out.println(line);
                                }
                                System.out.println("----------------------------------------------");
                            }
                            System.out.println("\nNotes Update Options:");
                            System.out.println("1. Add new lines to current notes");
                            System.out.println("2. Overwrite current notes (start fresh)");
                            System.out.println("3. Cancel (no change to notes)");
                            System.out.print("Enter your choice (1-3): ");
                            updateNotesChoice = scanner.nextLine().trim();
                            switch (updateNotesChoice) {
                                case "1":
                                    StringBuilder tempNotesBuilder = new StringBuilder(finalNotes);
                                    if (!finalNotes.isEmpty() && !finalNotes.endsWith("\n")) {
                                        tempNotesBuilder.append("\n");
                                    }
                                    System.out.println("Enter new lines to append (type 'END_NOTES' on a new line to finish):");
                                    String lineToAppend;
                                    while (true) {
                                        lineToAppend = scanner.nextLine();
                                        if (lineToAppend.equalsIgnoreCase("END_NOTES")) {
                                            break;
                                        }
                                        tempNotesBuilder.append(lineToAppend).append("\n");
                                    }
                                    if (tempNotesBuilder.length() > 0 && tempNotesBuilder.charAt(tempNotesBuilder.length() - 1) == '\n') {
                                        tempNotesBuilder.setLength(tempNotesBuilder.length() - 1);
                                    }
                                    finalNotes = tempNotesBuilder.toString();
                                    System.out.println("Notes appended.");
                                    break;
                                case "2":
                                    System.out.println("Enter new notes (type 'END_NOTES' on a new line to finish):");
                                    StringBuilder newNotesBuilder = new StringBuilder();
                                    String lineToOverwrite;
                                    while (true) {
                                        lineToOverwrite = scanner.nextLine();
                                        if (lineToOverwrite.equalsIgnoreCase("END_NOTES")) {
                                            break;
                                        }
                                        newNotesBuilder.append(lineToOverwrite).append("\n");
                                    }
                                    if (newNotesBuilder.length() > 0 && newNotesBuilder.charAt(newNotesBuilder.length() - 1) == '\n') {
                                        newNotesBuilder.setLength(newNotesBuilder.length() - 1);
                                    }
                                    finalNotes = newNotesBuilder.toString();
                                    System.out.println("Notes overwritten.");
                                    break;
                                case "3":
                                    System.out.println("Notes update cancelled.");
                                    finalNotes = currentNotes;
                                    break;
                                default:
                                    System.out.println("Invalid choice. Please enter 1, 2, or 3.");
                                    continue;
                            }
                            if (!updateNotesChoice.equals("3")) {
                                System.out.print("Notes updated. Press 'e' to edit notes again, or any other key to finalize: ");
                                String editAgain = scanner.nextLine().trim().toLowerCase();
                                if (!editAgain.equals("e")) {
                                    break;
                                }
                            } else {
                                break;
                            }
                        } while (true);
                        consToUpdate.setNotes(finalNotes);
                        break;
                    case 2:
                        if (consToUpdate.status == Consultation.Status.COMPLETED) {
                            System.out.println("You cannot modify the completed consultation.");
                            return;
                        }
                        System.out.print("Enter medical treatment: ");
                        consToUpdate.setMedicalTreatment(scanner.nextLine());
                        break;
                    case 3:
                        prescriptionControl.setPrescriptions(consToUpdate.getPrescription());
                        prescriptionControl.main();
                        break;
                    case 4:
                        if (consToUpdate.status != Consultation.Status.COMPLETED) {
                            manageLabTests(consToUpdate);
                        } else {
                            System.out.println("You cannot modify the completed consultation.");
                        }
                        break;
                    case 5:
                        if (consToUpdate.status == Consultation.Status.WAITING) {
                            consToUpdate.setStatus(Consultation.Status.IN_PROGRESS);
                            System.out.println("Status changed to IN PROGRESS.");
                        } else if (consToUpdate.status == Consultation.Status.IN_PROGRESS) {
                            consToUpdate.setStatus(Consultation.Status.BILLING);
                            System.out.println("Status changed to BILLING.");
                        } else if (consToUpdate.status == Consultation.Status.BILLING) {
                            dispenseBill(consToUpdate);
                            System.out.println("Status changed to COMPLETED.");
                        } else if (consToUpdate.status == Consultation.Status.COMPLETED) {
                            System.out.println("You cannot modify the completed consultation.");
                        }
                        break;
                    case 9:
                        return;
                    default:
                        System.out.println("Invalid choice.");
                        break;
                }
                UI.pause();
            } catch (IllegalArgumentException e) {
                System.out.println("Error: Invalid UUID format.");
                UI.pause();
            } catch (Exception e) {
                System.out.println("An error occurred during update.");
                e.printStackTrace();
                UI.pause();
            }
        }
    }

    public void removeConsultation() {
        System.out.println("\n=== REMOVE CONSULTATION ===");

        ListInterface<Consultation> removableConsultations = new ArrayList<>();
        for (int i = 0; i < consultationList.size(); i++) {
            removableConsultations.add(consultationList.get(i));
        }

        if (removableConsultations.isEmpty()) {
            UI.displayMessage("No consultations available for removal.");
            UI.pause();
            return;
        }

        Consultation selectedConsultation = UI.selectConsultation(removableConsultations);
        if (selectedConsultation == null) {
            UI.displayMessage("Operation cancelled.");
            UI.pause();
            return;
        }

        System.out.println("\nConsultation to Remove:");
        System.out.println("- Patient: " + (selectedConsultation.getPatient() != null ? selectedConsultation.getPatient().getName() : "N/A"));
        System.out.println("- Doctor: " + (selectedConsultation.getDoctor() != null ? selectedConsultation.getDoctor().getName() : "N/A"));
        System.out.println("- Status: " + (selectedConsultation.getStatus() != null ? selectedConsultation.getStatus() : "N/A"));

        System.out.print("\nConfirm removal? (Y/N): ");
        String confirm = scanner.nextLine().trim().toUpperCase();

        if (confirm.equals("Y")) {
            boolean removed = consultationList.remove(selectedConsultation);
            if (removed) {
                UI.displaySuccess("Consultation removed successfully!");
            } else {
                UI.displayError("Failed to remove consultation.");
            }
        } else {
            UI.displayMessage("Removal aborted.");
        }
        UI.pause();
    }

    public void dispenseBill(Consultation consultation) {
        if (consultation == null) {
            System.out.println("Error: Consultation not found.");
            return;
        }

        System.out.println("\n=== DISPENSING BILL ===");

        // Calculate total amounts
        double consultationFee = 50.0; // Base consultation fee
        double prescriptionTotal = calculatePrescriptionTotal(consultation);
        double labTestTotal = calculateLabTestTotal(consultation);
        double subtotal = consultationFee + prescriptionTotal + labTestTotal;
        double tax = subtotal * 0.06; // 6% SST
        double totalAmount = subtotal + tax;

        // Display detailed bill
        displayDetailedBill(consultation, consultationFee, prescriptionTotal, labTestTotal, subtotal, tax, totalAmount);

        // Payment processing
        processPayment(consultation, totalAmount);
    }

    private double calculatePrescriptionTotal(Consultation consultation) {
        double total = 0.0;
        ListInterface<Prescription> prescriptions = consultation.getPrescription();

        if (prescriptions != null && !prescriptions.isEmpty()) {
            for (int i = 0; i < prescriptions.size(); i++) {
                Prescription prescription = prescriptions.get(i);
                Medicine medicine = prescription.getMedicine();
                if (medicine != null) {
                    total += medicine.getPrice() * medicine.getQuantity();
                }
            }
        }
        return total;
    }

    private double calculateLabTestTotal(Consultation consultation) {
        double total = 0.0;
        ListInterface<LabTest> labTests = consultation.getLabTests();

        if (labTests != null && !labTests.isEmpty()) {
            for (int i = 0; i < labTests.size(); i++) {
                LabTest labTest = labTests.get(i);
                total += labTest.getPrice();
            }
        }
        return total;
    }

    private void displayDetailedBill(Consultation consultation, double consultationFee,
                                     double prescriptionTotal, double labTestsTotal,
                                     double subtotal, double tax, double totalAmount) {

        System.out.println("\n" + "=".repeat(62));
        System.out.println("                    MEDICAL CLINIC BILL");
        System.out.println("=".repeat(62));

        // Clinic and consultation info
        System.out.printf("Bill Date: %s%n", java.time.LocalDateTime.now().format(
                java.time.format.DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm:ss")));
        System.out.printf("Patient: %s%n", consultation.getPatient().getName());
        System.out.printf("Doctor: %s%n", consultation.getDoctor().getName());
        System.out.printf("Consultation Date: %s%n", consultation.getConsultatonDate());
        System.out.println("-".repeat(62));

        // Itemized billing
        System.out.printf("%-40s %10s %10s%n", "DESCRIPTION", "QTY", "AMOUNT (RM)");
        System.out.println("-".repeat(62));

        // Consultation fee
        System.out.printf("%-40s %10s %10.2f%n", "Consultation Fee", "1", consultationFee);

        // Medical treatment
        if (consultation.getMedicalTreatment() != null && !consultation.getMedicalTreatment().trim().isEmpty()) {
            System.out.printf("%-40s %10s %8s%n", "Treatment: " +
                    truncateString(consultation.getMedicalTreatment(), 30), "", "");
        }

        // Prescription items
        ListInterface<Prescription> prescriptions = consultation.getPrescription();
        if (prescriptions != null && !prescriptions.isEmpty()) {
            System.out.println("-".repeat(62));
            System.out.println("PRESCRIBED MEDICATIONS:");

            for (int i = 0; i < prescriptions.size(); i++) {
                Prescription prescription = prescriptions.get(i);
                Medicine medicine = prescription.getMedicine();
                if (medicine != null) {
                    String medicineName = truncateString(medicine.getName() + " - " + medicine.getBrand(), 35);
                    double itemTotal = medicine.getPrice() * medicine.getQuantity();

                    System.out.printf("%-40s %10d %10.2f%n",
                            medicineName, medicine.getQuantity(), itemTotal);

                    // Show dosage information
                    System.out.printf("  Dosage: %.1f %s, %d times/day, %d days%n",
                            prescription.getDosagePerTime(), medicine.getUnit(),
                            prescription.getTimesPerDay(), prescription.getDays());
                }
            }
        }

        // Sales items (if any)
        if (labTestsTotal > 0) {
            System.out.println("-".repeat(62));
            System.out.println("ADDITIONAL ITEMS:");
            System.out.printf("%-40s %10s %10.2f%n", "Other Items", "", labTestsTotal);
        }

        // Totals
        System.out.println("=".repeat(62));
        System.out.printf("%-52s RM %6.2f%n", "SUBTOTAL:", subtotal);
        System.out.printf("%-52s RM %6.2f%n", "SST (6%):", tax);
        System.out.println("-".repeat(62));
        System.out.printf("%-52s RM %6.2f%n", "TOTAL AMOUNT:", totalAmount);
        System.out.println("=".repeat(62));
    }

    private void processPayment(Consultation consultation, double totalAmount) {
        while (true) {
            System.out.printf("\nTotal Amount Due: RM %.2f%n", totalAmount);
            System.out.println("\nPayment Options:");
            System.out.println("1. Cash Payment");
            System.out.println("2. Card Payment");
            System.out.println("3. Cancel (Return to Billing Status)");
            System.out.print("Select payment method: ");

            String choice = scanner.nextLine().trim();

            switch (choice) {
                case "1":
                    if (processCashPayment(totalAmount)) {
                        completeBilling(consultation, totalAmount, "Cash");
                        return;
                    }
                    break;
                case "2":
                    if (processCardPayment(totalAmount)) {
                        completeBilling(consultation, totalAmount, "Card");
                        return;
                    }
                    break;
                case "3":
                    System.out.println("Payment cancelled. Consultation remains in BILLING status.");
                    return;
                default:
                    System.out.println("Invalid choice. Please try again.");
            }
        }
    }

    private boolean processCashPayment(double totalAmount) {
        System.out.printf("Total Amount: RM %.2f%n", totalAmount);

        while (true) {
            System.out.print("Enter amount received: RM ");
            try {
                double amountReceived = Double.parseDouble(scanner.nextLine().trim());

                if (amountReceived < totalAmount) {
                    System.out.printf("Insufficient amount. Short by RM %.2f%n",
                            totalAmount - amountReceived);
                    continue;
                }

                double change = amountReceived - totalAmount;
                System.out.printf("Amount Received: RM %.2f%n", amountReceived);
                System.out.printf("Change: RM %.2f%n", change);

                if (change > 0) {
                    System.out.println("\n*** PLEASE GIVE CHANGE TO CUSTOMER ***");
                }

                System.out.print("Confirm cash payment? (y/n): ");
                String confirm = scanner.nextLine().trim().toLowerCase();
                if (confirm.equals("y") || confirm.equals("yes")) {
                    return true;
                }

            } catch (NumberFormatException e) {
                System.out.println("Invalid amount format. Please try again.");
            }
        }
    }

    private boolean processCardPayment(double totalAmount) {
        System.out.printf("Processing card payment for RM %.2f%n", totalAmount);
        System.out.println("Please insert/swipe card and follow instructions on terminal...");
        System.out.print("Press Enter when payment is completed, or 'c' to cancel: ");

        String input = scanner.nextLine().trim().toLowerCase();
        if (input.equals("c")) {
            System.out.println("Card payment cancelled.");
            return false;
        }

        // Simulate card processing
        System.out.println("Processing...");
        try {
            Thread.sleep(2000); // Simulate processing time
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }

        System.out.println("Card payment successful!");
        return true;
    }

    private void completeBilling(Consultation consultation, double totalAmount, String paymentMethod) {
        // Update consultation
        consultation.setTotalPayment(totalAmount);
        consultation.setStatus(Consultation.Status.COMPLETED);
        consultation.setEndTime(java.time.LocalTime.now());

        // Print receipt
        printReceipt(consultation, totalAmount, paymentMethod);

        System.out.println("\n*** BILLING COMPLETED ***");
        System.out.println("Consultation status changed to COMPLETED.");
        UI.pause();
    }

    private void printReceipt(Consultation consultation, double totalAmount, String paymentMethod) {
        System.out.println("\n" + "=".repeat(40));
        System.out.println("           PAYMENT RECEIPT");
        System.out.println("=".repeat(40));
        System.out.printf("Date: %s%n", java.time.LocalDateTime.now().format(
                java.time.format.DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm")));
        System.out.printf("Consultation ID: %s%n", consultation.getId().toString().substring(0, 8));
        System.out.printf("Patient: %s%n", consultation.getPatient().getName());
        System.out.printf("Payment Method: %s%n", paymentMethod);
        System.out.printf("Total Paid: RM %.2f%n", totalAmount);
        System.out.println("-".repeat(40));
        System.out.println("Thank you for choosing our clinic!");
        System.out.println("=".repeat(40));
    }

    private String truncateString(String str, int maxLength) {
        if (str == null) return "";
        return str.length() > maxLength ? str.substring(0, maxLength - 3) + "..." : str;
    }

    private void manageLabTests(Consultation consultation) {
        while (true) {
            System.out.println("\n=== MANAGE LAB TESTS ===");
            System.out.println("Patient: " + consultation.getPatient().getName());
            
            // Display current lab tests
            ArrayList<LabTest> currentLabTests = consultation.getLabTests();
            if (currentLabTests != null && !currentLabTests.isEmpty()) {
                System.out.println("\nCurrent Lab Tests:");
                for (int i = 0; i < currentLabTests.size(); i++) {
                    LabTest test = currentLabTests.get(i);
                    System.out.printf("%d. %s - RM%.2f (%s)\n", 
                        i + 1, test.getName(), test.getPrice(), 
                        test.getReferringLab() != null ? test.getReferringLab().getName() : "Unknown Lab");
                }
            } else {
                System.out.println("\nNo lab tests currently ordered.");
                if (currentLabTests == null) {
                    consultation.setLabTests(new ArrayList<>());
                }
            }
            
            System.out.println("\nOptions:");
            System.out.println("1. Add Lab Test");
            System.out.println("2. Remove Lab Test");
            System.out.println("3. View Lab Test Details");
            System.out.println("9. Return to Consultation");
            System.out.print("Enter choice: ");
            
            try {
                int choice = scanner.nextInt();
                scanner.nextLine(); // consume newline
                
                switch (choice) {
                    case 1:
                        addLabTestToConsultation(consultation);
                        break;
                    case 2:
                        removeLabTestFromConsultation(consultation);
                        break;
                    case 3:
                        viewLabTestDetails(consultation);
                        break;
                    case 9:
                        return;
                    default:
                        System.out.println("Invalid choice.");
                }
                UI.pause();
            } catch (Exception e) {
                System.out.println("Invalid input. Please enter a number.");
                scanner.nextLine(); // consume invalid input
                UI.pause();
            }
        }
    }
    
    private void addLabTestToConsultation(Consultation consultation) {
        System.out.println("\n=== ADD LAB TEST ===");
        
        // Get available lab tests from pharmacy control and use pagination
        @SuppressWarnings("unchecked")
        HashedDictionary<String, LabTest> labTestDict = 
            (HashedDictionary<String, LabTest>) pharmacyControl.getLabTests();
            
        if (labTestDict == null || labTestDict.isEmpty()) {
            System.out.println("No lab tests available in the system.");
            return;
        }
        
        // Use the new pagination-based chooseLabTest method
        LabTest selectedTest = pharmacyControl.chooseLabTest(labTestDict);
        
        if (selectedTest == null) {
            return;
        }
        
        // Check if test is already added
        ArrayList<LabTest> currentTests = consultation.getLabTests();
        if (currentTests != null) {
            for (int i = 0; i < currentTests.size(); i++) {
                if (currentTests.get(i).getName().equals(selectedTest.getName())) {
                    System.out.println("This lab test is already ordered for this consultation.");
                    return;
                }
            }
        }
        
        // Add the lab test
        if (currentTests == null) {
            currentTests = new ArrayList<>();
            consultation.setLabTests(currentTests);
        }
        
        currentTests.add(selectedTest);
        System.out.println("Lab test '" + selectedTest.getName() + "' added successfully!");
    }
    
    private void removeLabTestFromConsultation(Consultation consultation) {
        System.out.println("\n=== REMOVE LAB TEST ===");
        
        ArrayList<LabTest> currentTests = consultation.getLabTests();
        if (currentTests == null || currentTests.isEmpty()) {
            System.out.println("No lab tests to remove.");
            return;
        }
        
        System.out.println("Current Lab Tests:");
        for (int i = 0; i < currentTests.size(); i++) {
            LabTest test = currentTests.get(i);
            System.out.printf("%d. %s - RM%.2f\n", i + 1, test.getName(), test.getPrice());
        }
        
        System.out.printf("Select lab test to remove [1-%d] or 0 to cancel: ", currentTests.size());
        try {
            int choice = scanner.nextInt();
            scanner.nextLine();
            
            if (choice == 0) {
                System.out.println("Cancelled.");
                return;
            }
            
            if (choice < 1 || choice > currentTests.size()) {
                System.out.println("Invalid choice.");
                return;
            }
            
            LabTest removedTest = currentTests.get(choice - 1);
            currentTests.remove(choice - 1);
            
            System.out.println("Lab test '" + removedTest.getName() + "' removed successfully!");
            
        } catch (Exception e) {
            System.out.println("Invalid input.");
            scanner.nextLine(); // consume invalid input
        }
    }
    
    private void viewLabTestDetails(Consultation consultation) {
        System.out.println("\n=== LAB TEST DETAILS ===");
        
        ArrayList<LabTest> currentTests = consultation.getLabTests();
        if (currentTests == null || currentTests.isEmpty()) {
            System.out.println("No lab tests to view.");
            return;
        }
        
        System.out.println("Lab Tests for this Consultation:");
        for (int i = 0; i < currentTests.size(); i++) {
            LabTest test = currentTests.get(i);
            System.out.printf("\n%d. %s\n", i + 1, test.getName());
            System.out.printf("   Code: %s\n", test.getCode() != null ? test.getCode() : "N/A");
            System.out.printf("   Price: RM%.2f\n", test.getPrice());
            System.out.printf("   Lab: %s\n", test.getReferringLab() != null ? test.getReferringLab().getName() : "Unknown");
            System.out.printf("   Fasting Required: %s\n", test.isFastingRequired() ? "Yes" : "No");
            System.out.printf("   Description: %s\n", test.getDescription() != null ? test.getDescription() : "N/A");
            if (test.getPatientPrecautions() != null && !test.getPatientPrecautions().isEmpty()) {
                System.out.printf("   Patient Precautions: %s\n", test.getPatientPrecautions());
            }
            if (test.getBloodTubes() != null && !test.getBloodTubes().isEmpty()) {
                System.out.printf("   Blood Tubes Required: %s\n", test.getBloodTubes());
            }
        }
    }

    private void revenueReport() {
        System.out.println("\n=== CONSULTATION REVENUE REPORT ===");
        System.out.println("Generated: " + LocalDateTime.now().format(DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm")));
        System.out.println("+" + "-".repeat(85) + "+");
        System.out.printf("| %-3s | %-20s | %-20s | %-13s | %-15s |\n", "No.", "Patient", "Doctor", "Status", "Total Amount");
        System.out.println("+" + "-".repeat(85) + "+");

        float totalRevenue = 0.0f;

        for (int i = 0; i < consultationList.size(); i++) {
            Consultation cons = consultationList.get(i);
            double payment = cons.getTotalPayment();
            totalRevenue += payment;

            String patientName = (cons.getPatient() != null) ? cons.getPatient().getName() : "N/A";
            String doctorName = (cons.getDoctor() != null) ? cons.getDoctor().getName() : "N/A";
            String status = cons.getStatus() != null ? cons.getStatus().toString() : "N/A";

            System.out.printf("| %-3d | %-20s | %-20s | %-13s | RM%13.2f |\n", (i + 1), patientName, doctorName, status, payment);
        }
        System.out.println("+" + "-".repeat(85) + "+");
        System.out.printf("| %-68s | %12d |%n", "TOTAL CONSULTATION TODAY", consultationList.size());
        System.out.printf("| %-68s | RM%10.2f |%n", "TOTAL REVENUE", totalRevenue);
        System.out.println("+" + "-".repeat(85) + "+");

        UI.pause();
    }

    private void durationReport() {
        System.out.println("\n=== CONSULTATION DURATION REPORT ===");
        System.out.println("Generated: " + LocalDateTime.now().format(DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm")));
        System.out.println("+" + "-".repeat(50) + "+");
        System.out.printf("| %-3s | %-10s | %-10s | %-15s |\n", "No.", "Start Time", "End Time", "Time Taken (min)");
        System.out.println("+" + "-".repeat(50) + "+");

        long totalDurationMinutes = 0;
        int completedConsultations = 0;

        for (int i = 0; i < consultationList.size(); i++) {
            Consultation cons = consultationList.get(i);

            // Only calculate for completed consultations with valid start/end times
            if (cons.getStatus() == Consultation.Status.COMPLETED && cons.getStartTime() != null && cons.getEndTime() != null) {
                long duration = ChronoUnit.MINUTES.between(cons.getStartTime(), cons.getEndTime());
                totalDurationMinutes += duration;
                completedConsultations++;

                System.out.printf("| %-3d | %-10s | %-10s | %-16d |\n", completedConsultations, formatter.format(cons.getStartTime()), formatter.format(cons.getEndTime()), duration);
            }
        }
        System.out.println("+" + "-".repeat(50) + "+");

        double averageDurationMinutes = (completedConsultations > 0) ? (double) totalDurationMinutes / completedConsultations : 0.0;

        System.out.printf("| %-34s | %11d |\n", "TOTAL COMPLETED CONSULTATION", completedConsultations);
        System.out.printf("| %-34s | %11d |\n", "TOTAL DURATION (MINUTES)", totalDurationMinutes);
        System.out.printf("| %-34s | %11.2f |\n", "AVERAGE DURATION (MINUTES)", averageDurationMinutes);
        System.out.println("+" + "-".repeat(50) + "+");

        UI.pause();
    }
}

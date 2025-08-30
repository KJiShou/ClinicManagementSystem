// Ng Zhe Wei
package boundary;

import adt.ArrayList;
import adt.LinkedQueue;
import adt.QueueInterface;
import entity.Patient;
import utility.MessageUI;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.util.Scanner;
import java.util.UUID;

public class PatientUI {
    private QueueInterface<String> choiceQueue;
    private MessageUI messageUI;
    private int pageSize;
    private static final SimpleDateFormat DATE_FMT = new SimpleDateFormat("yyyy-MM-dd");
    private Scanner scanner;

    public PatientUI() {
        choiceQueue = new LinkedQueue<>();
        messageUI = new MessageUI();
        pageSize = 10;
        scanner = new Scanner(System.in);
    }

    public Integer mainMenu() {
        choiceQueue.enqueue("Register New Patient");
        choiceQueue.enqueue("View All Patients");
        choiceQueue.enqueue("Edit Patient Details");
        choiceQueue.enqueue("Register Consultation");
        choiceQueue.enqueue("Delete Patient Details");
        choiceQueue.enqueue("Patient Reports");

        return messageUI.mainUI("Patient Management System", choiceQueue);
    }

//    public Integer viewPatientsMenu() {
//        choiceQueue.enqueue("View All Patients");
//        choiceQueue.enqueue("Search by Name");
//        choiceQueue.enqueue("Search by IC/Passport");
//        choiceQueue.enqueue("View Registration Queue");
//
//        return messageUI.mainUI("Patient View Options", choiceQueue);
//    }

    public Patient getPatientDetails() {
        System.out.println("\n=== REGISTER NEW PATIENT ===");
        System.out.println("Type 'CANCEL' at any time to stop and return to Main Menu.");

        UUID id = UUID.randomUUID();

        // Name validation
        String name;
        do {
            System.out.print("Name: ");
            name = scanner.nextLine().trim();
            if (name.equalsIgnoreCase("CANCEL")) return null;
            if (name.isEmpty()) {
                System.out.println("Error: Name cannot be empty");
            } else if (!name.matches("[a-zA-Z\\s]+")) {
                System.out.println("Error: Name must contain only alphabets and spaces");
                name = "";
            }
        } while (name.isEmpty());

        // Address validation
        String address;
        do {
            System.out.print("Address: ");
            address = scanner.nextLine().trim();
            if (address.equalsIgnoreCase("CANCEL")) return null;
            if (address.isEmpty()) {
                System.out.println("Error: Address cannot be empty");
            }
        } while (address.isEmpty());

        // Gender validation
        String gender;
        do {
            System.out.print("Gender (M/F): ");
            gender = scanner.nextLine().trim().toUpperCase();
            if (gender.equalsIgnoreCase("CANCEL")) return null;
            if (!gender.equals("M") && !gender.equals("F")) {
                System.out.println("Error: Gender must be 'M' or 'F'");
                gender = "";
            }
        } while (gender.isEmpty());

        // Phone validation
        String phone;
        do {
            System.out.print("Phone: ");
            phone = scanner.nextLine().trim();
            if (phone.equalsIgnoreCase("CANCEL")) return null;
            if (!phone.matches("\\d{10,11}")) {
                System.out.println("Error: Phone must be 10-11 digits");
                phone = "";
            }
        } while (phone.isEmpty());

        // Email validation
        String email;
        do {
            System.out.print("Email: ");
            email = scanner.nextLine().trim();
            if (email.equalsIgnoreCase("CANCEL")) return null;
            if (!email.matches("^[\\w-\\.]+@([\\w-]+\\.)+[\\w-]{2,4}$")) {
                System.out.println("Error: Invalid email format");
                email = "";
            }
        } while (email.isEmpty());

        // Date of Birth validation
        String dob;
        do {
            System.out.print("Date of Birth (YYYY-MM-DD): ");
            dob = scanner.nextLine().trim();
            if (dob.equalsIgnoreCase("CANCEL")) return null;

            // First check format with regex
            if (!dob.matches("\\d{4}-(0[1-9]|1[0-2])-(0[1-9]|[12]\\d|3[01])")) {
                System.out.println("Error: Date must be in valid YYYY-MM-DD format (e.g., 1990-01-15)");
                dob = "";
                continue;
            }

            try {
                // Then parse and validate the actual date
                LocalDate dateOfBirth = LocalDate.parse(dob);

                if (dateOfBirth.isAfter(LocalDate.now())) {
                    System.out.println("Error: Date of birth cannot be in the future");
                    dob = "";
                } else if (dateOfBirth.isBefore(LocalDate.of(1900, 1, 1))) {
                    System.out.println("Error: Date of birth cannot be before 1900-01-01");
                    dob = "";
                }
            } catch (Exception e) {
                System.out.println("Error: Invalid date (e.g., February 30th)");
                dob = "";
            }
        } while (dob.isEmpty());

        // Optional fields
        String patientIC;
        do {
            System.out.print("Patient IC (leave blank if none): ");
            patientIC = scanner.nextLine().trim();
            if (patientIC.equalsIgnoreCase("CANCEL")) return null;
            if (!patientIC.isEmpty() && !patientIC.matches("\\d{12}")) {
                System.out.println("Error: IC must be exactly 12 digits. Please try again.");
                patientIC = "";
            }
        } while (!patientIC.matches("\\d{12}"));

        System.out.print("Patient Passport (leave blank if none): ");
        String patientPassport = scanner.nextLine().trim();
        if (patientPassport.equalsIgnoreCase("CANCEL")) return null;

        // Validation: Either IC or passport must be provided
        if ((patientIC == null || patientIC.trim().isEmpty()) && 
            (patientPassport == null || patientPassport.trim().isEmpty())) {
            System.out.println("ERROR: Either IC or Passport must be provided. Patient creation failed.");
            return null;
        }

        try {
            return new Patient(
                    id, name, address, gender, phone, email, dob,
                    patientIC.isEmpty() ? null : patientIC,
                    patientPassport.isEmpty() ? null : patientPassport
            );
        } catch (IllegalArgumentException e) {
            System.out.println("ERROR: " + e.getMessage());
            return null;
        }
    }

    public void displayPatientList(ArrayList<Patient> patients, int totalItems,
                                   int currentPage, int totalPages, String searchQuery) {
        int start = (currentPage - 1) * pageSize;
        int end = Math.min(totalItems, start + pageSize);

        if(!(searchQuery == null || searchQuery.isEmpty())) {
            System.out.println("Search query: " + searchQuery);
        }

        // Header
        System.out.printf("Page %d/%d%n", currentPage, totalPages);
        System.out.println("+-----+--------------------------------+----------------------+--------------------------------+------------------------------------------+");
        System.out.printf("| %-3s | %-30s | %-20s | %-30s | %-40s |\n",
                "No.", "Name", "Gender", "Contact", "IC");
        System.out.println("+-----+--------------------------------+----------------------+--------------------------------+------------------------------------------+");

        // Rows
        for (int i = start; i < end; i++) {
            Patient patient = patients.get(i);

            // Determine the identification type with labels
            String identifier;
            if (patient.getPatientIC() != null && !patient.getPatientIC().isEmpty()) {
                identifier = patient.getPatientIC();
            } else if (patient.getPatientPassport() != null && !patient.getPatientPassport().isEmpty()) {
                identifier = patient.getPatientPassport() + " (passport)";
            } else {
                identifier = "No ID provided";
            }

            System.out.printf("| %-3d | %-30s | %-20s | %-30s | %-40s |\n",
                    i+1,
                    patient.getName(),
                    patient.getGender(),
                    patient.getPhone(),
                    identifier);
        }

        System.out.println("+-----+--------------------------------+----------------------+--------------------------------+------------------------------------------+");
        System.out.println();  // blank line between pages
    }

//    public int selectPatientForEdit(ArrayList<Patient> patients) {
//        System.out.println("\n=== EDIT PATIENT ===");
//        displayPatientList(patients, patients.size(), 1, 1, null); // reuse your table format
//        System.out.print("Enter patient number to edit (0 to cancel): ");
//        try {
//            int choice = Integer.parseInt(scanner.nextLine().trim());
//            return choice;
//        } catch (NumberFormatException e) {
//            return -1; // invalid input
//        }
//    }

//    public int selectPatientForDelete(ArrayList<Patient> patients) {
//        int currentPage = 1;
//        int totalItems = patients.size();
//        int totalPages = (int) Math.ceil((double) totalItems / pageSize);
//
//        while (true) {
//            System.out.println("\n=== DELETE PATIENT ===");
//            displayPatientList(patients, totalItems, currentPage, totalPages, null);
//
//            System.out.println("[N] Next Page  [P] Previous Page  [0] Cancel");
//            System.out.print("Enter patient number to delete: ");
//            String input = scanner.nextLine().trim();
//
//            // Cancel → go back
//            if (input.equals("0")) {
//                return 0;
//            }
//
//            // Page navigation
//            if (input.equalsIgnoreCase("N") && currentPage < totalPages) {
//                currentPage++;
//                continue;
//            } else if (input.equalsIgnoreCase("P") && currentPage > 1) {
//                currentPage--;
//                continue;
//            }
//
//            // Number selection
//            try {
//                int choice = Integer.parseInt(input);
//                int start = (currentPage - 1) * pageSize + 1;
//                int end = Math.min(totalItems, currentPage * pageSize);
//
//                if (choice >= start && choice <= end) {
//                    return choice; // valid patient number
//                } else {
//                    System.out.println("Invalid choice. Please try again.");
//                }
//            } catch (NumberFormatException e) {
//                System.out.println("Invalid input. Please enter a number, N, P, or 0.");
//            }
//        }
//    }

    public void printReportHeader(String reportTitle) {
        System.out.println("=======================================");
        System.out.println("        CLINIC MANAGEMENT SYSTEM       ");
        System.out.println("         PATIENT REPORT MODULE         ");
        System.out.println("---------------------------------------");
        System.out.printf(" Report Title : %s%n", reportTitle);
        System.out.printf(" Generated On : %s%n",
                new java.text.SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new java.util.Date()));
        System.out.println("=======================================\n");
    }

    public void displayPatientDetails(Patient patient) {
        System.out.println("\n=== PATIENT DETAILS ===");
        System.out.println("+--------------------------------+--------------------------------+");
        System.out.printf("| %-30s | %-30s |\n", "Field", "Value");
        System.out.println("+--------------------------------+--------------------------------+");

        System.out.printf("| %-30s | %-30s |\n", "Patient ID", patient.getUserID());
        System.out.printf("| %-30s | %-30s |\n", "Name", patient.getName());
        System.out.printf("| %-30s | %-30s |\n", "Gender", patient.getGender());
        System.out.printf("| %-30s | %-30s |\n", "Date of Birth", patient.getDateOfBirth());
        System.out.printf("| %-30s | %-30s |\n", "Address", patient.getAddress());
        System.out.printf("| %-30s | %-30s |\n", "Phone", patient.getPhone());
        System.out.printf("| %-30s | %-30s |\n", "Email", patient.getEmail());
        System.out.printf("| %-30s | %-30s |\n", "IC Number", patient.getPatientIC() != null ? patient.getPatientIC() : "-");
        System.out.printf("| %-30s | %-30s |\n", "Passport", patient.getPatientPassport() != null ? patient.getPatientPassport() : "-");
        System.out.println("+--------------------------------+--------------------------------+");
    }

//    public String getSearchQuery(String prompt) {
//        System.out.print(prompt);
//        return scanner.nextLine();
//    }

    public void displayMessage(String message) {
        System.out.println(message);
    }

    public void displayError(String message) {
        System.out.println("ERROR: " + message);
    }

    public void displaySuccess(String message) {
        System.out.println("SUCCESS: " + message);
    }
}
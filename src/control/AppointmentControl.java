// Teoh Yong Ming
package control;

import adt.*;
import boundary.AppointmentUI;
import entity.Appointment;
import entity.Doctor;
import entity.DutySchedule;
import entity.Patient;
import entity.Staff;

import java.io.IOException;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.Comparator;
import java.util.Scanner;
import java.util.UUID;

public class AppointmentControl {
    private static final int PAGE_SIZE = 5;
    private ListInterface<Appointment> appointmentList;
    private ArrayList<Patient> patients;
    private ListInterface<Doctor> doctors;
    private AuthenticationControl authControl;
    private HashedDictionary<UUID, ListInterface<DutySchedule>> schedules;
    private AppointmentUI ui;
    private Scanner scanner;

    public AppointmentControl(ArrayList<Patient> patients, ListInterface<Doctor> doctors, ListInterface<Appointment> appointmentList, AuthenticationControl authControl, HashedDictionary<UUID, ListInterface<DutySchedule>> schedules) {
        this.patients = patients;
        this.doctors = doctors;
        this.appointmentList = appointmentList;
        this.authControl = authControl;
        this.schedules = schedules;
        this.ui = new AppointmentUI();
        this.scanner = new Scanner(System.in);
    }

    public void main() throws IOException {
        while (true) {
            Integer choice = ui.mainMenu();
            switch (choice) {
                case 1:
                    addAppointment();
                    break;
                case 2:
                    updateAppointment();
                    break;
                case 3:
                    viewSchedule();
                    break;
                case 4:
                    cancelAppointment();
                    break;
                case 999:
                    return;
                default:
                    System.out.println("Invalid choice.");
            }
        }
    }

    public void addAppointment() {
        System.out.println("\n=== ADD APPOINTMENT ===");
        System.out.println("How would you like to book the appointment?");
        System.out.println("1. By Doctor Availability (Recommended)");
        System.out.println("2. By Time Slot");
        System.out.println("3. Cancel");
        System.out.print("Choose booking method: ");
        
        try {
            int choice = Integer.parseInt(scanner.nextLine().trim());
            switch (choice) {
                case 1:
                    addAppointmentByDoctor();
                    break;
                case 2:
                    addAppointmentByTime();
                    break;
                case 3:
                    ui.displayMessage("Operation cancelled.");
                    break;
                default:
                    System.out.println("Invalid choice.");
            }
        } catch (NumberFormatException e) {
            System.out.println("Please enter a valid number.");
        }
        ui.pause();
    }

    private void addAppointmentByDoctor() {
        System.out.println("\n=== BOOK BY DOCTOR AVAILABILITY ===");
        
        // Step 1: Select patient
        Patient selectedPatient = selectPatient();
        if (selectedPatient == null) {
            ui.displayMessage("Operation cancelled.");
            return;
        }

        // Step 2: Select doctor
        Doctor selectedDoctor = selectDoctor();
        if (selectedDoctor == null) {
            ui.displayMessage("Operation cancelled.");
            return;
        }

        // Step 3: Select date
        LocalDate selectedDate = selectAppointmentDate();
        if (selectedDate == null) {
            ui.displayMessage("Operation cancelled.");
            return;
        }

        // Step 4: Show doctor availability and let user choose
        LocalTime selectedTime = selectAvailableTimeSlot(selectedDoctor, selectedDate);
        if (selectedTime == null) {
            ui.displayMessage("No time selected.");
            return;
        }

        // Step 5: Get appointment details
        String appointmentType = selectAppointmentType();
        if (appointmentType == null) {
            ui.displayMessage("Operation cancelled.");
            return;
        }

        System.out.print("Description/Notes (optional): ");
        String description = scanner.nextLine().trim();

        // Step 6: Create appointment
        Appointment newAppointment = new Appointment(
            UUID.randomUUID(),
            selectedPatient,
            selectedDoctor,
            authControl.getCurrentUser(),
            selectedDate,
            selectedTime,
            selectedTime.plusMinutes(30), // Default 30 minutes
            appointmentType,
            description,
            Appointment.Status.SCHEDULED
        );

        appointmentList.add(newAppointment);
        ui.displaySuccess("Appointment scheduled successfully!");
        displayAppointmentSummary(newAppointment);
    }

    private void addAppointmentByTime() {
        System.out.println("\n=== BOOK BY TIME SLOT ===");
        
        // Step 1: Select patient
        Patient selectedPatient = selectPatient();
        if (selectedPatient == null) {
            ui.displayMessage("Operation cancelled.");
            return;
        }

        // Step 2: Select date
        LocalDate selectedDate = selectAppointmentDate();
        if (selectedDate == null) {
            ui.displayMessage("Operation cancelled.");
            return;
        }

        // Step 3: Select time
        LocalTime selectedTime = selectTimeSlot(selectedDate);
        if (selectedTime == null) {
            ui.displayMessage("Operation cancelled.");
            return;
        }

        // Step 4: Show available doctors at that time
        Doctor selectedDoctor = selectAvailableDoctor(selectedDate, selectedTime);
        if (selectedDoctor == null) {
            ui.displayMessage("No available doctor selected.");
            return;
        }

        // Step 5: Get appointment details
        String appointmentType = selectAppointmentType();
        if (appointmentType == null) {
            ui.displayMessage("Operation cancelled.");
            return;
        }

        System.out.print("Description/Notes (optional): ");
        String description = scanner.nextLine().trim();

        // Step 6: Create appointment
        Appointment newAppointment = new Appointment(
            UUID.randomUUID(),
            selectedPatient,
            selectedDoctor,
            authControl.getCurrentUser(),
            selectedDate,
            selectedTime,
            selectedTime.plusMinutes(30), // Default 30 minutes
            appointmentType,
            description,
            Appointment.Status.SCHEDULED
        );

        appointmentList.add(newAppointment);
        ui.displaySuccess("Appointment scheduled successfully!");
        displayAppointmentSummary(newAppointment);
    }

    public void updateAppointment() {
        System.out.println("\n=== UPDATE APPOINTMENT ===");
        
        ArrayList<Appointment> activeAppointments = getActiveAppointments();
        if (activeAppointments.isEmpty()) {
            ui.displayMessage("No active appointments found.");
            ui.pause();
            return;
        }

        Appointment selectedAppointment = ui.selectAppointment(activeAppointments);
        if (selectedAppointment == null) {
            ui.displayMessage("Operation cancelled.");
            ui.pause();
            return;
        }

        while (true) {
            System.out.println("\n=== UPDATE APPOINTMENT ===");
            System.out.println("Current Details:");
            System.out.println("- Date: " + selectedAppointment.getFormattedDate());
            System.out.println("- Time: " + selectedAppointment.getFormattedStartTime() + " - " + selectedAppointment.getFormattedEndTime());
            System.out.println("- Patient: " + selectedAppointment.getPatient().getName());
            System.out.println("- Doctor: " + selectedAppointment.getDoctor().getName());
            System.out.println("- Type: " + selectedAppointment.getAppointmentType());
            System.out.println("- Status: " + selectedAppointment.getStatusDisplay());
            System.out.println("- Notes: " + selectedAppointment.getDescription());
            String createdBy = selectedAppointment.getStaff() != null ? selectedAppointment.getStaff().getName() + " (" + selectedAppointment.getStaff().getAccount() + ")" : "N/A";
            System.out.println("- Created By: " + createdBy);

            System.out.println("\nWhat would you like to update?");
            System.out.println("1. Appointment Date");
            System.out.println("2. Appointment Time");
            System.out.println("3. Appointment Type");
            System.out.println("4. Notes/Description");
            System.out.println("5. Status");
            System.out.println("9. Save and Exit");
            System.out.print("Choice: ");

            try {
                int choice = Integer.parseInt(scanner.nextLine().trim());
                switch (choice) {
                    case 1:
                        updateAppointmentDate(selectedAppointment);
                        break;
                    case 2:
                        updateAppointmentTime(selectedAppointment);
                        break;
                    case 3:
                        updateAppointmentType(selectedAppointment);
                        break;
                    case 4:
                        updateAppointmentNotes(selectedAppointment);
                        break;
                    case 5:
                        updateAppointmentStatus(selectedAppointment);
                        break;
                    case 9:
                        ui.displaySuccess("Appointment updated successfully!");
                        ui.pause();
                        return;
                    default:
                        System.out.println("Invalid choice.");
                }
            } catch (NumberFormatException e) {
                System.out.println("Please enter a valid number.");
            }
        }
    }

    public void viewSchedule() {
        System.out.println("\n=== VIEW SCHEDULE ===");
        System.out.println("1. View Daily Schedule");
        System.out.println("2. View Weekly Schedule");
        System.out.println("3. View Doctor Schedule");
        System.out.println("4. View All Appointments");
        System.out.print("Choose view type: ");

        try {
            int choice = Integer.parseInt(scanner.nextLine().trim());
            switch (choice) {
                case 1:
                    viewDailySchedule();
                    break;
                case 2:
                    viewWeeklySchedule();
                    break;
                case 3:
                    viewDoctorSchedule();
                    break;
                case 4:
                    viewAllAppointments();
                    break;
                default:
                    System.out.println("Invalid choice.");
                    ui.pause();
            }
        } catch (NumberFormatException e) {
            System.out.println("Please enter a valid number.");
            ui.pause();
        }
    }

    public void cancelAppointment() {
        System.out.println("\n=== CANCEL APPOINTMENT ===");
        
        ArrayList<Appointment> cancellableAppointments = getCancellableAppointments();
        if (cancellableAppointments.isEmpty()) {
            ui.displayMessage("No appointments available for cancellation.");
            ui.pause();
            return;
        }

        Appointment selectedAppointment = selectAppointmentForCancellation(cancellableAppointments);
        if (selectedAppointment == null) {
            ui.displayMessage("Operation cancelled.");
            ui.pause();
            return;
        }

        System.out.println("\nAppointment to Cancel:");
        System.out.println("- Date: " + selectedAppointment.getFormattedDate());
        System.out.println("- Time: " + selectedAppointment.getFormattedStartTime() + " - " + selectedAppointment.getFormattedEndTime());
        System.out.println("- Patient: " + selectedAppointment.getPatient().getName());
        System.out.println("- Doctor: " + selectedAppointment.getDoctor().getName());
        System.out.println("- Type: " + selectedAppointment.getAppointmentType());
        System.out.println("- Status: " + selectedAppointment.getStatusDisplay());

        System.out.print("\nConfirm cancellation? (Y/N): ");
        String confirm = scanner.nextLine().trim().toUpperCase();

        if (confirm.equals("Y")) {
            selectedAppointment.setStatus(Appointment.Status.CANCELLED);
            ui.displaySuccess("Appointment cancelled successfully!");
        } else {
            ui.displayMessage("Cancellation aborted.");
        }
        ui.pause();
    }

    private Patient selectPatient() {
        System.out.println("\n=== SELECT PATIENT ===");
        
        if (patients.isEmpty()) {
            ui.displayError("No patients found in the system.");
            return null;
        }

        ArrayList<Patient> currentView = new ArrayList<>(patients);
        int currentPage = 1;
        String searchQuery = "";
        final int PATIENT_PAGE_SIZE = 10;

        while (true) {
            int totalItems = currentView.size();
            int totalPages = (totalItems + PATIENT_PAGE_SIZE - 1) / PATIENT_PAGE_SIZE;
            if (totalPages == 0) totalPages = 1;

            // Calculate display range
            int startIndex = (currentPage - 1) * PATIENT_PAGE_SIZE;
            int endIndex = Math.min(startIndex + PATIENT_PAGE_SIZE, totalItems);

            // Display header
            System.out.println("\n" + "=".repeat(80));
            System.out.printf("PATIENT SELECTION - Page %d/%d (%d patients)\n", currentPage, totalPages, totalItems);
            if (!searchQuery.isEmpty()) {
                System.out.println("Search: \"" + searchQuery + "\"");
            }
            System.out.println("=".repeat(80));

            // Display patients
            System.out.printf("%-4s %-25s %-15s %-15s %-20s\n", "No.", "Name", "IC Number", "Phone", "Age");
            System.out.println("-".repeat(80));

            for (int i = startIndex; i < endIndex; i++) {
                Patient patient = currentView.get(i);
                String ic = patient.getPatientIC() != null ? patient.getPatientIC() : 
                           (patient.getPatientPassport() != null ? patient.getPatientPassport() + " (P)" : "N/A");
                
                // Calculate age
                String age = "N/A";
                if (patient.getDateOfBirth() != null) {
                    try {
                        java.time.LocalDate birthDate = patient.getDateOfBirth();
                        java.time.Period period = java.time.Period.between(birthDate, java.time.LocalDate.now());
                        age = String.valueOf(period.getYears());
                    } catch (Exception e) {
                        age = "N/A";
                    }
                }

                System.out.printf("%-4d %-25s %-15s %-15s %-20s\n",
                                i + 1,
                                truncateString(patient.getName(), 23),
                                truncateString(ic, 13),
                                truncateString(patient.getPhone(), 13),
                                age);
            }

            System.out.println("-".repeat(80));
            System.out.printf("Showing %d-%d of %d patients\n", startIndex + 1, endIndex, totalItems);

            // Navigation options
            System.out.println("\nNavigation:");
            System.out.println("[A] Previous Page | [D] Next Page | [S] Search | [R] Reset Search");
            System.out.println("Enter patient number to select | [Q] Cancel");
            System.out.print("Choice: ");

            String input = scanner.nextLine().trim().toLowerCase();

            switch (input) {
                case "a":
                    if (currentPage > 1) {
                        currentPage--;
                    } else {
                        System.out.println("This is the first page.");
                    }
                    break;
                case "d":
                    if (currentPage < totalPages) {
                        currentPage++;
                    } else {
                        System.out.println("This is the last page.");
                    }
                    break;
                case "s":
                    System.out.print("Enter search (Name/IC/Phone): ");
                    searchQuery = scanner.nextLine().trim();
                    ArrayList<Patient> filtered = filterPatients(searchQuery);
                    if (!filtered.isEmpty()) {
                        currentView = filtered;
                        currentPage = 1;
                        System.out.println("Found " + filtered.size() + " matching patients.");
                    } else {
                        System.out.println("No results found for: " + searchQuery);
                    }
                    break;
                case "r":
                    currentView = new ArrayList<>(patients);
                    searchQuery = "";
                    currentPage = 1;
                    System.out.println("Search cleared. Showing all patients.");
                    break;
                case "q":
                    return null;
                default:
                    try {
                        int choice = Integer.parseInt(input);
                        if (choice >= 1 && choice <= currentView.size()) {
                            Patient selectedPatient = currentView.get(choice - 1);
                            
                            // Show patient details for confirmation
                            System.out.println("\n=== SELECTED PATIENT ===");
                            System.out.println("Name: " + selectedPatient.getName());
                            System.out.println("IC/Passport: " + selectedPatient.getDisplayId());
                            System.out.println("Phone: " + selectedPatient.getPhone());
                            System.out.println("Email: " + selectedPatient.getEmail());
                            System.out.print("\nConfirm selection? (Y/N): ");
                            
                            String confirm = scanner.nextLine().trim().toUpperCase();
                            if (confirm.equals("Y")) {
                                return selectedPatient;
                            } else {
                                System.out.println("Selection cancelled.");
                            }
                        } else {
                            System.out.println("Invalid patient number. Please enter 1-" + currentView.size());
                        }
                    } catch (NumberFormatException e) {
                        System.out.println("Invalid input. Please use navigation keys or enter a patient number.");
                    }
            }
        }
    }

    private ArrayList<Patient> filterPatients(String query) {
        ArrayList<Patient> results = new ArrayList<>();
        if (query == null || query.trim().isEmpty()) return results;

        String q = query.toLowerCase();

        for (int i = 0; i < patients.size(); i++) {
            Patient patient = patients.get(i);
            String name = patient.getName().toLowerCase();
            String ic = patient.getPatientIC() != null ? patient.getPatientIC().toLowerCase() : "";
            String passport = patient.getPatientPassport() != null ? patient.getPatientPassport().toLowerCase() : "";
            String phone = patient.getPhone().toLowerCase();

            if (name.contains(q) || ic.contains(q) || passport.contains(q) || phone.contains(q)) {
                results.add(patient);
            }
        }
        return results;
    }

    private String truncateString(String str, int maxLength) {
        if (str == null) return "N/A";
        if (str.length() <= maxLength) return str;
        return str.substring(0, maxLength - 3) + "...";
    }

    private Appointment selectAppointmentForCancellation(ArrayList<Appointment> appointments) {
        System.out.println("\n=== SELECT APPOINTMENT FOR CANCELLATION ===");
        
        if (appointments.isEmpty()) {
            System.out.println("No appointments available for cancellation.");
            return null;
        }

        ArrayList<Appointment> currentView = new ArrayList<>(appointments);
        int currentPage = 1;
        String searchQuery = "";
        final int APPOINTMENT_PAGE_SIZE = 8;

        while (true) {
            int totalItems = currentView.size();
            int totalPages = (totalItems + APPOINTMENT_PAGE_SIZE - 1) / APPOINTMENT_PAGE_SIZE;
            if (totalPages == 0) totalPages = 1;

            // Calculate display range
            int startIndex = (currentPage - 1) * APPOINTMENT_PAGE_SIZE;
            int endIndex = Math.min(startIndex + APPOINTMENT_PAGE_SIZE, totalItems);

            // Display header
            System.out.println("\n" + "=".repeat(100));
            System.out.printf("APPOINTMENT SELECTION - Page %d/%d (%d appointments)\n", currentPage, totalPages, totalItems);
            if (!searchQuery.isEmpty()) {
                System.out.println("Search: \"" + searchQuery + "\"");
            }
            System.out.println("=".repeat(100));

            // Display appointments
            System.out.printf("%-4s %-12s %-10s %-20s %-20s %-15s %-10s\n", 
                            "No.", "Date", "Time", "Patient", "Doctor", "Type", "Status");
            System.out.println("-".repeat(100));

            for (int i = startIndex; i < endIndex; i++) {
                Appointment apt = currentView.get(i);
                String patientName = truncateString(apt.getPatient().getName(), 18);
                String doctorName = truncateString("Dr. " + apt.getDoctor().getName(), 18);
                String type = truncateString(apt.getAppointmentType(), 13);
                String status = apt.getStatusDisplay();

                System.out.printf("%-4d %-12s %-10s %-20s %-20s %-15s %-10s\n",
                                i + 1,
                                apt.getFormattedDate(),
                                apt.getFormattedStartTime(),
                                patientName,
                                doctorName,
                                type,
                                status);
            }

            System.out.println("-".repeat(100));
            System.out.printf("Showing %d-%d of %d appointments\n", startIndex + 1, endIndex, totalItems);

            // Navigation options
            System.out.println("\nNavigation:");
            System.out.println("[A] Previous Page | [D] Next Page | [S] Search | [R] Reset Search");
            System.out.println("Enter appointment number to select | [Q] Cancel");
            System.out.print("Choice: ");

            String input = scanner.nextLine().trim().toLowerCase();

            switch (input) {
                case "a":
                    if (currentPage > 1) {
                        currentPage--;
                    } else {
                        System.out.println("This is the first page.");
                    }
                    break;
                case "d":
                    if (currentPage < totalPages) {
                        currentPage++;
                    } else {
                        System.out.println("This is the last page.");
                    }
                    break;
                case "s":
                    System.out.print("Enter search (Patient/Doctor/Type/Date): ");
                    searchQuery = scanner.nextLine().trim();
                    ArrayList<Appointment> filtered = filterCancellableAppointments(appointments, searchQuery);
                    if (!filtered.isEmpty()) {
                        currentView = filtered;
                        currentPage = 1;
                        System.out.println("Found " + filtered.size() + " matching appointments.");
                    } else {
                        System.out.println("No results found for: " + searchQuery);
                    }
                    break;
                case "r":
                    currentView = new ArrayList<>(appointments);
                    searchQuery = "";
                    currentPage = 1;
                    System.out.println("Search cleared. Showing all appointments.");
                    break;
                case "q":
                    return null;
                default:
                    try {
                        int choice = Integer.parseInt(input);
                        if (choice >= 1 && choice <= currentView.size()) {
                            Appointment selectedAppointment = currentView.get(choice - 1);
                            
                            // Show appointment details for confirmation
                            System.out.println("\n=== SELECTED APPOINTMENT ===");
                            System.out.println("Date: " + selectedAppointment.getFormattedDate());
                            System.out.println("Time: " + selectedAppointment.getFormattedStartTime() + " - " + selectedAppointment.getFormattedEndTime());
                            System.out.println("Patient: " + selectedAppointment.getPatient().getName());
                            System.out.println("Doctor: Dr. " + selectedAppointment.getDoctor().getName());
                            System.out.println("Type: " + selectedAppointment.getAppointmentType());
                            System.out.println("Status: " + selectedAppointment.getStatusDisplay());
                            if (selectedAppointment.getDescription() != null && !selectedAppointment.getDescription().isEmpty()) {
                                System.out.println("Notes: " + selectedAppointment.getDescription());
                            }
                            System.out.print("\nConfirm selection? (Y/N): ");
                            
                            String confirm = scanner.nextLine().trim().toUpperCase();
                            if (confirm.equals("Y")) {
                                return selectedAppointment;
                            } else {
                                System.out.println("Selection cancelled.");
                            }
                        } else {
                            System.out.println("Invalid appointment number. Please enter 1-" + currentView.size());
                        }
                    } catch (NumberFormatException e) {
                        System.out.println("Invalid input. Please use navigation keys or enter an appointment number.");
                    }
            }
        }
    }

    private ArrayList<Appointment> filterCancellableAppointments(ArrayList<Appointment> source, String query) {
        ArrayList<Appointment> results = new ArrayList<>();
        if (query == null || query.trim().isEmpty()) return results;

        String q = query.toLowerCase();

        for (int i = 0; i < source.size(); i++) {
            Appointment apt = source.get(i);
            String patientName = apt.getPatient().getName().toLowerCase();
            String doctorName = apt.getDoctor().getName().toLowerCase();
            String appointmentType = apt.getAppointmentType().toLowerCase();
            String date = apt.getFormattedDate().toLowerCase();
            String description = apt.getDescription() != null ? apt.getDescription().toLowerCase() : "";

            if (patientName.contains(q) || doctorName.contains(q) || 
                appointmentType.contains(q) || date.contains(q) || description.contains(q)) {
                results.add(apt);
            }
        }
        return results;
    }

    private Doctor selectDoctor() {
        System.out.println("\n=== SELECT DOCTOR ===");
        
        if (doctors.isEmpty()) {
            ui.displayError("No doctors found in the system.");
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

    private boolean hasTimeConflict(Appointment newAppointment) {
        UUID doctorId = newAppointment.getDoctorId();
        LocalDate appointmentDate = newAppointment.getappointmentDate();
        LocalTime startTime = newAppointment.getStartTime();
        LocalTime endTime = newAppointment.getEndTime();

        for (int i = 0; i < appointmentList.size(); i++) {
            Appointment existing = appointmentList.get(i);
            
            // Skip cancelled appointments
            if (existing.getStatus() == Appointment.Status.CANCELLED) {
                continue;
            }
            
            // Check same doctor and same date
            if (existing.getDoctorId().equals(doctorId) && 
                existing.getappointmentDate().equals(appointmentDate)) {
                
                // Check time overlap
                LocalTime existingStart = existing.getStartTime();
                LocalTime existingEnd = existing.getEndTime();
                
                if (timeOverlaps(startTime, endTime, existingStart, existingEnd)) {
                    return true;
                }
            }
        }
        return false;
    }

    private boolean timeOverlaps(LocalTime start1, LocalTime end1, LocalTime start2, LocalTime end2) {
        return start1.isBefore(end2) && start2.isBefore(end1);
    }

    private void showAvailableTimeSlots(Doctor doctor, LocalDate date) {
        System.out.println("\n=== AVAILABLE TIME SLOTS ===");
        System.out.println("Doctor: " + doctor.getName());
        System.out.println("Date: " + date);
        System.out.println("-".repeat(40));

        // Get existing appointments for this doctor on this date
        ArrayList<Appointment> doctorAppointments = getDoctorAppointments(doctor.getUserID(), date);
        
        // Generate time slots from 9 AM to 5 PM in 30-minute intervals
        LocalTime currentTime = LocalTime.of(9, 0);
        LocalTime endTime = LocalTime.of(17, 0);
        
        System.out.println("Available 30-minute slots:");
        while (currentTime.isBefore(endTime)) {
            LocalTime slotEnd = currentTime.plusMinutes(30);
            boolean isAvailable = true;
            
            for (int i = 0; i < doctorAppointments.size(); i++) {
                Appointment apt = doctorAppointments.get(i);
                if (timeOverlaps(currentTime, slotEnd, apt.getStartTime(), apt.getEndTime())) {
                    isAvailable = false;
                    break;
                }
            }
            
            if (isAvailable) {
                System.out.printf("  %s - %s Available\n",
                                currentTime.toString(), slotEnd.toString());
            } else {
                System.out.printf("  %s - %s Occupied\n",
                                currentTime.toString(), slotEnd.toString());
            }
            
            currentTime = currentTime.plusMinutes(30);
        }
    }

    private ArrayList<Appointment> getDoctorAppointments(UUID doctorId, LocalDate date) {
        ArrayList<Appointment> result = new ArrayList<>();
        for (int i = 0; i < appointmentList.size(); i++) {
            Appointment apt = appointmentList.get(i);
            if (apt.getDoctorId().equals(doctorId) && 
                apt.getappointmentDate().equals(date) &&
                apt.getStatus() != Appointment.Status.CANCELLED) {
                result.add(apt);
            }
        }
        // Sort by start time
        result.sort(Comparator.comparing(Appointment::getStartTime));
        return result;
    }

    private ArrayList<Appointment> getActiveAppointments() {
        ArrayList<Appointment> active = new ArrayList<>();
        for (int i = 0; i < appointmentList.size(); i++) {
            Appointment apt = appointmentList.get(i);
            if (apt.getStatus() == Appointment.Status.SCHEDULED) {
                active.add(apt);
            }
        }
        return active;
    }

    private ArrayList<Appointment> getCancellableAppointments() {
        ArrayList<Appointment> cancellable = new ArrayList<>();
        LocalDate today = LocalDate.now();
        
        for (int i = 0; i < appointmentList.size(); i++) {
            Appointment apt = appointmentList.get(i);
            if ((apt.getStatus() == Appointment.Status.SCHEDULED) &&
                !apt.getappointmentDate().isBefore(today)) {
                cancellable.add(apt);
            }
        }
        return cancellable;
    }

    private void viewDailySchedule() {
        LocalDate currentDate = LocalDate.now();
        
        while (true) {
            ArrayList<Appointment> dayAppointments = getAppointmentsByDate(currentDate);
            
            System.out.println("\n" + "=".repeat(80));
            System.out.println("DAILY SCHEDULE - " + currentDate);
            System.out.println("=".repeat(80));
            
            if (dayAppointments.isEmpty()) {
                System.out.println("No appointments scheduled for this date.");
            } else {
                ui.displayAppointmentList(dayAppointments);
            }
            
            System.out.println("\n" + "-".repeat(80));
            System.out.println("Navigation: [A] Previous Day | [D] Next Day | [Q] Back to Menu");
            System.out.print("Enter choice: ");
            
            String input = scanner.nextLine().trim().toLowerCase();
            
            switch (input) {
                case "a":
                    currentDate = currentDate.minusDays(1);
                    break;
                case "d":
                    currentDate = currentDate.plusDays(1);
                    break;
                case "q":
                    return;
                default:
                    System.out.println("Invalid choice. Use A (previous), D (next), or Q (quit).");
                    ui.pause();
            }
        }
    }

    private void viewWeeklySchedule() {
        // Start with current week (adjust to Monday)
        LocalDate currentWeekStart = LocalDate.now().minusDays(LocalDate.now().getDayOfWeek().getValue() - 1);
        
        while (true) {
            LocalDate endDate = currentWeekStart.plusDays(6);
            ArrayList<Appointment> weekAppointments = getAppointmentsByDateRange(currentWeekStart, endDate);
            
            System.out.println("\n" + "=".repeat(80));
            System.out.println("WEEKLY SCHEDULE - " + currentWeekStart + " to " + endDate);
            System.out.println("=".repeat(80));
            
            if (weekAppointments.isEmpty()) {
                System.out.println("No appointments scheduled for this week.");
            } else {
                displayWeeklyAppointments(weekAppointments, currentWeekStart);
            }
            
            System.out.println("\n" + "-".repeat(80));
            System.out.println("Navigation: [A] Previous Week | [D] Next Week | [Q] Back to Menu");
            System.out.print("Enter choice: ");
            
            String input = scanner.nextLine().trim().toLowerCase();
            
            switch (input) {
                case "a":
                    currentWeekStart = currentWeekStart.minusDays(7);
                    break;
                case "d":
                    currentWeekStart = currentWeekStart.plusDays(7);
                    break;
                case "q":
                    return;
                default:
                    System.out.println("Invalid choice. Use A (previous), D (next), or Q (quit).");
                    ui.pause();
            }
        }
    }

    private void viewDoctorSchedule() {
        Doctor selectedDoctor = selectDoctor();
        if (selectedDoctor == null) return;

        LocalDate currentDate = LocalDate.now();
        
        while (true) {
            ArrayList<Appointment> doctorAppointments = getDoctorAppointments(selectedDoctor.getUserID(), currentDate);
            
            System.out.println("\n" + "=".repeat(80));
            System.out.println("DOCTOR SCHEDULE - Dr. " + selectedDoctor.getName() + " - " + currentDate);
            System.out.println("Specialization: " + selectedDoctor.getSpecialization());
            System.out.println("=".repeat(80));
            
            if (doctorAppointments.isEmpty()) {
                System.out.println("No appointments scheduled for this doctor on this date.");
            } else {
                ui.displayAppointmentList(doctorAppointments);
            }
            
            System.out.println("\n" + "-".repeat(80));
            System.out.println("Navigation: [A] Previous Day | [D] Next Day | [S] Select Another Doctor | [Q] Back to Menu");
            System.out.print("Enter choice: ");
            
            String input = scanner.nextLine().trim().toLowerCase();
            
            switch (input) {
                case "a":
                    currentDate = currentDate.minusDays(1);
                    break;
                case "d":
                    currentDate = currentDate.plusDays(1);
                    break;
                case "s":
                    Doctor newDoctor = selectDoctor();
                    if (newDoctor != null) {
                        selectedDoctor = newDoctor;
                        currentDate = LocalDate.now(); // Reset to today for new doctor
                    }
                    break;
                case "q":
                    return;
                default:
                    System.out.println("Invalid choice. Use A (previous), D (next), S (select doctor), or Q (quit).");
                    ui.pause();
            }
        }
    }

    private void viewAllAppointments() {
        ArrayList<Appointment> allAppointments = new ArrayList<>();
        for (int i = 0; i < appointmentList.size(); i++) {
            allAppointments.add(appointmentList.get(i));
        }
        displayAppointmentsPaginated(allAppointments, "All Appointments");
    }

    private ArrayList<Appointment> getAppointmentsByDate(LocalDate date) {
        ArrayList<Appointment> result = new ArrayList<>();
        for (int i = 0; i < appointmentList.size(); i++) {
            Appointment apt = appointmentList.get(i);
            if (apt.getappointmentDate().equals(date)) {
                result.add(apt);
            }
        }
        result.sort(Comparator.comparing(Appointment::getStartTime));
        return result;
    }

    private ArrayList<Appointment> getAppointmentsByDateRange(LocalDate startDate, LocalDate endDate) {
        ArrayList<Appointment> result = new ArrayList<>();
        for (int i = 0; i < appointmentList.size(); i++) {
            Appointment apt = appointmentList.get(i);
            LocalDate aptDate = apt.getappointmentDate();
            if (!aptDate.isBefore(startDate) && !aptDate.isAfter(endDate)) {
                result.add(apt);
            }
        }
        result.sort(Comparator.comparing(Appointment::getappointmentDate).thenComparing(Appointment::getStartTime));
        return result;
    }

    private void displayAppointmentsPaginated(ArrayList<Appointment> appointments, String title) {
        if (appointments.isEmpty()) {
            System.out.println("\n" + title);
            System.out.println("No appointments found.");
            ui.pause();
            return;
        }

        int currentPage = 1;
        String searchQuery = "";
        ArrayList<Appointment> currentView = new ArrayList<>(appointments);

        while (true) {
            int totalItems = currentView.size();
            int totalPages = (totalItems + PAGE_SIZE - 1) / PAGE_SIZE;
            if (totalPages == 0) totalPages = 1;

            ui.displayAppointmentSchedule(currentView, currentPage, totalPages, searchQuery, null);

            System.out.println("Press: [A] Prev | [D] Next | [S] Search | [R] Reset | [Q] Back");
            System.out.print("Enter choice: ");
            String input = scanner.nextLine().trim().toLowerCase();

            switch (input) {
                case "a":
                    if (currentPage > 1) {
                        currentPage--;
                    } else {
                        System.out.println("This is the first page.");
                        ui.pause();
                    }
                    break;
                case "d":
                    if (currentPage < totalPages) {
                        currentPage++;
                    } else {
                        System.out.println("This is the last page.");
                        ui.pause();
                    }
                    break;
                case "s":
                    System.out.print("Enter search (Patient/Doctor/Type): ");
                    searchQuery = scanner.nextLine().trim();
                    ArrayList<Appointment> filtered = filterAppointments(appointments, searchQuery);
                    if (!filtered.isEmpty()) {
                        currentView = filtered;
                        currentPage = 1;
                    } else {
                        System.out.println("No results found for: " + searchQuery);
                        ui.pause();
                    }
                    break;
                case "r":
                    currentView = new ArrayList<>(appointments);
                    searchQuery = "";
                    currentPage = 1;
                    break;
                case "q":
                    return;
                default:
                    System.out.println("Invalid choice.");
                    ui.pause();
            }
        }
    }

    private ArrayList<Appointment> filterAppointments(ArrayList<Appointment> source, String query) {
        ArrayList<Appointment> results = new ArrayList<>();
        if (query == null || query.trim().isEmpty()) return results;

        String q = query.toLowerCase();

        for (int i = 0; i < source.size(); i++) {
            Appointment apt = source.get(i);
            String patientName = apt.getPatient().getName().toLowerCase();
            String doctorName = apt.getDoctor().getName().toLowerCase();
            String appointmentType = apt.getAppointmentType().toLowerCase();
            String description = apt.getDescription() != null ? apt.getDescription().toLowerCase() : "";

            if (patientName.contains(q) || doctorName.contains(q) || 
                appointmentType.contains(q) || description.contains(q)) {
                results.add(apt);
            }
        }
        return results;
    }
    
    private void displayWeeklyAppointments(ArrayList<Appointment> appointments, LocalDate weekStart) {
        LocalDate[] weekDays = new LocalDate[7];
        String[] dayNames = {"MONDAY", "TUESDAY", "WEDNESDAY", "THURSDAY", "FRIDAY", "SATURDAY", "SUNDAY"};
        
        for (int i = 0; i < 7; i++) {
            weekDays[i] = weekStart.plusDays(i);
        }
        
        for (int day = 0; day < 7; day++) {
            System.out.println("\n" + dayNames[day] + " - " + weekDays[day]);
            System.out.println("-".repeat(50));
            
            ArrayList<Appointment> dayAppointments = new ArrayList<>();
            for (int i = 0; i < appointments.size(); i++) {
                Appointment apt = appointments.get(i);
                if (apt.getappointmentDate().equals(weekDays[day])) {
                    dayAppointments.add(apt);
                }
            }
            
            if (dayAppointments.isEmpty()) {
                System.out.println("  No appointments");
            } else {
                for (int i = 0; i < dayAppointments.size(); i++) {
                    Appointment apt = dayAppointments.get(i);
                    System.out.printf("  %s - %s | %s | Dr. %s (%s)%n",
                                    apt.getFormattedStartTime(),
                                    apt.getFormattedEndTime(),
                                    apt.getPatient().getName(),
                                    apt.getDoctor().getName(),
                                    apt.getStatusDisplay());
                }
            }
        }
        
        System.out.println("\n" + "=".repeat(50));
        System.out.printf("Total appointments this week: %d%n", appointments.size());
    }

    // Update methods for appointment fields
    private void updateAppointmentDate(Appointment appointment) {
        LocalDate newDate = ui.getDateInput("Enter new appointment date");
        if (newDate != null && !newDate.isBefore(LocalDate.now())) {
            appointment.setappointmentDate(newDate);
            ui.displaySuccess("Appointment date updated to: " + newDate);
        } else if (newDate != null) {
            ui.displayError("Cannot schedule appointment in the past.");
        }
    }


    private void updateAppointmentType(Appointment appointment) {
        System.out.print("Enter new appointment type: ");
        String newType = scanner.nextLine().trim();
        if (!newType.isEmpty()) {
            appointment.setAppointmentType(newType);
            ui.displaySuccess("Appointment type updated to: " + newType);
        }
    }

    private void updateAppointmentNotes(Appointment appointment) {
        System.out.print("Enter new notes/description: ");
        String newNotes = scanner.nextLine().trim();
        appointment.setDescription(newNotes);
        ui.displaySuccess("Appointment notes updated.");
    }

    private void updateAppointmentTime(Appointment appointment) {
        System.out.println("\n=== UPDATE APPOINTMENT TIME ===");
        System.out.println("Current time: " + appointment.getFormattedStartTime() + " - " + appointment.getFormattedEndTime());
        System.out.println("Do you want to:");
        System.out.println("1. Choose from available slots");
        System.out.println("2. Enter specific time");
        System.out.println("3. Cancel");
        System.out.print("Choice: ");
        
        try {
            int choice = Integer.parseInt(scanner.nextLine().trim());
            switch (choice) {
                case 1:
                    LocalTime newTime = selectAvailableTimeSlot(appointment.getDoctor(), appointment.getappointmentDate());
                    if (newTime != null) {
                        appointment.setStartTime(newTime);
                        appointment.setEndTime(newTime.plusMinutes(30));
                        ui.displaySuccess("Appointment time updated to: " + newTime);
                    }
                    break;
                case 2:
                    updateAppointmentTimeManual(appointment);
                    break;
                case 3:
                    return;
                default:
                    System.out.println("Invalid choice.");
            }
        } catch (NumberFormatException e) {
            ui.displayError("Please enter a valid number.");
        }
    }

    private void updateAppointmentTimeManual(Appointment appointment) {
        System.out.print("Enter new start time (HH:mm): ");
        String timeInput = scanner.nextLine().trim();
        try {
            LocalTime newStartTime = LocalTime.parse(timeInput);
            if (newStartTime.isBefore(LocalTime.of(9, 0)) || newStartTime.isAfter(LocalTime.of(17, 30))) {
                ui.displayError("Please schedule within business hours (09:00 - 17:30).");
                return;
            }
            
            // Check for conflicts
            Appointment tempAppointment = new Appointment(
                UUID.randomUUID(),
                appointment.getPatient(),
                appointment.getDoctor(),
                appointment.getStaff(),
                appointment.getappointmentDate(),
                newStartTime,
                newStartTime.plusMinutes(30),
                appointment.getAppointmentType(),
                appointment.getDescription(),
                appointment.getStatus()
            );
            
            if (hasTimeConflictExcluding(tempAppointment, appointment.getId())) {
                ui.displayError("Time conflict detected! Doctor has another appointment at this time.");
                return;
            }
            
            appointment.setStartTime(newStartTime);
            appointment.setEndTime(newStartTime.plusMinutes(30));
            ui.displaySuccess("Appointment time updated to: " + newStartTime);
        } catch (Exception e) {
            ui.displayError("Invalid time format. Use HH:mm.");
        }
    }

    private void updateAppointmentStatus(Appointment appointment) {
        System.out.println("Select new status:");
        System.out.println("1. SCHEDULED");
        System.out.println("2. CANCELLED");
        System.out.print("Choice: ");
        
        try {
            int choice = Integer.parseInt(scanner.nextLine().trim());
            Appointment.Status newStatus = null;
            switch (choice) {
                case 1: newStatus = Appointment.Status.SCHEDULED; break;
                case 2: newStatus = Appointment.Status.CANCELLED; break;
                default:
                    System.out.println("Invalid choice.");
                    return;
            }
            appointment.setStatus(newStatus);
            ui.displaySuccess("Status updated to: " + newStatus);
        } catch (NumberFormatException e) {
            ui.displayError("Please enter a valid number.");
        }
    }

    // New helper methods for enhanced appointment booking
    private LocalDate selectAppointmentDate() {
        System.out.println("\n=== SELECT APPOINTMENT DATE ===");
        while (true) {
            System.out.print("Enter appointment date (YYYY-MM-DD) or 'cancel': ");
            String input = scanner.nextLine().trim();
            
            if (input.equalsIgnoreCase("cancel")) {
                return null;
            }
            
            try {
                LocalDate date = LocalDate.parse(input);
                if (date.isBefore(LocalDate.now())) {
                    System.out.println("Cannot schedule appointment in the past.");
                    continue;
                }
                return date;
            } catch (Exception e) {
                System.out.println("Invalid date format. Use YYYY-MM-DD.");
            }
        }
    }

    private String selectAppointmentType() {
        System.out.println("\n=== SELECT APPOINTMENT TYPE ===");
        System.out.println("1. Consultation");
        System.out.println("2. Blood Test");
        System.out.println("3. Wound Dressing");
        System.out.println("4. Other");
        System.out.println("5. Cancel");
        
        while (true) {
            System.out.print("Select appointment type (1-5): ");
            
            try {
                int choice = Integer.parseInt(scanner.nextLine().trim());
                switch (choice) {
                    case 1:
                        return "Consultation";
                    case 2:
                        return "Blood Test";
                    case 3:
                        return "Wound Dressing";
                    case 4:
                        System.out.print("Enter custom appointment type: ");
                        String customType = scanner.nextLine().trim();
                        if (!customType.isEmpty()) {
                            return customType;
                        } else {
                            System.out.println("Appointment type cannot be empty.");
                        }
                        break;
                    case 5:
                        return null;
                    default:
                        System.out.println("Invalid selection. Please choose 1-5.");
                }
            } catch (NumberFormatException e) {
                System.out.println("Please enter a valid number (1-5).");
            }
        }
    }

    private LocalTime selectAvailableTimeSlot(Doctor doctor, LocalDate date) {
        System.out.println("\n=== SELECT TIME SLOT ===");
        System.out.println("Doctor: Dr. " + doctor.getName() + " (" + doctor.getSpecialization() + ")");
        System.out.println("Date: " + date);
        System.out.println("-".repeat(80));

        // Check if doctor has duty schedule for this date
        ArrayList<DutySchedule> doctorDuties = getDoctorDutySchedule(doctor.getUserID(), date);
        if (doctorDuties.isEmpty()) {
            System.out.println("Doctor has no duty schedule for this date.");
            System.out.println("Showing default clinic hours (09:00 - 17:00)");
            return selectFromDefaultHours(doctor, date);
        }

        // Show duty schedule based availability
        return selectFromDutySchedule(doctor, date, doctorDuties);
    }

    private ArrayList<DutySchedule> getDoctorDutySchedule(UUID doctorId, LocalDate date) {
        ArrayList<DutySchedule> result = new ArrayList<>();
        if (schedules == null) return result;
        
        ListInterface<DutySchedule> doctorSchedules = schedules.getValue(doctorId);
        if (doctorSchedules == null) return result;

        for (int i = 0; i < doctorSchedules.size(); i++) {
            DutySchedule schedule = doctorSchedules.get(i);
            if (schedule.getDateObject().equals(date)) {
                result.add(schedule);
            }
        }
        return result;
    }

    private LocalTime selectFromDutySchedule(Doctor doctor, LocalDate date, ArrayList<DutySchedule> duties) {
        System.out.println("Available time slots based on duty schedule:");
        System.out.println("-".repeat(60));
        
        ArrayList<TimeSlot> availableSlots = new ArrayList<>();
        
        for (int d = 0; d < duties.size(); d++) {
            DutySchedule duty = duties.get(d);
            LocalTime dutyStart = duty.getStartTimeObject();
            LocalTime dutyEnd = duty.getEndTimeObject();
            
            System.out.println("Duty Period: " + duty.getStartTime() + " - " + duty.getEndTime());
            
            // Generate 30-minute slots within duty period
            LocalTime currentTime = dutyStart;
            while (currentTime.plusMinutes(30).isBefore(dutyEnd) || currentTime.plusMinutes(30).equals(dutyEnd)) {
                LocalTime slotEnd = currentTime.plusMinutes(30);
                
                if (!hasAppointmentConflict(doctor.getUserID(), date, currentTime, slotEnd)) {
                    availableSlots.add(new TimeSlot(currentTime, slotEnd, true));
                } else {
                    availableSlots.add(new TimeSlot(currentTime, slotEnd, false));
                }
                
                currentTime = currentTime.plusMinutes(30);
            }
        }
        
        return selectTimeFromSlots(availableSlots);
    }

    private LocalTime selectFromDefaultHours(Doctor doctor, LocalDate date) {
        System.out.println("Default clinic hours (09:00 - 17:00):");
        System.out.println("-".repeat(60));
        
        ArrayList<TimeSlot> availableSlots = new ArrayList<>();
        LocalTime currentTime = LocalTime.of(9, 0);
        LocalTime endTime = LocalTime.of(17, 0);
        
        while (currentTime.isBefore(endTime)) {
            LocalTime slotEnd = currentTime.plusMinutes(30);
            
            if (!hasAppointmentConflict(doctor.getUserID(), date, currentTime, slotEnd)) {
                availableSlots.add(new TimeSlot(currentTime, slotEnd, true));
            } else {
                availableSlots.add(new TimeSlot(currentTime, slotEnd, false));
            }
            
            currentTime = currentTime.plusMinutes(30);
        }
        
        return selectTimeFromSlots(availableSlots);
    }

    private LocalTime selectTimeFromSlots(ArrayList<TimeSlot> slots) {
        // Display available and occupied slots
        int slotNumber = 1;
        ArrayList<TimeSlot> availableOnly = new ArrayList<>();
        
        for (int i = 0; i < slots.size(); i++) {
            TimeSlot slot = slots.get(i);
            if (slot.isAvailable()) {
                System.out.printf("[%d] %s - %s (Available)\n", 
                                slotNumber++, slot.getStartTime(), slot.getEndTime());
                availableOnly.add(slot);
            } else {
                System.out.printf("    %s - %s (Occupied)\n", 
                                slot.getStartTime(), slot.getEndTime());
            }
        }
        
        if (availableOnly.isEmpty()) {
            System.out.println("No available time slots for this date.");
            return null;
        }
        
        // Let user select from available slots
        while (true) {
            System.out.printf("Select time slot (1-%d) or 0 to cancel: ", availableOnly.size());
            
            try {
                int choice = Integer.parseInt(scanner.nextLine().trim());
                if (choice == 0) {
                    return null;
                }
                if (choice >= 1 && choice <= availableOnly.size()) {
                    return availableOnly.get(choice - 1).getStartTimeObject();
                }
                System.out.println("Invalid selection.");
            } catch (NumberFormatException e) {
                System.out.println("Please enter a valid number.");
            }
        }
    }

    private boolean hasAppointmentConflict(UUID doctorId, LocalDate date, LocalTime startTime, LocalTime endTime) {
        for (int i = 0; i < appointmentList.size(); i++) {
            Appointment apt = appointmentList.get(i);
            
            if (apt.getStatus() == Appointment.Status.CANCELLED) continue;
            if (!apt.getDoctorId().equals(doctorId)) continue;
            if (!apt.getappointmentDate().equals(date)) continue;
            
            if (timeOverlaps(startTime, endTime, apt.getStartTime(), apt.getEndTime())) {
                return true;
            }
        }
        return false;
    }

    private boolean hasTimeConflictExcluding(Appointment newAppointment, UUID excludeId) {
        UUID doctorId = newAppointment.getDoctorId();
        LocalDate appointmentDate = newAppointment.getappointmentDate();
        LocalTime startTime = newAppointment.getStartTime();
        LocalTime endTime = newAppointment.getEndTime();

        for (int i = 0; i < appointmentList.size(); i++) {
            Appointment existing = appointmentList.get(i);
            
            if (existing.getId().equals(excludeId)) continue;
            if (existing.getStatus() == Appointment.Status.CANCELLED) continue;
            if (!existing.getDoctorId().equals(doctorId)) continue;
            if (!existing.getappointmentDate().equals(appointmentDate)) continue;
            
            if (timeOverlaps(startTime, endTime, existing.getStartTime(), existing.getEndTime())) {
                return true;
            }
        }
        return false;
    }

    private LocalTime selectTimeSlot(LocalDate appointmentDate) {
        System.out.println("\n=== SELECT TIME SLOT ===");
        System.out.println("Date: " + appointmentDate);
        
        // First, check if ANY doctor has duty on this date
        boolean anyDoctorHasDuty = false;
        for (int i = 0; i < doctors.size(); i++) {
            Doctor doctor = doctors.get(i);
            ArrayList<DutySchedule> duties = getDoctorDutySchedule(doctor.getUserID(), appointmentDate);
            if (!duties.isEmpty()) {
                anyDoctorHasDuty = true;
                break;
            }
        }
        
        if (!anyDoctorHasDuty) {
            System.out.println("No doctors have duty schedules for " + appointmentDate + ".");
            System.out.println("Please select a different date or contact administration.");
            return null;
        }
        
        System.out.println("Checking available time slots based on doctor duty schedules...");
        System.out.println("-".repeat(60));
        
        ArrayList<LocalTime> availableSlots = new ArrayList<>();
        
        // Get all unique time slots from all doctors' duty schedules for this date
        ArrayList<LocalTime> allPossibleSlots = new ArrayList<>();
        for (int i = 0; i < doctors.size(); i++) {
            Doctor doctor = doctors.get(i);
            ArrayList<DutySchedule> duties = getDoctorDutySchedule(doctor.getUserID(), appointmentDate);
            
            for (int j = 0; j < duties.size(); j++) {
                DutySchedule duty = duties.get(j);
                LocalTime dutyStart = duty.getStartTimeObject();
                LocalTime dutyEnd = duty.getEndTimeObject();
                
                // Generate 30-minute slots within this duty period
                LocalTime currentTime = dutyStart;
                while (currentTime.plusMinutes(30).isBefore(dutyEnd) || currentTime.plusMinutes(30).equals(dutyEnd)) {
                    // Add to possible slots if not already present
                    boolean alreadyExists = false;
                    for (int k = 0; k < allPossibleSlots.size(); k++) {
                        if (allPossibleSlots.get(k).equals(currentTime)) {
                            alreadyExists = true;
                            break;
                        }
                    }
                    if (!alreadyExists) {
                        allPossibleSlots.add(currentTime);
                    }
                    currentTime = currentTime.plusMinutes(30);
                }
            }
        }
        
        // Sort the time slots
        allPossibleSlots.sort(LocalTime::compareTo);
        
        // Now check which of these time slots have at least one available doctor
        for (int i = 0; i < allPossibleSlots.size(); i++) {
            LocalTime timeSlot = allPossibleSlots.get(i);
            
            // Check if any doctor has duty at this time AND is not occupied
            for (int j = 0; j < doctors.size(); j++) {
                Doctor doctor = doctors.get(j);
                if (isDoctorOnDutyAtTime(doctor, appointmentDate, timeSlot) && 
                    !hasAppointmentConflict(doctor.getUserID(), appointmentDate, timeSlot, timeSlot.plusMinutes(30))) {
                    availableSlots.add(timeSlot);
                    break; // Found at least one available doctor for this time slot
                }
            }
        }
        
        if (availableSlots.isEmpty()) {
            System.out.println("No available time slots found for " + appointmentDate + ".");
            System.out.println("All duty time slots are occupied by existing appointments.");
            return null;
        }
        
        // Display available time slots only
        System.out.println("Available time slots (based on doctor duty schedules):");
        System.out.println("-".repeat(60));
        
        for (int i = 0; i < availableSlots.size(); i++) {
            if (i % 4 == 0) System.out.println();
            System.out.printf("[%2d] %-8s ", i + 1, availableSlots.get(i).toString());
        }
        
        System.out.println("\n" + "-".repeat(60));
        System.out.printf("Total available slots: %d\n", availableSlots.size());
        
        while (true) {
            System.out.printf("Select time slot (1-%d) or 0 to cancel: ", availableSlots.size());
            
            try {
                int choice = Integer.parseInt(scanner.nextLine().trim());
                if (choice == 0) return null;
                if (choice >= 1 && choice <= availableSlots.size()) {
                    LocalTime selectedTime = availableSlots.get(choice - 1);
                    
                    // Show how many doctors are available at this time on the selected date
                    int availableDoctorCount = 0;
                    for (int i = 0; i < doctors.size(); i++) {
                        Doctor doctor = doctors.get(i);
                        if (isDoctorOnDutyAtTime(doctor, appointmentDate, selectedTime) && 
                            !hasAppointmentConflict(doctor.getUserID(), appointmentDate, selectedTime, selectedTime.plusMinutes(30))) {
                            availableDoctorCount++;
                        }
                    }
                    
                    System.out.printf("Selected time: %s (%d doctor(s) available on %s)\n", 
                                    selectedTime, availableDoctorCount, appointmentDate);
                    return selectedTime;
                }
                System.out.println("Invalid selection.");
            } catch (NumberFormatException e) {
                System.out.println("Please enter a valid number.");
            }
        }
    }

    private Doctor selectAvailableDoctor(LocalDate date, LocalTime time) {
        System.out.println("\n=== SELECT AVAILABLE DOCTOR ===");
        System.out.println("Date: " + date + ", Time: " + time);
        System.out.println("-".repeat(60));
        
        ArrayList<Doctor> availableDoctors = new ArrayList<>();
        
        // First, get all doctors who have duty schedules for this date
        for (int i = 0; i < doctors.size(); i++) {
            Doctor doctor = doctors.get(i);
            ArrayList<DutySchedule> doctorDuties = getDoctorDutySchedule(doctor.getUserID(), date);
            
            // Only consider doctors who have duty schedules for this date
            if (!doctorDuties.isEmpty()) {
                if (isDoctorAvailable(doctor, date, time)) {
                    availableDoctors.add(doctor);
                }
            }
        }
        
        if (availableDoctors.isEmpty()) {
            System.out.println("No doctors have duty schedules and are available at this time slot.");
            System.out.println("Please choose a different time or use 'By Doctor Availability' booking method.");
            return null;
        }
        
        System.out.println("Available doctors (with duty schedules):");
        for (int i = 0; i < availableDoctors.size(); i++) {
            Doctor doctor = availableDoctors.get(i);
            ArrayList<DutySchedule> duties = getDoctorDutySchedule(doctor.getUserID(), date);
            String dutyPeriod = "";
            if (!duties.isEmpty()) {
                dutyPeriod = " [Duty: " + duties.get(0).getStartTime() + "-" + duties.get(0).getEndTime() + "]";
            }
            System.out.printf("[%d] Dr. %s (%s)%s\n", 
                            i + 1, doctor.getName(), doctor.getSpecialization(), dutyPeriod);
        }
        
        while (true) {
            System.out.printf("Select doctor (1-%d) or 0 to cancel: ", availableDoctors.size());
            
            try {
                int choice = Integer.parseInt(scanner.nextLine().trim());
                if (choice == 0) return null;
                if (choice >= 1 && choice <= availableDoctors.size()) {
                    return availableDoctors.get(choice - 1);
                }
                System.out.println("Invalid selection.");
            } catch (NumberFormatException e) {
                System.out.println("Please enter a valid number.");
            }
        }
    }

    private boolean isDoctorAvailable(Doctor doctor, LocalDate date, LocalTime time) {
        // Check duty schedule first
        ArrayList<DutySchedule> duties = getDoctorDutySchedule(doctor.getUserID(), date);
        boolean onDuty = false;
        
        if (duties.isEmpty()) {
            // If no specific duty schedule, assume available during clinic hours
            LocalTime clinicStart = LocalTime.of(9, 0);
            LocalTime clinicEnd = LocalTime.of(17, 0);
            LocalTime appointmentEnd = time.plusMinutes(30);
            onDuty = !time.isBefore(clinicStart) && !appointmentEnd.isAfter(clinicEnd);
        } else {
            // Check if time falls within any duty period
            for (int i = 0; i < duties.size(); i++) {
                DutySchedule duty = duties.get(i);
                LocalTime dutyStart = duty.getStartTimeObject();
                LocalTime dutyEnd = duty.getEndTimeObject();
                
                // Check if appointment time (30min slot) fits within duty hours
                LocalTime appointmentEnd = time.plusMinutes(30);
                if (!time.isBefore(dutyStart) && !appointmentEnd.isAfter(dutyEnd)) {
                    onDuty = true;
                    break;
                }
            }
        }
        
        if (!onDuty) return false;
        
        // Check for existing appointments
        LocalTime endTime = time.plusMinutes(30);
        return !hasAppointmentConflict(doctor.getUserID(), date, time, endTime);
    }

    private boolean isDoctorOnDutyAtTime(Doctor doctor, LocalDate date, LocalTime time) {
        ArrayList<DutySchedule> duties = getDoctorDutySchedule(doctor.getUserID(), date);
        
        if (duties.isEmpty()) {
            return false; // No duty schedule means not on duty
        }
        
        // Check if time falls within any duty period
        for (int i = 0; i < duties.size(); i++) {
            DutySchedule duty = duties.get(i);
            LocalTime dutyStart = duty.getStartTimeObject();
            LocalTime dutyEnd = duty.getEndTimeObject();
            
            // Check if appointment time (30min slot) fits within duty hours
            LocalTime appointmentEnd = time.plusMinutes(30);
            if (!time.isBefore(dutyStart) && !appointmentEnd.isAfter(dutyEnd)) {
                return true;
            }
        }
        
        return false;
    }

    private void displayAppointmentSummary(Appointment appointment) {
        System.out.println("\n" + "=".repeat(50));
        System.out.println("APPOINTMENT CONFIRMATION");
        System.out.println("=".repeat(50));
        System.out.println("Date: " + appointment.getFormattedDate());
        System.out.println("Time: " + appointment.getFormattedStartTime() + " - " + appointment.getFormattedEndTime());
        System.out.println("Patient: " + appointment.getPatient().getName());
        System.out.println("Doctor: Dr. " + appointment.getDoctor().getName());
        System.out.println("Specialization: " + appointment.getDoctor().getSpecialization());
        System.out.println("Type: " + appointment.getAppointmentType());
        if (appointment.getDescription() != null && !appointment.getDescription().isEmpty()) {
            System.out.println("Notes: " + appointment.getDescription());
        }
        System.out.println("Status: " + appointment.getStatusDisplay());
        System.out.println("Created by: " + appointment.getStaff().getName());
        System.out.println("=".repeat(50));
    }

    // Inner class for time slot representation
    private static class TimeSlot {
        private LocalTime startTime;
        private LocalTime endTime;
        private boolean available;
        
        public TimeSlot(LocalTime startTime, LocalTime endTime, boolean available) {
            this.startTime = startTime;
            this.endTime = endTime;
            this.available = available;
        }
        
        public LocalTime getStartTimeObject() { return startTime; }
        public String getStartTime() { return startTime.toString(); }
        public String getEndTime() { return endTime.toString(); }
        public boolean isAvailable() { return available; }
    }
}
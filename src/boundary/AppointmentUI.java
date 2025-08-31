// Teoh Yong Ming
package boundary;

import adt.*;
import entity.Appointment;
import entity.Doctor;
import entity.Patient;
import entity.Staff;
import utility.MessageUI;

import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.Scanner;
import java.util.UUID;

public class AppointmentUI {
    private QueueInterface<String> choiceQueue;
    private MessageUI UI;
    private static final int PAGE_SIZE = 5;
    private Scanner scanner;
    private static final DateTimeFormatter DATE_FMT = DateTimeFormatter.ofPattern("yyyy-MM-dd");
    private static final DateTimeFormatter TIME_FMT = DateTimeFormatter.ofPattern("HH:mm");

    public AppointmentUI() {
        choiceQueue = new LinkedQueue<>();
        UI = new MessageUI();
        scanner = new Scanner(System.in);
    }

    public Integer mainMenu() {
        choiceQueue.clear();
        choiceQueue.enqueue("Add Appointment");
        choiceQueue.enqueue("Update Appointment");
        choiceQueue.enqueue("View Schedule");
        choiceQueue.enqueue("Cancel Appointment");
        return UI.mainUI("Appointment Management", choiceQueue);
    }

    public Appointment getAppointmentDetails(Patient patient, Doctor doctor, Staff createdBy) {
        System.out.println("\n=== ADD APPOINTMENT ===");
        System.out.println("Patient: " + patient.getName());
        System.out.println("Doctor: " + doctor.getName() + " (" + doctor.getSpecialization() + ")");
        System.out.println("-".repeat(50));

        // Get appointment date
        LocalDate appointmentDate = null;
        while (appointmentDate == null) {
            System.out.print("Enter appointment date (yyyy-mm-dd) or CANCEL: ");
            String dateInput = scanner.nextLine().trim();
            
            if (dateInput.equalsIgnoreCase("CANCEL")) {
                return null;
            }
            
            try {
                appointmentDate = LocalDate.parse(dateInput, DATE_FMT);
                if (appointmentDate.isBefore(LocalDate.now())) {
                    System.out.println("Cannot schedule appointment in the past.");
                    appointmentDate = null;
                }
            } catch (DateTimeParseException e) {
                System.out.println("Invalid date format. Use yyyy-mm-dd.");
            }
        }

        // Get start time
        LocalTime startTime = null;
        while (startTime == null) {
            System.out.print("Enter start time (HH:mm) or CANCEL: ");
            String timeInput = scanner.nextLine().trim();
            
            if (timeInput.equalsIgnoreCase("CANCEL")) {
                return null;
            }
            
            try {
                startTime = LocalTime.parse(timeInput, TIME_FMT);
                // Validate business hours (9 AM to 6 PM)
                if (startTime.isBefore(LocalTime.of(9, 0)) || startTime.isAfter(LocalTime.of(17, 30))) {
                    System.out.println("Please schedule within business hours (09:00 - 17:30).");
                    startTime = null;
                }
            } catch (DateTimeParseException e) {
                System.out.println("Invalid time format. Use HH:mm (24-hour format).");
            }
        }

        // Calculate end time (default 30 minutes)
        LocalTime endTime = startTime.plusMinutes(30);
        
        System.out.print("Appointment duration (default 30 minutes): ");
        String durationInput = scanner.nextLine().trim();
        if (!durationInput.isEmpty()) {
            try {
                int duration = Integer.parseInt(durationInput);
                if (duration > 0 && duration <= 120) {
                    endTime = startTime.plusMinutes(duration);
                } else {
                    System.out.println("Using default 30 minutes duration.");
                }
            } catch (NumberFormatException e) {
                System.out.println("Invalid duration. Using default 30 minutes.");
            }
        }

        // Get appointment type
        System.out.println("\nAppointment Types:");
        System.out.println("1. Consultation");
        System.out.println("2. Follow-up");
        System.out.println("3. Check-up");
        System.out.println("4. Procedure");
        System.out.println("5. Other");
        
        String appointmentType = "Consultation";
        System.out.print("Select appointment type (1-5): ");
        String typeChoice = scanner.nextLine().trim();
        
        switch (typeChoice) {
            case "1": appointmentType = "Consultation"; break;
            case "2": appointmentType = "Follow-up"; break;
            case "3": appointmentType = "Check-up"; break;
            case "4": appointmentType = "Procedure"; break;
            case "5": 
                System.out.print("Enter custom appointment type: ");
                String customType = scanner.nextLine().trim();
                appointmentType = !customType.isEmpty() ? customType : "Other";
                break;
            default: 
                System.out.println("Using default: Consultation");
                break;
        }

        // Get description/notes
        System.out.print("Enter appointment notes/description (optional): ");
        String description = scanner.nextLine().trim();
        if (description.isEmpty()) {
            description = "Scheduled appointment";
        }

        // Create appointment
        return new Appointment(
            UUID.randomUUID(),
            patient,
            doctor,
            createdBy,
            appointmentDate,
            startTime,
            endTime,
            appointmentType,
            description,
            Appointment.Status.SCHEDULED
        );
    }

    public void displayAppointmentSchedule(ArrayList<Appointment> appointments, int currentPage, 
                                         int totalPages, String searchQuery, LocalDate selectedDate) {
        System.out.println("\n" + "=".repeat(100));
        System.out.println("APPOINTMENT SCHEDULE" + (selectedDate != null ? " - " + selectedDate.format(DATE_FMT) : ""));
        System.out.println("=".repeat(100));
        
        if (!searchQuery.isEmpty()) {
            System.out.println("Search: \"" + searchQuery + "\"");
            System.out.println("-".repeat(100));
        }
        
        System.out.printf("Page %d of %d | Total: %d appointment(s)\n", currentPage, totalPages, appointments.size());
        System.out.println("-".repeat(100));
        
        int startIndex = (currentPage - 1) * PAGE_SIZE;
        int endIndex = Math.min(startIndex + PAGE_SIZE, appointments.size());
        
        if (appointments.isEmpty()) {
            System.out.println("No appointments found.");
            return;
        }
        
        System.out.printf("%-4s %-12s %-10s %-15s %-20s %-20s %-15s %-15s\n", 
                         "No.", "Date", "Time", "Duration", "Patient", "Doctor", "Status", "Created By");
        System.out.println("-".repeat(115));
        
        for (int i = startIndex; i < endIndex; i++) {
            Appointment apt = appointments.get(i);
            String duration = apt.getFormattedStartTime() + " - " + apt.getFormattedEndTime();
            String createdBy = apt.getStaff() != null ? apt.getStaff().getName() : "N/A";
            
            System.out.printf("%-4d %-12s %-10s %-15s %-20s %-20s %-15s %-15s\n",
                            i + 1,
                            apt.getFormattedDate(),
                            apt.getFormattedStartTime(),
                            duration,
                            truncateString(apt.getPatient().getName(), 18),
                            truncateString(apt.getDoctor().getName(), 18),
                            apt.getStatus().toString(),
                            truncateString(createdBy, 13));
                            
            if (apt.getDescription() != null && !apt.getDescription().trim().isEmpty()) {
                System.out.printf("     Type: %s | Notes: %s\n", 
                                apt.getAppointmentType(),
                                truncateString(apt.getDescription(), 50));
            }
            System.out.println();
        }
        System.out.println("-".repeat(100));
    }

    public void displayDoctorSchedule(ListInterface<Doctor> doctors, 
                                    HashedDictionary<UUID, ArrayList<Appointment>> doctorAppointments, 
                                    LocalDate selectedDate) {
        System.out.println("\n" + "=".repeat(100));
        System.out.println("DOCTOR SCHEDULE - " + selectedDate.format(DATE_FMT));
        System.out.println("=".repeat(100));
        
        for (int i = 0; i < doctors.size(); i++) {
            Doctor doctor = doctors.get(i);
            ArrayList<Appointment> appointments = doctorAppointments.getValue(doctor.getUserID());
            
            System.out.printf("\n[%s] Dr. %s (%s)\n", 
                            doctor.getUserID().toString().substring(0, 8),
                            doctor.getName(), 
                            doctor.getSpecialization());
            System.out.println("-".repeat(80));
            
            if (appointments == null || appointments.isEmpty()) {
                System.out.println("   No appointments scheduled");
            } else {
                // Sort appointments by time
                appointments.sort((a1, a2) -> a1.getStartTime().compareTo(a2.getStartTime()));
                
                for (int j = 0; j < appointments.size(); j++) {
                    Appointment apt = appointments.get(j);
                    System.out.printf("   %s - %s | %s | %s (%s)\n",
                                    apt.getFormattedStartTime(),
                                    apt.getFormattedEndTime(),
                                    apt.getStatusDisplay(),
                                    apt.getPatient().getName(),
                                    apt.getAppointmentType());
                }
            }
        }
        System.out.println("=".repeat(100));
    }

    public LocalDate getDateInput(String prompt) {
        LocalDate date = null;
        while (date == null) {
            System.out.print(prompt + " (yyyy-mm-dd) or CANCEL: ");
            String input = scanner.nextLine().trim();
            
            if (input.equalsIgnoreCase("CANCEL")) {
                return null;
            }
            
            try {
                date = LocalDate.parse(input, DATE_FMT);
            } catch (DateTimeParseException e) {
                System.out.println("Invalid date format. Use yyyy-mm-dd.");
            }
        }
        return date;
    }

    public Appointment selectAppointment(ArrayList<Appointment> appointments) {
        if (appointments.isEmpty()) {
            System.out.println("No appointments available to select.");
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
            System.out.printf("%-4s %-12s %-10s %-20s %-20s %-15s %-15s\n", 
                            "No.", "Date", "Time", "Patient", "Doctor", "Type", "Status");
            System.out.println("-".repeat(100));

            for (int i = startIndex; i < endIndex; i++) {
                Appointment apt = currentView.get(i);
                String patientName = truncateString(apt.getPatient().getName(), 18);
                String doctorName = truncateString("Dr. " + apt.getDoctor().getName(), 18);
                String type = truncateString(apt.getAppointmentType(), 13);
                String status = apt.getStatusDisplay();

                System.out.printf("%-4d %-12s %-10s %-20s %-20s %-15s %-15s\n",
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
                    ArrayList<Appointment> filtered = filterAppointments(appointments, searchQuery);
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

    public ArrayList<Appointment> filterAppointments(ArrayList<Appointment> source, String query) {
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
    
    public void displayAppointmentList(ArrayList<Appointment> appointments) {
        if (appointments.isEmpty()) {
            System.out.println("No appointments found.");
            return;
        }
        
        System.out.printf("%-4s %-12s %-15s %-18s %-18s %-12s %-12s %-15s%n",
                         "No.", "Time", "Type", "Patient", "Doctor", "Status", "Duration", "Created By");
        System.out.println("-".repeat(125));
        
        for (int i = 0; i < appointments.size(); i++) {
            Appointment apt = appointments.get(i);
            String createdBy = apt.getStaff() != null ? apt.getStaff().getName() : "N/A";
            System.out.printf("%-4d %-12s %-15s %-18s %-18s %-12s %-12s %-15s%n",
                            i + 1,
                            apt.getFormattedStartTime(),
                            truncateString(apt.getAppointmentType(), 13),
                            truncateString(apt.getPatient().getName(), 16),
                            truncateString("Dr. " + apt.getDoctor().getName(), 16),
                            apt.getStatusDisplay(),
                            apt.getFormattedStartTime() + "-" + apt.getFormattedEndTime(),
                            truncateString(createdBy, 13));
        }
        System.out.println("-".repeat(125));
        System.out.printf("Total appointments: %d%n", appointments.size());
    }

    public void pause() {
        System.out.print("Press Enter to continue...");
        scanner.nextLine();
    }
}
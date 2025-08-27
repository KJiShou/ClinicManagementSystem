package boundary;

import adt.*;
import entity.Appointment;
import entity.Doctor;
import entity.Patient;
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

    public Appointment getAppointmentDetails(Patient patient, Doctor doctor) {
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
            null, // staff can be null for now
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
        
        System.out.printf("%-4s %-12s %-10s %-15s %-20s %-20s %-15s\n", 
                         "No.", "Date", "Time", "Duration", "Patient", "Doctor", "Status");
        System.out.println("-".repeat(100));
        
        for (int i = startIndex; i < endIndex; i++) {
            Appointment apt = appointments.get(i);
            String duration = apt.getFormattedStartTime() + " - " + apt.getFormattedEndTime();
            
            System.out.printf("%-4d %-12s %-10s %-15s %-20s %-20s %-15s\n",
                            i + 1,
                            apt.getFormattedDate(),
                            apt.getFormattedStartTime(),
                            duration,
                            truncateString(apt.getPatient().getName(), 18),
                            truncateString(apt.getDoctor().getName(), 18),
                            apt.getStatus().toString());
                            
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

        while (true) {
            displayAppointmentSchedule(appointments, 1, 1, "", null);
            System.out.printf("Select appointment (1-%d) or 0 to cancel: ", appointments.size());
            
            try {
                int choice = Integer.parseInt(scanner.nextLine().trim());
                if (choice == 0) {
                    return null;
                }
                if (choice >= 1 && choice <= appointments.size()) {
                    return appointments.get(choice - 1);
                }
                System.out.println("Invalid selection.");
            } catch (NumberFormatException e) {
                System.out.println("Please enter a valid number.");
            }
        }
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
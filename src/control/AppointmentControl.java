// Teoh Yong Ming
package control;

import adt.*;
import boundary.AppointmentUI;
import entity.Appointment;
import entity.Doctor;
import entity.Patient;

import java.io.IOException;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.Scanner;
import java.util.UUID;

public class AppointmentControl {
    private static final int PAGE_SIZE = 5;
    private ListInterface<Appointment> appointmentList;
    private ArrayList<Patient> patients;
    private ListInterface<Doctor> doctors;
    private AppointmentUI ui;
    private Scanner scanner;

    public AppointmentControl(ArrayList<Patient> patients, ListInterface<Doctor> doctors, ListInterface<Appointment> appointmentList) {
        this.patients = patients;
        this.doctors = doctors;
        this.appointmentList = appointmentList;
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
        
        // Step 1: Select patient
        Patient selectedPatient = selectPatient();
        if (selectedPatient == null) {
            ui.displayMessage("Operation cancelled.");
            ui.pause();
            return;
        }

        // Step 2: Select doctor
        Doctor selectedDoctor = selectDoctor();
        if (selectedDoctor == null) {
            ui.displayMessage("Operation cancelled.");
            ui.pause();
            return;
        }

        // Step 3: Get appointment details
        Appointment newAppointment = ui.getAppointmentDetails(selectedPatient, selectedDoctor);
        if (newAppointment == null) {
            ui.displayMessage("Appointment creation cancelled.");
            ui.pause();
            return;
        }

        // Step 4: Check for conflicts
        if (hasTimeConflict(newAppointment)) {
            ui.displayError("Time conflict detected! Doctor already has an appointment at this time.");
            System.out.print("Do you want to see available time slots? (Y/N): ");
            String showSlots = scanner.nextLine().trim().toUpperCase();
            if (showSlots.equals("Y")) {
                showAvailableTimeSlots(selectedDoctor, newAppointment.getappointmentDate());
            }
            ui.pause();
            return;
        }

        // Step 5: Add appointment
        appointmentList.add(newAppointment);
        ui.displaySuccess("Appointment scheduled successfully!");
        System.out.println("Appointment Details:");
        System.out.println("- Date: " + newAppointment.getFormattedDate());
        System.out.println("- Time: " + newAppointment.getFormattedStartTime() + " - " + newAppointment.getFormattedEndTime());
        System.out.println("- Patient: " + newAppointment.getPatient().getName());
        System.out.println("- Doctor: " + newAppointment.getDoctor().getName());
        System.out.println("- Type: " + newAppointment.getAppointmentType());
        ui.pause();
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

        Appointment selectedAppointment = ui.selectAppointment(cancellableAppointments);
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

        while (true) {
            System.out.printf("Available Patients (1-%d):\n", patients.size());
            for (int i = 0; i < Math.min(patients.size(), 10); i++) {
                Patient patient = patients.get(i);
                System.out.printf("[%d] %s (IC: %s)\n", 
                                i + 1, 
                                patient.getName(), 
                                patient.getPatientIC() != null ? patient.getPatientIC() : "N/A");
            }
            
            if (patients.size() > 10) {
                System.out.println("... and " + (patients.size() - 10) + " more patients");
            }
            
            System.out.printf("Select patient (1-%d) or 0 to cancel: ", patients.size());
            
            try {
                int choice = Integer.parseInt(scanner.nextLine().trim());
                if (choice == 0) {
                    return null;
                }
                if (choice >= 1 && choice <= patients.size()) {
                    return patients.get(choice - 1);
                }
                System.out.println("Invalid selection.");
            } catch (NumberFormatException e) {
                System.out.println("Please enter a valid number.");
            }
        }
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
        result.sort((a1, a2) -> a1.getStartTime().compareTo(a2.getStartTime()));
        return result;
    }

    private ArrayList<Appointment> getActiveAppointments() {
        ArrayList<Appointment> active = new ArrayList<>();
        for (int i = 0; i < appointmentList.size(); i++) {
            Appointment apt = appointmentList.get(i);
            if (apt.getStatus() == Appointment.Status.SCHEDULED || 
                apt.getStatus() == Appointment.Status.CONFIRMED) {
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
            if ((apt.getStatus() == Appointment.Status.SCHEDULED || 
                 apt.getStatus() == Appointment.Status.CONFIRMED) &&
                !apt.getappointmentDate().isBefore(today)) {
                cancellable.add(apt);
            }
        }
        return cancellable;
    }

    private void viewDailySchedule() {
        LocalDate selectedDate = ui.getDateInput("Enter date to view");
        if (selectedDate == null) return;

        ArrayList<Appointment> dayAppointments = getAppointmentsByDate(selectedDate);
        displayAppointmentsPaginated(dayAppointments, "Daily Schedule - " + selectedDate);
    }

    private void viewWeeklySchedule() {
        LocalDate startDate = ui.getDateInput("Enter week start date (Monday)");
        if (startDate == null) return;

        // Adjust to Monday if necessary
        startDate = startDate.minusDays(startDate.getDayOfWeek().getValue() - 1);
        LocalDate endDate = startDate.plusDays(6);

        ArrayList<Appointment> weekAppointments = getAppointmentsByDateRange(startDate, endDate);
        displayAppointmentsPaginated(weekAppointments, "Weekly Schedule - " + startDate + " to " + endDate);
    }

    private void viewDoctorSchedule() {
        Doctor selectedDoctor = selectDoctor();
        if (selectedDoctor == null) return;

        LocalDate selectedDate = ui.getDateInput("Enter date to view");
        if (selectedDate == null) return;

        ArrayList<Appointment> doctorAppointments = getDoctorAppointments(selectedDoctor.getUserID(), selectedDate);
        displayAppointmentsPaginated(doctorAppointments, "Doctor Schedule - " + selectedDoctor.getName() + " - " + selectedDate);
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
        result.sort((a1, a2) -> a1.getStartTime().compareTo(a2.getStartTime()));
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
        result.sort((a1, a2) -> {
            int dateCompare = a1.getappointmentDate().compareTo(a2.getappointmentDate());
            return dateCompare != 0 ? dateCompare : a1.getStartTime().compareTo(a2.getStartTime());
        });
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

    private void updateAppointmentTime(Appointment appointment) {
        System.out.print("Enter new start time (HH:mm): ");
        String timeInput = scanner.nextLine().trim();
        try {
            LocalTime newStartTime = LocalTime.parse(timeInput);
            if (newStartTime.isBefore(LocalTime.of(9, 0)) || newStartTime.isAfter(LocalTime.of(17, 30))) {
                ui.displayError("Please schedule within business hours (09:00 - 17:30).");
                return;
            }
            
            appointment.setStartTime(newStartTime);
            appointment.setEndTime(newStartTime.plusMinutes(30)); // Default 30 min
            ui.displaySuccess("Appointment time updated to: " + newStartTime);
        } catch (Exception e) {
            ui.displayError("Invalid time format. Use HH:mm.");
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

    private void updateAppointmentStatus(Appointment appointment) {
        System.out.println("Select new status:");
        System.out.println("1. SCHEDULED");
        System.out.println("2. CONFIRMED");
        System.out.println("3. CANCELLED");
        System.out.println("4. COMPLETED");
        System.out.print("Choice: ");
        
        try {
            int choice = Integer.parseInt(scanner.nextLine().trim());
            Appointment.Status newStatus = null;
            switch (choice) {
                case 1: newStatus = Appointment.Status.SCHEDULED; break;
                case 2: newStatus = Appointment.Status.CONFIRMED; break;
                case 3: newStatus = Appointment.Status.CANCELLED; break;
                case 4: newStatus = Appointment.Status.COMPLETED; break;
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
}
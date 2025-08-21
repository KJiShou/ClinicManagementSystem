package boundary;

import adt.QueueInterface;
import adt.LinkedQueue;
import adt.ArrayList;
import entity.Patient;
import entity.Doctor;
import entity.User;
import utility.MessageUI;

import entity.Consultation;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Scanner;
import java.util.UUID;
import java.time.LocalDate;
import java.time.LocalTime;

public class ConsultationUI {
    private QueueInterface<String> choiceQueue;
    private MessageUI UI;
    private int pageSize;
    private static final SimpleDateFormat DATE_FMT = new SimpleDateFormat("yyyy-MM-dd");
    private Scanner scanner;

    public ConsultationUI() {
        choiceQueue = new LinkedQueue<String>();
        UI = new MessageUI();
        pageSize = 5;
        scanner = new Scanner(System.in);
    }

    public Integer mainMenu() throws IOException {
        choiceQueue.enqueue("View Consultation");
        choiceQueue.enqueue("Add Consultation");
        choiceQueue.enqueue("Update Consultation");
        choiceQueue.enqueue("Delete Consultation");

        return UI.mainUI("Welcome to Consultation Menu", choiceQueue);
    }

    public Integer viewConsultationMenu() throws IOException {
        choiceQueue.enqueue("View All Consultation");
        choiceQueue.enqueue("Sort by Status");
        choiceQueue.enqueue("Sort by Date");
        choiceQueue.enqueue("Sort by Payment");

        return UI.mainUI("View Consultation Selection", choiceQueue);
    }

    public void displayConsultationList(ArrayList<Consultation> consultation, int totalItems, int currentPage, int totalPages) {
        int start = (currentPage - 1) * pageSize;
        int end = Math.min(totalItems, start + pageSize);
        System.out.println("\n\n\n\n\n\n\n\n\n\n");

        System.out.printf("Page %d/%d\n", currentPage, totalPages);
        System.out.println("+-----+--------------------------------+--------------------------------+------------+-------------+-----------+");
        System.out.printf("| %-3s | %-30s | %-30s | %-10s | %-11s | %-9s |\n", "No.", "Patient Name", "Doctor Name", "Date", "Status", "Payment");
        System.out.println("+-----+--------------------------------+--------------------------------+------------+-------------+-----------+");

        for (int i = start; i < end; i++) {
            Consultation cons = consultation.get(i);

            System.out.printf("| %-3d | %-30s | %-30s | %-10s | %-11s | %-9s |\n",
                    i + 1,
                    cons.getPatient().getName(),
                    cons.getDoctor().getName(),
                    cons.getConsultatonDate(),
                    cons.status.toString(),
                    cons.getTotalPayment());
        }
        System.out.println("+-----+--------------------------------+--------------------------------+------------+-------------+-----------+\n\n");
    }
    
    public Consultation addConsultation() throws IOException {
        System.out.println("\n=== ADD NEW CONSULTATION ===");

        UUID id = UUID.randomUUID();

        //placeholder
        Patient patient = null;
        Doctor doctor = null;

        Date date;
        do {
            try {
                System.out.print("Consultation Date (YYYY-MM-DD): ");
                date = DATE_FMT.parse(scanner.nextLine().trim());

            } catch (Exception e) {
                System.out.println("Invalid date format, try again.\n");
                date = new Date();
            }
        } while (date == null);
        LocalDate consultationDate = LocalDate.parse(date.toString());

        System.out.print("Enter Notes: ");
        String notes = scanner.nextLine();

        LocalTime startTime;
        do {
            try {
                System.out.print("Enter Start Time (HH:MM, 24H format): ");
                startTime = LocalTime.parse(scanner.nextLine());
            } catch (Exception e) {
                System.out.println("Invalid time format, try again.\n");
                startTime = null;
            }
        } while (startTime == null);

        LocalTime endTime;
        do {
            try {
                System.out.print("Enter Start Time (HH:MM, 24H format): ");
                endTime = LocalTime.parse(scanner.nextLine());
            } catch (Exception e) {
                System.out.println("Invalid time format, try again.\n");
                endTime = null;
            }
        } while (endTime == null);

        float totalPayment = 0;
        do {
            try {
                System.out.print("Enter Total Payment: RM");
                totalPayment = scanner.nextFloat();
            } catch (Exception e) {
                System.out.println("Invalid amount, try again.\n");
            }
        } while (totalPayment <= 0);

        return new Consultation(
                id, patient, doctor, consultationDate, Consultation.Status.BILLING,
                notes, startTime, endTime, totalPayment
        );
    }

    public void displayConsultationDetails(Consultation consultation) {
        System.out.println("\n=== CONSULTATION DETAILS ===");
        System.out.println("+--------------------------------+--------------------------------+");
        System.out.printf("| %-30s | %-30s |\n", "Field", "Value");
        System.out.println("+--------------------------------+--------------------------------+");
        System.out.printf("| %-30s | %-30s |\n", "Consultation ID", consultation.getId());
        System.out.printf("| %-30s | %-30s |\n", "Patient ID", consultation.getPatientId());
        System.out.printf("| %-30s | %-30s |\n", "Doctor ID", consultation.getDoctorId());
        System.out.printf("| %-30s | %-30s |\n", "Date", consultation.getConsultatonDate());
        System.out.printf("| %-30s | %-30s |\n", "Status", consultation.status.toString());
        System.out.printf("| %-30s | %-30s |\n", "Start Time", consultation.getStartTime());
        System.out.printf("| %-30s | %-30s |\n", "End Time", consultation.getEndTime());
        System.out.printf("| %-30s | %-30s |\n", "Total Payment", String.format("%.2f", consultation.getTotalPayment()));
        System.out.printf("| %-30s | %-30s |\n", "Notes", consultation.getNotes());
        System.out.println("+--------------------------------+--------------------------------+");
    }
}

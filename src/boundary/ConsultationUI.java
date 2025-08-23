package boundary;

import adt.HashedDictionary;
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
import java.time.Duration;
import java.time.format.DateTimeFormatter;
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

    public ConsultationUI(Scanner scanner) {
        choiceQueue = new LinkedQueue<String>();
        UI = new MessageUI();
        pageSize = 5;
        this.scanner = scanner;
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

    private static final DateTimeFormatter ARRIVAL_FMT = DateTimeFormatter.ofPattern("HH:mm");

    public void displayConsultationSectionsWithArrival(
            ArrayList<Consultation> waitingList, int waitingTotal,
            ArrayList<Consultation> inProgressList, int inProgressTotal,
            ArrayList<Consultation> billingList, int billingTotal,
            ArrayList<Consultation> completedList, int completedTotal,
            HashedDictionary<Consultation, Integer> arrivalIndex // Consultation -> index number
    ) {
        System.out.println("\n\n\n\n\n\n\n\n\n\n");
        printSection("WAITING", waitingTotal, waitingList, arrivalIndex, true);
        printSection("IN PROGRESS", inProgressTotal, inProgressList, arrivalIndex, false);
        printSection("BILLING", billingTotal, billingList, arrivalIndex, false);
        printSection("COMPLETED", completedTotal, completedList, arrivalIndex, false);
    }

    private void printSection(
            String title, int totalCount, ArrayList<Consultation> rows,
            HashedDictionary<Consultation, Integer> arrivalIndex,
            boolean showWaitingTime // only true for WAITING section
    ) {
        String header = String.format("%s  ·  %d", title, totalCount);
        String bar = repeat('-', Math.max(6, header.length() + 4));

        System.out.println(bar);
        System.out.println("  " + header);
        System.out.println(bar);

        if (rows == null || rows.isEmpty()) {
            System.out.println("  No patients.");
            System.out.println();
            return;
        }

        // Columns: No. (arrival-based), Patient, Doctor, Arrival, (Waiting/Status), Payment
        System.out.println("+-----+----------------------+----------------------+----------------------+-----------------+-----------+");
        System.out.printf("| %-3s | %-20s | %-20s | %-20s | %-15s | %-9s |%n",
                "No.", "Patient Name", "Doctor Name", "Arrival", (showWaitingTime ? "Waiting Time" : "Status"), "Payment");
        System.out.println("+-----+----------------------+----------------------+----------------------+-----------------+-----------+");

        for (int i = 0; i < rows.size(); i++) {
            Consultation cons = rows.get(i);
            Integer num = arrivalIndex.getValue(cons);
            String arrivalStr = formatArrival(cons.getStartTime()); // adjust if method name differs
            String extra = showWaitingTime ? formatWaiting(cons.getStartTime()) : cons.status == Consultation.Status.IN_PROGRESS ? "IN PROGRESS":cons.status.toString();

            // Print the payment only for BILLING and COMPLETED statuses
            String payment = (cons.status == Consultation.Status.COMPLETED)
                    ? String.format("%.2f", cons.getTotalPayment())
                    : "-";

            System.out.printf("| %-3s | %-20s | %-20s | %-20s | %-15s | %-9s |%n",
                    num == null ? "-" : num.toString(),
                    cons.getPatient().getName(),
                    cons.getDoctor().getName(),
                    arrivalStr,
                    extra,
                    payment);
        }
        System.out.println("+-----+----------------------+----------------------+----------------------+-----------------+-----------+\n");
    }

    private String formatArrival(LocalTime t) {
        if (t == null) return "-";
        return t.format(ARRIVAL_FMT);
    }

    private String formatWaiting(LocalTime arrival) {
        if (arrival == null) return "-";
        Duration d = Duration.between(arrival, LocalTime.now());
        if (d.isNegative()) d = Duration.ZERO;

        long hours = d.toHours();
        long mins  = d.toMinutes() % 60;
        if (hours > 0) return hours + "h " + mins + "m";
        return mins + "m";
    }

    private String repeat(char c, int n) {
        StringBuilder sb = new StringBuilder(n);
        for (int i = 0; i < n; i++) sb.append(c);
        return sb.toString();
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
                notes, startTime, endTime, totalPayment, "medical Treatment",null
        );
    }

    public void displayConsultationDetails(Consultation consultation) {
        if (consultation == null) {
            System.out.println("Consultation details not available.");
            return;
        }
        System.out.println("\n=== CONSULTATION DETAILS ===");
        System.out.println("+--------------------------------+--------------------------------+");
        System.out.printf("| %-30s | %-30s |\n", "Field", "Value");
        System.out.println("+--------------------------------+--------------------------------+");
        String patientName = consultation.getPatient() != null ? consultation.getPatient().getName() : "N/A";
        String doctorName = consultation.getDoctor() != null ? consultation.getDoctor().getName() : "N/A";
        System.out.printf("| %-30s | %-30s |\n", "Patient Name", patientName);
        System.out.printf("| %-30s | %-30s |\n", "Doctor Name", doctorName);
        System.out.printf("| %-30s | %-30s |\n", "Date", consultation.getConsultatonDate().format(DateTimeFormatter.ISO_LOCAL_DATE));
        System.out.printf("| %-30s | %-30s |\n", "Status", consultation.status == Consultation.Status.IN_PROGRESS ? "IN PROGRESS":consultation.status.toString());
        System.out.printf("| %-30s | %-30s |\n", "Start Time", consultation.getStartTime() != null ? consultation.getStartTime().format(ARRIVAL_FMT) : "-");
        System.out.printf("| %-30s | %-30s |\n", "End Time", consultation.getEndTime() != null ? consultation.getEndTime().format(ARRIVAL_FMT) : "-");
        System.out.printf("| %-30s | %-30s |\n", "Medical Treatment", consultation.getMedicalTreatment());
        System.out.printf("| %-30s | %-30s |\n", "Total Payment", String.format("RM %.2f", consultation.getTotalPayment()));
        String notes = consultation.getNotes();
        if (notes != null) {
            notes = notes.trim();
        }
        if (notes != null && !notes.isEmpty()) {
            String[] noteLines = notes.split("\\R");
            int currentNoteLineIdx = 0;
            String firstContentPart = "";
            if (noteLines.length > 0 && !noteLines[0].isEmpty()) {
                firstContentPart = noteLines[0].substring(0, Math.min(noteLines[0].length(), 30));
                if (noteLines[0].length() > 30) {
                    System.out.printf("| %-30s | %-30s |\n", "Notes", firstContentPart);
                    currentNoteLineIdx = 0;
                    String remainingFirstLine = noteLines[0].substring(30);
                    for(int j = 0; j < remainingFirstLine.length(); j += 30) {
                        System.out.printf("| %-30s | %-30s |\n", "", remainingFirstLine.substring(j, Math.min(j + 30, remainingFirstLine.length())));
                    }
                    currentNoteLineIdx = 1;
                } else {
                    System.out.printf("| %-30s | %-30s |\n", "Notes", firstContentPart);
                    currentNoteLineIdx = 1;
                }
            } else {
                System.out.printf("| %-30s | %-30s |\n", "Notes", "");
                currentNoteLineIdx = 1;
            }
            for (int i = currentNoteLineIdx; i < noteLines.length; i++) {
                String currentLine = noteLines[i];
                for(int j = 0; j < currentLine.length(); j += 30) {
                    System.out.printf("| %-30s | %-30s |\n", "", currentLine.substring(j, Math.min(j + 30, currentLine.length())));
                }
            }
        } else {
            System.out.printf("| %-30s | %-30s |\n", "Notes", "No notes provided.");
        }
        System.out.println("+--------------------------------+--------------------------------+");
    }
}

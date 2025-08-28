package boundary;

import adt.*;
import entity.Patient;
import entity.Doctor;
import entity.User;
import entity.pharmacyManagement.LabTest;
import entity.pharmacyManagement.Medicine;
import entity.pharmacyManagement.Prescription;
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
        choiceQueue.enqueue("Consultation Revenue Report");
        choiceQueue.enqueue("Consultation Duration Report");

        return UI.mainUI("Welcome to Consultation Menu", choiceQueue);
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
        LocalDate consultationDate = LocalDate.now();

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
                notes, startTime, endTime, totalPayment, "medical Treatment",null, null
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

        // Display Notes
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

        // Display Prescription Count
        ListInterface<Prescription> prescriptions = consultation.getPrescription();
        int prescriptionCount = (prescriptions != null) ? prescriptions.size() : 0;
        System.out.printf("| %-30s | %-30s |\n", "Number of Prescriptions", String.valueOf(prescriptionCount));

        // Display Lab Test Count
        ArrayList<LabTest> labTests = consultation.getLabTests();
        int labTestCount = (labTests != null) ? labTests.size() : 0;
        System.out.printf("| %-30s | %-30s |\n", "Number of Lab Tests", String.valueOf(labTestCount));

        System.out.println("+--------------------------------+--------------------------------+");

        // Display Prescription Details
        if (prescriptions != null && !prescriptions.isEmpty()) {
            System.out.println("\n=== PRESCRIPTION DETAILS ===");

            for (int i = 0; i < prescriptions.size(); i++) {
                Prescription prescription = prescriptions.get(i);
                System.out.println("+--------------------------------+--------------------------------+");
                System.out.printf("| %-62s |\n", "PRESCRIPTION " + (i + 1));
                System.out.println("+--------------------------------+--------------------------------+");

                // Medicine details
                Medicine medicine = prescription.getMedicine();
                String medicineName = (medicine != null) ? medicine.getName() : "N/A";
                String medicineBrand = (medicine != null) ? medicine.getBrand() : "N/A";
                String medicineUnit = (medicine != null) ? medicine.getUnit() : "unit";
                int prescribedQuantity = (medicine != null) ? medicine.getQuantity() : 0;

                System.out.printf("| %-30s | %-30s |\n", "Medicine Name", medicineName);
                System.out.printf("| %-30s | %-30s |\n", "Brand", medicineBrand);
                System.out.printf("| %-30s | %-30s |\n", "Prescribed Quantity", prescribedQuantity + " " + medicineUnit);

                // Prescription details
                System.out.printf("| %-30s | %-30.2f |\n", "Dosage per Time", prescription.getDosagePerTime());
                System.out.printf("| %-30s | %-30s |\n", "Times per Day", String.valueOf(prescription.getTimesPerDay()));
                System.out.printf("| %-30s | %-30s |\n", "Duration (Days)", String.valueOf(prescription.getDays()));
                System.out.printf("| %-30s | RM %-27.2f |\n", "Price", prescription.getMedicine().getPrice() * prescription.getMedicine().getQuantity());

                // Description with proper text wrapping
                String description = prescription.getDescription();
                if (description != null && !description.trim().isEmpty()) {
                    String[] descLines = description.trim().split("\\R");
                    boolean firstLine = true;

                    for (String line : descLines) {
                        if (line.length() <= 30) {
                            System.out.printf("| %-30s | %-30s |\n", firstLine ? "Description" : "", line);
                            firstLine = false;
                        } else {
                            // Split long lines into 30-character chunks
                            for (int j = 0; j < line.length(); j += 30) {
                                String chunk = line.substring(j, Math.min(j + 30, line.length()));
                                System.out.printf("| %-30s | %-30s |\n", firstLine ? "Description" : "", chunk);
                                firstLine = false;
                            }
                        }
                    }
                } else {
                    System.out.printf("| %-30s | %-30s |\n", "Description", "No description provided.");
                }
            }
            System.out.println("+--------------------------------+--------------------------------+");

            // Display prescription summary
            System.out.println("\n=== PRESCRIPTION SUMMARY ===");
            float totalPrescriptionValue = 0.0f;
            int totalMedicinesCount = prescriptions.size();

            for (int i = 0; i < prescriptions.size(); i++) {
                Prescription prescription = prescriptions.get(i);
                Medicine medicine = prescription.getMedicine();
                if (medicine != null) {
                    float medicineValue = (float)medicine.getPrice() * medicine.getQuantity();
                    totalPrescriptionValue += medicineValue;
                }
            }

            System.out.println("+--------------------------------+--------------------------------+");
            System.out.printf("| %-30s | %-30s |\n", "Total Medicines Prescribed", String.valueOf(totalMedicinesCount));
            System.out.printf("| %-30s | %-30s |\n", "Total Prescription Value", String.format("RM %.2f", totalPrescriptionValue));
            System.out.println("+--------------------------------+--------------------------------+");
        } else {
            System.out.println("\n=== PRESCRIPTION DETAILS ===");
            System.out.println("No prescriptions found for this consultation.");
        }

        // Display Lab Test Details
        if (labTests != null && !labTests.isEmpty()) {
            System.out.println("\n=== LAB TEST DETAILS ===");

            for (int i = 0; i < labTests.size(); i++) {
                LabTest labTest = labTests.get(i);
                System.out.println("+--------------------------------+--------------------------------+");
                System.out.printf("| %-62s |\n", "LAB TEST " + (i + 1));
                System.out.println("+--------------------------------+--------------------------------+");

                // Lab Test basic details
                String testName = (labTest.getName() != null) ? labTest.getName() : "N/A";
                String testCode = (labTest.getCode() != null) ? labTest.getCode() : "N/A";
                String referringLab = (labTest.getReferringLab() != null) ? labTest.getReferringLab().getName() : "N/A";

                System.out.printf("| %-30s | %-30s |\n", "Test Name", testName);
                System.out.printf("| %-30s | %-30s |\n", "Test Code", testCode);
                System.out.printf("| %-30s | %-30s |\n", "Referring Lab", referringLab);
                System.out.printf("| %-30s | RM %-27.2f |\n", "Price", labTest.getPrice());
                System.out.printf("| %-30s | %-30s |\n", "Fasting Required", labTest.isFastingRequired() ? "Yes" : "No");

                // Blood tubes required
                String bloodTubes = labTest.getBloodTubes();
                if (bloodTubes != null && !bloodTubes.trim().isEmpty()) {
                    System.out.printf("| %-30s | %-30s |\n", "Blood Tubes Required", bloodTubes);
                } else {
                    System.out.printf("| %-30s | %-30s |\n", "Blood Tubes Required", "Not specified");
                }

                // Description with proper text wrapping
                String description = labTest.getDescription();
                if (description != null && !description.trim().isEmpty()) {
                    String[] descLines = description.trim().split("\\R");
                    boolean firstLine = true;

                    for (String line : descLines) {
                        if (line.length() <= 30) {
                            System.out.printf("| %-30s | %-30s |\n", firstLine ? "Description" : "", line);
                            firstLine = false;
                        } else {
                            // Split long lines into 30-character chunks
                            for (int j = 0; j < line.length(); j += 30) {
                                String chunk = line.substring(j, Math.min(j + 30, line.length()));
                                System.out.printf("| %-30s | %-30s |\n", firstLine ? "Description" : "", chunk);
                                firstLine = false;
                            }
                        }
                    }
                } else {
                    System.out.printf("| %-30s | %-30s |\n", "Description", "No description provided.");
                }

                // Patient precautions with proper text wrapping
                String precautions = labTest.getPatientPrecautions();
                if (precautions != null && !precautions.trim().isEmpty()) {
                    String[] precautionLines = precautions.trim().split("\\R");
                    boolean firstLine = true;

                    for (String line : precautionLines) {
                        if (line.length() <= 30) {
                            System.out.printf("| %-30s | %-30s |\n", firstLine ? "Patient Precautions" : "", line);
                            firstLine = false;
                        } else {
                            // Split long lines into 30-character chunks
                            for (int j = 0; j < line.length(); j += 30) {
                                String chunk = line.substring(j, Math.min(j + 30, line.length()));
                                System.out.printf("| %-30s | %-30s |\n", firstLine ? "Patient Precautions" : "", chunk);
                                firstLine = false;
                            }
                        }
                    }
                } else {
                    System.out.printf("| %-30s | %-30s |\n", "Patient Precautions", "None specified.");
                }
            }
            System.out.println("+--------------------------------+--------------------------------+");

            // Display lab test summary
            System.out.println("\n=== LAB TEST SUMMARY ===");
            double totalLabTestValue = 0.0;
            int totalLabTestCount = labTests.size();

            for (int i = 0; i < labTests.size(); i++) {
                LabTest labTest = labTests.get(i);
                totalLabTestValue += labTest.getPrice();
            }

            System.out.println("+--------------------------------+--------------------------------+");
            System.out.printf("| %-30s | %-30s |\n", "Total Lab Tests Ordered", String.valueOf(totalLabTestCount));
            System.out.printf("| %-30s | %-30s |\n", "Total Lab Test Value", String.format("RM %.2f", totalLabTestValue));
            System.out.println("+--------------------------------+--------------------------------+");
        } else {
            System.out.println("\n=== LAB TEST DETAILS ===");
            System.out.println("No lab tests found for this consultation.");
        }
    }
}

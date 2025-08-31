// Teoh Yong Ming
package boundary;

import adt.*;
import entity.Patient;
import entity.pharmacyManagement.LabTest;
import entity.pharmacyManagement.Medicine;
import entity.pharmacyManagement.Prescription;
import utility.MessageUI;

import entity.Consultation;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.time.Duration;
import java.time.format.DateTimeFormatter;
import java.util.Scanner;
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
        choiceQueue.enqueue("Remove Consultation");
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
        String header = String.format("%s    %d", title, totalCount);
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

    public void displayConsultationDetails(Consultation consultation) {
        if (consultation == null) {
            System.out.println("Consultation details not available.");
            return;
        }
        System.out.println("\n=== CONSULTATION DETAILS ===");
        System.out.println("+--------------------------------+------------------------------------------+");
        System.out.printf("| %-30s | %-40s |\n", "Field", "Value");
        System.out.println("+--------------------------------+------------------------------------------+");
        String patientName = consultation.getPatient() != null ? consultation.getPatient().getName() : "N/A";
        String doctorName = consultation.getDoctor() != null ? consultation.getDoctor().getName() : "N/A";
        System.out.printf("| %-30s | %-40s |\n", "Patient Name", patientName);
        System.out.printf("| %-30s | %-40s |\n", "Doctor Name", doctorName);
        System.out.printf("| %-30s | %-40s |\n", "Date", consultation.getConsultatonDate().format(DateTimeFormatter.ISO_LOCAL_DATE));
        System.out.printf("| %-30s | %-40s |\n", "Status", consultation.status == Consultation.Status.IN_PROGRESS ? "IN PROGRESS":consultation.status.toString());
        System.out.printf("| %-30s | %-40s |\n", "Start Time", consultation.getStartTime() != null ? consultation.getStartTime().format(ARRIVAL_FMT) : "-");
        System.out.printf("| %-30s | %-40s |\n", "End Time", consultation.getEndTime() != null ? consultation.getEndTime().format(ARRIVAL_FMT) : "-");
        System.out.printf("| %-30s | %-40s |\n", "Medical Treatment", consultation.getMedicalTreatment());
        System.out.printf("| %-30s | %-40s |\n", "Total Payment", String.format("RM %.2f", consultation.getTotalPayment()));

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
                firstContentPart = noteLines[0].substring(0, Math.min(noteLines[0].length(), 40));
                if (noteLines[0].length() > 40) {
                    System.out.printf("| %-30s | %-40s |\n", "Notes", firstContentPart);
                    currentNoteLineIdx = 0;
                    String remainingFirstLine = noteLines[0].substring(40);
                    for(int j = 0; j < remainingFirstLine.length(); j += 40) {
                        System.out.printf("| %-30s | %-40s |\n", "", remainingFirstLine.substring(j, Math.min(j + 40, remainingFirstLine.length())));
                    }
                    currentNoteLineIdx = 1;
                } else {
                    System.out.printf("| %-30s | %-40s |\n", "Notes", firstContentPart);
                    currentNoteLineIdx = 1;
                }
            } else {
                System.out.printf("| %-30s | %-40s |\n", "Notes", "");
                currentNoteLineIdx = 1;
            }
            for (int i = currentNoteLineIdx; i < noteLines.length; i++) {
                String currentLine = noteLines[i];
                for(int j = 0; j < currentLine.length(); j += 40) {
                    System.out.printf("| %-30s | %-40s |\n", "", currentLine.substring(j, Math.min(j + 40, currentLine.length())));
                }
            }
        } else {
            System.out.printf("| %-30s | %-40s |\n", "Notes", "No notes provided.");
        }

        // Display Prescription Count
        ListInterface<Prescription> prescriptions = consultation.getPrescription();
        int prescriptionCount = (prescriptions != null) ? prescriptions.size() : 0;
        System.out.printf("| %-30s | %-40s |\n", "Number of Prescriptions", String.valueOf(prescriptionCount));

        // Display Lab Test Count
        ArrayList<LabTest> labTests = consultation.getLabTests();
        int labTestCount = (labTests != null) ? labTests.size() : 0;
        System.out.printf("| %-30s | %-40s |\n", "Number of Lab Tests", String.valueOf(labTestCount));

        System.out.println("+--------------------------------+------------------------------------------+");

        // Display Prescription Details
        if (prescriptions != null && !prescriptions.isEmpty()) {
            System.out.println("\n=== PRESCRIPTION DETAILS ===");

            for (int i = 0; i < prescriptions.size(); i++) {
                Prescription prescription = prescriptions.get(i);
                System.out.println("+--------------------------------+------------------------------------------+");
                System.out.printf("| %-73s |\n", "PRESCRIPTION " + (i + 1));
                System.out.println("+--------------------------------+------------------------------------------+");

                // Medicine details
                Medicine medicine = prescription.getMedicine();
                String medicineName = (medicine != null) ? medicine.getName() : "N/A";
                String medicineBrand = (medicine != null) ? medicine.getBrand() : "N/A";
                String medicineUnit = (medicine != null) ? medicine.getUnit() : "unit";
                int prescribedQuantity = (medicine != null) ? medicine.getQuantity() : 0;

                System.out.printf("| %-30s | %-40s |\n", "Medicine Name", medicineName);
                System.out.printf("| %-30s | %-40s |\n", "Brand", medicineBrand);
                System.out.printf("| %-30s | %-40s |\n", "Prescribed Quantity", prescribedQuantity + " " + medicineUnit);

                // Prescription details
                System.out.printf("| %-30s | %-40.2f |\n", "Dosage per Time", prescription.getDosagePerTime());
                System.out.printf("| %-30s | %-40s |\n", "Times per Day", String.valueOf(prescription.getTimesPerDay()));
                System.out.printf("| %-30s | %-40s |\n", "Duration (Days)", String.valueOf(prescription.getDays()));
                System.out.printf("| %-30s | RM %-37.2f |\n", "Price", prescription.getMedicine().getPrice() * prescription.getMedicine().getQuantity());

                // Description with proper text wrapping
                String description = prescription.getDescription();
                if (description != null && !description.trim().isEmpty()) {
                    String[] descLines = description.trim().split("\\R");
                    boolean firstLine = true;

                    for (String line : descLines) {
                        if (line.length() <= 40) {
                            System.out.printf("| %-30s | %-40s |\n", firstLine ? "Description" : "", line);
                            firstLine = false;
                        } else {
                            // Split long lines into 40-character chunks
                            for (int j = 0; j < line.length(); j += 40) {
                                String chunk = line.substring(j, Math.min(j + 40, line.length()));
                                System.out.printf("| %-30s | %-40s |\n", firstLine ? "Description" : "", chunk);
                                firstLine = false;
                            }
                        }
                    }
                } else {
                    System.out.printf("| %-30s | %-40s |\n", "Description", "No description provided.");
                }
            }
            System.out.println("+--------------------------------+------------------------------------------+");

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

            System.out.println("+--------------------------------+------------------------------------------+");
            System.out.printf("| %-30s | %-40s |\n", "Total Medicines Prescribed", String.valueOf(totalMedicinesCount));
            System.out.printf("| %-30s | %-40s |\n", "Total Prescription Value", String.format("RM %.2f", totalPrescriptionValue));
            System.out.println("+--------------------------------+------------------------------------------+");
        } else {
            System.out.println("\n=== PRESCRIPTION DETAILS ===");
            System.out.println("No prescriptions found for this consultation.");
        }

        // Display Lab Test Details
        if (labTests != null && !labTests.isEmpty()) {
            System.out.println("\n=== LAB TEST DETAILS ===");

            for (int i = 0; i < labTests.size(); i++) {
                LabTest labTest = labTests.get(i);
                System.out.println("+--------------------------------+------------------------------------------+");
                System.out.printf("| %-73s |\n", "LAB TEST " + (i + 1));
                System.out.println("+--------------------------------+------------------------------------------+");

                // Lab Test basic details
                String testName = (labTest.getName() != null) ? labTest.getName() : "N/A";
                String testCode = (labTest.getCode() != null) ? labTest.getCode() : "N/A";
                String referringLab = (labTest.getReferringLab() != null) ? labTest.getReferringLab().getName() : "N/A";

                System.out.printf("| %-30s | %-40s |\n", "Test Name", testName);
                System.out.printf("| %-30s | %-40s |\n", "Test Code", testCode);
                System.out.printf("| %-30s | %-40s |\n", "Referring Lab", referringLab);
                System.out.printf("| %-30s | RM %-37.2f |\n", "Price", labTest.getPrice());
                System.out.printf("| %-30s | %-40s |\n", "Fasting Required", labTest.isFastingRequired() ? "Yes" : "No");

                // Blood tubes required
                String bloodTubes = labTest.getBloodTubes();
                if (bloodTubes != null && !bloodTubes.trim().isEmpty()) {
                    System.out.printf("| %-30s | %-40s |\n", "Blood Tubes Required", bloodTubes);
                } else {
                    System.out.printf("| %-30s | %-40s |\n", "Blood Tubes Required", "Not specified");
                }

                // Description with proper text wrapping
                String description = labTest.getDescription();
                if (description != null && !description.trim().isEmpty()) {
                    String[] descLines = description.trim().split("\\R");
                    boolean firstLine = true;

                    for (String line : descLines) {
                        if (line.length() <= 40) {
                            System.out.printf("| %-30s | %-40s |\n", firstLine ? "Description" : "", line);
                            firstLine = false;
                        } else {
                            // Split long lines into 40-character chunks
                            for (int j = 0; j < line.length(); j += 40) {
                                String chunk = line.substring(j, Math.min(j + 40, line.length()));
                                System.out.printf("| %-30s | %-40s |\n", firstLine ? "Description" : "", chunk);
                                firstLine = false;
                            }
                        }
                    }
                } else {
                    System.out.printf("| %-30s | %-40s |\n", "Description", "No description provided.");
                }

                // Patient precautions with proper text wrapping
                String precautions = labTest.getPatientPrecautions();
                if (precautions != null && !precautions.trim().isEmpty()) {
                    String[] precautionLines = precautions.trim().split("\\R");
                    boolean firstLine = true;

                    for (String line : precautionLines) {
                        if (line.length() <= 40) {
                            System.out.printf("| %-30s | %-40s |\n", firstLine ? "Patient Precautions" : "", line);
                            firstLine = false;
                        } else {
                            // Split long lines into 40-character chunks
                            for (int j = 0; j < line.length(); j += 40) {
                                String chunk = line.substring(j, Math.min(j + 40, line.length()));
                                System.out.printf("| %-30s | %-40s |\n", firstLine ? "Patient Precautions" : "", chunk);
                                firstLine = false;
                            }
                        }
                    }
                } else {
                    System.out.printf("| %-30s | %-40s |\n", "Patient Precautions", "None specified.");
                }
            }
            System.out.println("+--------------------------------+------------------------------------------+");

            // Display lab test summary
            System.out.println("\n=== LAB TEST SUMMARY ===");
            double totalLabTestValue = 0.0;
            int totalLabTestCount = labTests.size();

            for (int i = 0; i < labTests.size(); i++) {
                LabTest labTest = labTests.get(i);
                totalLabTestValue += labTest.getPrice();
            }

            System.out.println("+--------------------------------+------------------------------------------+");
            System.out.printf("| %-30s | %-40s |\n", "Total Lab Tests Ordered", String.valueOf(totalLabTestCount));
            System.out.printf("| %-30s | %-40s |\n", "Total Lab Test Value", String.format("RM %.2f", totalLabTestValue));
            System.out.println("+--------------------------------+------------------------------------------+");
        } else {
            System.out.println("\n=== LAB TEST DETAILS ===");
            System.out.println("No lab tests found for this consultation.");
        }
    }

    public Consultation selectConsultation(ListInterface<Consultation> consultations) {
        if (consultations.isEmpty()) {
            System.out.println("No consultations available to select.");
            return null;
        }

        // Convert to ArrayList for easier handling
        ArrayList<Consultation> consultationList = new ArrayList<>();
        for (int i = 0; i < consultations.size(); i++) {
            consultationList.add(consultations.get(i));
        }

        ArrayList<Consultation> currentView = new ArrayList<>(consultationList);
        int currentPage = 1;
        String searchQuery = "";

        while (true) {
            int totalItems = currentView.size();
            int totalPages = (totalItems + pageSize - 1) / pageSize;
            if (totalPages == 0) totalPages = 1;

            displayConsultationSelectionList(currentView, totalItems, currentPage, totalPages, searchQuery);

            System.out.println("Press: [A] Prev | [D] Next | [S] Search | [R] Reset |");
            System.out.println("Or enter consultation number to select | [Q] Cancel");
            System.out.print("Choice: ");
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
                    System.out.print("Enter search (Patient Name/Doctor Name): ");
                    searchQuery = scanner.nextLine().trim();
                    ArrayList<Consultation> filtered = filterConsultations(consultationList, searchQuery);
                    if (!filtered.isEmpty()) {
                        currentView = filtered;
                        currentPage = 1;
                    } else {
                        System.out.println("No results found for: " + searchQuery);
                        pause();
                    }
                    break;
                case "r":
                    currentView = new ArrayList<>(consultationList);
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
                            System.out.println("Invalid choice. Please select from the displayed consultations.");
                            pause();
                        }
                    } catch (NumberFormatException e) {
                        System.out.println("Please enter a valid number or command.");
                        pause();
                    }
            }
        }
    }

    private ArrayList<Consultation> filterConsultations(ArrayList<Consultation> consultations, String searchQuery) {
        if (searchQuery == null || searchQuery.trim().isEmpty()) {
            return new ArrayList<>(consultations);
        }

        String query = searchQuery.toLowerCase();
        ArrayList<Consultation> filtered = new ArrayList<>();

        for (int i = 0; i < consultations.size(); i++) {
            Consultation consultation = consultations.get(i);
            boolean matches = false;

            // Search by patient
            if (consultation.getPatient().getName() != null && consultation.getPatient().getName().toLowerCase().contains(query)) {
                matches = true;
            }

            // Search by doctor
            if (!matches && consultation.getDoctor().getName() != null && consultation.getDoctor().getName().toLowerCase().contains(query)) {
                matches = true;
            }

            if (matches) {
                filtered.add(consultation);
            }
        }

        return filtered;
    }

    public void displayConsultationSelectionList(ArrayList<Consultation> consultations, int totalItems, int currentPage, int totalPages, String searchQuery) {
        int start = (currentPage - 1) * pageSize;
        int end   = Math.min(totalItems, start + pageSize);
        System.out.println("\n\n\n\n\n\n\n\n\n\n");

        if(!(searchQuery == null || searchQuery.isEmpty())) {
            System.out.println("Search query: " + searchQuery);
        }

        // Header
        System.out.printf("Page %d/%d%n", currentPage, totalPages);
        System.out.println("+-----+------------------------------------------+------------------------------------------+--------------------------------+----------------------+");
        System.out.printf("| %-3s | %-40s | %-40s | %-30s | %-20s |\n", "No.", "Patient", "Doctor", "Status", "Total Payment");
        System.out.println("+-----+------------------------------------------+------------------------------------------+--------------------------------+----------------------+");

        // Rows
        for (int i = start; i < end; i++) {
            Consultation consultation = consultations.get(i);

            System.out.printf("| %-3d | %-40s | %-40s | %-30s | RM %-17.2f |\n", i+1, consultation.getPatient().getName(), consultation.getDoctor().getName(), consultation.status == Consultation.Status.IN_PROGRESS ? "IN PROGRESS":consultation.status.toString(), consultation.getTotalPayment());
        }

        System.out.println("+-----+------------------------------------------+------------------------------------------+--------------------------------+----------------------+");
        System.out.println();  // blank line between pages
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

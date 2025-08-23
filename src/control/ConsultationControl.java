package control;

import adt.HashedDictionary;
import adt.ListInterface;
import adt.ArrayList;
import boundary.ConsultationUI;
import entity.Consultation;
import entity.Doctor;
import entity.Patient;
import utility.GenerateConsultationData;

import java.io.IOException;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.Comparator;
import java.util.Scanner;
import java.util.UUID;

public class ConsultationControl {
    private static final int PAGE_SIZE = 5;
    ListInterface<Consultation> consultationList;
    ConsultationUI UI;
    Scanner scanner;

    ConsultationControl(ListInterface<Consultation> consultationList) {
        try {
            this.consultationList = consultationList;
            scanner = new Scanner(System.in);
            UI = new ConsultationUI(scanner);
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
                    //updateConsultation();
                    break;
                case 4:
                    deleteConsultation();
                    break;
                case 999:
                    return;
                default:
                    System.out.println("Invalid choice.");
            }
        }
    }

    public void addConsultation() throws IOException {
        Consultation newConsultation = UI.addConsultation();
        consultationList.add(newConsultation);
        System.out.println("Consultation added successfully!");
        pause();
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
                pause();
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
                pause();
                continue;
            }

            Consultation selected = pickMap.getValue(chosen);
            if (selected == null) {
                System.out.println("No consultation with that number. Try again.");
                pause();
                continue;
            }

            updateConsultation(selected);
        }
    }


    public void updateConsultation(Consultation consToUpdate) throws IOException {
        while (true) {
            System.out.println("\n=== UPDATE CONSULTATION ===");
            try {
                if (consToUpdate == null) {
                    System.out.println("Error: Consultation not found.");
                    pause();
                    return;
                }
                UI.displayConsultationDetails(consToUpdate);
                System.out.println("\n--- Select field to update ---");
                switch (consToUpdate.status) {
                    case WAITING:
                        System.out.println("1. Notes");
                        System.out.println("2. Medical Treatment");
                        System.out.println("3. Prescription");
                        System.out.println("4. Sales Item");
                        System.out.println("4. Change Status to IN PROGRESS");
                        System.out.println("9. Exit");
                        break;
                    case IN_PROGRESS:
                        System.out.println("1. Notes");
                        System.out.println("2. Medical Treatment");
                        System.out.println("3. Prescription");
                        System.out.println("4. Change Status to BILLING");
                        System.out.println("9. Exit");
                        break;
                    case BILLING:
                        System.out.println("1. Notes");
                        System.out.println("2. Medical Treatment");
                        System.out.println("3. Prescription");
                        System.out.println("4. Dispense Bill");
                        System.out.println("9. Exit");
                        break;
                    case COMPLETED:
                        System.out.println("Enter to continue..."); // No choice to edit notes here
                        scanner.nextLine();
                        return;
                }
                System.out.print("Enter choice: ");
                int choice = scanner.nextInt();
                scanner.nextLine();  // Consume the newline character
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
                        break;
                    case 4:
                        if (consToUpdate.status == Consultation.Status.WAITING) {
                            consToUpdate.setStatus(Consultation.Status.IN_PROGRESS);
                            System.out.println("Status changed to IN PROGRESS.");
                        } else if (consToUpdate.status == Consultation.Status.IN_PROGRESS) {
                            consToUpdate.setStatus(Consultation.Status.BILLING);
                            System.out.println("Status changed to BILLING.");
                        } else if (consToUpdate.status == Consultation.Status.BILLING) {
                            // TODO: dispense bill
                            consToUpdate.setStatus(Consultation.Status.COMPLETED);
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
                pause();
            } catch (IllegalArgumentException e) {
                System.out.println("Error: Invalid UUID format.");
                pause();
            } catch (Exception e) {
                System.out.println("An error occurred during update.");
                e.printStackTrace();
                pause();
            }
        }
    }


    public void deleteConsultation() {
        System.out.println("\n=== DELETE CONSULTATION ===");
        System.out.print("Enter Consultation ID to delete: ");
        String idString = scanner.nextLine().trim();
        try {
            UUID id = UUID.fromString(idString);
            boolean found = false;
            for (int i = 0; i < consultationList.size(); i++) {
                if (consultationList.get(i).getId().equals(id)) {
                    consultationList.remove(i);
                    found = true;
                    System.out.println("Consultation deleted successfully!");
                    break;
                }
            }
            if (!found) {
                System.out.println("Error: Consultation not found.");
            }
            pause();
        } catch (IllegalArgumentException e) {
            System.out.println("Error: Invalid UUID format.");
            pause();
        }
    }

    private Consultation findConsultationById(UUID id) {
        for (int i = 0; i < consultationList.size(); i++) {
            if (consultationList.get(i).getId().equals(id)) {
                return consultationList.get(i);
            }
        }
        return null;
    }

    private void pause() {
        System.out.print("Press Enter to continue...");
        scanner.nextLine();
    }
}

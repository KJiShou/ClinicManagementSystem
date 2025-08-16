package control;

import adt.ListInterface;
import adt.ArrayList;
import boundary.ConsultationUI;
import entity.Consultation;
import entity.Doctor;
import entity.Patient;

import java.io.IOException;
import java.util.Comparator;
import java.util.Scanner;
import java.util.UUID;

public class ConsultationControl {
    private static final int PAGE_SIZE = 5;
    ListInterface<Consultation> consultationList;
    ConsultationUI UI;
    Scanner scanner;

    ConsultationControl() {
        try {
            consultationList = new ArrayList<>();
            UI = new ConsultationUI();
            scanner = new Scanner(System.in);
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
                    updateConsultation();
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

    public void viewConsultation() throws IOException {
        if (consultationList.isEmpty()) {
            System.out.println("No consultations found.");
            pause();
            return;
        }

        ArrayList<Consultation> originalView = new ArrayList<>();
        for (int i = 0; i < consultationList.size(); i++) {
            originalView.add(consultationList.get(i));
        }

        ArrayList<Consultation> currentView = new ArrayList<>(originalView);
        int currentPage = 1;
        String sortOrder = "Unsorted";

        while (true) {
            int totalItems = currentView.size();
            int totalPages = (int) Math.ceil((double) totalItems / PAGE_SIZE);
            if (totalPages == 0) totalPages = 1;

            UI.displayConsultationList(currentView, totalItems, currentPage, totalPages);
            System.out.printf("Current view: %s | Page %d/%d\n", sortOrder, currentPage, totalPages);

            System.out.println("Press: [A] Prev | [D] Next | [S] Sort | [R] Reset | [Q] Quit");
            System.out.print("Enter choice: ");
            String input = scanner.nextLine().trim().toLowerCase();

            switch (input) {
                case "a":
                    if (currentPage > 1) currentPage--;
                    else {
                        System.out.println("This is the first page.");
                        pause();
                    }
                    break;
                case "d":
                    if (currentPage < totalPages) currentPage++;
                    else {
                        System.out.println("This is the last page.");
                        pause();
                    }
                    break;
//                case "s":
//                    Integer sortChoice = UI.viewConsultationMenu();
//                    switch (sortChoice) {
//                        case 1: // Sort by Status
//                            currentView.sort(Comparator.comparing(Consultation::Consultation.status));
//                            sortOrder = "Sorted by Status";
//                            break;
//                        case 2: // Sort by Date
//                            currentView.sort(Comparator.comparing(Consultation::getConsultatonDate));
//                            sortOrder = "Sorted by Date";
//                            break;
//                        case 3: // Sort by Payment
//                            currentView.sort(Comparator.comparing(Consultation::getTotalPayment));
//                            sortOrder = "Sorted by Payment";
//                            break;
//                        case 999: // Back
//                            break;
//                        default:
//                            System.out.println("Invalid sort choice.");
//                            pause();
//                    }
//                    currentPage = 1;
//                    break;
                case "r":
                    currentView = originalView;
                    sortOrder = "Unsorted";
                    currentPage = 1;
                    break;
                case "q":
                    return;
                default:
                    System.out.println("Invalid choice.");
                    pause();
            }
        }
    }

    public void updateConsultation() {
        System.out.println("\n=== UPDATE CONSULTATION ===");
        System.out.print("Enter Consultation ID to update: ");
        String idString = scanner.nextLine().trim();

        try {
            UUID id = UUID.fromString(idString);
            Consultation consToUpdate = findConsultationById(id);
            if (consToUpdate == null) {
                System.out.println("Error: Consultation not found.");
                pause();
                return;
            }

            UI.displayConsultationDetails(consToUpdate);
            System.out.println("\n--- Select field to update ---");
            System.out.println("1. Notes");
            System.out.println("2. Status");
            System.out.println("3. Total Payment");
            System.out.print("Enter choice: ");

            int choice = scanner.nextInt();
            scanner.nextLine();

            switch (choice) {
                case 1:
                    System.out.print("Enter new notes: ");
                    String newNotes = scanner.nextLine();
                    consToUpdate.setNotes(newNotes);
                    break;
                case 2:
                    System.out.println("Update Status logic would be here.");
                    break;
                case 3:
                    System.out.print("Enter new total payment: ");
                    float newPayment = scanner.nextFloat();
                    consToUpdate.setTotalPayment(newPayment);
                    scanner.nextLine();
                    break;
                default:
                    System.out.println("Invalid choice.");
                    break;
            }
            System.out.println("Consultation updated successfully!");
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

package control;

import adt.*;
import boundary.PrescriptionUI;
import entity.pharmacyManagement.*;
import utility.MessageUI; //test

import java.io.IOException;
import java.util.Scanner;
import java.util.UUID;

public class PrescriptionControl {

    private static final int PAGE_SIZE = 5;

    private DictionaryInterface<String, Medicine> meds;
    private ListInterface<Prescription> prescriptions;
    private PrescriptionUI ui;
    private Scanner scanner;
    private ArrayList<Medicine> medicineList;

    public PrescriptionControl(DictionaryInterface<String, Medicine> medicines) {
            meds = medicines;
            this.prescriptions = new ArrayList<>();
            this.ui = new PrescriptionUI();
            this.scanner = new Scanner(System.in);

        HashedDictionary<String, Medicine> dict = (HashedDictionary<String, Medicine>) meds;

        // Convert dictionary values to a list for indexed selection
        medicineList = dict.valueList();
    }

    public void main() throws IOException {

        int choice;
        do {
            choice = ui.prescriptionMenu();
            switch (choice) {
                case 1:
                    addPrescription();
                    break;
                case 2:
                    viewPrescriptions();
                    break;
                case 3:
                    editPrescription();
                    break;
                //case 4:
                //    deletePrescription();
                //    break;
                case 999:
                    System.out.println("Returning to Consultation Menu...");
                    break;
                default:
                    System.out.println("Invalid choice. Try again.");
            }
        } while (choice != 999);
    }

    private void addPrescription() {
        System.out.println("\n--- Add New Prescription ---");

        if (medicineList.isEmpty()) {
            System.out.println("No medications available.");
            return;
        }

        ArrayList<Medicine> originalView = medicineList;
        ArrayList<Medicine> currentView = new ArrayList<>(originalView);
        int currentPage = 1;
        String searchQuery = "";

        Medicine selectedMedicine = null;

        //  Select Medication section
        while (true) {
            int totalItems = currentView.size();
            int totalPages = Math.max(1, (totalItems + PAGE_SIZE - 1) / PAGE_SIZE);
            int start = (currentPage - 1) * PAGE_SIZE;
            int endExclusive = Math.min(start + PAGE_SIZE, totalItems);

            System.out.println("\nAvailable Medications (Page " + currentPage + " of " + totalPages + "):");
            for (int i = start; i < endExclusive; i++) {
                System.out.println((i - start + 1) + ". " + currentView.get(i).getName());
            }

            System.out.println("\nPress: [A] Prev | [D] Next | [S] Search | [R] Reset | [O] Select | [Q] Quit");
            System.out.print("Enter your choice: ");
            String input = scanner.nextLine().trim().toLowerCase();

            switch (input) {
                case "a":
                    if (currentPage > 1) currentPage--;
                    else System.out.println("This is the first page.");
                    break;
                case "d":
                    if (currentPage < totalPages) currentPage++;
                    else System.out.println("This is the last page.");
                    break;
                case "s":
                    System.out.print("Enter search (Name/Brand/Company): ");
                    searchQuery = scanner.nextLine().trim();
                    ArrayList<Medicine> filtered = filterMedicines(originalView, searchQuery);
                    if (filtered.isEmpty()) {
                        System.out.println("No results found for: " + searchQuery);
                        pause();
                    } else {
                        currentView = filtered;
                        currentPage = 1;
                    }
                    break;
                case "r":
                    currentView = new ArrayList<>(originalView);
                    searchQuery = "";
                    currentPage = 1;
                    break;
                case "o":
                    if (currentView.isEmpty()) {
                        System.out.println("No items on this page.");
                        break;
                    }
                    System.out.printf("Select item [1-%d]: ", endExclusive - start);
                    String raw = scanner.nextLine().trim();
                    int pick;
                    try {
                        pick = Integer.parseInt(raw);
                    } catch (NumberFormatException ex) {
                        System.out.println("Invalid number.");
                        break;
                    }
                    if (pick < 1 || pick > (endExclusive - start)) {
                        System.out.println("Out of range.");
                        break;
                    }
                    selectedMedicine = currentView.get(start + pick - 1);
                    break;
                case "q":
                    return;
                default:
                    System.out.println("Invalid choice.");
            }

            if (selectedMedicine != null) break; // Exit loop when medicine is chosen
        }

        // Proceed with prescription details
        if (selectedMedicine == null) return; // User quit

        System.out.println("\nMedication Selected: " + selectedMedicine.getName());
        System.out.print("Enter prescription description: ");
        String description = scanner.nextLine();

        System.out.print("Enter dosage per day (pills): ");
        float dosagePerDay = getFloatInput();

        System.out.print("Enter number of days: ");
        int days = getIntInput();

        Prescription prescription = new Prescription(UUID.randomUUID(), description, dosagePerDay, days, selectedMedicine);
        prescriptions.add(prescription);

        System.out.println("\nPrescription added successfully!");
        System.out.println(prescription);

        // Press Enter to continue...
        pause();
    }

    private void viewPrescriptions() {
        if (prescriptions.isEmpty()) {
            System.out.println("No prescriptions available.");
            return;
        }
        System.out.println("\n--- Prescriptions List ---");
        for (int i = 0; i < prescriptions.size(); i++) {
            System.out.println((i + 1) + ". " + prescriptions.get(i));
        }
    }

    private void editPrescription() {
        if (prescriptions.isEmpty()) {
            System.out.println("No prescriptions to edit.");
            return;
        }
        viewPrescriptions();
        System.out.print("Select prescription to edit: ");
        int index = getIntInput() - 1;

        if (index < 0 || index >= prescriptions.size()) {
            System.out.println("Invalid selection.");
            return;
        }

        Prescription prescription = prescriptions.get(index);
        System.out.println();
        System.out.println("--- Editing Prescription ---");
        System.out.println(prescription);

        System.out.print("Enter new description: ");
        String description = scanner.nextLine();
        prescription.setDescription(description);

        System.out.print("Enter new dosage per day: ");
        float dosage = getFloatInput();
        prescription.setDosagePerDay(dosage);

        System.out.print("Enter new duration (days): ");
        int days = getIntInput();
        prescription.setDays(days);

        System.out.println("Prescription updated successfully!");

        // Press Enter to continue...
        System.out.print("\nPress Enter to continue...");
        scanner.nextLine(); // Waits for Enter key
    }

    private void deletePrescription() {
        if (prescriptions.isEmpty()) {
            System.out.println("No prescriptions to delete.");
            return;
        }
        viewPrescriptions();
        System.out.print("Select prescription to delete: ");
        int index = getIntInput() - 1;

        if (index < 0 || index >= prescriptions.size()) {
            System.out.println("Invalid selection.");
            return;
        }

        prescriptions.remove(index);
        System.out.println("Prescription deleted successfully!");
    }

    private int getIntInput() {
        while (!scanner.hasNextInt()) {
            System.out.print("Invalid input. Enter a number: ");
            scanner.next();
        }
        int val = scanner.nextInt();
        scanner.nextLine();
        return val;
    }

    private float getFloatInput() {
        while (!scanner.hasNextFloat()) {
            System.out.print("Invalid input. Enter a number: ");
            scanner.next();
        }
        float val = scanner.nextFloat();
        scanner.nextLine();
        return val;
    }

    private void pause() {
        System.out.print("Press Enter to continue...");
        scanner.nextLine();
    }

    private ArrayList<Medicine> filterMedicines(ArrayList<Medicine> source, String query) {

        ArrayList<Medicine> results = new ArrayList<>();
        if (query == null || query.trim().isEmpty()) {
            return results;
        }

        String q = query.toLowerCase();

        for (int i = 0; i < source.size(); i++) {
            Medicine m = source.get(i);

            String name = safeLower(m.getName());
            String brand = safeLower(m.getBrand());
            String company = (m.getCompany() == null) ? "" : safeLower(m.getCompany().getName());

            boolean textMatch =
                    (name.contains(q) || brand.contains(q) || company.contains(q));

            if (textMatch) {
                results.add(m);
            }
        }
        return results;
    }

    private static String safeLower(String s) {
        return (s == null) ? "" : s.toLowerCase();
    }
}
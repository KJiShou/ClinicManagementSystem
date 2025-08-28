package control;

import adt.*;
import boundary.PrescriptionUI;
import entity.pharmacyManagement.*;
import utility.MessageUI;

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
    private PharmacyControl pharmacyControl;

    public PrescriptionControl(DictionaryInterface<String, Medicine> medicines, PharmacyControl pharmacyControl) {
        meds = medicines;
        this.prescriptions = new ArrayList<>();
        this.ui = new PrescriptionUI();
        this.scanner = new Scanner(System.in);
        this.pharmacyControl = pharmacyControl;

        HashedDictionary<String, Medicine> dict = (HashedDictionary<String, Medicine>) meds;
        medicineList = dict.valueList();
    }

    // Overloaded constructor to accept existing prescription list
    public PrescriptionControl(DictionaryInterface<String, Medicine> medicines, PharmacyControl pharmacyControl,
                               ListInterface<Prescription> existingPrescriptions) {
        this(medicines, pharmacyControl);
        this.prescriptions = existingPrescriptions;
    }

    public void main() throws IOException {
        main(prescriptions);
    }

    public void main(ListInterface<Prescription> prescriptionList) throws IOException {
        this.prescriptions = prescriptionList;

        int choice;
        do {
            choice = ui.prescriptionMenu();
            switch (choice) {
                case 1:
                    Prescription newPrescription = addPrescription();
                    if (newPrescription != null) {
                        System.out.println("Prescription created successfully!");
                    }
                    break;
                case 2:
                    viewPrescriptions(prescriptionList);
                    break;
                case 3:
                    editPrescription(prescriptionList);
                    break;
                case 4:
                    generateReports();
                    break;
                default:
                    System.out.println("Invalid choice. Try again.");
            }
        } while (choice != 999);
    }

    public Prescription addPrescription() {
        return addPrescription(prescriptions);
    }

    public Prescription addPrescription(ListInterface<Prescription> prescriptionList) {
        System.out.println("\n--- Add New Prescription ---");
        System.out.println("(Type 'exit' at any time to cancel)");

        try {
            System.out.println("\n1. Select Medicine:");
            Medicine selectedMedicine = pharmacyControl.chooseMedicine((HashedDictionary<String, Medicine>) meds);
            if (selectedMedicine == null) {
                System.out.println("No medicine selected. Prescription creation cancelled.");
                return null;
            }

            System.out.println("\nSelected Medicine Details:");
            System.out.printf("Name: %s | Brand: %s | Available Stock: %d %s\n",
                    selectedMedicine.getName(), selectedMedicine.getBrand(),
                    selectedMedicine.getQuantity(), selectedMedicine.getUnit());

            System.out.println("\n2. Enter prescription details:");
            String description = getValidatedStringInput("Enter prescription description: ", false);
            if (description == null) return null; // User chose to exit

            Float dosagePerTime = getValidatedFloatInput("Enter dosage per time (" + selectedMedicine.getUnit() + "): ", 0.1f, 50.0f);
            if (dosagePerTime == null) return null; // User chose to exit

            Integer timesPerDay = getValidatedIntInput("Enter times per day: ", 1, 10);
            if (timesPerDay == null) return null; // User chose to exit

            Integer days = getValidatedIntInput("Enter duration (days): ", 1, 365);
            if (days == null) return null; // User chose to exit

            System.out.printf("Available stock: %d %s\n", selectedMedicine.getQuantity(), selectedMedicine.getUnit());

            int maxQuantity = selectedMedicine.getQuantity();
            if (maxQuantity <= 0) {
                System.out.println("Error: No stock available for this medicine!");
                System.out.println("Prescription creation cancelled.");
                return null;
            }

            Integer quantityToGive = getValidatedIntInput(
                    String.format("Enter quantity to give (1-%d %s): ", maxQuantity, selectedMedicine.getUnit()),
                    1, maxQuantity);
            if (quantityToGive == null) return null; // User chose to exit

            Medicine prescriptionMedicine = createPrescriptionMedicine(selectedMedicine, quantityToGive);

            selectedMedicine.setQuantity(selectedMedicine.getQuantity() - quantityToGive);
            System.out.printf("Medicine stock updated. Remaining quantity: %d %s\n",
                    selectedMedicine.getQuantity(), selectedMedicine.getUnit());

            // Create prescription
            Prescription prescription = new Prescription(
                    UUID.randomUUID(),
                    description,
                    dosagePerTime,
                    timesPerDay,
                    days,
                    prescriptionMedicine
            );

            // Add to the provided list
            prescriptionList.add(prescription);

            // Display summary
            System.out.println("\n=== Prescription Summary ===");
            System.out.println(prescription);
            System.out.printf("Total quantity prescribed: %d %s\n", quantityToGive, selectedMedicine.getUnit());
            pause();
            return prescription;

        } catch (Exception e) {
            System.out.println("An error occurred while creating prescription: " + e.getMessage());
            return null;
        }
    }

    private Medicine createPrescriptionMedicine(Medicine originalMedicine, int prescriptionQuantity) {
        Medicine prescriptionMedicine = new Medicine(
                originalMedicine.getId(),
                originalMedicine.getName(),
                prescriptionQuantity,
                originalMedicine.getPrice(),
                originalMedicine.getDescription(),
                originalMedicine.getUnit(),
                originalMedicine.getCompany(),
                originalMedicine.getBrand(),
                originalMedicine.getStrength(),
                originalMedicine.getExpiryDate()
        );
        return prescriptionMedicine;
    }

    public void viewPrescriptions(ListInterface<Prescription> prescriptionList) {
        if (prescriptionList.isEmpty()) {
            System.out.println("No prescriptions available.");
            return;
        }

        System.out.println("\n--- Prescriptions List ---");
        for (int i = 0; i < prescriptionList.size(); i++) {
            System.out.println("=== Prescription " + (i + 1) + " ===");
            System.out.println(prescriptionList.get(i));
            System.out.println();
        }

        System.out.println("Press Enter to continue...");
        scanner.nextLine();
    }

    public void editPrescription(ListInterface<Prescription> prescriptionList) {
        if (prescriptionList.isEmpty()) {
            System.out.println("No prescriptions to edit.");
            return;
        }

        viewPrescriptions(prescriptionList);

        System.out.println("(Type 'exit' to cancel)");
        Integer index = getValidatedIntInput("Select prescription to edit (1-" + prescriptionList.size() + "): ",
                1, prescriptionList.size());

        if (index == null) {
            System.out.println("Edit cancelled.");
            return;
        }

        Prescription prescription = prescriptionList.get(index - 1);
        System.out.println("\nEditing Prescription: ");
        System.out.println(prescription);

        try {
            // Edit Description
            String newDescription = getValidatedStringInput("Enter new description (current: " +
                    prescription.getDescription() + "): ", true);
            if (newDescription != null && !newDescription.trim().isEmpty()) {
                prescription.setDescription(newDescription);
            }

            // Edit Dosage per Time
            Float newDosagePerTime = getValidatedFloatInput("Enter new dosage per time (current: " +
                    prescription.getDosagePerTime() + "): ", 0.1f, 50.0f, true);
            if (newDosagePerTime != null) {
                prescription.setDosagePerTime(newDosagePerTime);
            }

            // Edit Times per Day
            Integer newTimesPerDay = getValidatedIntInput("Enter new times per day (current: " +
                    prescription.getTimesPerDay() + "): ", 1, 10, true);
            if (newTimesPerDay != null) {
                prescription.setTimesPerDay(newTimesPerDay);
            }

            // Edit Duration
            Integer newDays = getValidatedIntInput("Enter new duration in days (current: " +
                    prescription.getDays() + "): ", 1, 365, true);
            if (newDays != null) {
                prescription.setDays(newDays);
            }

            System.out.println("\n=== Updated Prescription ===");
            System.out.println(prescription);
            System.out.println("Prescription updated successfully!");

        } catch (Exception e) {
            System.out.println("An error occurred while editing prescription: " + e.getMessage());
        }
    }

    public void deletePrescription(ListInterface<Prescription> prescriptionList) {
        if (prescriptionList.isEmpty()) {
            System.out.println("No prescriptions to delete.");
            return;
        }

        viewPrescriptions(prescriptionList);

        Integer index = getValidatedIntInput("Select prescription to delete (1-" + prescriptionList.size() + "): ",
                1, prescriptionList.size());

        if (index == null) {
            System.out.println("Delete cancelled.");
            return;
        }

        Prescription toDelete = prescriptionList.get(index - 1);
        System.out.println("Selected prescription:");
        System.out.println(toDelete);

        String confirm = getValidatedStringInput("Are you sure you want to delete this prescription? (y/n): ", true);
        if (confirm != null && (confirm.equalsIgnoreCase("y") || confirm.equalsIgnoreCase("yes"))) {
            prescriptionList.remove(index - 1);
            System.out.println("Prescription deleted successfully!");
        } else {
            System.out.println("Delete cancelled.");
        }
    }

    public void generateReports() {
        System.out.println("\n\n+==================================================================================+");
        System.out.println("|                           PRESCRIPTION SUMMARY REPORT                            |");
        System.out.println("+==================================================================================+");

        int totalPrescriptions = prescriptions.size();
        int totalQuantityAll = 0;

        // ArrayList to track medicine names, prescription count and quantity count
        ArrayList<String> medicineNames = new ArrayList<>();
        ArrayList<Integer> prescriptionCounts = new ArrayList<>();
        ArrayList<Integer> quantityCounts = new ArrayList<>();

        // Iterate through prescriptions
        for (int i = 0; i < prescriptions.size(); i++) {
            Prescription p = prescriptions.get(i);
            Medicine med = p.getMedicine();
            String medName = med.getName();

            //  Sums up total quantity for all prescribed meds
            int quantity = med.getQuantity();
            totalQuantityAll += quantity;

            //  Look inside medicineNames to find the index of medName.
            int index = medicineNames.indexOf(medName);

            //  If not found, create a new meds to track
            //  E.g.    medicineNames       ["Panadol", "Strepsils"]
            //          prescriptionCounts  [2, 1]
            //          quantityCounts      [7, 3]
            if (index == -1) {
                medicineNames.add(medName);
                prescriptionCounts.add(1);
                quantityCounts.add(quantity);
            } else {
                //  If found, tambah into existing index of meds
                //  E.g.    medicineNames       ["Panadol", "Strepsils"]
                //          prescriptionCounts  [3 (2 + 1), 1]
                //          quantityCounts      [11 (7 + 4), 3]
                prescriptionCounts.set(index, prescriptionCounts.get(index) + 1);
                quantityCounts.set(index, quantityCounts.get(index) + quantity);
            }
        }

        // Find most prescribed medicine
        int maxIndex = 0;
        for (int i = 1; i < prescriptionCounts.size(); i++) {
            if (prescriptionCounts.get(i) > prescriptionCounts.get(maxIndex)) {
                maxIndex = i;
            }
        }

        // Print summary report
        System.out.printf("| %-40s | %-25d             |\n", "Total Prescriptions", totalPrescriptions);
        System.out.printf("| %-40s | %-25s   (%d times) |\n", "Most Prescribed Medicine",
                medicineNames.get(maxIndex), prescriptionCounts.get(maxIndex));
        System.out.printf("| %-40s | %-25d             |\n", "Total Quantity (" + medicineNames.get(maxIndex) + ")",
                quantityCounts.get(maxIndex));
        System.out.printf("| %-40s | %-25d             |\n", "Total Quantity (All)", totalQuantityAll);
        System.out.println("+----------------------------------------------------------------------------------+");

        // Print detailed usage report
        System.out.println("\n+=======================================================+");
        System.out.println("|               PRESCRIPTION USAGE REPORT               |");
        System.out.println("+=======================================================+");
        System.out.printf("| %-25s | %-12s | %-9s |\n", "Medicine", "Prescriptions", "Units");
        System.out.println("+-------------------------------------------------------+");
        for (int i = 0; i < medicineNames.size(); i++) {
            System.out.printf("| %-25s | %-12d  | %-9d |\n",
                    medicineNames.get(i), prescriptionCounts.get(i), quantityCounts.get(i));
        }
        System.out.println("+-------------------------------------------------------+");
    }

    // Validation helper methods
    private String getValidatedStringInput(String prompt, boolean allowEmpty) {
        while (true) {
            System.out.print(prompt);
            String input = scanner.nextLine().trim();

            if (input.equalsIgnoreCase("exit")) {
                return null;
            }

            if (!allowEmpty && input.isEmpty()) {
                System.out.println("Input cannot be empty. Please try again.");
                continue;
            }

            return input;
        }
    }

    private Integer getValidatedIntInput(String prompt, int min, int max) {
        return getValidatedIntInput(prompt, min, max, false);
    }

    private Integer getValidatedIntInput(String prompt, int min, int max, boolean allowEmpty) {
        while (true) {
            System.out.print(prompt);
            String input = scanner.nextLine().trim();

            if (input.equalsIgnoreCase("exit")) {
                return null;
            }

            if (allowEmpty && input.isEmpty()) {
                return null; // Return null to indicate no change
            }

            if (input.isEmpty()) {
                System.out.println("Input cannot be empty. Please try again.");
                continue;
            }

            try {
                int value = Integer.parseInt(input);
                if (value < min || value > max) {
                    System.out.printf("Value must be between %d and %d. Please try again.\n", min, max);
                    continue;
                }
                return value;
            } catch (NumberFormatException e) {
                System.out.println("Invalid number format. Please try again.");
            }
        }
    }

    private Float getValidatedFloatInput(String prompt, float min, float max) {
        return getValidatedFloatInput(prompt, min, max, false);
    }

    private Float getValidatedFloatInput(String prompt, float min, float max, boolean allowEmpty) {
        while (true) {
            System.out.print(prompt);
            String input = scanner.nextLine().trim();

            if (input.equalsIgnoreCase("exit")) {
                return null;
            }

            if (allowEmpty && input.isEmpty()) {
                return null; // Return null to indicate no change
            }

            if (input.isEmpty()) {
                System.out.println("Input cannot be empty. Please try again.");
                continue;
            }

            try {
                float value = Float.parseFloat(input);
                if (value < min || value > max) {
                    System.out.printf("Value must be between %.2f and %.2f. Please try again.\n", min, max);
                    continue;
                }
                return value;
            } catch (NumberFormatException e) {
                System.out.println("Invalid number format. Please try again.");
            }
        }
    }

    // Getter methods
    public ListInterface<Prescription> getPrescriptions() {
        return prescriptions;
    }

    public void setPrescriptions(ListInterface<Prescription> prescriptions) {
        this.prescriptions = prescriptions;
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
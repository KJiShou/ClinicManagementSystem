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
                case 4:
                    System.out.println("Returning to Consultation Menu...");
                    break;
                default:
                    System.out.println("Invalid choice. Try again.");
            }
        } while (choice != 4);
    }

    private void addPrescription() {
        System.out.println("\n--- Add New Prescription ---");

        if (medicineList.isEmpty()) {
            System.out.println("No medications available.");
            return;
        }

        //  Select Medication section
        System.out.println("Available Medications:");
        for (int i = 0; i < medicineList.size(); i++) {
            System.out.println((i + 1) + ". " + medicineList.get(i).getName());
        }
        System.out.print("Select medication by number: ");
        int choice = getIntInput();

        if (choice < 1 || choice > medicineList.size()) {
            System.out.println("Invalid selection.");
            return;
        }

        Medicine selectedMedicine = medicineList.get(choice - 1);

        //  Manual description input section
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
        System.out.println("Editing Prescription: " + prescription);

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
}
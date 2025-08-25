package boundary;

import adt.*;
import entity.pharmacyManagement.BloodTube;
import entity.pharmacyManagement.LabTest;
import entity.pharmacyManagement.Medicine;
import utility.MessageUI;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Scanner;

public class PharmacyUI {
    QueueInterface<String> choiceQueue;
    MessageUI UI;
    int pageSize;
    private static final SimpleDateFormat DATE_FMT = new SimpleDateFormat("yyyy-MM-dd");
    Scanner scanner;
    public PharmacyUI() {
        choiceQueue = new LinkedQueue<String>();
        UI = new MessageUI();
        pageSize = 5;
        scanner = new Scanner(System.in);
    }

    public Integer mainMenu() throws IOException {
        // choice 1
        choiceQueue.enqueue("View Inventory");
        choiceQueue.enqueue("Stock In Sales Item");
        choiceQueue.enqueue("Update Sales Item details");
        choiceQueue.enqueue("Stock Out Sales Item");

        return UI.mainUI("Welcome to Pharmacy Management System", choiceQueue);
    }

    public Integer stockInSelectionUI() throws IOException {
        // choice 1
        choiceQueue.enqueue("Medicine");
        choiceQueue.enqueue("Lab Test");
        choiceQueue.enqueue("BloodTube");

        return UI.mainUI("Select Item to stock in", choiceQueue);
    }

    public Integer viewInventory() throws IOException {
        choiceQueue.enqueue("Medicine");
        choiceQueue.enqueue("Lab Test");
        choiceQueue.enqueue("Blood Tube");

        return UI.mainUI("Pharmacy Inventory", choiceQueue);
    }

    public Integer updateItem() throws IOException {
        choiceQueue.enqueue("Medicine");
        choiceQueue.enqueue("Lab Test");
        choiceQueue.enqueue("Blood Tube");

        return UI.mainUI("Pharmacy Inventory", choiceQueue);
    }

    public Integer stockOutItem() throws IOException {
        choiceQueue.enqueue("Medicine");
        choiceQueue.enqueue("Blood Tube");

        return UI.mainUI("Stock Out", choiceQueue);
    }

    public void displayMedicineList(ArrayList<Medicine> medicines, int totalItems, int currentPage, int totalPages, String searchQuery) {
        int start = (currentPage - 1) * pageSize;
        int end   = Math.min(totalItems, start + pageSize);
        System.out.println("\n\n\n\n\n\n\n\n\n\n");

        if(!(searchQuery == null || searchQuery.isEmpty())) {
            System.out.println("Search query: " + searchQuery);
        }

        // Header
        System.out.printf("Page %d/%d%n", currentPage, totalPages);
        System.out.println("+-----+------------------------------------------+----------------------+--------------------------------+----------------------+");
        System.out.printf("| %-3s | %-40s | %-20s | %-30s | %-20s |\n", "No.", "Name", "Unit", "Expiry Date", "Price");
        System.out.println("+-----+------------------------------------------+----------------------+--------------------------------+----------------------+");

        // Rows
        for (int i = start; i < end; i++) {
            Medicine medicine = medicines.get(i);

            System.out.printf("| %-3d | %-40s | %-20s | %-30s | RM %-17.2f |\n", i+1, medicine.getName(), (medicine.getQuantity() + " " + medicine.getUnit()), DATE_FMT.format(medicine.getExpiryDate()), medicine.getPrice());
        }

        System.out.println("+-----+------------------------------------------+----------------------+--------------------------------+----------------------+");
        System.out.println();  // blank line between pages
    }

    public void displayLabTestList(ArrayList<LabTest> labTests, int totalItems, int currentPage, int totalPages, String searchQuery) {
        int start = (currentPage - 1) * pageSize;
        int end   = Math.min(totalItems, start + pageSize);
        System.out.println("\n\n\n\n\n\n\n\n\n\n");

        if(!(searchQuery == null || searchQuery.isEmpty())) {
            System.out.println("Search query: " + searchQuery);
        }

        // Header
        System.out.printf("Page %d/%d%n", currentPage, totalPages);
        System.out.println("+-----+------------------------------------------+------------------------------------------+--------------------------------+----------------------+");
        System.out.printf("| %-3s | %-40s | %-40s | %-30s | %-20s |\n", "No.", "Name", "Tube Needed", "Lab", "Price");
        System.out.println("+-----+------------------------------------------+------------------------------------------+--------------------------------+----------------------+");

        // Rows
        for (int i = start; i < end; i++) {
            LabTest labTest = labTests.get(i);

            System.out.printf("| %-3d | %-40s | %-40s | %-30s | RM %-17.2f |\n", i+1, labTest.getName(), labTest.getBloodTubes(), labTest.getReferringLab().getName(), labTest.getPrice());
        }

        System.out.println("+-----+------------------------------------------+------------------------------------------+--------------------------------+----------------------+");
        System.out.println();  // blank line between pages
    }

    public void displayBloodTubeList(ArrayList<BloodTube> bloodTubes, int totalItems, int currentPage, int totalPages, String searchQuery) {
        int start = (currentPage - 1) * pageSize;
        int end   = Math.min(totalItems, start + pageSize);
        System.out.println("\n\n\n\n\n\n\n\n\n\n");

        if(!(searchQuery == null || searchQuery.isEmpty())) {
            System.out.println("Search query: " + searchQuery);
        }

        // Header
        System.out.printf("Page %d/%d%n", currentPage, totalPages);
        System.out.println("+-----+--------------------------------+--------------------------------+----------------------+----------------------+----------------------+");
        System.out.printf("| %-3s | %-30s | %-30s | %-20s | %-20s | %-20s |\n", "No.", "Name", "Cap Colour", "Expiry Date", "Unit", "Price");
        System.out.println("+-----+--------------------------------+--------------------------------+----------------------+----------------------+----------------------+");

        // Rows
        for (int i = start; i < end; i++) {
            BloodTube bloodTube = bloodTubes.get(i);

            System.out.printf("| %-3d | %-30s | %-30s | %-20s | %-20s | RM %-17.2f |\n", i+1, bloodTube.getName(), bloodTube.getCapColor(), DATE_FMT.format(bloodTube.getExpiryDate()), (bloodTube.getQuantity()), bloodTube.getPrice());
        }

        System.out.println("+-----+--------------------------------+--------------------------------+----------------------+----------------------+----------------------+");
        System.out.println();  // blank line between pages
    }

    public void displayInsufficientMedicines(
            DictionaryInterface<String, Medicine> medicines) {

        HashedDictionary<String, Medicine> insufficientMeds = filterInsufficientMedicines(10, medicines);
        System.out.println("\n\n\n\n\n\n\n\n\n\n");
        System.out.println("\n=== Insufficient Medicines ===");
        if (insufficientMeds.isEmpty()) {
            System.out.println("No Insufficient Medicines Found");
            System.out.println("Enter to continue...");
            scanner.nextLine();
            return;
        }
        System.out.println("+-----+------------------------------------------+--------------------------------+----------------------+----------------------+");
        System.out.printf("| %-3s | %-40s | %-30s | %-20s | %-20s |\n",
                "No.", "Name", "Company", "Quantity", "Price");
        System.out.println("+-----+------------------------------------------+--------------------------------+----------------------+----------------------+");

        ArrayList<String> medKeys = insufficientMeds.keyList();
        int counter = 1;
        for (int i = 0; i < medKeys.size(); i++) {
            String nameKey = medKeys.get(i);

            Medicine medObj = insufficientMeds.getValue(nameKey);

            System.out.printf("| %-3d | %-40s | %-30s | %-20s | RM%-18.2f |\n",
                    counter++, medObj.getName(), medObj.getCompany().getName(), medObj.getQuantity() + " " + medObj.getUnit(), medObj.getPrice());
        }
        System.out.println("+-----+------------------------------------------+--------------------------------+----------------------+----------------------+");
        System.out.println("Enter to continue...");
        scanner.nextLine();
        System.out.println();  // blank line between pages

    }

    public void displayInsufficientBloodTubes(
            DictionaryInterface<String, BloodTube> bloodTubes) {

        HashedDictionary<String, BloodTube> insufficientTubes = filterInsufficientBloodTubes(10, bloodTubes);
        System.out.println("\n\n\n\n\n\n\n\n\n\n");
        System.out.println("\n=== Insufficient Blood Tubes ===");
        if (insufficientTubes.isEmpty()) {
            System.out.println("No Insufficient Blood Tubes Found");
            System.out.println("Enter to continue...");
            scanner.nextLine();
            return;
        }
        System.out.println("+-----+--------------------------------+----------------------+----------------------+----------------------+");
        System.out.printf("| %-3s | %-30s | %-20s | %-20s | %-20s |\n",
                "No.", "Name", "Cap Colour", "Quantity", "Price");
        System.out.println("+-----+--------------------------------+----------------------+----------------------+----------------------+");

        ArrayList<String> tubeKeys = insufficientTubes.keyList();
        int counter = 1;
        for (int i = 0; i < tubeKeys.size(); i++) {
            String nameKey = tubeKeys.get(i);
            BloodTube tubeObj = insufficientTubes.getValue(nameKey);

            System.out.printf("| %-3d | %-30s | %-20s | %-20s | RM%-18.2f |\n",
                    counter++, tubeObj.getName(), tubeObj.getCapColor(), tubeObj.getQuantity(), tubeObj.getPrice());
        }
        System.out.println("+-----+--------------------------------+----------------------+----------------------+----------------------+");
        System.out.println("Enter to continue...");
        scanner.nextLine();
        System.out.println();  // blank line between pages
    }

    private HashedDictionary<String, Medicine> filterInsufficientMedicines(int threshold, DictionaryInterface<String, Medicine> meds) {
        HashedDictionary<String, Medicine> totals = new HashedDictionary<>();
        HashedDictionary<String, Medicine> medicineDict =
                (HashedDictionary<String, Medicine>) meds;

        ArrayList<Medicine> medicines = medicineDict.valueList();
        for (int i = 0; i < medicines.size(); i++) {
            Medicine m = medicines.get(i);
            String groupKey = m.getName();

            Medicine curr;
            if (totals.contains(groupKey)) {
                curr = totals.getValue(groupKey);
                curr.setQuantity(curr.getQuantity() + m.getQuantity());
            } else {
                curr = m;
            }
            totals.add(groupKey, curr);
        }

        HashedDictionary<String, Medicine> results = new HashedDictionary<>();
        ArrayList<String> keys = totals.keyList();
        for (int i = 0; i < keys.size(); i++) {
            String k = keys.get(i);
            Medicine bloodTube = totals.getValue(k);
            if (bloodTube.getQuantity() < threshold) {
                results.add(k, bloodTube);
            }
        }
        return results;
    }

    private HashedDictionary<String, BloodTube> filterInsufficientBloodTubes(int threshold, DictionaryInterface<String, BloodTube> bloodTubeInventory) {
        HashedDictionary<String, BloodTube> totals = new HashedDictionary<>();
        HashedDictionary<String, BloodTube> bloodTubesDict =
                (HashedDictionary<String, BloodTube>) bloodTubeInventory;

        ArrayList<BloodTube> bloodTubes = bloodTubesDict.valueList();
        for (int i = 0; i < bloodTubes.size(); i++) {
            BloodTube m = bloodTubes.get(i);
            String groupKey = m.getName();

            BloodTube curr;
            if (totals.contains(groupKey)) {
                curr = totals.getValue(groupKey);
                curr.setQuantity(curr.getQuantity() + m.getQuantity());
            } else {
                curr = m;
            }
            totals.add(groupKey, curr);
        }

        HashedDictionary<String, BloodTube> results = new HashedDictionary<>();
        ArrayList<String> keys = totals.keyList();
        for (int i = 0; i < keys.size(); i++) {
            String k = keys.get(i);
            BloodTube bloodTube = totals.getValue(k);
            if (bloodTube.getQuantity() < threshold) {
                results.add(k, bloodTube);
            }
        }
        return results;
    }
}

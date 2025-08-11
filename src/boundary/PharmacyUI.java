package boundary;

import adt.ArrayList;
import adt.Entry;
import adt.LinkedQueue;
import adt.QueueInterface;
import entity.pharmacyManagement.BloodTube;
import entity.pharmacyManagement.LabTest;
import entity.pharmacyManagement.Medicine;
import entity.pharmacyManagement.MedicineKey;
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

    public Integer viewInventory() throws IOException {
        // choice 1
        choiceQueue.enqueue("Medicine");
        choiceQueue.enqueue("Lab Test");
        choiceQueue.enqueue("Blood Tube");
        choiceQueue.enqueue("Insufficient Stock");

        return UI.mainUI("Pharmacy Inventory", choiceQueue);
    }

    public void displayMedicineList(ArrayList<Medicine> medicines, int totalItems, int currentPage, int totalPages, String searchQuery) {
        int start = (currentPage - 1) * pageSize;
        int end   = Math.min(totalItems, start + pageSize);

        if(!(searchQuery == null || searchQuery.isEmpty())) {
            System.out.println("Search query: " + searchQuery);
        }

        // Header
        System.out.printf("Page %d/%d%n", currentPage, totalPages);
        System.out.println("+-----+--------------------------------+----------------------+--------------------------------+----------------------+");
        System.out.printf("| %-3s | %-30s | %-20s | %-30s | %-20s |\n", "No.", "Name", "Unit", "Expiry Date", "Price");
        System.out.println("+-----+--------------------------------+----------------------+--------------------------------+----------------------+");

        // Rows
        for (int i = start; i < end; i++) {
            Medicine medicine = medicines.get(i);

            System.out.printf("| %-3d | %-30s | %-20s | %-30s | RM %-17.2f |\n", i+1, medicine.getName(), (medicine.getQuantity() + " " + medicine.getUnit()), DATE_FMT.format(medicine.getExpiryDate()), medicine.getPrice());
        }

        System.out.println("+-----+--------------------------------+----------------------+--------------------------------+----------------------+");
        System.out.println();  // blank line between pages
    }

    public void displayLabTestList(ArrayList<LabTest> labTests, int totalItems, int currentPage, int totalPages, String searchQuery) {
        int start = (currentPage - 1) * pageSize;
        int end   = Math.min(totalItems, start + pageSize);

        if(!(searchQuery == null || searchQuery.isEmpty())) {
            System.out.println("Search query: " + searchQuery);
        }

        // Header
        System.out.printf("Page %d/%d%n", currentPage, totalPages);
        System.out.println("+-----+--------------------------------+----------------------+--------------------------------+----------------------+");
        System.out.printf("| %-3s | %-30s | %-20s | %-30s | %-20s |\n", "No.", "Name", "Price", "Tube Needed", "Lab");
        System.out.println("+-----+--------------------------------+----------------------+--------------------------------+----------------------+");

        // Rows
        for (int i = start; i < end; i++) {
            LabTest labTest = labTests.get(i);

            System.out.printf("| %-3d | %-30s | %-20.2f | %-30s | %-20s |\n", i+1, labTest.getName(), labTest.getPrice(), labTest.getBloodTubes(), labTest.getReferringLab().getName());
        }

        System.out.println("+-----+--------------------------------+----------------------+--------------------------------+----------------------+");
        System.out.println();  // blank line between pages
    }

    public void displayBloodTubeList(ArrayList<BloodTube> bloodTubes, int totalItems, int currentPage, int totalPages, String searchQuery) {
        int start = (currentPage - 1) * pageSize;
        int end   = Math.min(totalItems, start + pageSize);

        if(!(searchQuery == null || searchQuery.isEmpty())) {
            System.out.println("Search query: " + searchQuery);
        }

        // Header
        System.out.printf("Page %d/%d%n", currentPage, totalPages);
        System.out.println("+-----+--------------------------------+----------------------+--------------------------------+----------------------+----------------------+");
        System.out.printf("| %-3s | %-30s | %-20s | %-30s | %-20s | %-20s |\n", "No.", "Name", "Unit", "Expiry Date", "Price", "Cap Colour");
        System.out.println("+-----+--------------------------------+----------------------+--------------------------------+----------------------+----------------------+");

        // Rows
        for (int i = start; i < end; i++) {
            BloodTube bloodTube = bloodTubes.get(i);

            System.out.printf("| %-3d | %-30s | %-20s | %-30s | RM %-17.2f | %-20s |\n", i+1, bloodTube.getName(), (bloodTube.getQuantity()), DATE_FMT.format(bloodTube.getExpiryDate()), bloodTube.getPrice(), bloodTube.getCapColor());
        }

        System.out.println("+-----+--------------------------------+----------------------+--------------------------------+----------------------+----------------------+");
        System.out.println();  // blank line between pages
    }
}

package boundary;

import adt.ArrayList;
import adt.Entry;
import adt.LinkedQueue;
import adt.QueueInterface;
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
        choiceQueue.enqueue("View Sales Item");
        choiceQueue.enqueue("Stock In Sales Item");
        choiceQueue.enqueue("Update Sales Item details");
        choiceQueue.enqueue("Stock Out Sales Item");

        return UI.mainUI("Welcome to Pharmacy Management System", choiceQueue);
    }

    public void displayMedicineList(ArrayList<Entry<MedicineKey, Medicine>> medicines, int totalItems, int currentPage, int totalPages, String searchQuery) {
            int start = (currentPage - 1) * pageSize;
            int end   = Math.min(totalItems, start + pageSize);

            // Header
            System.out.printf("Page %d/%d%n", currentPage, totalPages);
            System.out.println("+-----+----------------------+----------------------+----------------------+----------------------+");
            System.out.printf("| %-3s | %-20s | %-20s | %-20s | %-20s |\n", "No.", "Name", "Brand", "Expiry Date", "Company");
            System.out.println("+-----+----------------------+----------------------+----------------------+----------------------+");

            // Rows
            for (int i = start; i < end; i++) {
                Medicine medicine = medicines.get(i).getValue();

                System.out.printf("| %-3d | %-20s | %-20s | %-20s | %-20s |\n", i+1, medicine.getName(), medicine.getBrand(), DATE_FMT.format(medicine.getExpiryDate()), medicine.getCompany().getName());
            }

            System.out.println("+-----+----------------------+----------------------+----------------------+----------------------+");
            System.out.println();  // blank line between pages
        }
}

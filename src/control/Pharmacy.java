package control;

import adt.ArrayList;
import adt.DictionaryInterface;
import adt.Entry;
import adt.HashedDictionary;
import boundary.PharmacyUI;
import entity.pharmacyManagement.Medicine;
import entity.pharmacyManagement.MedicineKey;
import utility.GenerateMedicineData;

import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Scanner;

public class Pharmacy {

    private static final int PAGE_SIZE = 5;
    private static final SimpleDateFormat DATE_FMT = new SimpleDateFormat("yyyy-MM-dd");

    private DictionaryInterface<MedicineKey, Medicine> meds; // main store (hashed)
    private PharmacyUI UI;
    private Scanner scanner;

    public Pharmacy() {
        try {
            meds = GenerateMedicineData.createMedicineTable(); // HashedDictionary<MedicineKey, Medicine>
            UI = new PharmacyUI();
            scanner = new Scanner(System.in);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // Main menu loop (optional, but nicer UX)
    public void main() throws IOException {
        while (true) {
            Integer choice = UI.mainMenu();
            switch (choice) {
                case 1: // View Sales Items
                    viewSalesItems();
                    break;
                case 2:
                    // other features...
                    break;
                case 3:
                    // other features...
                    break;
                case 4:
                    // other features...
                    break;
                case 999:
                    return;
                default:
                    System.out.println("Invalid choice.");
            }
        }
    }

    public int viewSalesItems() {
        // Pull a fresh view from the hashed dictionary each time we display
        ArrayList<Entry<MedicineKey, Medicine>> entries = meds.entryList();
        return medicineList(entries);
    }

    /**
     * Display table with pagination + search.
     * Uses only ArrayList for the current view, but source of truth is meds (hashed).
     */
    public int medicineList(ArrayList<Entry<MedicineKey, Medicine>> originalView) {
        if (originalView == null || originalView.isEmpty()) {
            System.out.println("No medicines found.");
            return 0;
        }

        // currentView is what we paginate; when searching we rebuild it from meds.entryList()
        ArrayList<Entry<MedicineKey, Medicine>> currentView = new ArrayList<>(originalView);
        int currentPage = 1;
        String searchQuery = "";

        while (true) {
            int totalItems = currentView.size();
            int totalPages = (totalItems + PAGE_SIZE - 1) / PAGE_SIZE;
            if (totalPages == 0) totalPages = 1; // guard

            UI.displayMedicineList(currentView, totalItems, currentPage, totalPages, searchQuery);

            System.out.println("Press: [A] Previous | [D] Next | [S] Search | [R] Reset | [Q] Quit");
            System.out.print("Enter your choice: ");
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
                    System.out.print("Enter search (Name/Brand/Company): ");
                    searchQuery = scanner.nextLine().trim();

                    // Always search against the latest data from the hashed dictionary
                    ArrayList<Entry<MedicineKey, Medicine>> freshAll = meds.entryList();
                    ArrayList<Entry<MedicineKey, Medicine>> filtered = filterMedicines(freshAll, searchQuery);

                    if (filtered.isEmpty()) {
                        System.out.println("No results found for: " + searchQuery);
                        pause();
                        // keep currentView unchanged
                    } else {
                        currentView = filtered;
                        currentPage = 1; // reset to first page of new results
                    }
                    break;

                case "r":
                    // Reset back to full list from hashed store
                    currentView = meds.entryList();
                    searchQuery = "";
                    currentPage = 1;
                    break;

                case "q":
                    return 0;

                default:
                    System.out.println("Invalid choice.");
            }
        }
    }

    private void pause() {
        System.out.print("Press Enter to continue...");
        scanner.nextLine();
    }

    /**
     * Filters by contains(name/brand/company)
     */
    private ArrayList<Entry<MedicineKey, Medicine>> filterMedicines(
            ArrayList<Entry<MedicineKey, Medicine>> source, String query) {

        ArrayList<Entry<MedicineKey, Medicine>> results = new ArrayList<>();
        if (query == null || query.trim().isEmpty()) {
            return results; // empty query → empty results; caller can decide what to do
        }

        String q = query.toLowerCase();

        for (int i = 0; i < source.size(); i++) {
            Entry<MedicineKey, Medicine> e = source.get(i);
            Medicine m = e.getValue();

            String name = safeLower(m.getName());
            String brand = safeLower(m.getBrand());
            String company = (m.getCompany() == null) ? "" : safeLower(m.getCompany().getName());

            boolean textMatch =
                    (name.contains(q) || brand.contains(q) || company.contains(q));

            if (textMatch) {
                results.add(e);
            }
        }
        return results;
    }

    private static String safeLower(String s) {
        return (s == null) ? "" : s.toLowerCase();
    }
}

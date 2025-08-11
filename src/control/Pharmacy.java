package control;

import adt.ArrayList;
import adt.DictionaryInterface;
import adt.HashedDictionary;
import boundary.PharmacyUI;
import entity.pharmacyManagement.BloodTube;
import entity.pharmacyManagement.LabTest;
import entity.pharmacyManagement.Medicine;
import entity.pharmacyManagement.MedicineKey;
import utility.GeneratePharmacyData;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Scanner;

public class Pharmacy {

    private static final int PAGE_SIZE = 5;
    private static final SimpleDateFormat DATE_FMT = new SimpleDateFormat("yyyy-MM-dd");

    private DictionaryInterface<MedicineKey, Medicine> meds;
    private DictionaryInterface<String, LabTest> labTests;
    private DictionaryInterface<String, BloodTube> bloodTubeInventory;
    private PharmacyUI UI;
    private Scanner scanner;

    public Pharmacy() {
        try {
            meds = GeneratePharmacyData.createMedicineTable();
            labTests = GeneratePharmacyData.createLabTests();
            bloodTubeInventory = GeneratePharmacyData.createBloodTubeInventory();
            UI = new PharmacyUI();
            scanner = new Scanner(System.in);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void main() throws IOException {
        while (true) {
            Integer choice = UI.mainMenu();
            switch (choice) {
                case 1: // View Sales Items
                    viewInventory();
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

    public void viewInventory() throws IOException {
        while (true) {
            Integer choice = UI.viewInventory();
            switch (choice) {
                case 1: // View Medicine
                    if (meds instanceof HashedDictionary) {
                        HashedDictionary<MedicineKey, Medicine> hashedMeds = (HashedDictionary<MedicineKey, Medicine>) meds;
                        ArrayList<Medicine> medicines = hashedMeds.valueList();
                        medicineList(medicines);
                    }
                    break;
                case 2:
                    // View Lab Test
                    if (labTests instanceof HashedDictionary) {
                        HashedDictionary<String, LabTest> hashedLabTests = (HashedDictionary<String, LabTest>) labTests;
                        ArrayList<LabTest> labTestList = hashedLabTests.valueList();
                        labTestList(labTestList);
                    }
                    break;
                case 3:
                    // View Blood Tube Stock
                    if (bloodTubeInventory instanceof HashedDictionary) {
                        HashedDictionary<String, BloodTube> hashedBloodTubeInventory = (HashedDictionary<String, BloodTube>) bloodTubeInventory;
                        ArrayList<BloodTube> bloodTubeList = hashedBloodTubeInventory.valueList();
                        bloodTubeList(bloodTubeList);
                    }
                    break;
                case 4:
                    // View Insufficient Stock
                    break;
                case 999:
                    return;
                default:
                    System.out.println("Invalid choice.");
            }
        }
    }

    public void labTestList(ArrayList<LabTest> originalView) {
        if (originalView == null || originalView.isEmpty()) {
            System.out.println("No lab test found.");
            return;
        }

        ArrayList<LabTest> currentView = new ArrayList<>(originalView);
        int currentPage = 1;
        String searchQuery = "";

        while (true) {
            int totalItems = currentView.size();
            int totalPages = (totalItems + PAGE_SIZE - 1) / PAGE_SIZE;
            if (totalPages == 0) totalPages = 1; // guard

            UI.displayLabTestList(currentView, totalItems, currentPage, totalPages, searchQuery);

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
                    System.out.print("Enter search (Name/Tube Needed/Lab): ");
                    searchQuery = scanner.nextLine().trim();

                    ArrayList<LabTest> filtered = filterLabTest(originalView, searchQuery);

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
                    currentView = originalView;
                    searchQuery = "";
                    currentPage = 1;
                    break;

                case "q":
                    return ;

                default:
                    System.out.println("Invalid choice.");
            }
        }
    }

    /**
     * Display table with pagination + search.
     * Uses only ArrayList for the current view, but source of truth is meds (hashed).
     */
    public void medicineList(ArrayList<Medicine> originalView) {
        if (originalView == null || originalView.isEmpty()) {
            System.out.println("No medicines found.");
            return;
        }

        ArrayList<Medicine> currentView = new ArrayList<>(originalView);
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

                    ArrayList<Medicine> filtered = filterMedicines(originalView, searchQuery);

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
                    currentView = originalView;
                    searchQuery = "";
                    currentPage = 1;
                    break;

                case "q":
                    return ;

                default:
                    System.out.println("Invalid choice.");
            }
        }
    }

    public void bloodTubeList(ArrayList<BloodTube> originalView) {
        if (originalView == null || originalView.isEmpty()) {
            System.out.println("No lab test found.");
            return;
        }

        ArrayList<BloodTube> currentView = new ArrayList<>(originalView);
        int currentPage = 1;
        String searchQuery = "";

        while (true) {
            int totalItems = currentView.size();
            int totalPages = (totalItems + PAGE_SIZE - 1) / PAGE_SIZE;
            if (totalPages == 0) totalPages = 1; // guard

            UI.displayBloodTubeList(currentView, totalItems, currentPage, totalPages, searchQuery);

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
                    System.out.print("Enter search (Name/Cap color): ");
                    searchQuery = scanner.nextLine().trim();

                    // Always search against the latest data from the hashed dictionary
                    ArrayList<BloodTube> filtered = filterBloodTube(originalView, searchQuery);

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
                    currentView = originalView;
                    searchQuery = "";
                    currentPage = 1;
                    break;

                case "q":
                    return ;

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
    private ArrayList<Medicine> filterMedicines(
            ArrayList<Medicine> source, String query) {

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

    private ArrayList<LabTest> filterLabTest(
            ArrayList<LabTest> source, String query) {

        ArrayList<LabTest> results = new ArrayList<>();
        if (query == null || query.trim().isEmpty()) {
            return results;
        }

        String q = query.toLowerCase();

        for (int i = 0; i < source.size(); i++) {
            LabTest labTest = source.get(i);

            String name = safeLower(labTest.getName());
            String bloodTubeNeeded = safeLower(labTest.getBloodTubes());
            String referringLab = (labTest.getReferringLab() == null) ? "" : safeLower(labTest.getReferringLab().getName());

            boolean textMatch =
                    (name.contains(q) || bloodTubeNeeded.contains(q) || referringLab.contains(q));

            if (textMatch) {
                results.add(labTest);
            }
        }
        return results;
    }

    private ArrayList<BloodTube> filterBloodTube(
            ArrayList<BloodTube> source, String query) {

        ArrayList<BloodTube> results = new ArrayList<>();
        if (query == null || query.trim().isEmpty()) {
            return results;
        }

        String q = query.toLowerCase();

        for (int i = 0; i < source.size(); i++) {
            BloodTube bloodTube = source.get(i);

            String name = safeLower(bloodTube.getName());
            String color = safeLower(bloodTube.getCapColor());

            boolean textMatch =
                    (name.contains(q) || color.contains(q));

            if (textMatch) {
                results.add(bloodTube);
            }
        }
        return results;
    }

    private static String safeLower(String s) {
        return (s == null) ? "" : s.toLowerCase();
    }

    public DictionaryInterface<MedicineKey, Medicine> getMeds() {
        return meds;
    }

    public void setMeds(DictionaryInterface<MedicineKey, Medicine> meds) {
        this.meds = meds;
    }

    public DictionaryInterface<String, LabTest> getLabTests() {
        return labTests;
    }

    public void setLabTests(DictionaryInterface<String, LabTest> labTests) {
        this.labTests = labTests;
    }

    public DictionaryInterface<String, BloodTube> getBloodTubeInventory() {
        return bloodTubeInventory;
    }

    public void setBloodTubeInventory(DictionaryInterface<String, BloodTube> bloodTubeInventory) {
        this.bloodTubeInventory = bloodTubeInventory;
    }
}

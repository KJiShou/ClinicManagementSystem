package control;

import adt.*;
import boundary.PharmacyUI;
import entity.pharmacyManagement.*;
import utility.GeneratePharmacyData;
import utility.MessageUI;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Scanner;
import java.util.UUID;

import static utility.MessageUI.askPositiveDouble;
import static utility.MessageUI.askPositiveInt;

public class Pharmacy {

    private static final int PAGE_SIZE = 5;
    private static final SimpleDateFormat DATE_FMT = new SimpleDateFormat("yyyy-MM-dd");

    private DictionaryInterface<String, Medicine> meds;
    private DictionaryInterface<String, LabTest> labTests;
    private DictionaryInterface<String, BloodTube> bloodTubeInventory;
    private PharmacyUI UI;
    private MessageUI messageUI;
    private Scanner scanner;

    public Pharmacy(DictionaryInterface<String, Medicine> medicines, DictionaryInterface<String, LabTest> labTests, DictionaryInterface<String, BloodTube> bloodTubeInventory) {
        try {
            meds = medicines;
            this.labTests = labTests;
            this.bloodTubeInventory = bloodTubeInventory;
            UI = new PharmacyUI();
            messageUI = new MessageUI();
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
                    // View Inventory
                    viewInventory();
                    break;
                case 2:
                    // Stock In Item
                    stockInItem();
                    break;
                case 3:
                    // Update Item details
                    break;
                case 4:
                    // Stock out Sales Item
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
                    if (meds instanceof HashedDictionary<String, Medicine> hashedMeds) {
                        ArrayList<Medicine> medicines = hashedMeds.valueList();
                        medicineList(medicines);
                    }
                    break;
                case 2:
                    // View Lab Test
                    if (labTests instanceof HashedDictionary<String, LabTest> hashedLabTests) {
                        ArrayList<LabTest> labTestList = hashedLabTests.valueList();
                        labTestList(labTestList);
                    }
                    break;
                case 3:
                    // View Blood Tube Stock
                    if (bloodTubeInventory instanceof HashedDictionary<String, BloodTube> hashedBloodTubeInventory) {
                        ArrayList<BloodTube> bloodTubeList = hashedBloodTubeInventory.valueList();
                        bloodTubeList(bloodTubeList);
                    }
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

            System.out.println("Press: [A] Previous | [D] Next | [S] Search | [I] Insufficient Stock | [R] Reset | [Q] Quit");
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

                case "i":
                    UI.displayInsufficientMedicines(meds);

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

            System.out.println("Press: [A] Previous | [D] Next | [S] Search | [I] insufficient Stock | [R] Reset | [Q] Quit");
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

                case "i":
                    UI.displayInsufficientBloodTubes(bloodTubeInventory);

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

    private void stockInItem() throws IOException {
        int choice = UI.stockInSelectionUI();

        switch (choice) {
            case 1:
                // Stock In Medicine
                stockInMedicine();
                break;
            case 2:
                // Stock In Lab Test
                createOrUpdateLabTest();
                break;
            case 3:
                // Stock In Blood Tube
                stockInBloodTube();
                break;
            case 999:
                return;
            default:
                System.out.println("Invalid choice.");
        }
    }

    private void stockInMedicine() {
        @SuppressWarnings("unchecked")
        HashedDictionary<String, Medicine> dict = (HashedDictionary<String, Medicine>) meds;

        System.out.println("\n=== Stock In Medicine ===");

        // Collect all medicine details from user
        System.out.print("Name: ");
        String name = scanner.nextLine().trim();

        System.out.print("Brand: ");
        String brand = scanner.nextLine().trim();

        System.out.print("Strength (e.g., 500mg): ");
        String strength = scanner.nextLine().trim();

        boolean unitFlag = true;
        String unit = "";
        while (unitFlag) {
            System.out.print("Unit (e.g., tablet, ml, capsule): ");
            unit = scanner.nextLine().trim();
            if (unit.equalsIgnoreCase("tablet") ||
                    unit.equalsIgnoreCase("ml") ||
                    unit.equalsIgnoreCase("capsule")) {
                unitFlag = false;
            } else {
                System.out.println("Invalid unit");
            }
        }

        int addQty = askPositiveInt(scanner, "Quantity to add: ");
        double price = askPositiveDouble(scanner, "Unit Price (RM): ");

        System.out.print("Description: ");
        String desc = scanner.nextLine().trim();

        // NEW: choose existing company or create a new one
        Company company = selectOrCreateCompany();

        System.out.print("Expiry date (yyyy-MM-dd): ");
        Date expiry;
        try {
            expiry = DATE_FMT.parse(scanner.nextLine().trim());
        } catch (Exception e) {
            System.out.println("Invalid date format, using today.");
            expiry = new Date();
        }

        // Build a temporary Medicine to generate the key (name|strength|expiry-YYYY-MM-DD)
        Medicine temp = new Medicine(
                UUID.randomUUID(), name, addQty, price, desc, unit,
                company, brand, strength, expiry
        );
        String key = temp.getMedicineKey();

        // Upsert into dictionary
        if (dict.contains(key)) {
            Medicine existing = dict.getValue(key);
            existing.setQuantity(existing.getQuantity() + addQty);
            System.out.printf("Updated: %s | %d %s\n",
                    existing.getName(), existing.getQuantity(), existing.getUnit());
        } else {
            dict.add(key, temp);
            System.out.printf("Added NEW: %s | %d %s\n",
                    temp.getName(), temp.getQuantity(), temp.getUnit());
        }

        System.out.println("Press Enter to continue...");
        scanner.nextLine();
    }


    // Let user choose an existing company (from current medicines) or create a new one.
    private Company selectOrCreateCompany() {
        @SuppressWarnings("unchecked")
        HashedDictionary<String, Medicine> dict = (HashedDictionary<String, Medicine>) meds;

        // Build a unique list of companies by name (avoid duplicates).
        ArrayList<Company> uniqueCompanies = new ArrayList<>();
        ArrayList<String> seen = new ArrayList<>();

        ArrayList<Medicine> allMeds = dict.valueList();
        for (int i = 0; i < allMeds.size(); i++) {
            Medicine m = allMeds.get(i);
            if (m.getCompany() != null) {
                String name = m.getCompany().getName();
                if (name != null && !seen.contains(name)) {
                    uniqueCompanies.add(m.getCompany());
                    seen.add(name);
                }
            }
        }

        // If none exist, force creating a new one
        if (uniqueCompanies.isEmpty()) {
            System.out.println("No companies found. Please create a new company.");
            return createCompanyViaPrompt();
        }

        // Build the choices queue for your pretty UI
        QueueInterface<String> q = new LinkedQueue<>();
        q.enqueue("Create new company");        // Option 1
        for (int i = 0; i < uniqueCompanies.size(); i++) {
            q.enqueue(uniqueCompanies.get(i).getName());  // Options 2..N+1
        }

        // Use your box UI to get choice (1..N+1 or 999 to exit)
        Integer choice = messageUI.mainUI("Select Company", q);

        if (choice == null || choice == 999) {
            System.out.println("Cancelled.");
            return null; // caller can decide to abort the flow
        }

        if (choice == 1) {
            // Create new
            return createCompanyViaPrompt();
        }

        // Map choice to list index: choice 2 -> index 0
        int idx = choice - 2;
        if (idx < 0 || idx >= uniqueCompanies.size()) {
            System.out.println("Invalid selection.");
            return null;
        }
        return uniqueCompanies.get(idx);
    }

    // Prompt user to create a new company object.
    private Company createCompanyViaPrompt() {
        System.out.print("Company name: ");
        String name = scanner.nextLine().trim();

        System.out.print("Address (optional): ");
        String address = scanner.nextLine().trim();

        System.out.print("Email (optional): ");
        String email = scanner.nextLine().trim();

        System.out.print("Phone (optional): ");
        String phone = scanner.nextLine().trim();

        return new Company(UUID.randomUUID(), name,
                address.isEmpty() ? "-" : address,
                email.isEmpty() ? "-" : email,
                phone.isEmpty() ? "-" : phone);
    }

    // ---- create (or upsert) a LabTest
    private void createOrUpdateLabTest() {
        @SuppressWarnings("unchecked")
        HashedDictionary<String, LabTest> dict =
                (HashedDictionary<String, LabTest>) labTests;

        System.out.println("\n=== Create / Update Lab Test ===");

        System.out.print("Test Name: ");
        String name = scanner.nextLine().trim();

        // often provided by referring lab; keep optional
        System.out.print("Code (optional): ");
        String code = scanner.nextLine().trim();

        double price = askPositiveDouble(scanner, "Price (RM): ");

        System.out.print("Description: ");
        String desc = scanner.nextLine().trim();

        Company referringLab = selectOrCreateCompany();

        boolean fastingRequired = askYesNo("Fasting required? (y/n): ");

        System.out.print("Patient precautions (optional): ");
        String patientPrecautions = scanner.nextLine().trim();

        // NEW: pick blood tubes from inventory (multi-select)
        BloodTube bloodTubes = pickBloodTubesFromInventory();

        LabTest test = new LabTest(
                UUID.randomUUID(),
                name,
                price,
                desc,
                referringLab,
                code,
                fastingRequired,
                patientPrecautions,
                bloodTubes.getName()
        );

        String key = name; // keep your key logic, or switch to name|lab|code for uniqueness

        if (dict.contains(key)) {
            dict.add(key, test); // overwrites (add acts like upsert in your impl)
            System.out.println("Updated Lab Test: " + name + " (" + referringLab.getName() + ")");
        } else {
            dict.add(key, test);
            System.out.println("Added NEW Lab Test: " + name + " (" + referringLab.getName() + ")");
        }

        System.out.println("Press Enter to continue...");
        scanner.nextLine();
    }

    @SuppressWarnings("unchecked")
    private BloodTube pickBloodTubesFromInventory() {
        HashedDictionary<String, BloodTube> tubeDict =
                (HashedDictionary<String, BloodTube>) bloodTubeInventory;

        ArrayList<BloodTube> all = tubeDict.valueList();
        if (all.isEmpty()) {
            System.out.println("No BloodTube in inventory.");
            System.out.print("Enter blood tubes manually (comma separated), or leave blank to skip: ");
            return null;
        }

        HashedDictionary<String, BloodTube> unique = new HashedDictionary<>();
        for (BloodTube t : all) {
            unique.add(t.getBloodTubeKey(), t);
        }

        ArrayList<BloodTube> labels = new ArrayList<>(unique.valueList());
        while (true) {
            System.out.println("\nSelect Blood Tube required for this test:");
            for (int i = 0; i < labels.size(); i++) {
                System.out.printf("  [%d] %s %.1fml%n",
                        i + 1,
                        labels.get(i).getName(),
                        labels.get(i).getVolumeMl());
            }
            System.out.printf("Enter a number [1-%d]: ", labels.size());

            String raw = scanner.nextLine().trim();
            if (raw.isEmpty()) {
                System.out.println("Please enter a number.");
                continue;
            }

            int choice;
            try {
                choice = Integer.parseInt(raw);
            } catch (NumberFormatException e) {
                System.out.println("Invalid input. Please enter a whole number.");
                continue;
            }

            if (choice < 1 || choice > labels.size()) {
                System.out.println("Out of range. Choose between 1 and " + labels.size() + ".");
                continue;
            }

            // valid
            return labels.get(choice - 1);
        }

    }

    private boolean askYesNo(String prompt) {
        while (true) {
            System.out.print(prompt);
            String s = scanner.nextLine().trim().toLowerCase();
            if (s.equals("y") || s.equals("yes")) return true;
            if (s.equals("n") || s.equals("no")) return false;
            System.out.println("Please answer y/n.");
        }
    }

    private void stockInBloodTube() {
        @SuppressWarnings("unchecked")
        HashedDictionary<String, BloodTube> dict =
                (HashedDictionary<String, BloodTube>) bloodTubeInventory;

        System.out.println("\n=== Stock In Blood Tube ===");

        // Basic details
        System.out.print("Tube Name: ");
        String name = scanner.nextLine().trim();

        Company company = selectOrCreateCompany();

        double volumeMl = askPositiveDouble(scanner, "Volume (ml): ");

        System.out.print("Cap Color (e.g., red, purple, blue): ");
        String capColor = scanner.nextLine().trim();

        System.out.print("Additive (e.g., EDTA, Citrate, None): ");
        String additive = scanner.nextLine().trim();

        int addQty = askPositiveInt(scanner, "Quantity to add: ");
        double price = askPositiveDouble(scanner, "Unit Price (RM): ");

        System.out.print("Description: ");
        String desc = scanner.nextLine().trim();

        System.out.print("Expiry date (yyyy-MM-dd): ");
        Date expiry;
        try {
            expiry = DATE_FMT.parse(scanner.nextLine().trim());
        } catch (Exception e) {
            System.out.println("Invalid date format, using today.");
            expiry = new Date();
        }

        // Create new object
        BloodTube tube = new BloodTube(
                UUID.randomUUID(),
                name,
                price,
                addQty,
                desc,
                company,
                expiry,
                volumeMl,
                capColor,
                additive
        );

        // Build unique key
        String key = tube.getBloodTubeKey();

        // Upsert into dictionary
        if (dict.contains(key)) {
            BloodTube existing = dict.getValue(key);
            existing.setQuantity(existing.getQuantity() + addQty);
            System.out.printf("Updated: %s | %d tubes (%.1f ml, %s, %s)\n",
                    existing.getName(), existing.getQuantity(),
                    existing.getVolumeMl(), existing.getCapColor(),
                    existing.getAdditive());
        } else {
            dict.add(key, tube);
            System.out.printf("Added NEW: %s | %d tubes (%.1f ml, %s, %s)\n",
                    tube.getName(), tube.getQuantity(),
                    tube.getVolumeMl(), tube.getCapColor(),
                    tube.getAdditive());
        }

        System.out.println("Press Enter to continue...");
        scanner.nextLine();
    }

    private static String safeLower(String s) {
        return (s == null) ? "" : s.toLowerCase();
    }

    public DictionaryInterface<String, Medicine> getMeds() {
        return meds;
    }

    public void setMeds(DictionaryInterface<String, Medicine> meds) {
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

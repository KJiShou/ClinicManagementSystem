// Kong Ji Shou
package control;

import adt.*;
import adt.ArrayList;
import boundary.PharmacyUI;
import entity.pharmacyManagement.*;
import utility.MessageUI;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.Comparator;
import java.util.Date;
import java.util.Scanner;
import java.util.UUID;

import static utility.MessageUI.askPositiveDouble;
import static utility.MessageUI.askPositiveInt;

public class PharmacyControl {

    private static final int PAGE_SIZE = 5;
    private static final SimpleDateFormat DATE_FMT = new SimpleDateFormat("yyyy-MM-dd");

    private DictionaryInterface<String, Medicine> meds;
    private DictionaryInterface<String, LabTest> labTests;
    private DictionaryInterface<String, BloodTube> bloodTubeInventory;
    private PharmacyUI UI;
    private MessageUI messageUI;
    private Scanner scanner;

    public PharmacyControl(DictionaryInterface<String, Medicine> medicines, DictionaryInterface<String, LabTest> labTests, DictionaryInterface<String, BloodTube> bloodTubeInventory) {
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
                    updateItemDetails();
                    break;
                case 4:
                    // Stock out Sales Item
                    stockOutItem();
                    break;
                case 5:
                    generateLowStockReport((HashedDictionary<String, Medicine>) meds, 10);
                    break;
                case 6:
                    generateExpiryAlertReport((HashedDictionary<String, Medicine>) meds, 100);
                    break;
                case 999:
                    return;
                default:
                    System.out.println("Invalid choice.");
            }
        }
    }

    private void stockOutItem() throws IOException {
        while (true) {
            Integer choice = UI.stockOutItem();

            if (choice == 999) return;

            switch (choice) {
                case 1:
                    if (meds instanceof HashedDictionary<String, Medicine> hashedMeds) {
                        stockOutMedicine(hashedMeds);
                    } else {
                        System.out.println("Medicine store not available.");
                        pause();
                    }
                    break;
                case 2:
                    if (bloodTubeInventory instanceof HashedDictionary<String, BloodTube> hashedTubes) {
                        stockOutBloodTube(hashedTubes);
                    } else {
                        System.out.println("Blood tube store not available.");
                        pause();
                    }
                    break;
                default:
                    System.out.println("Invalid choice.");
            }
        }
    }

    private void stockOutMedicine(HashedDictionary<String, Medicine> dict) {
        if (dict == null || dict.isEmpty()) {
            System.out.println("No medicines available.");
            pause();
            return;
        }

        ArrayList<Medicine> originalView = dict.valueList();
        ArrayList<Medicine> currentView = new ArrayList<>(originalView);
        int currentPage = 1;
        String searchQuery = "";

        while (true) {
            int totalItems = currentView.size();
            int totalPages = Math.max(1, (totalItems + PAGE_SIZE - 1) / PAGE_SIZE);

            UI.displayMedicineList(currentView, totalItems, currentPage, totalPages, searchQuery);
            System.out.println("Press: [A] Prev | [D] Next | [S] Search | [R] Reset | [O] Stock Out | [Q] Quit");
            System.out.print("Enter your choice: ");
            String input = scanner.nextLine().trim().toLowerCase();

            switch (input) {
                case "a":
                    if (currentPage > 1) currentPage--;
                    else { System.out.println("This is the first page."); pause(); }
                    break;
                case "d":
                    if (currentPage < totalPages) currentPage++;
                    else { System.out.println("This is the last page."); pause(); }
                    break;
                case "s": {
                    System.out.print("Enter search (Name/Brand/Company): ");
                    searchQuery = scanner.nextLine().trim();
                    ArrayList<Medicine> filtered = filterMedicines(dict.valueList(), searchQuery);
                    if (filtered.isEmpty()) { System.out.println("No results found for: " + searchQuery); pause(); }
                    else { currentView = filtered; currentPage = 1; }
                    break;
                }
                case "r":
                    originalView = dict.valueList();
                    currentView = new ArrayList<>(originalView);
                    searchQuery = ""; currentPage = 1;
                    break;
                case "o": {
                    if (currentView.isEmpty()) { System.out.println("No items on this page."); pause(); break; }

                    int start = (currentPage - 1) * PAGE_SIZE;
                    int endExclusive = Math.min(start + PAGE_SIZE, totalItems);

                    System.out.printf("Select item [%d-%d] on this page to stock out: ", start + 1, endExclusive);
                    String raw = scanner.nextLine().trim();
                    int pick;
                    try { pick = Integer.parseInt(raw); } catch (NumberFormatException ex) { System.out.println("Invalid number."); pause(); break; }
                    if (pick < start + 1 || pick > endExclusive) { System.out.println("Out of range."); pause(); break; }

                    Medicine chosen = currentView.get(pick - 1);
                    String key = chosen.getMedicineKey();

                    System.out.printf("Current qty: %d. Enter quantity to stock out: ", chosen.getQuantity());
                    int outQty;
                    try { outQty = Integer.parseInt(scanner.nextLine().trim()); }
                    catch (NumberFormatException ex) { System.out.println("Invalid quantity."); pause(); break; }

                    if (outQty <= 0) { System.out.println("Quantity must be positive."); pause(); break; }

                    int available = chosen.getQuantity();
                    if (outQty > available) {
                        System.out.printf("Insufficient stock. Available: %d. Stock out all available instead? (y/n): ", available);
                        if (!askYesNo("")) { pause(); break; }
                        outQty = available;
                    }

                    int newQty = available - outQty;
                    chosen.setQuantity(Math.max(0, newQty));

                    System.out.printf("Stocked out %d of %s. New qty: %d%n", outQty, chosen.getName(), chosen.getQuantity());

                    if (chosen.getQuantity() == 0) {
                        System.out.print("Quantity is 0. Remove this lot from inventory? (y/n): ");
                        if (askYesNo("")) {
                            if (dict.contains(key)) dict.remove(key);
                            System.out.println("Removed from inventory.");
                        }
                    }

                    // refresh views
                    originalView = dict.valueList();
                    currentView = searchQuery.isEmpty() ? new ArrayList<>(originalView) : filterMedicines(originalView, searchQuery);
                    int newTotal = currentView.size();
                    int newPages = Math.max(1, (newTotal + PAGE_SIZE - 1) / PAGE_SIZE);
                    currentPage = Math.min(currentPage, newPages);

                    pause();
                    break;
                }
                case "q":
                    return;
                default:
                    System.out.println("Invalid choice.");
            }
        }
    }

    private void stockOutBloodTube(HashedDictionary<String, BloodTube> dict) {
        if (dict == null || dict.isEmpty()) {
            System.out.println("No blood tubes available.");
            pause();
            return;
        }

        ArrayList<BloodTube> originalView = dict.valueList();
        ArrayList<BloodTube> currentView = new ArrayList<>(originalView);
        int currentPage = 1;
        String searchQuery = "";

        while (true) {
            int totalItems = currentView.size();
            int totalPages = Math.max(1, (totalItems + PAGE_SIZE - 1) / PAGE_SIZE);

            UI.displayBloodTubeList(currentView, totalItems, currentPage, totalPages, searchQuery);
            System.out.println("Press: [A] Prev | [D] Next | [S] Search | [R] Reset | [O] Stock Out | [Q] Quit");
            System.out.print("Enter your choice: ");
            String input = scanner.nextLine().trim().toLowerCase();

            switch (input) {
                case "a":
                    if (currentPage > 1) currentPage--;
                    else { System.out.println("This is the first page."); pause(); }
                    break;
                case "d":
                    if (currentPage < totalPages) currentPage++;
                    else { System.out.println("This is the last page."); pause(); }
                    break;
                case "s": {
                    System.out.print("Enter search (Name/Cap color): ");
                    searchQuery = scanner.nextLine().trim();
                    ArrayList<BloodTube> filtered = filterBloodTube(dict.valueList(), searchQuery);
                    if (filtered.isEmpty()) { System.out.println("No results found for: " + searchQuery); pause(); }
                    else { currentView = filtered; currentPage = 1; }
                    break;
                }
                case "r":
                    originalView = dict.valueList();
                    currentView = new ArrayList<>(originalView);
                    searchQuery = ""; currentPage = 1;
                    break;
                case "o": {
                    if (currentView.isEmpty()) { System.out.println("No items on this page."); pause(); break; }

                    int start = (currentPage - 1) * PAGE_SIZE;
                    int endExclusive = Math.min(start + PAGE_SIZE, totalItems);

                    System.out.printf("Select item [%d-%d] on this page to stock out: ", start+1, endExclusive);
                    String raw = scanner.nextLine().trim();
                    int pick;
                    try { pick = Integer.parseInt(raw); } catch (NumberFormatException ex) { System.out.println("Invalid number."); pause(); break; }
                    if (pick < start+1 || pick > endExclusive) { System.out.println("Out of range."); pause(); break; }

                    BloodTube chosen = currentView.get(pick - 1);
                    String key = chosen.getBloodTubeKey();

                    System.out.printf("Current qty: %d. Enter quantity to stock out: ", chosen.getQuantity());
                    int outQty;
                    try { outQty = Integer.parseInt(scanner.nextLine().trim()); }
                    catch (NumberFormatException ex) { System.out.println("Invalid quantity."); pause(); break; }

                    if (outQty <= 0) { System.out.println("Quantity must be positive."); pause(); break; }

                    int available = chosen.getQuantity();
                    if (outQty > available) {
                        System.out.printf("Insufficient stock. Available: %d. Stock out all available instead? (y/n): ", available);
                        if (!askYesNo("")) { pause(); break; }
                        outQty = available;
                    }

                    int newQty = available - outQty;
                    chosen.setQuantity(Math.max(0, newQty));

                    System.out.printf("Stocked out %d of %s (%.1f ml, %s, %s). New qty: %d%n",
                            outQty, chosen.getName(), chosen.getVolumeMl(), chosen.getCapColor(), chosen.getAdditive(), chosen.getQuantity());

                    if (chosen.getQuantity() == 0) {
                        System.out.print("Quantity is 0. Remove this lot from inventory? (y/n): ");
                        if (askYesNo("")) {
                            if (dict.contains(key)) dict.remove(key);
                            System.out.println("Removed from inventory.");
                        }
                    }

                    // refresh views
                    originalView = dict.valueList();
                    currentView = searchQuery.isEmpty() ? new ArrayList<>(originalView) : filterBloodTube(originalView, searchQuery);
                    int newTotal = currentView.size();
                    int newPages = Math.max(1, (newTotal + PAGE_SIZE - 1) / PAGE_SIZE);
                    currentPage = Math.min(currentPage, newPages);

                    pause();
                    break;
                }
                case "q":
                    return;
                default:
                    System.out.println("Invalid choice.");
            }
        }
    }

    public void updateItemDetails() throws IOException {
        while (true) {
            Integer choice = UI.updateItem();
            switch (choice) {
                case 1: // View Medicine
                    if (meds instanceof HashedDictionary<String, Medicine> hashedMeds) {
                        updateMedicine(hashedMeds);
                    }
                    break;
                case 2:
                    // View Lab Test
                    if (labTests instanceof HashedDictionary<String, LabTest> hashedLabTests) {
                        updateLabTest(hashedLabTests);
                    }
                    break;
                case 3:
                    // View Blood Tube Stock
                    if (bloodTubeInventory instanceof HashedDictionary<String, BloodTube> hashedBloodTubeInventory) {
                        updateBloodTube(hashedBloodTubeInventory);
                    }
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


    public Medicine chooseMedicine(HashedDictionary<String, Medicine> dict) {
        if (dict == null || dict.isEmpty()) {
            System.out.println("No medicines available to choose from.");
            return null;
        }

        ArrayList<Medicine> originalView = dict.valueList();
        ArrayList<Medicine> currentView = new ArrayList<>(originalView);
        int currentPage = 1;
        String searchQuery = "";

        while (true) {
            int totalItems = currentView.size();
            int totalPages = (totalItems + PAGE_SIZE - 1) / PAGE_SIZE;
            if (totalPages == 0) totalPages = 1;

            UI.displayMedicineList(currentView, totalItems, currentPage, totalPages, searchQuery);

            System.out.println("Press: [A] Prev | [D] Next | [S] Search | [R] Reset | [C] Choose item | [Q] Quit");
            System.out.print("Enter your choice: ");
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

                case "c": {
                    if (currentView.isEmpty()) {
                        System.out.println("No items to choose from.");
                        pause();
                        break;
                    }

                    int start = (currentPage - 1) * PAGE_SIZE;
                    int endExclusive = Math.min(start + PAGE_SIZE, totalItems);
                    int visibleCount = endExclusive - start;

                    System.out.printf("Select item [1-%d] on this page to choose: ", visibleCount);
                    String raw = scanner.nextLine().trim();
                    int pick;

                    try {
                        pick = Integer.parseInt(raw);
                    } catch (NumberFormatException ex) {
                        System.out.println("Invalid number.");
                        pause();
                        break;
                    }

                    if (pick < 1 || pick > visibleCount) {
                        System.out.println("Out of range.");
                        pause();
                        break;
                    }

                    Medicine chosen = currentView.get(start + (pick - 1));
                    System.out.println("Selected: " + chosen.getName() + " - " + chosen.getBrand());
                    return chosen;
                }

                case "q":
                    System.out.println("No medicine selected.");
                    return null;

                default:
                    System.out.println("Invalid choice.");
            }
        }
    }

    public LabTest chooseLabTest(HashedDictionary<String, LabTest> dict) {
        if (dict == null || dict.isEmpty()) {
            System.out.println("No lab tests available to choose from.");
            return null;
        }

        ArrayList<LabTest> originalView = dict.valueList();
        ArrayList<LabTest> currentView = new ArrayList<>(originalView);
        int currentPage = 1;
        String searchQuery = "";

        while (true) {
            int totalItems = currentView.size();
            int totalPages = (totalItems + PAGE_SIZE - 1) / PAGE_SIZE;
            if (totalPages == 0) totalPages = 1;

            UI.displayLabTestList(currentView, totalItems, currentPage, totalPages, searchQuery);

            System.out.println("Press: [A] Prev | [D] Next | [S] Search | [R] Reset | [C] Choose item | [Q] Quit");
            System.out.print("Enter your choice: ");
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

                case "s":
                    System.out.print("Enter search (Name/Tube Needed/Lab): ");
                    searchQuery = scanner.nextLine().trim();
                    ArrayList<LabTest> filtered = filterLabTest(originalView, searchQuery);
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

                case "c": {
                    if (currentView.isEmpty()) {
                        System.out.println("No items to choose from.");
                        pause();
                        break;
                    }

                    int start = (currentPage - 1) * PAGE_SIZE;
                    int endExclusive = Math.min(start + PAGE_SIZE, totalItems);

                    System.out.printf("Select item [%d-%d] on this page to choose: ", start + 1, endExclusive);
                    String raw = scanner.nextLine().trim();
                    int pick;

                    try {
                        pick = Integer.parseInt(raw);
                    } catch (NumberFormatException ex) {
                        System.out.println("Invalid number.");
                        pause();
                        break;
                    }

                    if (pick < start+1 || pick > endExclusive) {
                        System.out.println("Out of range.");
                        pause();
                        break;
                    }

                    LabTest chosen = currentView.get(pick - 1);
                    System.out.println("Selected: " + chosen.getName() + 
                        " - " + (chosen.getReferringLab() != null ? chosen.getReferringLab().getName() : "Unknown Lab"));
                    return chosen;
                }

                case "q":
                    System.out.println("No lab test selected.");
                    return null;

                default:
                    System.out.println("Invalid choice.");
            }
        }
    }

    public void updateMedicine(HashedDictionary<String, Medicine> dict) {
        if (dict == null || dict.isEmpty()) {
            System.out.println("No medicines to update.");
            return;
        }

        ArrayList<Medicine> originalView = dict.valueList();
        ArrayList<Medicine> currentView = new ArrayList<>(originalView);
        int currentPage = 1;
        String searchQuery = "";

        while (true) {
            int totalItems = currentView.size();
            int totalPages = (totalItems + PAGE_SIZE - 1) / PAGE_SIZE;
            if (totalPages == 0) totalPages = 1;

            UI.displayMedicineList(currentView, totalItems, currentPage, totalPages, searchQuery);

            System.out.println("Press: [A] Prev | [D] Next | [S] Search | [R] Reset | [E] Edit item | [Q] Quit");
            System.out.print("Enter your choice: ");
            String input = scanner.nextLine().trim().toLowerCase();

            switch (input) {
                case "a":
                    if (currentPage > 1) currentPage--;
                    else { System.out.println("This is the first page."); pause(); }
                    break;

                case "d":
                    if (currentPage < totalPages) currentPage++;
                    else { System.out.println("This is the last page."); pause(); }
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
                    currentView = originalView = dict.valueList();
                    searchQuery = "";
                    currentPage = 1;
                    break;

                case "e": {
                    if (currentView.isEmpty()) {
                        System.out.println("No items to edit.");
                        pause();
                        break;
                    }
                    // compute indices for current page
                    int start = (currentPage - 1) * PAGE_SIZE;
                    int endExclusive = Math.min(start + PAGE_SIZE, totalItems);

                    System.out.printf("Select item [%d-%d] on this page to edit: ", start+1, endExclusive);
                    String raw = scanner.nextLine().trim();
                    int pick;
                    try {
                        pick = Integer.parseInt(raw);
                    } catch (NumberFormatException ex) {
                        System.out.println("Invalid number.");
                        pause();
                        break;
                    }
                    if (pick < start+1 || pick > endExclusive) {
                        System.out.println("Out of range.");
                        pause();
                        break;
                    }
                    Medicine toEdit = currentView.get(pick - 1);
                    String oldKey = toEdit.getMedicineKey();
                    editMedicineInPlace(toEdit);

                    String newKey = toEdit.getMedicineKey();
                    if (!oldKey.equals(newKey)) {
                        if (dict.contains(oldKey)) dict.remove(oldKey);

                        if (dict.contains(newKey)) {
                            Medicine target = dict.getValue(newKey);
                            target.setQuantity(target.getQuantity() + toEdit.getQuantity());
                            target.setName(toEdit.getName());
                            target.setBrand(toEdit.getBrand());
                            target.setStrength(toEdit.getStrength());
                            target.setUnit(toEdit.getUnit());
                            target.setPrice(toEdit.getPrice());
                            target.setDescription(toEdit.getDescription());
                            target.setCompany(toEdit.getCompany());
                            target.setExpiryDate(toEdit.getExpiryDate());
                            System.out.println("Key collision detected; merged quantities into existing entry.");
                        } else {
                            dict.add(newKey, toEdit);
                        }
                    } else {
                        if (!dict.contains(oldKey)) dict.add(oldKey, toEdit);
                    }

                    originalView = dict.valueList();
                    if (searchQuery.isEmpty()) {
                        currentView = new ArrayList<>(originalView);
                    } else {
                        currentView = filterMedicines(originalView, searchQuery);
                        totalItems = currentView.size();
                        totalPages = Math.max(1, (totalItems + PAGE_SIZE - 1) / PAGE_SIZE);
                        currentPage = Math.min(currentPage, totalPages);
                    }

                    System.out.println("Item updated.");
                    pause();
                    break;
                }

                case "q":
                    return;

                default:
                    System.out.println("Invalid choice.");
            }
        }
    }

    public void updateBloodTube(HashedDictionary<String, BloodTube> dict) {
        if (dict == null || dict.isEmpty()) {
            System.out.println("No blood tubes to update.");
            return;
        }

        ArrayList<BloodTube> originalView = dict.valueList();
        ArrayList<BloodTube> currentView = new ArrayList<>(originalView);
        int currentPage = 1;
        String searchQuery = "";

        while (true) {
            int totalItems = currentView.size();
            int totalPages = Math.max(1, (totalItems + PAGE_SIZE - 1) / PAGE_SIZE);

            UI.displayBloodTubeList(currentView, totalItems, currentPage, totalPages, searchQuery);
            System.out.println("Press: [A] Prev | [D] Next | [S] Search | [R] Reset | [E] Edit item | [Q] Quit");
            System.out.print("Enter your choice: ");
            String input = scanner.nextLine().trim().toLowerCase();

            switch (input) {
                case "a":
                    if (currentPage > 1) currentPage--; else { System.out.println("This is the first page."); pause(); }
                    break;

                case "d":
                    if (currentPage < totalPages) currentPage++; else { System.out.println("This is the last page."); pause(); }
                    break;

                case "s": {
                    System.out.print("Enter search (Name/Cap color): ");
                    searchQuery = scanner.nextLine().trim();
                    ArrayList<BloodTube> filtered = filterBloodTube(dict.valueList(), searchQuery);
                    if (filtered.isEmpty()) { System.out.println("No results found for: " + searchQuery); pause(); }
                    else { currentView = filtered; currentPage = 1; }
                    break;
                }

                case "r":
                    originalView = dict.valueList();
                    currentView = new ArrayList<>(originalView);
                    searchQuery = ""; currentPage = 1;
                    break;

                case "e": {
                    if (currentView.isEmpty()) { System.out.println("No items to edit."); pause(); break; }

                    int start = (currentPage - 1) * PAGE_SIZE;
                    int endExclusive = Math.min(start + PAGE_SIZE, totalItems);

                    System.out.printf("Select item [%d-%d] on this page to edit: ", start+1, endExclusive);
                    String raw = scanner.nextLine().trim();
                    int pick;
                    try { pick = Integer.parseInt(raw); } catch (NumberFormatException ex) { System.out.println("Invalid number."); pause(); break; }
                    if (pick < start + 1 || pick > endExclusive) { System.out.println("Out of range."); pause(); break; }

                    BloodTube toEdit = currentView.get(pick - 1);
                    String oldKey = toEdit.getBloodTubeKey();
                    editBloodTubeInPlace(toEdit);

                    String newKey = toEdit.getBloodTubeKey();
                    if (!oldKey.equals(newKey)) {
                        if (dict.contains(oldKey)) dict.remove(oldKey);
                        if (dict.contains(newKey)) {
                            BloodTube target = dict.getValue(newKey);
                            target.setQuantity(target.getQuantity() + toEdit.getQuantity());
                            target.setName(toEdit.getName());
                            target.setPrice(toEdit.getPrice());
                            target.setDescription(toEdit.getDescription());
                            target.setCompany(toEdit.getCompany());
                            target.setExpiryDate(toEdit.getExpiryDate());
                            target.setVolumeMl(toEdit.getVolumeMl());
                            target.setCapColor(toEdit.getCapColor());
                            target.setAdditive(toEdit.getAdditive());
                            System.out.println("Key collision detected; merged quantities into existing entry.");
                        } else {
                            dict.add(newKey, toEdit);
                        }
                    } else {
                        if (!dict.contains(oldKey)) dict.add(oldKey, toEdit);
                    }

                    // refresh views
                    originalView = dict.valueList();
                    currentView = searchQuery.isEmpty() ? new ArrayList<>(originalView) : filterBloodTube(originalView, searchQuery);
                    int newTotal = currentView.size();
                    int newPages = Math.max(1, (newTotal + PAGE_SIZE - 1) / PAGE_SIZE);
                    currentPage = Math.min(currentPage, newPages);

                    System.out.println("Blood tube updated.");
                    pause();
                    break;
                }

                case "q":
                    return;

                default:
                    System.out.println("Invalid choice.");
            }
        }
    }

    private void editBloodTubeInPlace(BloodTube t) {
        while (true) {
            System.out.println("\n=== Edit Blood Tube ===");
            System.out.println("1) Name:                " + t.getName());
            System.out.println("2) Company:             " + (t.getCompany() == null ? "-" : t.getCompany().getName()));
            System.out.println("3) Volume (ml):         " + t.getVolumeMl());
            System.out.println("4) Cap Color:           " + t.getCapColor());
            System.out.println("5) Additive:            " + t.getAdditive());
            System.out.println("6) Price (RM):          " + t.getPrice());
            System.out.println("7) Description:         " + (t.getDescription() == null ? "-" : t.getDescription()));
            System.out.println("8) Expiry (yyyy-MM-dd): " + DATE_FMT.format(t.getExpiryDate()));
            System.out.println("9) Quantity:            " + t.getQuantity());
            System.out.println("999) Done");
            System.out.print("Choose a field to edit: ");

            String raw = scanner.nextLine().trim();
            int choice;
            try { choice = Integer.parseInt(raw); } catch (NumberFormatException e) { System.out.println("Invalid."); continue; }
            if (choice == 999) return;

            switch (choice) {
                case 1:
                    System.out.print("New Name: ");
                    String n = scanner.nextLine().trim();
                    if (!n.isEmpty()) t.setName(n);
                    break;
                case 2: {
                    Company c = selectOrCreateCompany();
                    if (c != null) t.setCompany(c);
                    break;
                }
                case 3: {
                    double v = askPositiveDouble(scanner, "New Volume (ml): ");
                    t.setVolumeMl(v);
                    break;
                }
                case 4:
                    System.out.print("New Cap Color: ");
                    String c = scanner.nextLine().trim();
                    if (!c.isEmpty()) t.setCapColor(c);
                    break;
                case 5:
                    System.out.print("New Additive: ");
                    String a = scanner.nextLine().trim();
                    if (!a.isEmpty()) t.setAdditive(a);
                    break;
                case 6: {
                    double p = askPositiveDouble(scanner, "New Price (RM): ");
                    t.setPrice(p);
                    break;
                }
                case 7:
                    System.out.print("New Description (blank to keep): ");
                    String d = scanner.nextLine().trim();
                    if (!d.isEmpty()) t.setDescription(d);
                    break;
                case 8:
                    Date newExpiry;
                    while (true) {
                        System.out.print("New Expiry date (yyyy-MM-dd): ");
                        String dateInput = scanner.nextLine().trim();
                        if (dateInput.isEmpty()) {
                            System.out.println("Keeping previous expiry date.");
                            return;
                        }
                        try {
                            newExpiry = DATE_FMT.parse(dateInput);
                            Date today = new Date();
                            if (newExpiry.before(today)) {
                                System.out.println("Warning: New expiry date is in the past. Are you sure you want to continue? (y/n): ");
                                if (!askYesNo("")) {
                                    continue;
                                }
                            }
                            t.setExpiryDate(newExpiry);
                            break;
                        } catch (Exception e) {
                            System.out.println("Error: Invalid date format. Please enter date in yyyy-MM-dd format (e.g., 2025-12-31).");
                        }
                    }
                    break;
                case 9: {
                    System.out.println("1) Set absolute quantity");
                    System.out.println("2) Add to quantity");
                    System.out.print("Choose: ");
                    String opt = scanner.nextLine().trim();
                    switch (opt) {
                        case "1": {
                            int q = askPositiveInt(scanner, "New absolute quantity: ");
                            t.setQuantity(q);
                            break;
                        }
                        case "2": {
                            int delta = askPositiveInt(scanner, "Add quantity: ");
                            t.setQuantity(t.getQuantity() + delta);
                            break;
                        }
                        default:
                            System.out.println("Invalid option.");
                    }
                    break;
                }
                default:
                    System.out.println("Invalid selection.");
            }
        }
    }


    private void editMedicineInPlace(Medicine m) {
        while (true) {
            System.out.println("\n=== Edit Medicine ===");
            System.out.println("1) Name:          " + m.getName());
            System.out.println("2) Brand:         " + m.getBrand());
            System.out.println("3) Strength:      " + m.getStrength());
            System.out.println("4) Unit:          " + m.getUnit());
            System.out.println("5) Price (RM):    " + m.getPrice());
            System.out.println("6) Description:   " + (m.getDescription() == null ? "-" : m.getDescription()));
            System.out.println("7) Company:       " + (m.getCompany() == null ? "-" : m.getCompany().getName()));
            System.out.println("8) Expiry (yyyy-MM-dd): " + DATE_FMT.format(m.getExpiryDate()));
            System.out.println("9) Quantity:      " + m.getQuantity());
            System.out.println("999) Done");
            System.out.print("Choose a field to edit: ");
            String raw = scanner.nextLine().trim();

            int choice;
            try { choice = Integer.parseInt(raw); } catch (NumberFormatException e) { System.out.println("Invalid."); continue; }
            if (choice == 999) return;

            switch (choice) {
                case 1: {
                    System.out.print("New Name: ");
                    String v = scanner.nextLine().trim();
                    if (!v.isEmpty()) m.setName(v);
                    break;
                }
                case 2: {
                    System.out.print("New Brand: ");
                    String v = scanner.nextLine().trim();
                    if (!v.isEmpty()) m.setBrand(v);
                    break;
                }
                case 3: {
                    String v;
                    while (true) {
                        System.out.print("New Strength (e.g., 500mg, 10ml, 250mcg): ");
                        v = scanner.nextLine().trim();
                        if (v.isEmpty()) {
                            System.out.println("Strength cannot be empty.");
                            continue;
                        }
                        if (isValidStrength(v)) {
                            m.setStrength(v);
                            break;
                        }
                        System.out.println("Error: Invalid strength format. Please use format like '500mg', '10ml', '250mcg', etc.");
                    }
                    break;
                }
                case 4: {
                    while (true) {
                        System.out.print("New Unit (tablet/ml/capsule): ");
                        String v = scanner.nextLine().trim();
                        if (v.equalsIgnoreCase("tablet") || v.equalsIgnoreCase("ml") || v.equalsIgnoreCase("capsule")) {
                            m.setUnit(v);
                            break;
                        }
                        System.out.println("Invalid unit.");
                    }
                    break;
                }
                case 5: {
                    double p = askPositiveDouble(scanner, "New Price (RM): ");
                    m.setPrice(p);
                    break;
                }
                case 6: {
                    System.out.print("New Description (blank to keep): ");
                    String v = scanner.nextLine().trim();
                    if (!v.isEmpty()) m.setDescription(v);
                    break;
                }
                case 7: {
                    Company c = selectOrCreateCompany();
                    if (c != null) m.setCompany(c);
                    break;
                }
                case 8: {
                    Date newExpiry;
                    while (true) {
                        System.out.print("New Expiry date (yyyy-MM-dd): ");
                        String dateInput = scanner.nextLine().trim();
                        if (dateInput.isEmpty()) {
                            System.out.println("Keeping previous expiry date.");
                            return;
                        }
                        try {
                            newExpiry = DATE_FMT.parse(dateInput);
                            Date today = new Date();
                            if (newExpiry.before(today)) {
                                System.out.println("Warning: New expiry date is in the past. Are you sure you want to continue? (y/n): ");
                                if (!askYesNo("")) {
                                    continue;
                                }
                            }
                            m.setExpiryDate(newExpiry);
                            break;
                        } catch (Exception e) {
                            System.out.println("Error: Invalid date format. Please enter date in yyyy-MM-dd format (e.g., 2025-12-31).");
                        }
                    }
                    break;
                }
                case 9: {
                    System.out.println("1) Set absolute quantity");
                    System.out.println("2) Add to quantity");
                    System.out.print("Choose: ");
                    String opt = scanner.nextLine().trim();
                    switch (opt) {
                        case "1": {
                            int q = askPositiveInt(scanner, "New absolute quantity: ");
                            m.setQuantity(q);
                            break;
                        }
                        case "2": {
                            int delta = askPositiveInt(scanner, "Add quantity: ");
                            m.setQuantity(m.getQuantity() + delta);
                            break;
                        }
                        default:
                            System.out.println("Invalid option.");
                    }
                    break;
                }
                default:
                    System.out.println("Invalid selection.");
            }
        }
    }

    public void updateLabTest(HashedDictionary<String, LabTest> dict) {
        if (dict == null || dict.isEmpty()) {
            System.out.println("No lab tests to update.");
            return;
        }

        ArrayList<LabTest> originalView = dict.valueList();
        ArrayList<LabTest> currentView = new ArrayList<>(originalView);
        int currentPage = 1;
        String searchQuery = "";

        while (true) {
            int totalItems = currentView.size();
            int totalPages = Math.max(1, (totalItems + PAGE_SIZE - 1) / PAGE_SIZE);

            UI.displayLabTestList(currentView, totalItems, currentPage, totalPages, searchQuery);
            System.out.println("Press: [A] Prev | [D] Next | [S] Search | [R] Reset | [E] Edit item | [Q] Quit");
            System.out.print("Enter your choice: ");
            String input = scanner.nextLine().trim().toLowerCase();

            switch (input) {
                case "a":
                    if (currentPage > 1) currentPage--; else { System.out.println("This is the first page."); pause(); }
                    break;

                case "d":
                    if (currentPage < totalPages) currentPage++; else { System.out.println("This is the last page."); pause(); }
                    break;

                case "s": {
                    System.out.print("Enter search (Name/Tube Needed/Lab): ");
                    searchQuery = scanner.nextLine().trim();
                    ArrayList<LabTest> filtered = filterLabTest(dict.valueList(), searchQuery);
                    if (filtered.isEmpty()) { System.out.println("No results found for: " + searchQuery); pause(); }
                    else { currentView = filtered; currentPage = 1; }
                    break;
                }

                case "r":
                    originalView = dict.valueList();
                    currentView = new ArrayList<>(originalView);
                    searchQuery = ""; currentPage = 1;
                    break;

                case "e": {
                    if (currentView.isEmpty()) { System.out.println("No items to edit."); pause(); break; }

                    int start = (currentPage - 1) * PAGE_SIZE;
                    int endExclusive = Math.min(start + PAGE_SIZE, totalItems);

                    System.out.printf("Select item [%d-%d] on this page to edit: ", start+1, endExclusive);
                    String raw = scanner.nextLine().trim();
                    int pick;
                    try { pick = Integer.parseInt(raw); } catch (NumberFormatException ex) { System.out.println("Invalid number."); pause(); break; }
                    if (pick < start +1 || pick > endExclusive) { System.out.println("Out of range."); pause(); break; }

                    LabTest toEdit = currentView.get(pick - 1);
                    String oldKey = toEdit.getName();
                    editLabTestInPlace(toEdit);

                    String newKey = toEdit.getName();
                    if (!oldKey.equals(newKey)) {
                        if (dict.contains(oldKey)) dict.remove(oldKey);
                        dict.add(newKey, toEdit);
                    } else {
                        if (!dict.contains(oldKey)) dict.add(oldKey, toEdit);
                    }

                    // refresh views
                    originalView = dict.valueList();
                    currentView = searchQuery.isEmpty() ? new ArrayList<>(originalView) : filterLabTest(originalView, searchQuery);
                    int newTotal = currentView.size();
                    int newPages = Math.max(1, (newTotal + PAGE_SIZE - 1) / PAGE_SIZE);
                    currentPage = Math.min(currentPage, newPages);

                    System.out.println("Lab test updated.");
                    pause();
                    break;
                }

                case "q":
                    return;

                default:
                    System.out.println("Invalid choice.");
            }
        }
    }

    private void editLabTestInPlace(LabTest t) {
        while (true) {
            System.out.println("\n=== Edit Lab Test ===");
            System.out.println("1) Name:               " + t.getName());
            System.out.println("2) Code:               " + (t.getCode() == null ? "-" : t.getCode()));
            System.out.println("3) Price (RM):         " + t.getPrice());
            System.out.println("4) Description:        " + (t.getDescription() == null ? "-" : t.getDescription()));
            System.out.println("5) Referring Lab:      " + (t.getReferringLab() == null ? "-" : t.getReferringLab().getName()));
            System.out.println("6) Fasting Required:   " + (t.isFastingRequired() ? "Yes" : "No"));
            System.out.println("7) Patient Precautions:" + (t.getPatientPrecautions() == null ? "-" : t.getPatientPrecautions()));
            System.out.println("8) Blood Tube Needed:  " + (t.getBloodTubes() == null ? "-" : t.getBloodTubes()));
            System.out.println("999) Done");
            System.out.print("Choose a field to edit: ");

            String raw = scanner.nextLine().trim();
            int choice;
            try { choice = Integer.parseInt(raw); } catch (NumberFormatException e) { System.out.println("Invalid."); continue; }
            if (choice == 999) return;

            switch (choice) {
                case 1:
                    System.out.print("New Name: ");
                    String name = scanner.nextLine().trim();
                    if (!name.isEmpty()) t.setName(name);
                    break;
                case 2:
                    System.out.print("New Code (blank to clear): ");
                    String code = scanner.nextLine().trim();
                    t.setCode(code.isEmpty() ? null : code);
                    break;
                case 3: {
                    double p = askPositiveDouble(scanner, "New Price (RM): ");
                    t.setPrice(p);
                    break;
                }
                case 4:
                    System.out.print("New Description (blank to keep): ");
                    String d = scanner.nextLine().trim();
                    if (!d.isEmpty()) t.setDescription(d);
                    break;
                case 5: {
                    Company lab = selectOrCreateCompany();
                    if (lab != null) t.setReferringLab(lab);
                    break;
                }
                case 6: {
                    boolean f = askYesNo("Fasting required? (y/n): ");
                    t.setFastingRequired(f);
                    break;
                }
                case 7:
                    System.out.print("New Patient Precautions (blank to keep): ");
                    String pp = scanner.nextLine().trim();
                    if (!pp.isEmpty()) t.setPatientPrecautions(pp);
                    break;
                case 8: {
                    BloodTube pick = pickBloodTubesFromInventory();
                    if (pick != null) t.setBloodTubes(pick.getName());
                    break;
                }
                default:
                    System.out.println("Invalid selection.");
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
            if (totalPages == 0) totalPages = 1;

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
                    } else {
                        currentView = filtered;
                        currentPage = 1;
                    }
                    break;

                case "r":
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
            if (totalPages == 0) totalPages = 1;

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
                    } else {
                        currentView = filtered;
                        currentPage = 1;
                    }
                    break;

                case "i":
                    UI.displayInsufficientMedicines(meds);

                case "r":
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
            if (totalPages == 0) totalPages = 1;

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

                    ArrayList<BloodTube> filtered = filterBloodTube(originalView, searchQuery);

                    if (filtered.isEmpty()) {
                        System.out.println("No results found for: " + searchQuery);
                        pause();
                    } else {
                        currentView = filtered;
                        currentPage = 1;
                    }
                    break;

                case "i":
                    UI.displayInsufficientBloodTubes(bloodTubeInventory);

                case "r":
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
        Integer choice = UI.stockInSelectionUI();
        
        if (choice == null) {
            System.out.println("Error: Unable to get user selection.");
            return;
        }

        switch (choice) {
            case 1:
                // Stock In Medicine
                try {
                    if (meds == null) {
                        System.out.println("Error: Medicine inventory not initialized.");
                        return;
                    }
                    stockInMedicine();
                } catch (Exception e) {
                    System.out.println("Error occurred while stocking medicine: " + e.getMessage());
                }
                break;
            case 2:
                // Stock In Lab Test
                try {
                    if (labTests == null) {
                        System.out.println("Error: Lab test inventory not initialized.");
                        return;
                    }
                    createOrUpdateLabTest();
                } catch (Exception e) {
                    System.out.println("Error occurred while creating/updating lab test: " + e.getMessage());
                }
                break;
            case 3:
                // Stock In Blood Tube
                try {
                    if (bloodTubeInventory == null) {
                        System.out.println("Error: Blood tube inventory not initialized.");
                        return;
                    }
                    stockInBloodTube();
                } catch (Exception e) {
                    System.out.println("Error occurred while stocking blood tube: " + e.getMessage());
                }
                break;
            case 999:
                return;
            default:
                System.out.println("Invalid choice. Please select a valid option (1-3 or 999 to exit).");
        }
    }

    private void stockInMedicine() {
        @SuppressWarnings("unchecked")
        HashedDictionary<String, Medicine> dict = (HashedDictionary<String, Medicine>) meds;

        System.out.println("\n=== Stock In Medicine ===");

        System.out.print("Name: ");
        String name = scanner.nextLine().trim();

        System.out.print("Brand: ");
        String brand = scanner.nextLine().trim();

        String strength;
        while (true) {
            System.out.print("Strength (e.g., 500mg, 10ml, 250mg): ");
            strength = scanner.nextLine().trim();
            if (strength.isEmpty()) {
                System.out.println("Error: Strength cannot be empty. Please enter a valid strength.");
                continue;
            }
            if (isValidStrength(strength)) {
                break;
            }
            System.out.println("Error: Invalid strength format. Please use format like '500mg', '10ml', '250mcg', etc.");
        }

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

        Company company = selectOrCreateCompany();

        Date expiry;
        while (true) {
            System.out.print("Expiry date (yyyy-MM-dd): ");
            String dateInput = scanner.nextLine().trim();
            if (dateInput.isEmpty()) {
                System.out.println("Error: Expiry date cannot be empty. Please enter a date in yyyy-MM-dd format.");
                continue;
            }
            try {
                expiry = DATE_FMT.parse(dateInput);
                Date today = new Date();
                if (expiry.before(today)) {
                    System.out.println("Warning: Expiry date is in the past. Are you sure you want to continue? (y/n): ");
                    if (!askYesNo("")) {
                        continue;
                    }
                }
                break;
            } catch (Exception e) {
                System.out.println("Error: Invalid date format. Please enter date in yyyy-MM-dd format (e.g., 2025-12-31).");
            }
        }

        Medicine temp = new Medicine(
                UUID.randomUUID(), name, addQty, price, desc, unit,
                company, brand, strength, expiry
        );
        String key = temp.getMedicineKey();

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

        if (uniqueCompanies.isEmpty()) {
            System.out.println("No companies found. Please create a new company.");
            return createCompanyViaPrompt();
        }

        QueueInterface<String> q = new LinkedQueue<>();
        q.enqueue("Create new company");        // Option 1
        for (int i = 0; i < uniqueCompanies.size(); i++) {
            q.enqueue(uniqueCompanies.get(i).getName());  // Options 2..N+1
        }

        Integer choice = messageUI.mainUI("Select Company", q);

        if (choice == null || choice == 999) {
            System.out.println("Cancelled.");
            return null;
        }

        if (choice == 1) {
            return createCompanyViaPrompt();
        }

        int idx = choice - 2;
        if (idx < 0 || idx >= uniqueCompanies.size()) {
            System.out.println("Invalid selection.");
            return null;
        }
        return uniqueCompanies.get(idx);
    }

    private Company createCompanyViaPrompt() {
        String name;
        while (true) {
            System.out.print("Company name: ");
            name = scanner.nextLine().trim();
            if (name.isEmpty()) {
                System.out.println("Error: Company name cannot be empty. Please enter a valid name.");
                continue;
            }
            if (name.length() > 100) {
                System.out.println("Error: Company name cannot exceed 100 characters. Please enter a shorter name.");
                continue;
            }
            break;
        }

        System.out.print("Address (optional): ");
        String address = scanner.nextLine().trim();
        if (address.length() > 200) {
            System.out.println("Warning: Address too long, truncating to 200 characters.");
            address = address.substring(0, 200);
        }

        String email;
        while (true) {
            System.out.print("Email (optional): ");
            email = scanner.nextLine().trim();
            if (email.isEmpty()) {
                break;
            }
            if (isValidEmail(email)) {
                break;
            }
            System.out.println("Error: Invalid email format. Please enter a valid email address or leave blank.");
        }

        String phone;
        while (true) {
            System.out.print("Phone (optional): ");
            phone = scanner.nextLine().trim();
            if (phone.isEmpty()) {
                break;
            }
            if (isValidPhone(phone)) {
                break;
            }
            System.out.println("Error: Invalid phone format. Please enter numbers only (8-15 digits) or leave blank.");
        }

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

        System.out.print("Code (optional): ");
        String code = scanner.nextLine().trim();

        double price = askPositiveDouble(scanner, "Price (RM): ");

        System.out.print("Description: ");
        String desc = scanner.nextLine().trim();

        Company referringLab = selectOrCreateCompany();

        boolean fastingRequired = askYesNo("Fasting required? (y/n): ");

        System.out.print("Patient precautions (optional): ");
        String patientPrecautions = scanner.nextLine().trim();

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

        String key = name;

        if (dict.contains(key)) {
            dict.add(key, test);
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

        Date expiry;
        while (true) {
            System.out.print("Expiry date (yyyy-MM-dd): ");
            String dateInput = scanner.nextLine().trim();
            if (dateInput.isEmpty()) {
                System.out.println("Error: Expiry date cannot be empty. Please enter a date in yyyy-MM-dd format.");
                continue;
            }
            try {
                expiry = DATE_FMT.parse(dateInput);
                Date today = new Date();
                if (expiry.before(today)) {
                    System.out.println("Warning: Expiry date is in the past. Are you sure you want to continue? (y/n): ");
                    if (!askYesNo("")) {
                        continue;
                    }
                }
                break;
            } catch (Exception e) {
                System.out.println("Error: Invalid date format. Please enter date in yyyy-MM-dd format (e.g., 2025-12-31).");
            }
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

    private static String truncateString(String s, int maxLength) {
        if (s == null) return "";
        return s.length() <= maxLength ? s : s.substring(0, maxLength - 3) + "...";
    }

    private boolean isValidEmail(String email) {
        if (email == null || email.isEmpty()) {
            return false;
        }
        return email.matches("^[a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+\\.[a-zA-Z]{2,}$");
    }

    private boolean isValidPhone(String phone) {
        if (phone == null || phone.isEmpty()) {
            return false;
        }
        String cleanPhone = phone.replaceAll("[\\s\\-\\(\\)\\+]", "");
        return cleanPhone.matches("^\\d{8,15}$");
    }

    private boolean isValidStrength(String strength) {
        if (strength == null || strength.trim().isEmpty()) {
            return false;
        }
        String trimmed = strength.trim();
        return trimmed.matches("^\\d+(\\.\\d+)?(mg|g|ml|mcg|ug|l|kg|IU|units?)$");
    }

    // 1. LOW STOCK ALERT REPORT (Most Critical)
    public void generateLowStockReport(HashedDictionary<String, Medicine> medicines, int reorderLevel) {
        System.out.println("\n=== LOW STOCK ALERT REPORT SUMMARY ===");
        System.out.println("Generated: " + LocalDateTime.now().format(DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm")));
        System.out.println("Reorder Level: " + reorderLevel);
        System.out.println("+" + "-".repeat(32) + "+" + "-".repeat(12) + "+");

        ArrayList<Medicine> allMeds = medicines.valueList();
        int totalMedicines = allMeds.size();
        int outOfStockCount = 0;
        int lowStockCount = 0;
        int adequateStockCount = 0;

        for (int i = 0; i < allMeds.size(); i++) {
            Medicine med = allMeds.get(i);
            if (med.getQuantity() == 0) {
                outOfStockCount++;
            } else if (med.getQuantity() <= reorderLevel) {
                lowStockCount++;
            } else {
                adequateStockCount++;
            }
        }

        System.out.printf("| %-30s | %-10s |%n", "CATEGORY", "COUNT");
        System.out.println("+" + "-".repeat(32) + "+" + "-".repeat(12) + "+");
        System.out.printf("| %-30s | %-10d |%n", "Total Medicines", totalMedicines);
        System.out.printf("| %-30s | %-10d |%n", "Out of Stock", outOfStockCount);
        System.out.printf("| %-30s | %-10d |%n", "Low Stock", lowStockCount);
        System.out.printf("| %-30s | %-10d |%n", "Adequate Stock", adequateStockCount);
        System.out.println("+" + "-".repeat(32) + "+" + "-".repeat(12) + "+");
        
        int totalRequiringAttention = outOfStockCount + lowStockCount;
        System.out.printf("Total medicines requiring attention: %d%n", totalRequiringAttention);
        
        if (totalRequiringAttention > 0) {
            System.out.println("WARNING: ACTION REQUIRED - Please restock medicines below reorder level");
        } else {
            System.out.println("SUCCESS: All medicines are adequately stocked");
        }
        pause();
    }

    // 2. EXPIRY ALERT REPORT
    public void generateExpiryAlertReport(HashedDictionary<String, Medicine> medicines, int daysAhead) {
        System.out.println("\n=== MEDICINE EXPIRY ALERT REPORT SUMMARY ===");
        System.out.println("Generated: " + LocalDateTime.now().format(DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm")));
        System.out.printf("Alert Period: Next %d days%n", daysAhead);
        System.out.println("+" + "-".repeat(32) + "+" + "-".repeat(12) + "+");


        Date today = new Date();
        long cutoffTime = today.getTime() + (daysAhead * 24L * 60L * 60L * 1000L);
        Date cutoffDate = new Date(cutoffTime);
        ArrayList<Medicine> allMeds = medicines.valueList();
        
        int totalMedicines = allMeds.size();
        int expiredCount = 0;
        int expiringCount = 0;
        int safeCount = 0;

        for (int i = 0; i < allMeds.size(); i++) {
            Medicine med = allMeds.get(i);
            if (med.getExpiryDate() != null) {
                long daysLeft = (med.getExpiryDate().getTime() - today.getTime()) / (24L * 60L * 60L * 1000L);
                
                if (daysLeft < 0) {
                    expiredCount++;
                } else if (daysLeft <= daysAhead) {
                    expiringCount++;
                } else {
                    safeCount++;
                }
            } else {
                safeCount++; // Treat medicines without expiry date as safe
            }
        }

        System.out.printf("| %-30s | %-10s |%n", "CATEGORY", "COUNT");
        System.out.println("+" + "-".repeat(32) + "+" + "-".repeat(12) + "+");
        System.out.printf("| %-30s | %-10d |%n", "Total Medicines", totalMedicines);
        System.out.printf("| %-30s | %-10d |%n", "Already Expired", expiredCount);
        System.out.printf("| %-30s | %-10d |%n", "Expiring Soon", expiringCount);
        System.out.printf("| %-30s | %-10d |%n", "Safe (Not Expiring)", safeCount);
        System.out.println("+" + "-".repeat(32) + "+" + "-".repeat(12) + "+");
        
        int totalRequiringAttention = expiredCount + expiringCount;
        System.out.printf("Total medicines requiring attention: %d%n", totalRequiringAttention);
        
        if (expiredCount > 0) {
            System.out.println("CRITICAL: " + expiredCount + " medicine(s) have already expired - remove immediately");
        }
        if (expiringCount > 0) {
            System.out.println("WARNING: " + expiringCount + " medicine(s) expiring within " + daysAhead + " days");
        }
        if (totalRequiringAttention == 0) {
            System.out.println("SUCCESS: All medicines are within safe expiry period");
        }
        pause();
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

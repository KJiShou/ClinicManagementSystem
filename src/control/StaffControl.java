// Kong Ji Shou
package control;

import adt.ArrayList;
import adt.ListInterface;
import boundary.StaffUI;
import entity.Staff;
import entity.User;

import java.io.IOException;
import java.util.Scanner;
import java.util.UUID;

public class StaffControl {
    private static final int PAGE_SIZE = 5;
    private ListInterface<Staff> staffList;
    private AuthenticationControl authControl;
    private StaffUI ui;
    private Scanner scanner;

    public StaffControl(ListInterface<Staff> staffList, AuthenticationControl authControl) {
        this.staffList = staffList;
        this.authControl = authControl;
        this.ui = new StaffUI();
        this.scanner = new Scanner(System.in);
    }

    public void main() throws IOException {
        if (!authControl.requireRole(Staff.Role.ADMIN)) {
            return;
        }

        while (true) {
            Integer choice = ui.mainMenu();
            switch (choice) {
                case 1:
                    addStaff();
                    break;
                case 2:
                    viewStaff();
                    break;
                case 3:
                    updateStaff();
                    break;
                case 4:
                    deleteStaff();
                    break;
                case 5:
                    searchStaff();
                    break;
                case 999:
                    return;
                default:
                    System.out.println("Invalid choice.");
            }
        }
    }

    public void addStaff() {
        System.out.println("\n=== ADD STAFF ===");
        
        Staff newStaff;
        try {
            newStaff = ui.getStaffDetails();
            if (newStaff == null) {
                ui.displayMessage("Staff registration cancelled.");
                ui.pause();
                return;
            }
        } catch (Exception e) {
            ui.displayError("Failed to create staff: " + e.getMessage());
            ui.pause();
            return;
        }

        // Check if account already exists
        if (isAccountExists(newStaff.getAccount())) {
            ui.displayError("Account '" + newStaff.getAccount() + "' already exists. Please choose a different account.");
            ui.pause();
            return;
        }

        // Validate required fields (additional validation beyond entity validation)
        if (!validateStaffData(newStaff)) {
            ui.pause();
            return;
        }

        staffList.add(newStaff);
        ui.displaySuccess("Staff member added successfully!");
        System.out.println("Staff Details:");
        System.out.println("- Name: " + newStaff.getName());
        System.out.println("- Account: " + newStaff.getAccount());
        System.out.println("- Role: " + newStaff.getRole());
        System.out.println("- Position: " + newStaff.getPosition());
        System.out.println("- Department: " + newStaff.getDepartment());
        ui.pause();
    }

    public void viewStaff() {
        System.out.println("\n=== VIEW STAFF ===");
        
        if (staffList.isEmpty()) {
            ui.displayMessage("No staff members found.");
            ui.pause();
            return;
        }

        ArrayList<Staff> staffArray = convertToArrayList();
        displayStaffPaginated(staffArray, "All Staff Members");
    }

    public void updateStaff() {
        System.out.println("\n=== UPDATE STAFF ===");
        
        if (staffList.isEmpty()) {
            ui.displayMessage("No staff members found.");
            ui.pause();
            return;
        }

        ArrayList<Staff> staffArray = convertToArrayList();
        Staff selectedStaff = selectStaff(staffArray);
        
        if (selectedStaff == null) {
            ui.displayMessage("Operation cancelled.");
            ui.pause();
            return;
        }

        updateStaffDetails(selectedStaff);
    }

    public void deleteStaff() {
        System.out.println("\n=== DELETE STAFF ===");
        
        if (staffList.isEmpty()) {
            ui.displayMessage("No staff members found.");
            ui.pause();
            return;
        }

        ArrayList<Staff> staffArray = convertToArrayList();
        Staff selectedStaff = selectStaff(staffArray);
        
        if (selectedStaff == null) {
            ui.displayMessage("Operation cancelled.");
            ui.pause();
            return;
        }

        // Prevent deleting current user
        if (selectedStaff.equals(authControl.getCurrentUser())) {
            ui.displayError("You cannot delete your own account while logged in.");
            ui.pause();
            return;
        }

        System.out.println("\nStaff to Delete:");
        displayStaffDetails(selectedStaff);
        System.out.print("\nConfirm deletion? (Y/N): ");
        String confirm = scanner.nextLine().trim().toUpperCase();

        if (confirm.equals("Y")) {
            // Find and remove from staffList
            for (int i = 0; i < staffList.size(); i++) {
                if (staffList.get(i).getUserID().equals(selectedStaff.getUserID())) {
                    staffList.remove(i);
                    break;
                }
            }
            ui.displaySuccess("Staff member deleted successfully!");
        } else {
            ui.displayMessage("Deletion cancelled.");
        }
        ui.pause();
    }

    public void searchStaff() {
        System.out.println("\n=== SEARCH STAFF ===");
        System.out.print("Enter search term (Name/Account/Position/Department): ");
        String searchTerm = scanner.nextLine().trim();

        if (searchTerm.isEmpty()) {
            ui.displayMessage("Search cancelled.");
            ui.pause();
            return;
        }

        ArrayList<Staff> results = searchStaffMembers(searchTerm);
        
        if (results.isEmpty()) {
            ui.displayMessage("No staff members found matching: " + searchTerm);
        } else {
            displayStaffPaginated(results, "Search Results: " + searchTerm);
        }
    }

    private boolean isAccountExists(String account) {
        for (int i = 0; i < staffList.size(); i++) {
            if (staffList.get(i).getAccount().equals(account)) {
                return true;
            }
        }
        return false;
    }

    private boolean validateStaffData(Staff staff) {
        if (staff.getName() == null || staff.getName().trim().isEmpty()) {
            ui.displayError("Name is required.");
            return false;
        }
        
        if (staff.getAccount() == null || staff.getAccount().trim().isEmpty()) {
            ui.displayError("Account is required.");
            return false;
        }
        
        if (staff.getPassword() == null || staff.getPassword().length() < 4) {
            ui.displayError("Password must be at least 4 characters long.");
            return false;
        }
        
        if (staff.getRole() == null) {
            ui.displayError("Role is required.");
            return false;
        }
        
        return true;
    }

    private ArrayList<Staff> convertToArrayList() {
        ArrayList<Staff> result = new ArrayList<>();
        for (int i = 0; i < staffList.size(); i++) {
            result.add(staffList.get(i));
        }
        return result;
    }

    private Staff selectStaff(ArrayList<Staff> staffArray) {
        while (true) {
            displayStaffList(staffArray);
            System.out.printf("Select staff member (1-%d) or 0 to cancel: ", staffArray.size());
            
            try {
                int choice = Integer.parseInt(scanner.nextLine().trim());
                if (choice == 0) {
                    return null;
                }
                if (choice >= 1 && choice <= staffArray.size()) {
                    return staffArray.get(choice - 1);
                }
                System.out.println("ERROR: Invalid selection.");
            } catch (NumberFormatException e) {
                System.out.println("ERROR: Please enter a valid number.");
            }
        }
    }

    private void displayStaffList(ArrayList<Staff> staffArray) {
        System.out.println("\nStaff Members:");
        System.out.println("-".repeat(80));
        System.out.printf("%-4s %-20s %-15s %-15s %-15s %-10s\n", 
                         "No.", "Name", "Account", "Role", "Position", "Department");
        System.out.println("-".repeat(80));
        
        for (int i = 0; i < staffArray.size(); i++) {
            Staff staff = staffArray.get(i);
            System.out.printf("%-4d %-20s %-15s %-15s %-15s %-10s\n",
                            i + 1,
                            truncateString(staff.getName(), 18),
                            truncateString(staff.getAccount(), 13),
                            staff.getRole(),
                            truncateString(staff.getPosition(), 13),
                            truncateString(staff.getDepartment(), 8));
        }
        System.out.println("-".repeat(80));
    }

    private void displayStaffDetails(Staff staff) {
        System.out.println("-".repeat(50));
        System.out.println("STAFF DETAILS");
        System.out.println("-".repeat(50));
        System.out.println("ID: " + staff.getUserID());
        System.out.println("Name: " + staff.getName());
        System.out.println("Account: " + staff.getAccount());
        System.out.println("Role: " + staff.getRole());
        System.out.println("Position: " + staff.getPosition());
        System.out.println("Department: " + staff.getDepartment());
        System.out.println("Phone: " + staff.getPhone());
        System.out.println("Email: " + staff.getEmail());
        System.out.println("Address: " + staff.getAddress());
        System.out.println("Gender: " + staff.getGender());
        System.out.println("Date of Birth: " + staff.getDateOfBirth());
        System.out.println("-".repeat(50));
    }

    private void updateStaffDetails(Staff staff) {
        while (true) {
            System.out.println("\n=== UPDATE STAFF DETAILS ===");
            displayStaffDetails(staff);
            
            System.out.println("\nWhat would you like to update?");
            System.out.println("1. Name");
            System.out.println("2. Position");
            System.out.println("3. Department");
            System.out.println("4. Phone");
            System.out.println("5. Email");
            System.out.println("6. Address");
            System.out.println("7. Role");
            System.out.println("8. Reset Password");
            System.out.println("9. Save and Exit");
            System.out.print("Choice: ");

            try {
                int choice = Integer.parseInt(scanner.nextLine().trim());
                switch (choice) {
                    case 1:
                        updateStaffName(staff);
                        break;
                    case 2:
                        updateStaffPosition(staff);
                        break;
                    case 3:
                        updateStaffDepartment(staff);
                        break;
                    case 4:
                        updateStaffPhone(staff);
                        break;
                    case 5:
                        updateStaffEmail(staff);
                        break;
                    case 6:
                        updateStaffAddress(staff);
                        break;
                    case 7:
                        updateStaffRole(staff);
                        break;
                    case 8:
                        resetStaffPassword(staff);
                        break;
                    case 9:
                        ui.displaySuccess("Staff details updated successfully!");
                        ui.pause();
                        return;
                    default:
                        System.out.println("Invalid choice.");
                }
            } catch (NumberFormatException e) {
                System.out.println("Please enter a valid number.");
            }
        }
    }

    private void updateStaffName(Staff staff) {
        System.out.print("Enter new name (current: " + staff.getName() + "): ");
        String newName = scanner.nextLine().trim();
        if (!newName.isEmpty()) {
            staff.setName(newName);
            System.out.println("SUCCESS: Name updated.");
        }
    }

    private void updateStaffPosition(Staff staff) {
        System.out.print("Enter new position (current: " + staff.getPosition() + "): ");
        String newPosition = scanner.nextLine().trim();
        if (!newPosition.isEmpty()) {
            staff.setPosition(newPosition);
            System.out.println("SUCCESS: Position updated.");
        }
    }

    private void updateStaffDepartment(Staff staff) {
        System.out.print("Enter new department (current: " + staff.getDepartment() + "): ");
        String newDepartment = scanner.nextLine().trim();
        if (!newDepartment.isEmpty()) {
            staff.setDepartment(newDepartment);
            System.out.println("SUCCESS: Department updated.");
        }
    }

    private void updateStaffPhone(Staff staff) {
        System.out.print("Enter new phone (current: " + staff.getPhone() + "): ");
        String newPhone = scanner.nextLine().trim();
        if (!newPhone.isEmpty()) {
            staff.setPhone(newPhone);
            System.out.println("SUCCESS: Phone updated.");
        }
    }

    private void updateStaffEmail(Staff staff) {
        System.out.print("Enter new email (current: " + staff.getEmail() + "): ");
        String newEmail = scanner.nextLine().trim();
        if (!newEmail.isEmpty()) {
            staff.setEmail(newEmail);
            System.out.println("SUCCESS: Email updated.");
        }
    }

    private void updateStaffAddress(Staff staff) {
        System.out.print("Enter new address (current: " + staff.getAddress() + "): ");
        String newAddress = scanner.nextLine().trim();
        if (!newAddress.isEmpty()) {
            staff.setAddress(newAddress);
            System.out.println("SUCCESS: Address updated.");
        }
    }

    private void updateStaffRole(Staff staff) {
        System.out.println("Select new role:");
        Staff.Role[] roles = Staff.Role.values();
        for (int i = 0; i < roles.length; i++) {
            System.out.println((i + 1) + ". " + roles[i]);
        }
        System.out.print("Choice (current: " + staff.getRole() + "): ");
        
        try {
            int choice = Integer.parseInt(scanner.nextLine().trim());
            if (choice >= 1 && choice <= roles.length) {
                staff.setRole(roles[choice - 1]);
                System.out.println("SUCCESS: Role updated to: " + staff.getRole());
            } else {
                System.out.println("Invalid choice.");
            }
        } catch (NumberFormatException e) {
            System.out.println("Please enter a valid number.");
        }
    }

    private void resetStaffPassword(Staff staff) {
        System.out.print("Enter new password (min 4 characters): ");
        String newPassword = scanner.nextLine().trim();
        
        if (newPassword.length() < 4) {
            System.out.println("ERROR: Password must be at least 4 characters long.");
            return;
        }
        
        staff.setPassword(newPassword);
        System.out.println("SUCCESS: Password reset successfully!");
    }

    private ArrayList<Staff> searchStaffMembers(String searchTerm) {
        ArrayList<Staff> results = new ArrayList<>();
        String term = searchTerm.toLowerCase();
        
        for (int i = 0; i < staffList.size(); i++) {
            Staff staff = staffList.get(i);
            if (staff.getName().toLowerCase().contains(term) ||
                staff.getAccount().toLowerCase().contains(term) ||
                staff.getPosition().toLowerCase().contains(term) ||
                staff.getDepartment().toLowerCase().contains(term) ||
                staff.getRole().toString().toLowerCase().contains(term)) {
                results.add(staff);
            }
        }
        
        return results;
    }

    private void displayStaffPaginated(ArrayList<Staff> staffArray, String title) {
        if (staffArray.isEmpty()) {
            System.out.println("\n" + title);
            System.out.println("No staff members found.");
            ui.pause();
            return;
        }

        int currentPage = 1;
        String searchQuery = "";
        ArrayList<Staff> currentView = new ArrayList<>(staffArray);

        while (true) {
            int totalItems = currentView.size();
            int totalPages = (totalItems + PAGE_SIZE - 1) / PAGE_SIZE;
            if (totalPages == 0) totalPages = 1;

            displayStaffPage(currentView, currentPage, totalPages, title);

            System.out.println("Press: [A] Prev | [D] Next | [S] Search | [R] Reset | [V] View Details | [Q] Back");
            System.out.print("Enter choice: ");
            String input = scanner.nextLine().trim().toLowerCase();

            switch (input) {
                case "a":
                    if (currentPage > 1) {
                        currentPage--;
                    } else {
                        System.out.println("This is the first page.");
                        ui.pause();
                    }
                    break;
                case "d":
                    if (currentPage < totalPages) {
                        currentPage++;
                    } else {
                        System.out.println("This is the last page.");
                        ui.pause();
                    }
                    break;
                case "s":
                    System.out.print("Enter search term: ");
                    searchQuery = scanner.nextLine().trim();
                    ArrayList<Staff> filtered = searchStaffMembers(searchQuery);
                    if (!filtered.isEmpty()) {
                        currentView = filtered;
                        currentPage = 1;
                    } else {
                        System.out.println("No results found for: " + searchQuery);
                        ui.pause();
                    }
                    break;
                case "r":
                    currentView = new ArrayList<>(staffArray);
                    searchQuery = "";
                    currentPage = 1;
                    break;
                case "v":
                    viewStaffDetails(currentView);
                    break;
                case "q":
                    return;
                default:
                    System.out.println("Invalid choice.");
                    ui.pause();
            }
        }
    }

    private void displayStaffPage(ArrayList<Staff> staffArray, int currentPage, int totalPages, String title) {
        System.out.println("\n" + "=".repeat(90));
        System.out.println(title);
        System.out.println("=".repeat(90));
        System.out.printf("Page %d of %d | Total: %d staff member(s)\n", currentPage, totalPages, staffArray.size());
        System.out.println("-".repeat(90));
        
        int startIndex = (currentPage - 1) * PAGE_SIZE;
        int endIndex = Math.min(startIndex + PAGE_SIZE, staffArray.size());
        
        System.out.printf("%-4s %-20s %-15s %-12s %-15s %-12s %-8s\n", 
                         "No.", "Name", "Account", "Role", "Position", "Department", "Phone");
        System.out.println("-".repeat(90));
        
        for (int i = startIndex; i < endIndex; i++) {
            Staff staff = staffArray.get(i);
            System.out.printf("%-4d %-20s %-15s %-12s %-15s %-12s %-8s\n",
                            i + 1,
                            truncateString(staff.getName(), 18),
                            truncateString(staff.getAccount(), 13),
                            staff.getRole(),
                            truncateString(staff.getPosition(), 13),
                            truncateString(staff.getDepartment(), 10),
                            truncateString(staff.getPhone(), 6));
        }
        System.out.println("-".repeat(90));
    }

    private void viewStaffDetails(ArrayList<Staff> staffArray) {
        Staff selectedStaff = selectStaff(staffArray);
        if (selectedStaff != null) {
            displayStaffDetails(selectedStaff);
            ui.pause();
        }
    }

    private String truncateString(String str, int maxLength) {
        if (str == null) return "";
        return str.length() > maxLength ? str.substring(0, maxLength - 3) + "..." : str;
    }
}
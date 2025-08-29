// Kong Ji Shou
package boundary;

import adt.*;
import entity.Staff;
import entity.User;
import utility.MessageUI;

import java.time.format.DateTimeParseException;
import java.util.Scanner;
import java.util.UUID;

public class StaffUI {
    private QueueInterface<String> choiceQueue;
    private MessageUI UI;
    private Scanner scanner;

    public StaffUI() {
        choiceQueue = new LinkedQueue<>();
        UI = new MessageUI();
        scanner = new Scanner(System.in);
    }

    public Integer mainMenu() {
        choiceQueue.clear();
        choiceQueue.enqueue("Add Staff");
        choiceQueue.enqueue("View Staff");
        choiceQueue.enqueue("Update Staff");
        choiceQueue.enqueue("Delete Staff");
        choiceQueue.enqueue("Search Staff");
        return UI.mainUI("Staff Management", choiceQueue);
    }

    public Staff getStaffDetails() {
        System.out.println("\n=== ADD NEW STAFF ===");
        System.out.println("Enter staff details (type CANCEL to abort):");
        System.out.println("-".repeat(50));

        // Basic Information
        String name = getValidatedInput("Full Name", this::validateName);
        if (name == null) return null;

        String phone = getValidatedInput("Phone Number", this::validatePhone);
        if (phone == null) return null;

        String email = getValidatedInput("Email", this::validateEmail);
        if (email == null) return null;

        String address = getValidInput("Address", false);
        if (address == null) return null;

        String dateOfBirth = getValidatedInput("Date of Birth (yyyy-mm-dd)", this::validateDateOfBirth);
        if (dateOfBirth == null) return null;

        // Gender selection
        User.Gender gender = selectGender();
        if (gender == null) return null;

        // Position and Department
        String position = getValidInput("Position", false);
        if (position == null) return null;

        String department = getValidInput("Department", false);
        if (department == null) return null;

        // Account credentials
        System.out.println("\n--- Account Setup ---");
        String account = getValidatedInput("Account/Username (3-20 characters, letters/numbers/underscore only)", this::validateAccount);
        if (account == null) return null;

        String password = getValidPassword();
        if (password == null) return null;

        // Role selection
        Staff.Role role = selectRole();
        if (role == null) return null;

        // Create and return new Staff
        return new Staff(
            UUID.randomUUID(),
            name,
            address,
            gender,
            phone,
            email,
            dateOfBirth,
            position,
            department,
            account,
            password,
            role
        );
    }

    private String getValidInput(String fieldName, boolean canBeEmpty) {
        while (true) {
            System.out.print(fieldName + ": ");
            String input = scanner.nextLine().trim();
            
            if (input.equalsIgnoreCase("CANCEL")) {
                return null;
            }
            
            if (!input.isEmpty() || canBeEmpty) {
                return input;
            }
            
            System.out.println("ERROR: " + fieldName + " is required. Please enter a value.");
        }
    }

    private User.Gender selectGender() {
        while (true) {
            System.out.println("\nSelect Gender:");
            System.out.println("1. MALE");
            System.out.println("2. FEMALE");
            System.out.print("Choice (1-2) or CANCEL: ");
            
            String input = scanner.nextLine().trim();
            
            if (input.equalsIgnoreCase("CANCEL")) {
                return null;
            }
            
            switch (input) {
                case "1":
                    return User.Gender.MALE;
                case "2":
                    return User.Gender.FEMALE;
                default:
                    System.out.println("ERROR: Invalid choice. Please select 1 or 2.");
            }
        }
    }

    private String getUniqueAccount() {
        while (true) {
            System.out.print("Account/Username (for login): ");
            String account = scanner.nextLine().trim();
            
            if (account.equalsIgnoreCase("CANCEL")) {
                return null;
            }
            
            if (account.isEmpty()) {
                System.out.println("ERROR: Account is required.");
                continue;
            }
            
            if (account.length() < 3) {
                System.out.println("ERROR: Account must be at least 3 characters long.");
                continue;
            }
            
            // Note: Uniqueness check would be done in StaffControl
            return account;
        }
    }

    private String getValidPassword() {
        while (true) {
            System.out.print("Password (min 4 characters): ");
            String password = scanner.nextLine().trim();
            
            if (password.equalsIgnoreCase("CANCEL")) {
                return null;
            }
            
            if (password.length() < 4) {
                System.out.println("ERROR: Password must be at least 4 characters long.");
                continue;
            }
            
            System.out.print("Confirm password: ");
            String confirmPassword = scanner.nextLine().trim();
            
            if (!password.equals(confirmPassword)) {
                System.out.println("ERROR: Passwords do not match. Please try again.");
                continue;
            }
            
            return password;
        }
    }

    private Staff.Role selectRole() {
        while (true) {
            System.out.println("\nSelect Role:");
            Staff.Role[] roles = Staff.Role.values();
            for (int i = 0; i < roles.length; i++) {
                System.out.println((i + 1) + ". " + roles[i]);
            }
            System.out.print("Choice (1-" + roles.length + ") or CANCEL: ");
            
            String input = scanner.nextLine().trim();
            
            if (input.equalsIgnoreCase("CANCEL")) {
                return null;
            }
            
            try {
                int choice = Integer.parseInt(input);
                if (choice >= 1 && choice <= roles.length) {
                    return roles[choice - 1];
                } else {
                    System.out.println("ERROR: Invalid choice. Please select a number between 1 and " + roles.length + ".");
                }
            } catch (NumberFormatException e) {
                System.out.println("ERROR: Please enter a valid number.");
            }
        }
    }

    public void displayStaffList(ArrayList<Staff> staff, int currentPage, int totalPages, String searchQuery) {
        System.out.println("\n" + "=".repeat(100));
        System.out.println("STAFF LIST");
        System.out.println("=".repeat(100));
        
        if (!searchQuery.isEmpty()) {
            System.out.println("Search: \"" + searchQuery + "\"");
            System.out.println("-".repeat(100));
        }
        
        System.out.printf("Page %d of %d | Total: %d staff member(s)\n", currentPage, totalPages, staff.size());
        System.out.println("-".repeat(100));
        
        if (staff.isEmpty()) {
            System.out.println("No staff members found.");
            return;
        }
        
        System.out.printf("%-4s %-20s %-15s %-12s %-15s %-12s %-15s\n", 
                         "No.", "Name", "Account", "Role", "Position", "Department", "Phone");
        System.out.println("-".repeat(100));
        
        for (int i = 0; i < staff.size(); i++) {
            Staff s = staff.get(i);
            System.out.printf("%-4d %-20s %-15s %-12s %-15s %-12s %-15s\n",
                            i + 1,
                            truncateString(s.getName(), 18),
                            truncateString(s.getAccount(), 13),
                            s.getRole().toString(),
                            truncateString(s.getPosition(), 13),
                            truncateString(s.getDepartment(), 10),
                            truncateString(s.getPhone(), 13));
        }
        System.out.println("-".repeat(100));
    }

    public void displayStaffDetails(Staff staff) {
        System.out.println("\n" + "=".repeat(70));
        System.out.println("STAFF DETAILS");
        System.out.println("=".repeat(70));
        
        System.out.printf("%-20s: %s\n", "ID", staff.getUserID());
        System.out.printf("%-20s: %s\n", "Name", staff.getName());
        System.out.printf("%-20s: %s\n", "Account", staff.getAccount());
        System.out.printf("%-20s: %s\n", "Role", staff.getRole());
        System.out.printf("%-20s: %s\n", "Position", staff.getPosition());
        System.out.printf("%-20s: %s\n", "Department", staff.getDepartment());
        
        System.out.println("-".repeat(70));
        System.out.println("PERSONAL INFORMATION");
        System.out.println("-".repeat(70));
        
        System.out.printf("%-20s: %s\n", "Phone", staff.getPhone());
        System.out.printf("%-20s: %s\n", "Email", staff.getEmail());
        System.out.printf("%-20s: %s\n", "Address", staff.getAddress());
        System.out.printf("%-20s: %s\n", "Gender", staff.getGender());
        System.out.printf("%-20s: %s\n", "Date of Birth", staff.getDateOfBirth());
        
        System.out.println("=".repeat(70));
    }

    public Staff selectStaff(ArrayList<Staff> staffList) {
        if (staffList.isEmpty()) {
            System.out.println("No staff members available to select.");
            return null;
        }

        while (true) {
            displayStaffSelectionList(staffList);
            System.out.printf("Select staff member (1-%d) or 0 to cancel: ", staffList.size());
            
            try {
                int choice = Integer.parseInt(scanner.nextLine().trim());
                if (choice == 0) {
                    return null;
                }
                if (choice >= 1 && choice <= staffList.size()) {
                    return staffList.get(choice - 1);
                }
                System.out.println("ERROR: Invalid selection.");
            } catch (NumberFormatException e) {
                System.out.println("ERROR: Please enter a valid number.");
            }
        }
    }

    private void displayStaffSelectionList(ArrayList<Staff> staffList) {
        System.out.println("\nSelect Staff Member:");
        System.out.println("-".repeat(80));
        System.out.printf("%-4s %-25s %-15s %-15s %-15s\n", 
                         "No.", "Name", "Account", "Role", "Position");
        System.out.println("-".repeat(80));
        
        for (int i = 0; i < staffList.size(); i++) {
            Staff staff = staffList.get(i);
            System.out.printf("%-4d %-25s %-15s %-15s %-15s\n",
                            i + 1,
                            truncateString(staff.getName(), 23),
                            truncateString(staff.getAccount(), 13),
                            staff.getRole().toString(),
                            truncateString(staff.getPosition(), 13));
        }
        System.out.println("-".repeat(80));
    }

    public String getSearchTerm() {
        System.out.print("Enter search term (Name/Account/Position/Department/Role): ");
        return scanner.nextLine().trim();
    }

    public boolean confirmDeletion(Staff staff) {
        System.out.println("\nWARNING: DELETE STAFF MEMBER");
        System.out.println("-".repeat(50));
        System.out.println("Name: " + staff.getName());
        System.out.println("Account: " + staff.getAccount());
        System.out.println("Role: " + staff.getRole());
        System.out.println("Position: " + staff.getPosition());
        System.out.println("-".repeat(50));
        
        System.out.print("Are you sure you want to delete this staff member? (Y/N): ");
        String response = scanner.nextLine().trim().toUpperCase();
        return response.equals("Y") || response.equals("YES");
    }

    public void displayLoginScreen() {
        System.out.println("\n" + "=".repeat(60));
        System.out.println("            CLINIC MANAGEMENT SYSTEM");
        System.out.println("                 STAFF LOGIN");
        System.out.println("=".repeat(60));
        System.out.println("Please login with your staff credentials");
        System.out.println("Type 'help' for default accounts");
        System.out.println("-".repeat(60));
    }

    public String[] getLoginCredentials() {
        System.out.print("Account: ");
        String account = scanner.nextLine().trim();
        
        if (account.equalsIgnoreCase("help")) {
            return new String[]{"help", ""};
        }
        
        if (account.equalsIgnoreCase("exit")) {
            return new String[]{"exit", ""};
        }
        
        System.out.print("Password: ");
        String password = scanner.nextLine().trim();
        
        return new String[]{account, password};
    }

    private String truncateString(String str, int maxLength) {
        if (str == null) return "";
        return str.length() > maxLength ? str.substring(0, maxLength - 3) + "..." : str;
    }

    public void displayMessage(String message) {
        System.out.println(message);
    }

    public void displaySuccess(String message) {
        System.out.println("SUCCESS: " + message);
    }

    public void displayError(String message) {
        System.out.println("ERROR: " + message);
    }

    public void pause() {
        System.out.print("Press Enter to continue...");
        scanner.nextLine();
    }

    // Enhanced validation methods
    private String getValidatedInput(String fieldName, ValidationFunction validator) {
        while (true) {
            System.out.print(fieldName + ": ");
            String input = scanner.nextLine().trim();
            
            if (input.equalsIgnoreCase("CANCEL")) {
                return null;
            }
            
            String validationResult = validator.validate(input);
            if (validationResult == null) {
                return input;
            } else {
                System.out.println("ERROR: " + validationResult);
            }
        }
    }

    @FunctionalInterface
    private interface ValidationFunction {
        String validate(String input);
    }

    private String validateName(String name) {
        if (name == null || name.trim().isEmpty()) {
            return "Name is required";
        }
        if (!name.matches("^[a-zA-Z\\s'-]+$")) {
            return "Name can only contain letters, spaces, apostrophes, and hyphens";
        }
        if (name.length() < 2) {
            return "Name must be at least 2 characters long";
        }
        if (name.length() > 50) {
            return "Name must not exceed 50 characters";
        }
        return null;
    }

    private String validatePhone(String phone) {
        if (phone == null || phone.trim().isEmpty()) {
            return "Phone number is required";
        }
        String cleanPhone = phone.replaceAll("[\\s-()]", "");
        if (!cleanPhone.matches("^\\d{10,15}$")) {
            return "Phone number must be 10-15 digits";
        }
        return null;
    }

    private String validateEmail(String email) {
        if (email == null || email.trim().isEmpty()) {
            return "Email is required";
        }
        if (!email.matches("^[\\w\\.-]+@[\\w\\.-]+\\.[\\w]{2,}$")) {
            return "Invalid email format";
        }
        return null;
    }

    private String validateDateOfBirth(String dateOfBirth) {
        if (dateOfBirth == null || dateOfBirth.trim().isEmpty()) {
            return "Date of birth is required";
        }
        if (!dateOfBirth.matches("^\\d{4}-\\d{2}-\\d{2}$")) {
            return "Date must be in yyyy-mm-dd format";
        }
        try {
            java.time.LocalDate date = java.time.LocalDate.parse(dateOfBirth);
            if (date.isAfter(java.time.LocalDate.now())) {
                return "Date of birth cannot be in the future";
            }
            if (date.isBefore(java.time.LocalDate.of(1900, 1, 1))) {
                return "Date of birth cannot be before 1900";
            }
        } catch (Exception e) {
            return "Invalid date";
        }
        return null;
    }

    private String validateAccount(String account) {
        if (account == null || account.trim().isEmpty()) {
            return "Account is required";
        }
        if (!account.matches("^[a-zA-Z0-9_]{3,20}$")) {
            return "Account must be 3-20 characters containing only letters, numbers, and underscores";
        }
        return null;
    }
}
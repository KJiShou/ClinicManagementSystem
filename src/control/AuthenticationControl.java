package control;

import adt.ArrayList;
import adt.ListInterface;
import entity.Staff;

import java.util.Scanner;

public class AuthenticationControl {
    private ListInterface<Staff> staffList;
    private Staff currentUser;
    private Scanner scanner;

    public AuthenticationControl(ListInterface<Staff> staffList) {
        this.staffList = staffList;
        this.scanner = new Scanner(System.in);
        this.currentUser = null;
    }

    public boolean login() {
        System.out.println("\n" + "=".repeat(50));
        System.out.println("            STAFF AUTHENTICATION");
        System.out.println("=".repeat(50));
        
        int maxAttempts = 3;
        int attempts = 0;
        
        while (attempts < maxAttempts) {
            System.out.print("Account: ");
            String account = scanner.nextLine().trim();
            
            if (account.equalsIgnoreCase("EXIT")) {
                System.out.println("Login cancelled.");
                return false;
            }
            
            System.out.print("Password: ");
            String password = scanner.nextLine().trim();
            
            Staff staff = authenticateUser(account, password);
            if (staff != null) {
                this.currentUser = staff;
                System.out.println("\nSUCCESS: Login successful!");
                System.out.println("Welcome, " + staff.getName() + " (" + staff.getRole() + ")");
                pause();
                return true;
            } else {
                attempts++;
                System.out.println("ERROR: Invalid account or password.");
                if (attempts < maxAttempts) {
                    System.out.println("Attempts remaining: " + (maxAttempts - attempts));
                    System.out.println("Type 'EXIT' as account to cancel login.\n");
                }
            }
        }
        
        System.out.println("Maximum login attempts exceeded. Access denied.");
        pause();
        return false;
    }

    private Staff authenticateUser(String account, String password) {
        if (staffList == null || staffList.isEmpty()) {
            return null;
        }
        
        for (int i = 0; i < staffList.size(); i++) {
            Staff staff = staffList.get(i);
            if (staff.getAccount().equals(account) && staff.getPassword().equals(password)) {
                return staff;
            }
        }
        return null;
    }

    public void logout() {
        if (currentUser != null) {
            System.out.println("Goodbye, " + currentUser.getName() + "!");
            this.currentUser = null;
        }
    }

    public boolean isLoggedIn() {
        return currentUser != null;
    }

    public Staff getCurrentUser() {
        return currentUser;
    }

    public boolean hasRole(Staff.Role requiredRole) {
        return currentUser != null && currentUser.getRole() == requiredRole;
    }

    public boolean hasAnyRole(Staff.Role... roles) {
        if (currentUser == null) return false;
        
        Staff.Role userRole = currentUser.getRole();
        for (Staff.Role role : roles) {
            if (userRole == role) {
                return true;
            }
        }
        return false;
    }

    public boolean requireLogin() {
        if (!isLoggedIn()) {
            System.out.println("ERROR: Authentication required. Please login first.");
            return login();
        }
        return true;
    }

    public boolean requireRole(Staff.Role requiredRole) {
        if (!requireLogin()) {
            return false;
        }
        
        if (!hasRole(requiredRole)) {
            System.out.println("ERROR: Access denied. Required role: " + requiredRole);
            System.out.println("Your role: " + currentUser.getRole());
            pause();
            return false;
        }
        return true;
    }

    public boolean requireAnyRole(Staff.Role... roles) {
        if (!requireLogin()) {
            return false;
        }
        
        if (!hasAnyRole(roles)) {
            System.out.print("ERROR: Access denied. Required roles: ");
            for (int i = 0; i < roles.length; i++) {
                System.out.print(roles[i]);
                if (i < roles.length - 1) System.out.print(", ");
            }
            System.out.println();
            System.out.println("Your role: " + currentUser.getRole());
            pause();
            return false;
        }
        return true;
    }

    public void displayCurrentUser() {
        if (currentUser != null) {
            System.out.println("\n" + "=".repeat(40));
            System.out.println("CURRENT USER SESSION");
            System.out.println("-".repeat(40));
            System.out.println("Name: " + currentUser.getName());
            System.out.println("Account: " + currentUser.getAccount());
            System.out.println("Role: " + currentUser.getRole());
            System.out.println("Position: " + currentUser.getPosition());
            System.out.println("Department: " + currentUser.getDepartment());
            System.out.println("=".repeat(40));
        } else {
            System.out.println("No user currently logged in.");
        }
    }

    public void changePassword() {
        if (!requireLogin()) {
            return;
        }

        System.out.println("\n=== CHANGE PASSWORD ===");
        System.out.print("Enter current password: ");
        String currentPassword = scanner.nextLine().trim();

        if (!currentUser.getPassword().equals(currentPassword)) {
            System.out.println("ERROR: Current password is incorrect.");
            pause();
            return;
        }

        String newPassword = null;
        while (newPassword == null) {
            System.out.print("Enter new password (min 4 characters): ");
            String password1 = scanner.nextLine().trim();

            if (password1.length() < 4) {
                System.out.println("ERROR: Password must be at least 4 characters long.");
                continue;
            }

            System.out.print("Confirm new password: ");
            String password2 = scanner.nextLine().trim();

            if (!password1.equals(password2)) {
                System.out.println("ERROR: Passwords do not match.");
                continue;
            }

            newPassword = password1;
        }

        currentUser.setPassword(newPassword);
        System.out.println("SUCCESS: Password changed successfully!");
        pause();
    }

    // Helper method for creating a simple hash (for demo purposes)
    // In a real system, use proper password hashing libraries
    public static String simpleHash(String password) {
        int hash = 0;
        for (char c : password.toCharArray()) {
            hash = hash * 31 + c;
        }
        return String.valueOf(Math.abs(hash));
    }

    public void displayLoginHelp() {
        System.out.println("\n" + "=".repeat(50));
        System.out.println("DEFAULT ACCOUNTS (for testing)");
        System.out.println("=".repeat(50));
        System.out.println("Administrator:");
        System.out.println("  Account: admin");
        System.out.println("  Password: admin123");
        System.out.println("  Role: ADMIN");
        System.out.println();
        System.out.println("Doctor:");
        System.out.println("  Account: doctor");
        System.out.println("  Password: doctor123");
        System.out.println("  Role: DOCTOR");
        System.out.println();
        System.out.println("Receptionist:");
        System.out.println("  Account: reception");
        System.out.println("  Password: reception123");
        System.out.println("  Role: RECEPTIONIST");
        System.out.println("=".repeat(50));
        pause();
    }

    private void pause() {
        System.out.print("Press Enter to continue...");
        scanner.nextLine();
    }
}
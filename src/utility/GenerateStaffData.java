// Kong Ji Shou
package utility;

import adt.ArrayList;
import adt.ListInterface;
import entity.Staff;
import entity.User;

import java.util.UUID;

public class GenerateStaffData {

    public static ListInterface<Staff> createSampleStaff() {
        ListInterface<Staff> staffList = new ArrayList<>();

        // Administrator Account
        staffList.add(new Staff(
            UUID.randomUUID(),
            "Admin User",
            "123 Admin Street, City",
            User.Gender.MALE,
            "012-3456789",
            "admin@clinic.com",
            "1980-01-01",
            "System Administrator",
            "IT Department",
            "admin",
            "admin123",
            Staff.Role.ADMIN
        ));

        // Doctor Accounts
        staffList.add(new Staff(
            UUID.randomUUID(),
            "Dr. Sarah Johnson",
            "456 Medical Plaza, City",
            User.Gender.FEMALE,
            "012-9876543",
            "sarah.johnson@clinic.com",
            "1975-03-15",
            "Senior Doctor",
            "General Medicine",
            "doctor",
            "doctor123",
            Staff.Role.DOCTOR
        ));

        staffList.add(new Staff(
            UUID.randomUUID(),
            "Dr. Michael Chen",
            "789 Health Avenue, City",
            User.Gender.MALE,
            "012-5555666",
            "michael.chen@clinic.com",
            "1982-07-22",
            "Specialist",
            "Cardiology",
            "drchen",
            "chen123",
            Staff.Role.DOCTOR
        ));

        // Nurse Accounts
        staffList.add(new Staff(
            UUID.randomUUID(),
            "Nurse Emily Wong",
            "321 Care Road, City",
            User.Gender.FEMALE,
            "012-1111222",
            "emily.wong@clinic.com",
            "1988-11-08",
            "Senior Nurse",
            "General Ward",
            "nurse1",
            "nurse123",
            Staff.Role.NURSE
        ));

        staffList.add(new Staff(
            UUID.randomUUID(),
            "Nurse David Lee",
            "654 Nursing Lane, City",
            User.Gender.MALE,
            "012-3333444",
            "david.lee@clinic.com",
            "1990-05-12",
            "Staff Nurse",
            "Emergency",
            "nurse2",
            "nurse456",
            Staff.Role.NURSE
        ));

        // Receptionist Accounts
        staffList.add(new Staff(
            UUID.randomUUID(),
            "Lisa Anderson",
            "987 Reception Street, City",
            User.Gender.FEMALE,
            "012-7777888",
            "lisa.anderson@clinic.com",
            "1992-09-03",
            "Senior Receptionist",
            "Front Desk",
            "reception",
            "reception123",
            Staff.Role.RECEPTIONIST
        ));

        staffList.add(new Staff(
            UUID.randomUUID(),
            "James Wilson",
            "147 Welcome Road, City",
            User.Gender.MALE,
            "012-9999000",
            "james.wilson@clinic.com",
            "1987-12-18",
            "Receptionist",
            "Registration",
            "frontdesk",
            "front123",
            Staff.Role.RECEPTIONIST
        ));

        // Additional Admin Account
        staffList.add(new Staff(
            UUID.randomUUID(),
            "Maria Rodriguez",
            "258 Management Ave, City",
            User.Gender.FEMALE,
            "012-4444555",
            "maria.rodriguez@clinic.com",
            "1978-04-25",
            "Clinic Manager",
            "Administration",
            "manager",
            "manager123",
            Staff.Role.ADMIN
        ));

        System.out.println("Generated " + staffList.size() + " sample staff accounts");
        return staffList;
    }

    // Create staff accounts with specific roles
    public static ListInterface<Staff> createStaffByRole(Staff.Role role, int count) {
        ListInterface<Staff> staffList = new ArrayList<>();
        
        String[] maleNames = {"John", "Michael", "David", "James", "Robert", "William", "Richard", "Thomas"};
        String[] femaleNames = {"Sarah", "Emily", "Lisa", "Maria", "Jennifer", "Jessica", "Ashley", "Amanda"};
        String[] surnames = {"Johnson", "Smith", "Brown", "Davis", "Miller", "Wilson", "Moore", "Taylor"};
        String[] departments = {"General Medicine", "Emergency", "Pediatrics", "Cardiology", "Oncology", "Orthopedics"};
        
        for (int i = 0; i < count; i++) {
            User.Gender gender = (i % 2 == 0) ? User.Gender.MALE : User.Gender.FEMALE;
            String firstName = gender == User.Gender.MALE ? 
                             maleNames[i % maleNames.length] : 
                             femaleNames[i % femaleNames.length];
            String lastName = surnames[i % surnames.length];
            String fullName = firstName + " " + lastName;
            
            String position = getPositionByRole(role);
            String department = departments[i % departments.length];
            String account = role.toString().toLowerCase() + (i + 1);
            String password = role.toString().toLowerCase() + "123";
            
            Staff staff = new Staff(
                UUID.randomUUID(),
                fullName,
                (100 + i) + " " + role + " Street, City",
                gender,
                "012-" + String.format("%07d", 1000000 + i),
                firstName.toLowerCase() + "." + lastName.toLowerCase() + "@clinic.com",
                getRandomBirthDate(i),
                position,
                department,
                account,
                password,
                role
            );
            
            staffList.add(staff);
        }
        
        return staffList;
    }
    
    private static String getPositionByRole(Staff.Role role) {
        switch (role) {
            case ADMIN:
                return "Administrator";
            case DOCTOR:
                return "Medical Doctor";
            case NURSE:
                return "Registered Nurse";
            case RECEPTIONIST:
                return "Front Desk Receptionist";
            default:
                return "Staff Member";
        }
    }
    
    private static String getRandomBirthDate(int seed) {
        int year = 1970 + (seed % 25); // Birth years from 1970-1995
        int month = 1 + (seed % 12);   // Months 1-12
        int day = 1 + (seed % 28);     // Days 1-28 (safe for all months)
        
        return String.format("%04d-%02d-%02d", year, month, day);
    }

    // Create test accounts for development
    public static ListInterface<Staff> createTestAccounts() {
        ListInterface<Staff> testAccounts = new ArrayList<>();

        // Simple test accounts
        testAccounts.add(new Staff(
            UUID.randomUUID(),
            "Test Admin",
            "Test Address",
            User.Gender.MALE,
            "012-0000000",
            "test@admin.com",
            "1990-01-01",
            "Test Administrator",
            "Testing",
            "test",
            "test",
            Staff.Role.ADMIN
        ));

        testAccounts.add(new Staff(
            UUID.randomUUID(),
            "Demo User",
            "Demo Address",
            User.Gender.FEMALE,
            "012-1111111",
            "demo@user.com",
            "1985-01-01",
            "Demo Staff",
            "Demo Department",
            "demo",
            "demo",
            Staff.Role.RECEPTIONIST
        ));

        return testAccounts;
    }
}
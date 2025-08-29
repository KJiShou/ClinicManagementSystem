// Kong Ji Shou
package entity;
import java.util.UUID;

public class Staff extends User {
    private String position;
    private String department;
    private String account;
    private String password;
    private Role role;

    public Staff(UUID id, String name, String address, Gender gender, String phone, String email, String dateOfBirth,
                 String position, String department, String account, String password, Role role) {
        super(id, name, address, gender, phone, email, dateOfBirth);
        
        // Validation for Staff-specific fields
        if (position == null || position.trim().isEmpty()) {
            throw new IllegalArgumentException("Position is required for staff");
        }
        
        if (department == null || department.trim().isEmpty()) {
            throw new IllegalArgumentException("Department is required for staff");
        }
        
        if (account == null || account.trim().isEmpty()) {
            throw new IllegalArgumentException("Account is required for staff");
        }
        
        if (password == null || password.length() < 4) {
            throw new IllegalArgumentException("Password must be at least 4 characters long");
        }
        
        if (role == null) {
            throw new IllegalArgumentException("Role is required for staff");
        }
        
        // Validate account format (alphanumeric and underscore only)
        if (!account.matches("^[a-zA-Z0-9_]{3,20}$")) {
            throw new IllegalArgumentException("Account must be 3-20 characters containing only letters, numbers, and underscores");
        }
        
        // Validate password strength
        if (!isPasswordValid(password)) {
            throw new IllegalArgumentException("Password must contain at least one letter and one number");
        }
        
        this.position = position.trim();
        this.department = department.trim();
        this.account = account.trim().toLowerCase();
        this.password = password;
        this.role = role;
    }
    
    private boolean isPasswordValid(String password) {
        return password.matches(".*[a-zA-Z].*") && password.matches(".*\\d.*");
    }

    // Getters & Setters
    public String getPosition() {
        return position;
    }
    public void setPosition(String position) {
        this.position = position;
    }

    public String getDepartment() {
        return department;
    }
    public void setDepartment(String department) {
        this.department = department;
    }

    public String getAccount() {
        return account;
    }
    public void setAccount(String account) {
        this.account = account;
    }

    public String getPassword() {
        return password;
    }
    public void setPassword(String password) {
        this.password = password;
    }

    public Role getRole() {
        return role;
    }
    public void setRole(Role role) {
        this.role = role;
    }

    @Override
    public String toString() {
        return super.toString() + "\n" +
                "Position: " + position + "\n" +
                "Department: " + department + "\n" +
                "Account: " + account + "\n" +
                "Role: " + role;
    }

    public enum Role {
        ADMIN,
        DOCTOR,
        NURSE,
        RECEPTIONIST
    }
}

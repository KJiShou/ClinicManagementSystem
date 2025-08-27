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
        this.position = position;
        this.department = department;
        this.account = account;
        this.password = password;
        this.role = role;
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

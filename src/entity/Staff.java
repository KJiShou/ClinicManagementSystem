package entity;
import java.util.UUID;

public class Staff extends User {
    private String position;
    private String department;

    public Staff(UUID id, String name, String address, Gender gender, String phone, String email, String dateOfBirth,
                 String position, String department) {
        super(id, name, address, gender, phone, email, dateOfBirth);
        this.position = position;
        this.department = department;
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

    @Override
    public String toString() {
        return super.toString() + "\n" +
                "Position: " + position + "\n" +
                "Department: " + department;
    }
}

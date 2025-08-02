package entity;

public class Staff extends User {
    private String staffIC;

    public Staff(String id, String name, String address, String gender, String phone, String email, String dateOfBirth,
                 String staffIC) {
        super(id, name, address, gender, phone, email, dateOfBirth);
        this.staffIC = staffIC;
    }

    // Getter and setter
    public String getStaffIC() {
        return staffIC;
    }

    public void setStaffIC(String staffIC) {
        this.staffIC = staffIC;
    }
}

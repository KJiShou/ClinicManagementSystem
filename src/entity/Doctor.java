package entity;
import java.util.UUID;

public class Doctor extends User {
    private String specialization;
    private String licenseNumber;

    public Doctor(UUID id, String name, String address, Gender gender, String phone, String email, String dateOfBirth,
                  String specialization, String licenseNumber) {
        super(id, name, address, gender, phone, email, dateOfBirth);
        this.specialization = specialization;
        this.licenseNumber = licenseNumber;
    }

    public Doctor(UUID id, String name, String address, String male, String phone, String mail, String date, String specialization, String l0001, String cardiology) {
    }


    // Getters & Setters
    public String getSpecialization() {
        return specialization;
    }
    public void setSpecialization(String specialization) {
        this.specialization = specialization;
    }

    public String getLicenseNumber() {
        return licenseNumber;
    }
    public void setLicenseNumber(String licenseNumber) {
        this.licenseNumber = licenseNumber;
    }

    @Override
    public String toString() {
        return super.toString() + "\n" +
                "Specialization: " + specialization + "\n" +
                "License Number: " + licenseNumber;
    }
}

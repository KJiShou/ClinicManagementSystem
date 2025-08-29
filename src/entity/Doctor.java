// Chea Hong Jun
package entity;
import java.util.UUID;

public class Doctor extends User {
    private String specialization;
    private String licenseNumber;

    public Doctor(UUID id, String name, String address, Gender gender, String phone, String email, String dateOfBirth,
                  String specialization, String licenseNumber) {
        super(id, name, address, gender, phone, email, dateOfBirth);
        
        // Validation for Doctor-specific fields
        if (specialization == null || specialization.trim().isEmpty()) {
            throw new IllegalArgumentException("Specialization is required for doctor");
        }
        
        if (licenseNumber == null || licenseNumber.trim().isEmpty()) {
            throw new IllegalArgumentException("License number is required for doctor");
        }
        
        // Validate license number format (basic validation)
        if (!licenseNumber.matches("^[A-Z0-9]{6,15}$")) {
            throw new IllegalArgumentException("License number must be 6-15 characters containing only uppercase letters and numbers");
        }
        
        this.specialization = specialization.trim();
        this.licenseNumber = licenseNumber.trim().toUpperCase();
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

package entity;

import java.io.Serializable;
import java.util.UUID;

public class User implements Serializable {
    // Inner Gender enum
    public enum Gender {
        MALE("M"),
        FEMALE("F");

        private final String code;

        Gender(String code) {
            this.code = code;
        }

        public String getCode() {
            return code;
        }

        public static Gender fromString(String input) {
            if (input == null || input.trim().isEmpty()) {
                throw new IllegalArgumentException("Gender cannot be null or empty");
            }
            String normalized = input.trim().toUpperCase();
            for (Gender gender : values()) {
                if (gender.code.equals(normalized)) {
                    return gender;
                }
            }
            throw new IllegalArgumentException("Invalid gender. Must be 'M' or 'F'");
        }
    }

    private UUID userID;
    private String name;
    private String address;
    private Gender gender;  // Changed from String to Gender enum
    private String phone;
    private String email;
    private String dateOfBirth;

    public User() {
        // Default constructor
    }

    public User(UUID userID, String name, String address, Gender gender,
                String phone, String email, String dateOfBirth) {
        this.userID = userID;
        this.name = name;
        this.address = address;
        this.gender = gender;
        this.phone = phone;
        this.email = email;
        this.dateOfBirth = dateOfBirth;
    }

    // Getters
    public UUID getUserID() {
        return userID;
    }

    public String getName() {
        return name;
    }

    public String getAddress() {
        return address;
    }

    public Gender getGender() {
        return gender;
    }

    // Returns gender code (M/F) for compatibility
    public String getGenderCode() {
        return gender.getCode();
    }

    public String getPhone() {
        return phone;
    }

    public String getEmail() {
        return email;
    }

    public String getDateOfBirth() {
        return dateOfBirth;
    }

    // Setters
    public void setUserID(UUID userID) {
        this.userID = userID;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public void setGender(Gender gender) {
        this.gender = gender;
    }

    // Convenience setter that accepts String (M/F)
    public void setGender(String genderCode) {
        this.gender = Gender.fromString(genderCode);
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public void setDateOfBirth(String dateOfBirth) {
        this.dateOfBirth = dateOfBirth;
    }

    @Override
    public String toString() {
        return "UserID: " + userID + "\n" +
                "Name: " + name + "\n" +
                "Address: " + address + "\n" +
                "Gender: " + gender.getCode() + "\n" +
                "Phone: " + phone + "\n" +
                "Email: " + email + "\n" +
                "Date of Birth: " + dateOfBirth;
    }
}
// Ng Zhe Wei
package entity;

import java.io.Serializable;
import java.util.UUID;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;

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
    private Gender gender;
    private String phone;
    private String email;
    private LocalDate dateOfBirth;

    // Date formatter (for parsing from String)
    private static final DateTimeFormatter DATE_FORMAT = DateTimeFormatter.ofPattern("yyyy-MM-dd");

    public User() {
        // Default constructor
    }

    public User(UUID userID, String name, String address, Gender gender,
                String phone, String email, LocalDate dateOfBirth) {
        this.userID = userID;
        this.name = name;
        this.address = address;
        this.gender = gender;
        this.phone = phone;
        this.email = email;
        this.dateOfBirth = dateOfBirth;
    }

    // Overloaded constructor for String DOB
    public User(UUID userID, String name, String address, Gender gender,
                String phone, String email, String dateOfBirth) {
        this(userID, name, address, gender, phone, email, parseDate(dateOfBirth));
    }

    // Parse string date
    private static LocalDate parseDate(String dateStr) {
        try {
            return (dateStr == null || dateStr.isBlank()) ? null : LocalDate.parse(dateStr, DATE_FORMAT);
        } catch (DateTimeParseException e) {
            throw new IllegalArgumentException("Invalid date format. Expected yyyy-MM-dd, got: " + dateStr);
        }
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

    public String getGenderCode() {
        return gender != null ? gender.getCode() : "-";
    }

    public String getPhone() {
        return phone;
    }

    public String getEmail() {
        return email;
    }

    public LocalDate getDateOfBirth() {
        return dateOfBirth;
    }

    // Return DOB as string
    public String getDateOfBirthString() {
        return (dateOfBirth != null) ? dateOfBirth.format(DATE_FORMAT) : "-";
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

    public void setGender(String genderCode) {
        this.gender = Gender.fromString(genderCode);
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    // Accept both LocalDate and String for DOB
    public void setDateOfBirth(LocalDate dateOfBirth) {
        this.dateOfBirth = dateOfBirth;
    }

    public void setDateOfBirth(String dateOfBirth) {
        this.dateOfBirth = parseDate(dateOfBirth);
    }

    @Override
    public String toString() {
        return "UserID: " + userID + "\n" +
                "Name: " + name + "\n" +
                "Address: " + address + "\n" +
                "Gender: " + getGenderCode() + "\n" +
                "Phone: " + phone + "\n" +
                "Email: " + email + "\n" +
                "Date of Birth: " + getDateOfBirthString();
    }
}

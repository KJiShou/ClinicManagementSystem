package entity;

import java.io.Serializable;

public class User implements Serializable {
    private String userID;
    private String name;
    private String address;
    private String gender;
    private String phone;
    private String email;
    private String dateOfBirth;

    public User() {
        // Default constructor
    }

    public User(String userID, String name, String address, String gender,
                String phone, String email, String dateOfBirth) {
        this.userID = userID;
        this.name = name;
        this.address = address;
        this.gender = gender;
        this.phone = phone;
        this.email = email;
        this.dateOfBirth = dateOfBirth;
    }

    // Getters and Setters
    public String getUserID() {
        return userID;
    }

    public void setUserID(String userID) {
        this.userID = userID;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getGender() {
        return gender;
    }

    public void setGender(String gender) {
        this.gender = gender;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getDateOfBirth() {
        return dateOfBirth;
    }

    public void setDateOfBirth(String dateOfBirth) {
        this.dateOfBirth = dateOfBirth;
    }

    // Optional toString method for debugging or display
    @Override
    public String toString() {
        return "UserID: " + userID + "\n" +
                "Name: " + name + "\n" +
                "Address: " + address + "\n" +
                "Gender: " + gender + "\n" +
                "Phone: " + phone + "\n" +
                "Email: " + email + "\n" +
                "Date of Birth: " + dateOfBirth;
    }
}

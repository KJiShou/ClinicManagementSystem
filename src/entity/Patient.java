package entity;

import java.time.LocalDate;
import java.util.UUID;

public class Patient {
    private UUID patientID;
    private String patientIC;
    private String patientPassport;
    private String studentID;
    private String address;
    private LocalDate dateOfBirth;
    private String gender;
    private String phone;

    public Patient(UUID patientID, String patientIC, String patientPassport, String studentID,
                   String address, LocalDate dateOfBirth, String gender, String phone) {
        this.patientID = patientID;
        this.patientIC = patientIC;
        this.patientPassport = patientPassport;
        this.studentID = studentID;
        this.address = address;
        this.dateOfBirth = dateOfBirth;
        this.gender = gender;
        this.phone = phone;
    }

    // Getters
    public UUID getPatientID() {
        return patientID;
    }

    public String getPatientIC() {
        return patientIC;
    }

    public String getPatientPassport() {
        return patientPassport;
    }

    public String getStudentID() {
        return studentID;
    }

    public String getAddress() {
        return address;
    }

    public LocalDate getDateOfBirth() {
        return dateOfBirth;
    }

    public String getGender() {
        return gender;
    }

    public String getPhone() {
        return phone;
    }

    // Setters
    public void setPatientID(UUID patientID) {
        this.patientID = patientID;
    }

    public void setPatientIC(String patientIC) {
        this.patientIC = patientIC;
    }

    public void setPatientPassport(String patientPassport) {
        this.patientPassport = patientPassport;
    }

    public void setStudentID(String studentID) {
        this.studentID = studentID;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public void setDateOfBirth(LocalDate dateOfBirth) {
        this.dateOfBirth = dateOfBirth;
    }

    public void setGender(String gender) {
        this.gender = gender;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    @Override
    public String toString() {
        return "PatientID: " + patientID +
                "\nIC: " + patientIC +
                "\nPassport: " + patientPassport +
                "\nStudent ID: " + studentID +
                "\nAddress: " + address +
                "\nDOB: " + dateOfBirth +
                "\nGender: " + gender +
                "\nPhone: " + phone;
    }
}

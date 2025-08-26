package entity;

import java.util.UUID;

public class Patient extends User {
    private String patientIC;
    private String patientPassport;
    private String studentID;

    public Patient(UUID id, String name, String address, Gender gender, String phone, String email, String dateOfBirth,
                   String patientIC, String patientPassport, String studentID) {
        super(id, name, address, gender, phone, email, dateOfBirth);
        this.patientIC = patientIC;
        this.patientPassport = patientPassport;
        this.studentID = studentID;
    }

    // Alternate constructor that accepts gender as String
    public Patient(UUID id, String name, String address, String genderCode, String phone, String email, String dateOfBirth,
                   String patientIC, String patientPassport, String studentID) {
        this(id, name, address, Gender.fromString(genderCode), phone, email, dateOfBirth,
                patientIC, patientPassport, studentID);
    }

    public String getPatientIC() {
        return patientIC;
    }

    public void setPatientIC(String patientIC) {
        this.patientIC = patientIC;
    }

    public String getPatientPassport() {
        return patientPassport;
    }

    public void setPatientPassport(String patientPassport) {
        this.patientPassport = patientPassport;
    }

    public String getStudentID() {
        return studentID;
    }

    public void setStudentID(String studentID) {
        this.studentID = studentID;
    }

    public String getDisplayId() {
        if (patientIC != null && !patientIC.isBlank()) {
            // Check if it looks like a Malaysian IC (12 digits or nnnnnn-nn-nnnn)
            if (patientIC.matches("\\d{12}") || patientIC.matches("\\d{6}-\\d{2}-\\d{4}")) {
                return patientIC; // IC only
            } else {
                return patientIC + " (Other)";
            }
        }
        if (patientPassport != null && !patientPassport.isBlank()) {
            return patientPassport + " (Passport)";
        }
        if (studentID != null && !studentID.isBlank()) {
            return studentID + " (Student)";
        }
        return "N/A";
    }

    @Override
    public String toString() {
        return super.toString() + "\n" +
                "Patient IC: " + (patientIC != null ? patientIC : "N/A") + "\n" +
                "Passport: " + (patientPassport != null ? patientPassport : "N/A") + "\n" +
                "Student ID: " + (studentID != null ? studentID : "N/A");
    }
}

package entity;

public class Patient extends User {
    private String patientIC;
    private String patientPassport;
    private String studentID;

    public Patient(String id, String name, String address, String gender, String phone, String email, String dateOfBirth,
                   String patientIC, String patientPassport, String studentID) {
        super(id, name, address, gender, phone, email, dateOfBirth);
        this.patientIC = patientIC;
        this.patientPassport = patientPassport;
        this.studentID = studentID;
    }

    // Getters and setters
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
}

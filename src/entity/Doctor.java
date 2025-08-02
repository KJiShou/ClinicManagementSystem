package entity;

public class Doctor extends User {
    private String doctorIC;
    private String licenseID;
    private String specializeField;

    public Doctor(String id, String name, String address, String gender, String phone, String email, String dateOfBirth,
                  String doctorIC, String licenseID, String specializeField) {
        super(id, name, address, gender, phone, email, dateOfBirth);
        this.doctorIC = doctorIC;
        this.licenseID = licenseID;
        this.specializeField = specializeField;
    }

    // Getters and setters
    public String getDoctorIC() {
        return doctorIC;
    }

    public void setDoctorIC(String doctorIC) {
        this.doctorIC = doctorIC;
    }


    public String getLicenseID() {
        return licenseID;
    }

    public void setLicenseID(String licenseID) {
        this.licenseID = licenseID;
    }

    public String getSpecializeField() {
        return specializeField;
    }

    public void setSpecializeField(String specializeField) {
        this.specializeField = specializeField;
    }
}

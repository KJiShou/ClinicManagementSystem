package entity;

public class MedicalTreatment {
    private String treatmentID;
    private String appointmentID;
    private String prescriptionID;
    private String medicalRecordID;
    private String treatmentType;
    private String description;

    public MedicalTreatment(String treatmentID, String appointmentID, String prescriptionID,
                            String medicalRecordID, String treatmentType, String description) {
        this.treatmentID = treatmentID;
        this.appointmentID = appointmentID;
        this.prescriptionID = prescriptionID;
        this.medicalRecordID = medicalRecordID;
        this.treatmentType = treatmentType;
        this.description = description;
    }

    // Getters and Setters
    public String getTreatmentID() {
        return treatmentID;
    }

    public void setTreatmentID(String treatmentID) {
        this.treatmentID = treatmentID;
    }

    public String getAppointmentID() {
        return appointmentID;
    }

    public void setAppointmentID(String appointmentID) {
        this.appointmentID = appointmentID;
    }

    public String getPrescriptionID() {
        return prescriptionID;
    }

    public void setPrescriptionID(String prescriptionID) {
        this.prescriptionID = prescriptionID;
    }

    public String getMedicalRecordID() {
        return medicalRecordID;
    }

    public void setMedicalRecordID(String medicalRecordID) {
        this.medicalRecordID = medicalRecordID;
    }

    public String getTreatmentType() {
        return treatmentType;
    }

    public void setTreatmentType(String treatmentType) {
        this.treatmentType = treatmentType;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }
}

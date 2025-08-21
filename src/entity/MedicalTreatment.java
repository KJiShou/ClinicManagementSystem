package entity;

public class MedicalTreatment {
    private String treatmentID;
    private String treatmentType;
    private String description;

    public MedicalTreatment(String treatmentID, String treatmentType, String description) {
        this.treatmentID = treatmentID;
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

    public String toString() {
        return "** Medical Treatment **\n" +
                "Treatment ID       : " + treatmentID + "\n" +
                "Treatment Type     : " + treatmentType + "\n" +
                "Description        : " + description + "\n";
    }
}

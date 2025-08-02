package entity.pharmacyManagement;

import java.util.UUID;

public class LabTest extends SalesItem {
    private String code;
    private boolean fastingRequired;
    private String patientPrecautions;
    private double requiredVolumeMl;

    public LabTest(UUID id, String name, double price, String description, String unit, Company company, String code, boolean fastingRequired, String patientPrecautions, double requiredVolumeMl) {
        super(id, name, price, description, unit, company);
        this.code = code;
        this.fastingRequired = fastingRequired;
        this.patientPrecautions = patientPrecautions;
        this.requiredVolumeMl = requiredVolumeMl;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public boolean isFastingRequired() {
        return fastingRequired;
    }

    public void setFastingRequired(boolean fastingRequired) {
        this.fastingRequired = fastingRequired;
    }

    public String getPatientPrecautions() {
        return patientPrecautions;
    }

    public void setPatientPrecautions(String patientPrecautions) {
        this.patientPrecautions = patientPrecautions;
    }

    public double getRequiredVolumeMl() {
        return requiredVolumeMl;
    }

    public void setRequiredVolumeMl(double requiredVolumeMl) {
        this.requiredVolumeMl = requiredVolumeMl;
    }
}

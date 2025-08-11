package entity.pharmacyManagement;

import adt.DictionaryInterface;
import adt.HashedDictionary;

import java.util.UUID;

public class LabTest {
    private UUID id;
    private String name;
    private String code;
    private double price;
    private String description;
    private Company referringLab;
    private boolean fastingRequired;
    private String patientPrecautions;
    private String bloodTubes;

    public LabTest(UUID id, String name, double price, String description, Company referringLab, String code, boolean fastingRequired, String patientPrecautions,  String bloodTubes) {
        this.id = id;
        this.name = name;
        this.price = price;
        this.description = description;
        this.referringLab = referringLab;
        this.code = code;
        this.fastingRequired = fastingRequired;
        this.patientPrecautions = patientPrecautions;
        this.bloodTubes = bloodTubes;
    }

    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public double getPrice() {
        return price;
    }

    public void setPrice(double price) {
        this.price = price;
    }

    public Company getReferringLab() {
        return referringLab;
    }

    public void setReferringLab(Company referringLab) {
        this.referringLab = referringLab;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
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

    public String getBloodTubes() {
        return bloodTubes;
    }

    public void setBloodTubes(String bloodTubes) {
        this.bloodTubes = bloodTubes;
    }
}

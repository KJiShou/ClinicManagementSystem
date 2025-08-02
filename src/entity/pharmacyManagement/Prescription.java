package entity.pharmacyManagement;

import java.util.UUID;

public class Prescription {
    private UUID id;
    private String description;
    private float dosage;
    private int days;
    private SalesItem salesItem;

    public Prescription(UUID id, String description, float dosage, int days, SalesItem salesItem) {
        this.id = id;
        this.description = description;
        this.dosage = dosage;
        this.days = days;
        this.salesItem = salesItem;
    }

    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public float getDosage() {
        return dosage;
    }

    public void setDosage(float dosage) {
        this.dosage = dosage;
    }

    public int getDays() {
        return days;
    }

    public void setDays(int days) {
        this.days = days;
    }

    public SalesItem getSalesItem() {
        return salesItem;
    }

    public void setSalesItem(SalesItem salesItem) {
        this.salesItem = salesItem;
    }


}

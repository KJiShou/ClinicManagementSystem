package entity.pharmacyManagement;

import java.time.LocalDate;
import java.util.UUID;

public class Prescription {
    private UUID id;
    private String description;
    private float dosagePerDay; // Pills as unit
    private int days;
    private SalesItem salesItem;

    public Prescription(UUID id, String description, float dosagePerDay, int days, SalesItem salesItem) {
        this.id = id;
        this.description = description;
        this.dosagePerDay = dosagePerDay;
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

    public float getDosagePerDay() {
        return dosagePerDay;
    }

    public void setDosagePerDay(float dosagePerDay) {
        this.dosagePerDay = dosagePerDay;
    }

    //  Additional Function to get total pills
    public float getTotalPills() {
        return dosagePerDay * days;
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

    @Override
    public String toString() {
        return "** Prescription Details **  \n" +
                "Prescription ID     : " + id           + "\n" +
                "Medication          : " + salesItem.getName()   + "\n" +
                "Description         : " + description  + "\n" +
                "Dosage per Day      : " + dosagePerDay + "\n" +
                "Duration (Day)      : " + days                  + "\n";
    }
}

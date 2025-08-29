// Tan Yew Shen
package entity.pharmacyManagement;

import java.time.LocalDate;
import java.util.UUID;

public class Prescription {
    private UUID id;
    private String description;
    private float dosagePerTime;
    private int timesPerDay;
    private int days;
    private Medicine medicine;

    public Prescription(UUID id, String description, float dosagePerTime, int timesPerDay,int days, Medicine medicine) {
        this.id = id;
        this.description = description;
        this.dosagePerTime = dosagePerTime;
        this.timesPerDay = timesPerDay;
        this.days = days;
        this.medicine = medicine;
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

    //  Additional Function to get total pills
    public float getTotalPills() {
        return dosagePerTime * timesPerDay * days;
    }

    public int getDays() {
        return days;
    }

    public void setDays(int days) {
        this.days = days;
    }

    public Medicine getMedicine() {
        return medicine;
    }

    public void setMedicine(Medicine medicine) {
        this.medicine = medicine;
    }

    public float getDosagePerTime() {
        return dosagePerTime;
    }

    public void setDosagePerTime(float dosagePerTime) {
        this.dosagePerTime = dosagePerTime;
    }

    public int getTimesPerDay() {
        return timesPerDay;
    }

    public void setTimesPerDay(int timesPerDay) {
        this.timesPerDay = timesPerDay;
    }

    @Override
    public String toString() {
        return "** Prescription Details **  \n" +
                "Medication          : " + medicine.getName()   + "\n" +
                "Description         : " + description  + "\n" +
                "Dosage per Time     : " + dosagePerTime + "\n" +
                "Times per day       : " + timesPerDay + "\n" +
                "Duration (Day)      : " + days                  + "\n" +
                "Quantity Given      : " + medicine.getQuantity() + "\n" +
                "Price               : RM " + medicine.getPrice() * medicine.getQuantity();
    }
}

package entity.pharmacyManagement;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.UUID;

public class Medicine extends SalesItem{
    private MedicineKey key;
    private String brand;
    private String strength;
    private String unit;
    private Date expiryDate;


    public Medicine(UUID id, String name, double price, String description, String unit, Company company, String brand, String strength, String unit1, Date expiryDate) {
        super(id, name, price, description, unit, company);
        this.brand = brand;
        this.strength = strength;
        this.unit = unit1;
        this.expiryDate = expiryDate;
        this.key = new MedicineKey(name, brand, expiryDate);
    }

    public MedicineKey getKey() {
        return key;
    }

    public void setKey(MedicineKey key) {
        this.key = key;
    }

    public String getBrand() {
        return brand;
    }

    public void setBrand(String brand) {
        this.brand = brand;
    }

    public String getStrength() {
        return strength;
    }

    public void setStrength(String strength) {
        this.strength = strength;
    }

    @Override
    public String getUnit() {
        return unit;
    }

    @Override
    public void setUnit(String unit) {
        this.unit = unit;
    }

    public Date getExpiryDate() {
        return expiryDate;
    }

    public void setExpiryDate(Date expiryDate) {
        this.expiryDate = expiryDate;
    }

    @Override
    public String toString() {
        return super.toString() +
                "brand='" + brand + '\'' +
                ", expiryDate=" + expiryDate +
                '}';
    }
}

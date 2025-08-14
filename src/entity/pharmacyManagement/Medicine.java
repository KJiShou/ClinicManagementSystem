package entity.pharmacyManagement;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.UUID;

public class Medicine extends SalesItem{
    private String brand;
    private String strength;
    private int quantity;
    private String unit;


    public Medicine(UUID id, String name, int quantity, double price, String description, String unit, Company company, String brand, String strength, Date expiryDate) {
        super(id, name, price, description, company, expiryDate);
        this.quantity = quantity;
        this.brand = brand;
        this.strength = strength;
        this.unit = unit;
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

    public int getQuantity() {
        return quantity;
    }

    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }

    public String getUnit() {
        return unit;
    }

    public void setUnit(String unit) {
        this.unit = unit;
    }

    @Override
    public String toString() {
        return super.toString() +
                "brand='" + brand + '\'' +
                ", expiryDate=" + super.getExpiryDate() +
                '}';
    }

    public String getMedicineKey() {
        return getName() + "|"  + getStrength() + "|" + getExpiryDate();
    }
}

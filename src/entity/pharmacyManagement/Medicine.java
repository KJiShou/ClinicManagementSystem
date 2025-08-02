package entity.pharmacyManagement;

import java.util.Date;
import java.util.UUID;

public class Medicine extends SalesItem{
    private String brandName;
    private String strength;
    private String unit;
    private Date expiryDate;

    public Medicine(UUID id, String name, double price, String description, String unit, Company company, String brandName, String strength, String unit1, Date expiryDate) {
        super(id, name, price, description, unit, company);
        this.brandName = brandName;
        this.strength = strength;
        this.unit = unit1;
        this.expiryDate = expiryDate;
    }

    public String getBrandName() {
        return brandName;
    }

    public void setBrandName(String brandName) {
        this.brandName = brandName;
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
}

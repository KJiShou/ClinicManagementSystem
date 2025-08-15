package entity.pharmacyManagement;

import java.util.Date;
import java.util.UUID;

public class SalesItem {
    private UUID id;
    private String name;
    private double price;
    private String description;
    private Company company;
    private Date expiryDate;

    public SalesItem(UUID id, String name, double price, String description, Company company,  Date expiryDate) {
        this.id = id;
        this.name = name;
        this.price = price;
        this.description = description;
        this.company = company;
        this.expiryDate = expiryDate;
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

    public Company getCompany() {
        return company;
    }

    public void setCompany(Company company) {
        this.company = company;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Date getExpiryDate() {return  expiryDate;}

    public void setExpiryDate(Date expiryDate) {
        this.expiryDate = expiryDate;
    }

    public String toString() {
        return " Name: " + name + " Price: " + price + " Company: " +company;
    }
}

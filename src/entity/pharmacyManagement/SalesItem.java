package entity.pharmacyManagement;

import java.util.UUID;

public class SalesItem {
    private UUID id;
    private String name;
    private double price;
    private Company company;

    public SalesItem(UUID id, String name, double price, Company company) {
        this.id = id;
        this.name = name;
        this.price = price;
        this.company = company;
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

    public String toString() {
        return "ID: " + id + " Name: " + name + " Price: " + price + " Company: " +company;
    }
}

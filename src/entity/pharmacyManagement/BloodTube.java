package entity.pharmacyManagement;

import java.util.Date;
import java.util.UUID;

public class BloodTube {
    private UUID id;
    private String name;
    private int quantity;
    private Company company;
    private Date expiryDate;
    private double volumeMl;

    BloodTube(UUID id, String name, int quantity, Company company, Date expiryDate, double volumeMl) {
        this.id = id;
        this.name = name;
        this.quantity = quantity;
        this.company = company;
        this.expiryDate = expiryDate;
        this.volumeMl = volumeMl;
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

    public int getQuantity() {
        return quantity;
    }

    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }

    public Company getCompany() {
        return company;
    }

    public void setCompany(Company company) {
        this.company = company;
    }

    public Date getExpiryDate() {
        return expiryDate;
    }

    public void setExpiryDate(Date expiryDate) {
        this.expiryDate = expiryDate;
    }

    public double getVolumeMl() {
        return volumeMl;
    }

    public void setVolumeMl(double volumeMl) {
        this.volumeMl = volumeMl;
    }
}

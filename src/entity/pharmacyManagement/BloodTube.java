package entity.pharmacyManagement;

import java.util.Date;
import java.util.UUID;

public class BloodTube extends SalesItem{
    private String capColor;
    private String additive;
    private int quantity;
    private double volumeMl;

    public BloodTube(UUID id, String name, double price, int quantity, String description, Company company, Date expiryDate, double volumeMl, String capColor, String additive) {
        super(id, name, price, description, company, expiryDate);
        this.quantity = quantity;
        this.volumeMl = volumeMl;
        this.capColor = capColor;
        this.additive = additive;
    }

    public int getQuantity() {
        return quantity;
    }

    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }

    public double getVolumeMl() {
        return volumeMl;
    }

    public void setVolumeMl(double volumeMl) {
        this.volumeMl = volumeMl;
    }

    public String getAdditive() {
        return additive;
    }

    public void setAdditive(String additive) {
        this.additive = additive;
    }

    public String getCapColor() {
        return capColor;
    }

    public void setCapColor(String capColor) {
        this.capColor = capColor;
    }
}

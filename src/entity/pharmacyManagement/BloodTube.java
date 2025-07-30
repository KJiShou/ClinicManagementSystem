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
}

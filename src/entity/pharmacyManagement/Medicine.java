package entity.pharmacyManagement;

import java.util.Date;
import java.util.UUID;

public class Medicine extends SalesItem{
    private String brandName;
    private String strength;
    private Enum unit;
    private Date expiryDate;
    public Medicine(UUID id, String name, double price, Company company) {
        super(id, name, price, company);
    }
}

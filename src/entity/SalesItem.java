package entity;

import java.util.UUID;

public class SalesItem {
    public UUID getId() {
        return Id;
    }

    public void setId(UUID id) {
        Id = id;
    }

    public String getName() {
        return Name;
    }

    public void setName(String name) {
        Name = name;
    }

    public double getPrice() {
        return Price;
    }

    public void setPrice(double price) {
        Price = price;
    }

    private UUID Id;
    private String Name;
    private double  Price;
    public SalesItem(UUID id, String name, double price) {
        this.Id = id;
        this.Name = name;
        this.Price = price;
    }

}

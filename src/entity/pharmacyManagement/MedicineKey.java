package entity.pharmacyManagement;

import java.text.SimpleDateFormat;
import java.util.Date;

public class MedicineKey {
    private final String name;
    private final String brand;
    private final Date expiry;

    public MedicineKey(String name, String brand, Date expiry) {
        this.name   = name;
        this.brand = brand;
        this.expiry = expiry;
    }

    @Override public boolean equals(Object o) {
        if (!(o instanceof MedicineKey)) return false;
        MedicineKey k = (MedicineKey)o;
        return name.equals(k.name)
                && expiry.equals(k.expiry) && brand.equals(k.brand);
    }
    @Override public int hashCode() {
        return 31 * name.hashCode() + brand.hashCode();
    }

    @Override
    public String toString() {
        return "name='" + name + '\'' +
                ", brand='" + brand + '\'' +
                ", expiry=" + expiry;
    }
}
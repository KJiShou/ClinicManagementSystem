// Kong Ji Shou
package entity.pharmacyManagement;

import adt.DictionaryInterface;
import adt.HashedDictionary;
import entity.Staff;

import java.util.Date;
import java.util.UUID;

public class StockIn {
    private UUID id;
    private Date date;
    private Staff staff;
    private SalesItem salesItem;
    private DictionaryInterface<Integer, SalesItem> salesItems = new HashedDictionary<>();

    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
    }

    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
        this.date = date;
    }

    public Staff getStaff() {
        return staff;
    }

    public void setStaff(Staff staff) {
        this.staff = staff;
    }

    public SalesItem getSalesItem() {
        return salesItem;
    }

    public void setSalesItem(SalesItem salesItem) {
        this.salesItem = salesItem;
    }

    public DictionaryInterface<Integer, SalesItem> getSalesItems() {
        return salesItems;
    }

    public void setSalesItems(DictionaryInterface<Integer, SalesItem> salesItems) {
        this.salesItems = salesItems;
    }
}

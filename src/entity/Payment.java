// Teoh Yong Ming
package entity;

import java.util.UUID;

public class Payment {
    private UUID id;
    private Staff staff;
    private String paymentType;
    private float paidAmount;

    public Payment() {}

    public Payment(UUID id, Staff staff, String paymentType, float paidAmount) {
        this.id = id;
        this.staff = staff;
        this.paymentType = paymentType;
        this.paidAmount = paidAmount;
    }

    // Getter
    public UUID getId() { return id; }
    public Staff getStaff() { return staff; }
    public UUID getStaffId() { return staff.getUserID(); }
    public String getPaymentType() { return paymentType; }
    public float getPaidAmount() { return paidAmount; }

    // Setter
    public void setId(UUID id) { this.id = id; }
    public void setStaff(Staff staff) { this.staff = staff; }
    public void setPaymentType(String paymentType) { this.paymentType = paymentType; }
    public void setPaidAmount(float paidAmount) { this.paidAmount = paidAmount; }

    public String toString() {
        return  "Payment ID   : " + id +
                "\nStaff ID     : " + getStaffId() +
                "\nPayment Type : " + paymentType +
                "\nPaid Amount  : RM" + paidAmount;
    }
}

package entity.pharmacyManagement;

import java.util.UUID;

public class Company {
    private UUID Id;
    private String Name;
    private String Address;
    private String Email;
    private String PhoneNumber;

    public Company(UUID id, String name, String address, String email, String phoneNumber) {
        this.Id = id;
        this.Name = name;
        this.Address = address;
        this.Email = email;
        this.PhoneNumber = phoneNumber;
    }
}

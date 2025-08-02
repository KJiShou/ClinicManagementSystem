package entity.pharmacyManagement;

import java.util.Date;
import java.util.UUID;



public class Staff {
    enum Gender {
        MALE, FEMALE
    }
    private UUID id;
    private String name;
    private String IC;
    private String address;
    private Date dateOfBirth;
    private String phoneNumber;
    private Gender gender;
}

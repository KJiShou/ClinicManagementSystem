package utility;

import adt.ArrayList;
import adt.ListInterface;
import java.util.UUID;
import entity.Doctor;

public class GenerateDoctorData {
    public static ListInterface<Doctor> createSampleDoctors(){
        ListInterface<Doctor> doctors = new ArrayList<>();

        // wait for id change to uuid
        doctors.add(new Doctor(
                UUID.randomUUID(),
                "Dr.Kong Ji Shou",
                "10, Jalan Indah, Johor Bahru",
                "Male",
                "012-3456789",
                "jisho@gmail.com",
                "2005-07-30",
                "050730-14-5678",
                "L0001",
                "Cardiology"
        ));

        doctors.add(new Doctor(
                UUID.randomUUID(),
                "Dr.Kong Ji Yu",
                "10, Jalan Setapak, Kuala Lumpur",
                "Male",
                "013-2233445",
                "jiyu@gmail.com",
                "2005-07-29",
                "050729-14-5678",
                "L0002",
                "Orthopedics"
        ));

        doctors.add(new Doctor(
                UUID.randomUUID(),
                "Dr.Alex",
                "88 Jalan Tunku Abdul Rahman, Kuala Lumpur",
                "Male",
                "019-8765432",
                "alex@gmail.com",
                "2004-02-02",
                "040202-14-5678",
                "L0003",
                "Pediatrics"
        ));

        doctors.add(new Doctor(
                UUID.randomUUID(),
                "Dr.Susan",
                "45 Jalan Bukit Bintang, Kuala Lumpur",
                "Female",
                "019-9291738",
                "susan@gmail.com",
                "2004-12-02",
                "041202-14-5678",
                "L0004",
                "Pediatrics"
        ));

        return doctors;
    }

}

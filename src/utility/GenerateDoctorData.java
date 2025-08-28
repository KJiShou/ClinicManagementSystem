// Chea Hong Jun
package utility;

import adt.ArrayList;
import adt.ListInterface;
import entity.Doctor;
import entity.User;
import java.text.ParseException;
import java.util.UUID;

public class GenerateDoctorData {

    public static ListInterface<Doctor> createSampleDoctors() throws ParseException {
        ListInterface<Doctor> doctors = new ArrayList<>();

        // wait for id change to uuid
        doctors.add(new Doctor(
                UUID.randomUUID(),
                "Dr.Kong Ji Shou",
                "10, Jalan Indah, Johor Bahru",
                User.Gender.MALE,
                "012-3456789",
                "jisho@gmail.com",
                "2005-07-30",
                "Cardiology",
                "L0001"
        ));

        doctors.add(new Doctor(
                UUID.randomUUID(),
                "Dr.Kong Ji Yu",
                "10, Jalan Setapak, Kuala Lumpur",
                User.Gender.MALE,
                "013-2233445",
                "jiyu@gmail.com",
                "2005-07-29",
                "Orthopedics",
                "L0002"
        ));

        doctors.add(new Doctor(
                UUID.randomUUID(),
                "Dr.Alex",
                "88 Jalan Tunku Abdul Rahman, Kuala Lumpur",
                User.Gender.MALE,
                "019-8765432",
                "alex@gmail.com",
                "2004-02-02",
                "Pediatrics",
                "L0003"
        ));

        doctors.add(new Doctor(
                UUID.randomUUID(),
                "Dr.Susan",
                "45 Jalan Bukit Bintang, Kuala Lumpur",
                User.Gender.FEMALE,
                "019-9291738",
                "susan@gmail.com",
                "2004-12-02",
                "Pediatrics",
                "L0004"
        ));

        return doctors;
    }

}

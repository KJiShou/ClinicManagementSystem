package utility;

import adt.ArrayList;
import entity.Patient;

import java.util.UUID;

public class GeneratePatientData {

    public static ArrayList<Patient> createSamplePatients() {
        ArrayList<Patient> patients = new ArrayList<>();

        patients.add(new Patient(
                UUID.randomUUID(),
                "Ahmad Zulkifli",
                "No. 12, Jalan Bukit Bintang, 55100 Kuala Lumpur",
                "M",
                "012-3456789",
                "ahmad.zulkifli@example.com",
                "1990-05-15",
                "900515145678",
                null,
                null
        ));

        patients.add(new Patient(
                UUID.randomUUID(),
                "Siti Aminah",
                "23, Taman Desa Jaya, 81100 Johor Bahru, Johor",
                "F",
                "013-7654321",
                "siti.aminah@example.com",
                "1988-09-20",
                "880920-01-2345",
                null,
                null
        ));

        patients.add(new Patient(
                UUID.randomUUID(),
                "Lim Wei Sheng",
                "55, Jalan Ipoh, 51200 Kuala Lumpur",
                "M",
                "016-9988776",
                "lim.weisheng@example.com",
                "1995-12-01",
                "951201109988",
                null,
                "TP12345"
        ));

        patients.add(new Patient(
                UUID.randomUUID(),
                "Aishah Rahman",
                "78, Jalan Telawi, Bangsar, 59000 Kuala Lumpur",
                "F",
                "017-1122334",
                "aishah.rahman@example.com",
                "1992-03-10",
                "920310073344",
                null,
                null
        ));

        patients.add(new Patient(
                UUID.randomUUID(),
                "Raj Kumar",
                "101, Jalan Gasing, 46000 Petaling Jaya, Selangor",
                "M",
                "019-5566778",
                "raj.kumar@example.com",
                "1999-07-25",
                "990725085566",
                null,
                null
        ));

        return patients;
    }
}

package utility;

import adt.ArrayList;
import entity.Patient;

import java.util.UUID;

public class GeneratePatientData {

    public static ArrayList<Patient> createSamplePatients() {
        ArrayList<Patient> patients = new ArrayList<>();

        patients.add(new Patient(UUID.randomUUID(), "Ahmad Zulkifli", "No. 12, Jalan Bukit Bintang, 55100 Kuala Lumpur", "M", "012-3456789", "ahmad.zulkifli@example.com", "1990-05-15", "900515145678", null, null));
        patients.add(new Patient(UUID.randomUUID(), "Siti Aminah", "23, Taman Desa Jaya, 81100 Johor Bahru, Johor", "F", "013-7654321", "siti.aminah@example.com", "1988-09-20", "880920012345", null, null));
        patients.add(new Patient(UUID.randomUUID(), "Lim Wei Sheng", "55, Jalan Ipoh, 51200 Kuala Lumpur", "M", "016-9988776", "lim.weisheng@example.com", "1995-12-01", "951201109988", null, "TP12345"));
        patients.add(new Patient(UUID.randomUUID(), "Aishah Rahman", "78, Jalan Telawi, Bangsar, 59000 Kuala Lumpur", "F", "017-1122334", "aishah.rahman@example.com", "1992-03-10", "920310073344", null, null));
        patients.add(new Patient(UUID.randomUUID(), "Raj Kumar", "101, Jalan Gasing, 46000 Petaling Jaya, Selangor", "M", "019-5566778", "raj.kumar@example.com", "1999-07-25", "990725085566", null, null));
        patients.add(new Patient(UUID.randomUUID(), "Tan Mei Ling", "45, Jalan SS2, 47300 Petaling Jaya, Selangor", "F", "018-3344556", "tan.meiling@example.com", "2001-08-14", "010814141234", null, null));
        patients.add(new Patient(UUID.randomUUID(), "John Smith", "22, Orchard Road, Singapore", "M", "065-81234567", "john.smith@example.com", "1985-11-05", null, "E1234567", null));
        patients.add(new Patient(UUID.randomUUID(), "Nurul Huda", "12, Kampung Baru, 50300 Kuala Lumpur", "F", "014-7896543", "nurul.huda@example.com", "1997-02-18", "970218145678", null, null));
        patients.add(new Patient(UUID.randomUUID(), "David Lee", "9, Jalan Tun Razak, 50400 Kuala Lumpur", "M", "011-2233445", "david.lee@example.com", "1978-04-30", "780430106655", null, null));
        patients.add(new Patient(UUID.randomUUID(), "Maria Gonzales", "11, Jalan Ampang, 50450 Kuala Lumpur", "F", "010-8899776", "maria.gonzales@example.com", "1983-06-22", null, "P9876543", null));
        patients.add(new Patient(UUID.randomUUID(), "Ali Hassan", "90, Jalan Meru, 41050 Klang, Selangor", "M", "017-5566778", "ali.hassan@example.com", "2000-12-12", "001212108899", null, null));
        patients.add(new Patient(UUID.randomUUID(), "Chong Wai Yan", "32, Jalan Kuchai Lama, 58200 Kuala Lumpur", "F", "019-9988776", "chong.waiyan@example.com", "1993-07-01", "930701073322", null, null));
        patients.add(new Patient(UUID.randomUUID(), "Michael Tan", "17, Jalan Sultan, 50000 Kuala Lumpur", "M", "018-4455667", "michael.tan@example.com", "1980-01-25", "800125145566", null, null));
        patients.add(new Patient(UUID.randomUUID(), "Sophia Wong", "56, Jalan Raja Chulan, 50200 Kuala Lumpur", "F", "016-2233445", "sophia.wong@example.com", "1996-09-09", "960909087654", null, null));
        patients.add(new Patient(UUID.randomUUID(), "Chen Wei Hao", "21, Taman Universiti, 81300 Skudai, Johor", "M", "015-6677889", "chen.weihao@example.com", "2002-03-21", "020321011223", null, "TP99887"));
        patients.add(new Patient(UUID.randomUUID(), "Jessica Lim", "88, Jalan Sri Hartamas, 50480 Kuala Lumpur", "F", "012-8899776", "jessica.lim@example.com", "1991-12-02", "911202088765", null, null));
        patients.add(new Patient(UUID.randomUUID(), "Muhammad Irfan", "14, Taman Setiawangsa, 54200 Kuala Lumpur", "M", "013-1122334", "irfan.muhammad@example.com", "1998-10-17", "981017077654", null, null));
        patients.add(new Patient(UUID.randomUUID(), "Emily Johnson", "77, Jalan Holland, Singapore", "F", "065-93456789", "emily.johnson@example.com", "1987-02-05", null, "S3344556", null));
        patients.add(new Patient(UUID.randomUUID(), "Hafiz Abdullah", "45, Jalan Sentosa, 43000 Kajang, Selangor", "M", "014-4433221", "hafiz.abdullah@example.com", "1994-04-19", "940419066788", null, null));
        patients.add(new Patient(UUID.randomUUID(), "George Brown", "34, Clarke Quay, Singapore", "M", "065-87765432", "george.brown@example.com", "1984-01-22", null, "A1237890", null));
        patients.add(new Patient(UUID.randomUUID(), "Lee Jia Hao", "99, Jalan Damansara, 60000 Kuala Lumpur", "M", "012-6677889", "lee.jiahao@example.com", "2003-06-15", "030615098877", null, null));
        patients.add(new Patient(UUID.randomUUID(), "Noraini Omar", "7, Jalan Masjid India, 50100 Kuala Lumpur", "F", "013-2299887", "noraini.omar@example.com", "1979-08-21", "790821044566", null, null));
        patients.add(new Patient(UUID.randomUUID(), "Peter Chan", "18, Bukit Timah Road, Singapore", "M", "065-93456788", "peter.chan@example.com", "1990-11-11", null, "B8765432", null));
        patients.add(new Patient(UUID.randomUUID(), "Fatimah Zahra", "66, Jalan Permas, 81750 Masai, Johor", "F", "017-4455667", "fatimah.zahra@example.com", "1995-03-08", "950308088776", null, null));
        patients.add(new Patient(UUID.randomUUID(), "James Wilson", "20, Jalan Lavender, Singapore", "M", "065-99887766", "james.wilson@example.com", "1982-09-09", null, "X2233445", null));
        patients.add(new Patient(UUID.randomUUID(), "Kavitha Rani", "44, Jalan Tun Sambanthan, 50470 Kuala Lumpur", "F", "016-2233446", "kavitha.rani@example.com", "1999-05-23", "990523067899", null, null));
        patients.add(new Patient(UUID.randomUUID(), "Hassan Ali", "101, Jalan Merdeka, 75000 Melaka", "M", "019-3344556", "hassan.ali@example.com", "1975-12-31", "751231056789", null, null));
        patients.add(new Patient(UUID.randomUUID(), "Olivia Martinez", "12, Marina Bay Sands, Singapore", "F", "065-81237654", "olivia.martinez@example.com", "1988-07-12", null, "Z7788990", null));
        patients.add(new Patient(UUID.randomUUID(), "Syafiqah Azmi", "54, Taman Perling, 81200 Johor Bahru", "F", "018-6677889", "syafiqah.azmi@example.com", "2001-01-01", "010101102233", null, null));
        patients.add(new Patient(UUID.randomUUID(), "Abdul Rahman", "Kampung Air, 88000 Kota Kinabalu, Sabah", "M", "013-7778899", "abdul.rahman@example.com", "1965-06-30", "650630035566", null, null));
        patients.add(new Patient(UUID.randomUUID(), "Grace Tan", "Green Lane, 11600 George Town, Penang", "F", "012-9988776", "grace.tan@example.com", "2005-09-12", "050912087654", null, "TP44556"));
        patients.add(new Patient(UUID.randomUUID(), "Steven Wong", "Kuching Waterfront, 93000 Kuching, Sarawak", "M", "016-4455667", "steven.wong@example.com", "1972-03-05", "720305056677", null, null));
        patients.add(new Patient(UUID.randomUUID(), "Rina Susanto", "Jakarta Selatan, Indonesia", "F", "062-812334455", "rina.susanto@example.com", "1990-11-25", null, "ID778899", null));
        patients.add(new Patient(UUID.randomUUID(), "Mark Johnson", "Los Angeles, USA", "M", "001-2135556677", "mark.johnson@example.com", "1981-02-14", null, "US998877", null));
        patients.add(new Patient(UUID.randomUUID(), "Amirul Hakim", "No. 5, Kolej Kediaman, Universiti Malaya, 50603 Kuala Lumpur", "M", "017-8899221", "amirul.hakim@um.edu.my", "2002-11-15", null, null, "STU1001"));
        patients.add(new Patient(UUID.randomUUID(), "Sarah Tan", "Kolej Kediaman 12, Universiti Teknologi Malaysia, 81310 Skudai, Johor", "F", "018-6655443", "sarah.tan@student.utm.my", "2003-03-25", null, null, "STU1002"));
        patients.add(new Patient(UUID.randomUUID(), "Daniel Wong", "UTAR Kampar Campus, Jalan Universiti, 31900 Kampar, Perak", "M", "016-4455332", "daniel.wong@student.utar.edu.my", "2001-07-30", null, null, "STU1003"));
        patients.add(new Patient(UUID.randomUUID(), "Adam Firdaus", "45, Jalan Damai, 43000 Kajang, Selangor", "M", "017-2233445", "adam.firdaus@example.com", "2010-06-12", "100612105432", null, null));
        patients.add(new Patient(UUID.randomUUID(), "Chloe Lim", "12, Taman Bukit Indah, 81200 Johor Bahru, Johor", "F", "019-7788990", "chloe.lim@example.com", "2009-09-25", "090925101234", null, null));
        patients.add(new Patient(UUID.randomUUID(), "Harith Iskandar", "27, Taman Seri Gombak, 68100 Batu Caves, Selangor", "M", "011-6677889", "harith.iskandar@example.com", "2012-01-08", "120108107899", null, null));
        patients.add(new Patient(UUID.randomUUID(), "Alicia Tan", "8, Jalan Songket, 93350 Kuching, Sarawak", "F", "012-5566778", "alicia.tan@example.com", "2015-04-17", null, "P4455667", null));
        patients.add(new Patient(UUID.randomUUID(), "Muhammad Danish", "Sekolah Menengah Taman Melati, 53100 Kuala Lumpur", "M", "018-3344556", "danish.muhammad@student.my", "2008-11-03", null, null, "STU2001"));   // Age 17, Student ID
        
        return patients;
    }
}

// Teoh Yong Ming
package utility;

import adt.ArrayList;
import adt.DictionaryInterface;
import adt.ListInterface;
import entity.Consultation;
import entity.Doctor;
import entity.Patient;
import entity.pharmacyManagement.LabTest;
import entity.pharmacyManagement.Medicine;
import entity.pharmacyManagement.Prescription;
import entity.pharmacyManagement.SalesItem;

import java.text.ParseException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.UUID;

public class GenerateConsultationData {
    private static final DateTimeFormatter TIME_FORMATTER = DateTimeFormatter.ofPattern("HH:mm");
    private static LocalTime parseTime(String timeString) {
        return LocalTime.parse(timeString, TIME_FORMATTER);
    }

    private static ArrayList<Prescription> createPrescriptionList(DictionaryInterface<String, Medicine> medicines, String... medicineKeys) {
        ArrayList<Prescription> prescriptions = new ArrayList<>();
        for (String key : medicineKeys) {
            Medicine medicine = medicines.getValue(key);
            if (medicine != null) {
                prescriptions.add(new Prescription(
                    UUID.randomUUID(),
                    "Take as directed by doctor",
                    1.0f, // dosagePerTime
                    2, // timesPerDay
                    7, // days
                    medicine
                ));
            }
        }
        return prescriptions;
    }

    private static ArrayList<LabTest> createLabTestList(DictionaryInterface<String, LabTest> labTests, String... labTestKeys) {
        ArrayList<LabTest> testList = new ArrayList<>();
        for (String key : labTestKeys) {
            LabTest test = labTests.getValue(key);
            if (test != null) {
                testList.add(test);
            }
        }
        return testList;
    }

    public static ListInterface<Consultation> createSampleConsultation(ListInterface<Doctor> doctors, ArrayList<Patient> patients, DictionaryInterface<String, Medicine> medicines, DictionaryInterface<String, LabTest> labTests) throws ParseException {        ListInterface<Consultation> consultations = new ArrayList<>();

        LocalDate today = LocalDate.now();
        LocalDate yesterday = today.minusDays(1);
        LocalTime now = LocalTime.now();

        LocalTime w1Arr = now.minusMinutes(75);
        LocalTime w1End = w1Arr.plusMinutes(30);

        LocalTime ip1Arr = now.minusMinutes(40);
        LocalTime ip1End = ip1Arr.plusMinutes(60);

        LocalTime w2Arr = now.minusMinutes(15);
        LocalTime w2End = w2Arr.plusMinutes(30);

        LocalTime ip2Arr = now.minusMinutes(25);
        LocalTime ip2End = ip2Arr.plusMinutes(45);

        consultations.add(new Consultation(
                UUID.randomUUID(),
                patients.get(0),
                doctors.get(0),
                today,                   // date
                Consultation.Status.COMPLETED,
                "Fever, cough",
                parseTime("11:45"),
                parseTime("12:30"),
                50.00f,
                "medical treatment",
                createPrescriptionList(medicines, "Paracetamol|500 mg|2027-12-31", "Chlorpheniramine|4 mg|2027-12-31"),
                createLabTestList(labTests, "Full Blood Count")
        ));

        consultations.add(new Consultation(
                UUID.randomUUID(),
                patients.get(1),
                doctors.get(0),
                today,
                Consultation.Status.BILLING,
                "Follow-up for persistent fever and headache.",
                parseTime("09:30"),
                parseTime("10:00"),
                35.50f,
                "medical treatment",
                createPrescriptionList(medicines, "Paracetamol|500 mg|2027-12-31", "Ibuprofen|400 mg|2027-12-31"),
                new ArrayList<LabTest>()
        ));

        consultations.add(new Consultation(
                UUID.randomUUID(),
                patients.get(3),
                doctors.get(1),
                today,                  // today
                Consultation.Status.WAITING,
                "Initial consultation, general check-up.",
                w1Arr,
                w1End,
                75.00f,
                "medical treatment",
                new ArrayList<Prescription>(),
                createLabTestList(labTests, "Full Blood Count", "Lipid Profile")
        ));

        consultations.add(new Consultation(
                UUID.randomUUID(),
                patients.get(4),
                doctors.get(1),
                today,                 // today
                Consultation.Status.IN_PROGRESS,
                "Blood pressure monitoring and medication review.",
                ip1Arr,
                ip1End,
                150.00f,
                "medical treatment",
                createPrescriptionList(medicines, "Losartan|50 mg|2027-12-31", "Amlodipine|10 mg|2027-12-31"),
                createLabTestList(labTests, "Lipid Profile", "HbA1c")
        ));

        consultations.add(new Consultation(
                UUID.randomUUID(),
                patients.get(4),
                doctors.get(3),
                today,
                Consultation.Status.COMPLETED,
                "Allergy testing and lifestyle advice.",
                parseTime("13:15"),
                parseTime("14:00"),
                180.25f,
                "medical treatment",
                createPrescriptionList(medicines, "Cetirizine|10 mg|2027-12-31", "Bilastine|20 mg|2027-12-31"),
                createLabTestList(labTests, "Total IgE")
        ));

        consultations.add(new Consultation(
                UUID.randomUUID(),
                patients.get(4),
                doctors.get(0),
                today,
                Consultation.Status.BILLING,
                "Pediatric routine check-up and vaccination.",
                parseTime("09:00"),
                parseTime("09:45"),
                105.00f,
                "medical treatment",
                createPrescriptionList(medicines, "Children's Paracetamol (Syrup)|120 mg/5ml|2027-12-31"),
                createLabTestList(labTests, "Full Blood Count")
        ));

        consultations.add(new Consultation(
                UUID.randomUUID(),
                patients.get(0),
                doctors.get(0),
                today,                  // today
                Consultation.Status.WAITING,
                "Ankle sprain from playing sports.",
                w2Arr,
                w2End,
                60.00f,
                "medical treatment",
                createPrescriptionList(medicines, "Ibuprofen|400 mg|2027-12-31", "Diclofenac Gel|1%|2027-12-31"),
                new ArrayList<LabTest>()
        ));

        consultations.add(new Consultation(
                UUID.randomUUID(),
                patients.get(0),
                doctors.get(0),
                today,                 // today
                Consultation.Status.IN_PROGRESS,
                "Follow-up for anxiety and stress management.",
                ip2Arr,
                ip2End,
                250.00f,
                "medical treatment",
                createPrescriptionList(medicines, "Alprazolam|0.5 mg|2027-12-31"),
                new ArrayList<LabTest>()
        ));

        consultations.add(new Consultation(
                UUID.randomUUID(),
                patients.get(0),
                doctors.get(0),
                today,
                Consultation.Status.COMPLETED,
                "Rash on arm, prescribed cream.",
                parseTime("16:00"),
                parseTime("16:30"),
                90.00f,
                "medical treatment",
                createPrescriptionList(medicines, "Hydrocortisone Cream|1%|2027-12-31", "Chlorpheniramine|4 mg|2027-12-31"),
                new ArrayList<LabTest>()
        ));

        consultations.add(new Consultation(
                UUID.randomUUID(),
                patients.get(0),
                doctors.get(0),
                today,
                Consultation.Status.BILLING,
                "Discussing dietary plan for weight loss.",
                parseTime("11:45"),
                parseTime("12:15"),
                85.75f,
                "medical treatment",
                new ArrayList<Prescription>(),
                createLabTestList(labTests, "Lipid Profile", "HbA1c", "Fasting Blood Sugar")
        ));

        consultations.add(new Consultation(
                UUID.randomUUID(),
                patients.get(0),
                doctors.get(1),
                yesterday,
                Consultation.Status.COMPLETED,
                "Routine health checkup, blood pressure check.",
                parseTime("09:15"),
                parseTime("09:45"),
                75.00f,
                "Blood pressure monitoring, general examination",
                new ArrayList<Prescription>(),
                createLabTestList(labTests, "Full Blood Count", "Lipid Profile")
        ));

        consultations.add(new Consultation(
                UUID.randomUUID(),
                patients.get(1),
                doctors.get(0),
                yesterday,
                Consultation.Status.COMPLETED,
                "Follow-up for diabetes management.",
                parseTime("10:30"),
                parseTime("11:15"),
                120.50f,
                "Diabetes consultation, medication review",
                createPrescriptionList(medicines, "Metformin|500 mg|2027-12-31", "Gliclazide MR|30 mg|2027-12-31"),
                createLabTestList(labTests, "HbA1c", "Fasting Blood Sugar")
        ));

        consultations.add(new Consultation(
                UUID.randomUUID(),
                patients.get(2),
                doctors.get(2),
                yesterday,
                Consultation.Status.COMPLETED,
                "Migraine treatment and prevention advice.",
                parseTime("14:00"),
                parseTime("14:30"),
                95.25f,
                "Prescribed pain relief, lifestyle advice",
                createPrescriptionList(medicines, "Paracetamol|500 mg|2027-12-31", "Ibuprofen|400 mg|2027-12-31"),
                new ArrayList<LabTest>()
        ));

        consultations.add(new Consultation(
                UUID.randomUUID(),
                patients.get(3),
                doctors.get(1),
                yesterday,
                Consultation.Status.COMPLETED,
                "Skin condition treatment, prescribed ointment.",
                parseTime("15:30"),
                parseTime("16:00"),
                85.00f,
                "Dermatology consultation, topical treatment",
                createPrescriptionList(medicines, "Hydrocortisone Cream|1%|2027-12-31", "Betamethasone Cream|0.1%|2027-12-31"),
                new ArrayList<LabTest>()
        ));

        consultations.add(new Consultation(
                UUID.randomUUID(),
                patients.get(4),
                doctors.get(0),
                yesterday,
                Consultation.Status.COMPLETED,
                "Annual physical examination.",
                parseTime("11:00"),
                parseTime("12:00"),
                150.00f,
                "Comprehensive health screening",
                new ArrayList<Prescription>(),
                createLabTestList(labTests, "Full Blood Count", "Lipid Profile", "HbA1c", "Liver Function Test")
        ));

        consultations.add(new Consultation(
                UUID.randomUUID(),
                patients.get(0),
                doctors.get(3),
                yesterday,
                Consultation.Status.COMPLETED,
                "Respiratory infection treatment.",
                parseTime("16:45"),
                parseTime("17:15"),
                110.75f,
                "Antibiotics prescribed, rest advised",
                createPrescriptionList(medicines, "Amoxicillin|500 mg|2027-12-31", "Paracetamol|500 mg|2027-12-31"),
                createLabTestList(labTests, "Full Blood Count")
        ));

        return consultations;
    }
}

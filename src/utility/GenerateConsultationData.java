package utility;

import adt.ArrayList;
import adt.ListInterface;
import entity.Consultation;
import entity.Doctor;
import entity.Patient;
import entity.pharmacyManagement.Prescription;

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

    public static ListInterface<Consultation> createSampleConsultation() throws ParseException {
        ListInterface<Consultation> consultations = new ArrayList<>();
        ListInterface<Doctor> doctors = GenerateDoctorData.createSampleDoctors();
        ListInterface<Patient> patients = GeneratePatientData.createSamplePatients();

        LocalDate today = LocalDate.now();
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
                parseTime("11:30"),
                50.00f,
                "medical treatment",
                null
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
                new ArrayList<Prescription>()
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
                new ArrayList<Prescription>()
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
                new ArrayList<Prescription>()
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
                new ArrayList<Prescription>()
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
                new ArrayList<Prescription>()
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
                new ArrayList<Prescription>()
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
                new ArrayList<Prescription>()
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
                new ArrayList<Prescription>()
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
                new ArrayList<Prescription>()
        ));

        return consultations;
    }
}

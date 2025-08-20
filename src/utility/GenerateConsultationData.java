package utility;

import adt.ArrayList;
import adt.ListInterface;
import entity.Consultation;
import entity.Doctor;
import entity.Patient;
import entity.User;
import java.text.ParseException;
import java.util.UUID;
import java.time.LocalDate;
import java.time.LocalTime;

public class GenerateConsultationData {

    public static ListInterface<Consultation> createSampleConsultation() throws ParseException {
        ListInterface<Consultation> consultations = new ArrayList<>();

        consultations.add(new Consultation(
                UUID.randomUUID(),
                null, //patient
                null, //doctor
                LocalDate.parse("2025-05-19"),
                Consultation.Status.COMPLETED,
                "Fever, cough",
                LocalTime.parse("10:00"),
                LocalTime.parse("11:30"),
                50.00f
        ));

        consultations.add(new Consultation(
                UUID.randomUUID(),
                null, // patient
                null, // doctor
                LocalDate.parse("2025-05-20"),
                Consultation.Status.BILLING,
                "Follow-up for persistent fever and headache.",
                LocalTime.parse("09:30"),
                LocalTime.parse("10:00"),
                35.50f
        ));

                consultations.add(new Consultation(
                UUID.randomUUID(),
                null, // patient
                null, // doctor
                LocalDate.parse("2025-05-21"),
                Consultation.Status.WAITING,
                "Initial consultation, general check-up.",
                LocalTime.parse("14:00"),
                LocalTime.parse("14:45"),
                75.00f
        ));

                consultations.add(new Consultation(
                UUID.randomUUID(),
                null, // patient
                null, // doctor
                LocalDate.parse("2025-05-22"),
                Consultation.Status.IN_PROGRESS,
                "Blood pressure monitoring and medication review.",
                LocalTime.parse("11:00"),
                LocalTime.parse("12:00"),
                150.00f
        ));

                consultations.add(new Consultation(
                UUID.randomUUID(),
                null, // patient
                null, // doctor
                LocalDate.parse("2025-05-23"),
                Consultation.Status.COMPLETED,
                "Allergy testing and lifestyle advice.",
                LocalTime.parse("13:15"),
                LocalTime.parse("14:00"),
                180.25f
        ));

                consultations.add(new Consultation(
                UUID.randomUUID(),
                null, // patient
                null, // doctor
                LocalDate.parse("2025-05-24"),
                Consultation.Status.BILLING,
                "Pediatric routine check-up and vaccination.",
                LocalTime.parse("09:00"),
                LocalTime.parse("09:45"),
                105.00f
        ));

                consultations.add(new Consultation(
                UUID.randomUUID(),
                null, // patient
                null, // doctor
                LocalDate.parse("2025-05-26"),
                Consultation.Status.WAITING,
                "Ankle sprain from playing sports.",
                LocalTime.parse("15:30"),
                LocalTime.parse("16:00"),
                60.00f
        ));

                consultations.add(new Consultation(
                UUID.randomUUID(),
                null, // patient
                null, // doctor
                LocalDate.parse("2025-05-27"),
                Consultation.Status.IN_PROGRESS,
                "Follow-up for anxiety and stress management.",
                LocalTime.parse("10:30"),
                LocalTime.parse("11:15"),
                250.00f
        ));

               consultations.add(new Consultation(
                UUID.randomUUID(),
                null, // patient
                null, // doctor
                LocalDate.parse("2025-05-28"),
                Consultation.Status.COMPLETED,
                "Rash on arm, prescribed cream.",
                LocalTime.parse("16:00"),
                LocalTime.parse("16:30"),
                90.00f
        ));

                consultations.add(new Consultation(
                UUID.randomUUID(),
                null, // patient
                null, // doctor
                LocalDate.parse("2025-05-29"),
                Consultation.Status.BILLING,
                "Discussing dietary plan for weight loss.",
                LocalTime.parse("11:45"),
                LocalTime.parse("12:15"),
                85.75f
        ));
        
        return consultations;
    }

}

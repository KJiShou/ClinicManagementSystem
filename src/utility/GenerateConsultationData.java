package utility;

import adt.ArrayList;
import adt.ListInterface;
import entity.Consultation;
import entity.Doctor;
import entity.Patient;
import entity.User;
import java.text.ParseException;
import java.util.UUID;

public class GenerateConsultationData {

    public static ListInterface<Consultation> createSampleConsultation() throws ParseException {
        ListInterface<Consultation> consultations = new ArrayList<>();

        consultations.add(new Consultation(
                UUID.randomUUID(),
                null, //patient
                null, //doctor
                "2025-05-19",
                Consultation.Status.COMPLETED,
                "Fever, cough",
                "10:00",
                "11:30",
                200.00
        ));

        return consultations;
    }

}

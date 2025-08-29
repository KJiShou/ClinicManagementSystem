// Teoh Yong Ming
package utility;

import adt.ArrayList;
import adt.ListInterface;
import entity.Appointment;
import entity.Doctor;
import entity.Patient;
import entity.Staff;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.UUID;

public class GenerateAppointmentData {

    public static ListInterface<Appointment> createSampleAppointments(ListInterface<Doctor> doctors, ArrayList<Patient> patients, ListInterface<Staff> staff) {
        ListInterface<Appointment> appointments = new ArrayList<>();
        
        if (doctors == null || doctors.isEmpty() || patients == null || patients.isEmpty()) {
            System.out.println("Warning: No doctors or patients provided, returning empty appointment list");
            return appointments;
        }
        
        // Use first staff member (receptionist/admin) as the default appointment creator
        Staff appointmentCreator = null;
        if (staff != null && !staff.isEmpty()) {
            appointmentCreator = staff.get(0);
        }

        LocalDate today = LocalDate.now();
        LocalDate tomorrow = today.plusDays(1);
        LocalDate dayAfterTomorrow = today.plusDays(2);
        LocalDate yesterday = today.minusDays(1);

        // Today's appointments
        appointments.add(new Appointment(
            UUID.randomUUID(),
            patients.get(0),
            doctors.get(0),
            appointmentCreator,
            today,
            LocalTime.of(9, 0),
            LocalTime.of(9, 30),
            "Consultation",
            "Regular health check-up",
            Appointment.Status.SCHEDULED
        ));

        appointments.add(new Appointment(
            UUID.randomUUID(),
            patients.get(1),
            doctors.get(0),
            appointmentCreator,
            today,
            LocalTime.of(10, 30),
            LocalTime.of(11, 0),
            "Follow-up",
            "Blood pressure monitoring follow-up",
            Appointment.Status.SCHEDULED
        ));

        appointments.add(new Appointment(
            UUID.randomUUID(),
            patients.get(2),
            doctors.get(1),
            appointmentCreator,
            today,
            LocalTime.of(14, 0),
            LocalTime.of(14, 30),
            "Consultation",
            "Skin condition examination",
            Appointment.Status.SCHEDULED
        ));

        appointments.add(new Appointment(
            UUID.randomUUID(),
            patients.get(3),
            doctors.get(1),
            appointmentCreator,
            today,
            LocalTime.of(15, 30),
            LocalTime.of(16, 0),
            "Procedure",
            "Minor procedure - wound dressing",
            Appointment.Status.SCHEDULED
        ));

        // Tomorrow's appointments
        appointments.add(new Appointment(
            UUID.randomUUID(),
            patients.get(4),
            doctors.get(0),
            appointmentCreator,
            tomorrow,
            LocalTime.of(9, 30),
            LocalTime.of(10, 0),
            "Consultation",
            "Diabetes management consultation",
            Appointment.Status.SCHEDULED
        ));

        appointments.add(new Appointment(
            UUID.randomUUID(),
            patients.get(0),
            doctors.get(2),
            appointmentCreator,
            tomorrow,
            LocalTime.of(11, 0),
            LocalTime.of(11, 30),
            "Check-up",
            "Annual physical examination",
            Appointment.Status.SCHEDULED
        ));

        appointments.add(new Appointment(
            UUID.randomUUID(),
            patients.get(1),
            doctors.get(2),
            appointmentCreator,
            tomorrow,
            LocalTime.of(13, 30),
            LocalTime.of(14, 0),
            "Follow-up",
            "Post-surgery follow-up examination",
            Appointment.Status.SCHEDULED
        ));

        appointments.add(new Appointment(
            UUID.randomUUID(),
            patients.get(3),
            doctors.get(0),
            appointmentCreator,
            tomorrow,
            LocalTime.of(16, 0),
            LocalTime.of(16, 30),
            "Consultation",
            "Migraine treatment consultation",
            Appointment.Status.SCHEDULED
        ));

        // Day after tomorrow's appointments
        appointments.add(new Appointment(
            UUID.randomUUID(),
            patients.get(2),
            doctors.get(1),
            appointmentCreator,
            dayAfterTomorrow,
            LocalTime.of(10, 0),
            LocalTime.of(10, 30),
            "Follow-up",
            "Treatment progress evaluation",
            Appointment.Status.SCHEDULED
        ));

        appointments.add(new Appointment(
            UUID.randomUUID(),
            patients.get(4),
            doctors.get(2),
            appointmentCreator,
            dayAfterTomorrow,
            LocalTime.of(14, 30),
            LocalTime.of(15, 0),
            "Consultation",
            "Respiratory system examination",
            Appointment.Status.SCHEDULED
        ));

        // Yesterday's completed appointments
        appointments.add(new Appointment(
            UUID.randomUUID(),
            patients.get(0),
            doctors.get(1),
            appointmentCreator,
            yesterday,
            LocalTime.of(9, 15),
            LocalTime.of(9, 45),
            "Consultation",
            "Routine health consultation",
            Appointment.Status.SCHEDULED
        ));

        appointments.add(new Appointment(
            UUID.randomUUID(),
            patients.get(2),
            doctors.get(0),
            appointmentCreator,
            yesterday,
            LocalTime.of(11, 30),
            LocalTime.of(12, 0),
            "Check-up",
            "Quarterly health assessment",
            Appointment.Status.SCHEDULED
        ));

        appointments.add(new Appointment(
            UUID.randomUUID(),
            patients.get(4),
            doctors.get(1),
            appointmentCreator,
            yesterday,
            LocalTime.of(15, 0),
            LocalTime.of(15, 30),
            "Follow-up",
            "Medication review and adjustment",
            Appointment.Status.SCHEDULED
        ));

        // Some cancelled appointments
        appointments.add(new Appointment(
            UUID.randomUUID(),
            patients.get(1),
            doctors.get(2),
            appointmentCreator,
            today,
            LocalTime.of(12, 0),
            LocalTime.of(12, 30),
            "Consultation",
            "Patient cancelled due to emergency",
            Appointment.Status.CANCELLED
        ));

        appointments.add(new Appointment(
            UUID.randomUUID(),
            patients.get(3),
            doctors.get(0),
            appointmentCreator,
            yesterday,
            LocalTime.of(16, 30),
            LocalTime.of(17, 0),
            "Procedure",
            "Cancelled - doctor unavailable",
            Appointment.Status.CANCELLED
        ));

        // Future appointments (next week)
        LocalDate nextWeek = today.plusDays(7);
        
        appointments.add(new Appointment(
            UUID.randomUUID(),
            patients.get(0),
            doctors.get(0),
            appointmentCreator,
            nextWeek,
            LocalTime.of(10, 0),
            LocalTime.of(10, 30),
            "Follow-up",
            "One week follow-up appointment",
            Appointment.Status.SCHEDULED
        ));

        appointments.add(new Appointment(
            UUID.randomUUID(),
            patients.get(1),
            doctors.get(1),
            appointmentCreator,
            nextWeek,
            LocalTime.of(14, 0),
            LocalTime.of(15, 0),
            "Procedure",
            "Scheduled medical procedure",
            Appointment.Status.SCHEDULED
        ));

        appointments.add(new Appointment(
            UUID.randomUUID(),
            patients.get(2),
            doctors.get(2),
            appointmentCreator,
            nextWeek,
            LocalTime.of(11, 30),
            LocalTime.of(12, 0),
            "Consultation",
            "Specialist consultation referral",
            Appointment.Status.SCHEDULED
        ));

        // Various appointment types for demonstration
        appointments.add(new Appointment(
            UUID.randomUUID(),
            patients.get(3),
            doctors.get(0),
            appointmentCreator,
            nextWeek.plusDays(1),
            LocalTime.of(13, 0),
            LocalTime.of(13, 30),
            "Vaccination",
            "Annual flu vaccination",
            Appointment.Status.SCHEDULED
        ));

        appointments.add(new Appointment(
            UUID.randomUUID(),
            patients.get(4),
            doctors.get(1),
            appointmentCreator,
            nextWeek.plusDays(2),
            LocalTime.of(15, 30),
            LocalTime.of(16, 30),
            "Physical Therapy",
            "Post-injury rehabilitation session",
            Appointment.Status.SCHEDULED
        ));

        // Weekend appointments (if applicable)
        if (doctors.size() > 3) {
            LocalDate saturday = today.plusDays(6 - today.getDayOfWeek().getValue() + 6);
            
            appointments.add(new Appointment(
                UUID.randomUUID(),
                patients.get(0),
                doctors.get(3),
                appointmentCreator,
                saturday,
                LocalTime.of(10, 0),
                LocalTime.of(10, 30),
                "Emergency Consultation",
                "Weekend emergency appointment",
                Appointment.Status.SCHEDULED
            ));
        }

        return appointments;
    }
}
package utility;

import adt.ArrayList;
import adt.ListInterface;
import entity.Appointment;
import entity.Doctor;
import entity.Patient;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.UUID;

public class GenerateAppointmentData {

    public static ListInterface<Appointment> createSampleAppointments(ListInterface<Doctor> doctors, ArrayList<Patient> patients) {
        ListInterface<Appointment> appointments = new ArrayList<>();
        
        if (doctors == null || doctors.isEmpty() || patients == null || patients.isEmpty()) {
            System.out.println("Warning: No doctors or patients provided, returning empty appointment list");
            return appointments;
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
            null, // staff
            today,
            LocalTime.of(9, 0),
            LocalTime.of(9, 30),
            "Consultation",
            "Regular health check-up",
            Appointment.Status.CONFIRMED
        ));

        appointments.add(new Appointment(
            UUID.randomUUID(),
            patients.get(1),
            doctors.get(0),
            null,
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
            null,
            today,
            LocalTime.of(14, 0),
            LocalTime.of(14, 30),
            "Consultation",
            "Skin condition examination",
            Appointment.Status.CONFIRMED
        ));

        appointments.add(new Appointment(
            UUID.randomUUID(),
            patients.get(3),
            doctors.get(1),
            null,
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
            null,
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
            null,
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
            null,
            tomorrow,
            LocalTime.of(13, 30),
            LocalTime.of(14, 0),
            "Follow-up",
            "Post-surgery follow-up examination",
            Appointment.Status.CONFIRMED
        ));

        appointments.add(new Appointment(
            UUID.randomUUID(),
            patients.get(3),
            doctors.get(0),
            null,
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
            null,
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
            null,
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
            null,
            yesterday,
            LocalTime.of(9, 15),
            LocalTime.of(9, 45),
            "Consultation",
            "Routine health consultation",
            Appointment.Status.COMPLETED
        ));

        appointments.add(new Appointment(
            UUID.randomUUID(),
            patients.get(2),
            doctors.get(0),
            null,
            yesterday,
            LocalTime.of(11, 30),
            LocalTime.of(12, 0),
            "Check-up",
            "Quarterly health assessment",
            Appointment.Status.COMPLETED
        ));

        appointments.add(new Appointment(
            UUID.randomUUID(),
            patients.get(4),
            doctors.get(1),
            null,
            yesterday,
            LocalTime.of(15, 0),
            LocalTime.of(15, 30),
            "Follow-up",
            "Medication review and adjustment",
            Appointment.Status.COMPLETED
        ));

        // Some cancelled appointments
        appointments.add(new Appointment(
            UUID.randomUUID(),
            patients.get(1),
            doctors.get(2),
            null,
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
            null,
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
            null,
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
            null,
            nextWeek,
            LocalTime.of(14, 0),
            LocalTime.of(15, 0),
            "Procedure",
            "Scheduled medical procedure",
            Appointment.Status.CONFIRMED
        ));

        appointments.add(new Appointment(
            UUID.randomUUID(),
            patients.get(2),
            doctors.get(2),
            null,
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
            null,
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
            null,
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
                null,
                saturday,
                LocalTime.of(10, 0),
                LocalTime.of(10, 30),
                "Emergency Consultation",
                "Weekend emergency appointment",
                Appointment.Status.CONFIRMED
            ));
        }

        System.out.println("Generated " + appointments.size() + " sample appointments");
        return appointments;
    }

    // Utility method to create appointments for a specific date range
    public static ListInterface<Appointment> createAppointmentsForDateRange(
            ListInterface<Doctor> doctors, 
            ArrayList<Patient> patients, 
            LocalDate startDate, 
            LocalDate endDate) {
        
        ListInterface<Appointment> appointments = new ArrayList<>();
        
        if (doctors == null || doctors.isEmpty() || patients == null || patients.isEmpty()) {
            return appointments;
        }

        LocalDate currentDate = startDate;
        int appointmentCounter = 0;
        String[] appointmentTypes = {"Consultation", "Follow-up", "Check-up", "Procedure", "Vaccination"};
        String[] descriptions = {
            "Regular health consultation",
            "Follow-up examination",
            "Annual health check-up",
            "Minor medical procedure",
            "Preventive vaccination"
        };

        while (!currentDate.isAfter(endDate)) {
            // Skip weekends (optional - remove if weekend appointments are needed)
            if (currentDate.getDayOfWeek().getValue() < 6) { // Monday to Friday
                
                // Generate 2-4 appointments per day
                int appointmentsPerDay = 2 + (appointmentCounter % 3);
                
                for (int i = 0; i < appointmentsPerDay; i++) {
                    // Select random doctor and patient
                    Doctor doctor = doctors.get(appointmentCounter % doctors.size());
                    Patient patient = patients.get(appointmentCounter % patients.size());
                    
                    // Generate time slots (9 AM to 5 PM)
                    int hour = 9 + (i * 2); // Spread appointments throughout the day
                    int minute = (appointmentCounter % 2) * 30; // 0 or 30 minutes
                    
                    if (hour < 17) { // Ensure within business hours
                        LocalTime startTime = LocalTime.of(hour, minute);
                        LocalTime endTime = startTime.plusMinutes(30);
                        
                        String appointmentType = appointmentTypes[appointmentCounter % appointmentTypes.length];
                        String description = descriptions[appointmentCounter % descriptions.length];
                        
                        Appointment.Status status;
                        if (currentDate.isBefore(LocalDate.now())) {
                            status = (appointmentCounter % 10 == 0) ? Appointment.Status.CANCELLED : Appointment.Status.COMPLETED;
                        } else if (currentDate.equals(LocalDate.now())) {
                            status = (appointmentCounter % 3 == 0) ? Appointment.Status.CONFIRMED : Appointment.Status.SCHEDULED;
                        } else {
                            status = Appointment.Status.SCHEDULED;
                        }
                        
                        appointments.add(new Appointment(
                            UUID.randomUUID(),
                            patient,
                            doctor,
                            null,
                            currentDate,
                            startTime,
                            endTime,
                            appointmentType,
                            description,
                            status
                        ));
                        
                        appointmentCounter++;
                    }
                }
            }
            currentDate = currentDate.plusDays(1);
        }

        System.out.println("Generated " + appointments.size() + " appointments for date range: " + startDate + " to " + endDate);
        return appointments;
    }
}
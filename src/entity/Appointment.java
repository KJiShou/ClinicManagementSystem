package entity;

import java.util.UUID;
import java.time.LocalDate;
import java.time.LocalTime;

public class Appointment {
    private UUID id;
    private Patient patient;
    private Doctor doctor;
    private Staff staff;
    private LocalDate appointmentDate;
    private LocalTime startTime;
    private LocalTime endTime;
    private String appointmentType;
    private String description;

    public Appointment() {}

    public Appointment(UUID id, Patient patient, Doctor doctor, Staff staff, LocalDate appointmentDate, LocalTime startTime, LocalTime endTime, String appointmentType, String description) {
        this.id = id;
        this.patient = patient;
        this.doctor = doctor;
        this.staff = staff;
        this.appointmentDate = appointmentDate;
        this.appointmentType = appointmentType;
        this.startTime = startTime;
        this.endTime = endTime;
        this.description = description;
    }

    // Getter
    public UUID getId() { return id; }
    public Patient getPatient() { return patient; }
    public UUID getPatientId() { return patient.getUserID(); }
    public Doctor getDoctor() { return doctor; }
    public UUID getDoctorId() { return doctor.getUserID(); }
    public Staff getStaff() { return staff; }
    public UUID getStaffId() { return staff.getUserID(); }
    public LocalDate getappointmentDate() { return appointmentDate; }
    public String getAppointmentType() { return appointmentType; }
    public LocalTime getStartTime() { return startTime; }
    public LocalTime getEndTime() { return endTime; }
    public String getDescription() { return description; }

    // Setter
    public void setId(UUID id) { this.id = id; }
    public void setPatient(Patient patient) { this.patient = patient; }
    public void setDoctor(Doctor doctor) { this.doctor = doctor; }
    public void setappointmentDate(LocalDate appointmentDate) { this.appointmentDate = appointmentDate; }
    public void setAppointmentType(String appointmentType) { this.appointmentType = appointmentType; }
    public void setStartTime(LocalTime startTime) { this.startTime = startTime; }
    public void setEndTime(LocalTime endTime) { this.endTime = endTime; }
    public void setDescription(String description) { this.description = description; }

    public String toString(){
        return  "Appointment ID  : " + id +
                "\nPatient ID      : " + getPatientId() +
                "\nDoctor ID       : " + getDoctorId() +
                "\nStaff ID        : " + getStaffId() +
                "\nDate            : " + appointmentDate +
                "\nAppointmentType : " + appointmentType +
                "\nStart Time      : " + startTime +
                "\nEnd Time        : " + endTime +
                "\nDescription     : " + description;
    }
}

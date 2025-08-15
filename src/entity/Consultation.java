package entity;

import java.util.UUID;
import java.time.LocalDate;
import java.time.LocalTime;

public class Consultation {
    private UUID id;
    private Patient patient;
    private Doctor doctor;
    private LocalDate consultatonDate;
    public Status status;
    private String notes;
    private LocalTime startTime;
    private LocalTime endTime;
    private float totalPayment;

    public Consultation() {}

    public Consultation(UUID id, Patient patient, Doctor doctor, LocalDate consultatonDate, Status status, String notes, LocalTime startTime, LocalTime endTime, float totalPayment) {
        this.id = id;
        this.patient = patient;
        this.doctor = doctor;
        this.consultatonDate = consultatonDate;
        this.status = status;
        this.notes = notes;
        this.startTime = startTime;
        this.endTime = endTime;
        this.totalPayment = totalPayment;
    }

    // Getter
    public UUID getId() { return id; }
    public Patient getPatient() { return patient; }
    public UUID getPatientId() { return patient.getUserID(); }
    public Doctor getDoctor() { return doctor; }
    public UUID getDoctorId() {return doctor.getUserID(); }
    public LocalDate getConsultatonDate() { return consultatonDate; }
    public Status getStatus() { return status; }
    public String getNotes() { return notes; }
    public LocalTime getStartTime() { return startTime; }
    public LocalTime getEndTime() { return endTime; }
    public float getTotalPayment() { return totalPayment; }

    // Setter
    public void setId(UUID id) { this.id = id; }
    public void setPatient(Patient patient) { this.patient = patient; }
    public void setDoctor(Doctor doctor) { this.doctor = doctor; }
    public void setConsultatonDate(LocalDate consultatonDate) { this.consultatonDate = consultatonDate; }
    public void setStatus(Status status) { this.status = status; }
    public void setNotes(String notes) { this.notes = notes; }
    public void setStartTime(LocalTime startTime) { this.startTime = startTime; }
    public void setEndTime(LocalTime endTime) { this.endTime = endTime; }
    public void setTotalPayment(float totalPayment) { this.totalPayment = totalPayment; }

    public String toString(){
        return  "Consultation ID : " + id +
                "\nPatient ID      : " + getPatientId() +
                "\nDoctor ID       : " + getDoctorId() +
                "\nDate            : " + consultatonDate +
                "\nStatus          : " + status +
                "\nNotes           : " + notes +
                "\nStart Time      : " + startTime +
                "\nEnd Time        : " + endTime +
                "\nTotal Payment   : " + totalPayment;
    }

    private enum Status {
        WAITING,
        BILLING,
        IN_PROGRESS,
        COMPLETED
    }
}

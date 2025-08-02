package control;

//import entity.Doctor;
//import entity.Patient;

import java.util.UUID;
import java.time.LocalDate;
import java.time.LocalTime;

public class Consultation {
    private UUID id;
    private UUID patientId;
    private UUID doctorId;
    private LocalDate consultatonDate;
    private String notes;
    private LocalTime startTime;
    private LocalTime endTime;
    private float totalPayment;

    public Consultation(UUID id, UUID patientId, UUID doctorId, LocalDate consultatonDate, String notes, LocalTime startTime, LocalTime endTime, float totalPayment) {
        this.id = id;
        this.patientId = patientId;
        this.doctorId = doctorId;
        this.consultatonDate = consultatonDate;
        this.notes = notes;
        this.startTime = startTime;
        this.endTime = endTime;
        this.totalPayment = totalPayment;
    }

    // Getter
    public UUID getId() { return id; }
    public UUID getPatientId() { return patientId; }
    public UUID getDoctorId() { return doctorId; }
    public LocalDate getConsultatonDate() { return consultatonDate; }
    public String getNotes() { return notes; }
    public LocalTime getStartTime() { return startTime; }
    public LocalTime getEndTime() { return endTime; }
    public float getTotalPayment() { return totalPayment; }

    // Setter
    public void setId(UUID id) { this.id = id; }
    public void setPatientId(UUID patientId) { this.patientId = patientId; }
    public void setDoctorId(UUID doctorId) { this.doctorId = doctorId; }
    public void setConsultatonDate(LocalDate consultatonDate) { this.consultatonDate = consultatonDate; }
    public void setNotes(String notes) { this.notes = notes; }
    public void setStartTime(LocalTime startTime) { this.startTime = startTime; }
    public void setEndTime(LocalTime endTime) { this.endTime = endTime; }
    public void setTotalPayment(float totalPayment) { this.totalPayment = totalPayment; }

    public String toString(){
        return  "Consultation ID : " + id +
                "Patient ID      : " + patientId +
                "Doctor ID       : " + doctorId +
                "Date            : " + consultatonDate +
                "Notes           : " + notes +
                "Start Time      : " + startTime +
                "End Time        : " + endTime +
                "Total Payment   : " + totalPayment;
    }
}

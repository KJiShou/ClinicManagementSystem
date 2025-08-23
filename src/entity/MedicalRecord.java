package entity;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.UUID;

public class MedicalRecord {
    private String medicalRecordID;
    private LocalTime startTime;
    private LocalTime endTime;
    private LocalDate date;
    private String diagnosis;
    private String note;

    public MedicalRecord(String medicalRecordID, LocalTime startTime,
                         LocalTime endTime, LocalDate date, String diagnosis, String note) {
        this.medicalRecordID = medicalRecordID;
        this.startTime = startTime;
        this.endTime = endTime;
        this.date = date;
        this.diagnosis = diagnosis;
        this.note = note;
    }

    // Getters and Setters
    public String getMedicalRecordID() {
        return medicalRecordID;
    }

    public void setMedicalRecordID(String medicalRecordID) {
        this.medicalRecordID = medicalRecordID;
    }

    public LocalTime getStartTime() {
        return startTime;
    }

    public void setStartTime(LocalTime startTime) {
        this.startTime = startTime;
    }

    public LocalTime getEndTime() {
        return endTime;
    }

    public void setEndTime(LocalTime endTime) {
        this.endTime = endTime;
    }

    public LocalDate getDate() {
        return date;
    }

    public void setDate(LocalDate date) {
        this.date = date;
    }

    public String getDiagnosis() {
        return diagnosis;
    }

    public void setDiagnosis(String diagnosis) {
        this.diagnosis = diagnosis;
    }
}

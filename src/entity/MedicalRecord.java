package entity;

// Change up for Pull Request

import java.time.LocalDate;
import java.time.LocalTime;

public class MedicalRecord {
    private String medicalRecordID;
    private String treatmentID;
    private LocalTime startTime;
    private LocalTime endTime;
    private LocalDate date;
    private String diagnosis;
    private String note;

    public MedicalRecord(String medicalRecordID, String treatmentID, LocalTime startTime,
                         LocalTime endTime, LocalDate date, String diagnosis, String note) {
        this.medicalRecordID = medicalRecordID;
        this.treatmentID = treatmentID;
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

    public String getTreatmentID() {
        return treatmentID;
    }

    public void setTreatmentID(String treatmentID) {
        this.treatmentID = treatmentID;
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

    public String getNote() {
        return note;
    }

    public void setNote(String note) {
        this.note = note;
    }
}


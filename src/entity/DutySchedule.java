package entity;

import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;

public class DutySchedule {
    private LocalTime startTime;
    private LocalTime endTime;
    private LocalDate date;

    private static final DateTimeFormatter DATE_FMT = DateTimeFormatter.ofPattern("yyyy-MM-dd");
    private static final DateTimeFormatter TIME_FMT = DateTimeFormatter.ofPattern("HH:mm");

    public DutySchedule(String dateStr, String start, String end) {
        try {
            this.date = LocalDate.parse(dateStr, DATE_FMT);
            this.startTime = LocalTime.parse(start, TIME_FMT);
            this.endTime = LocalTime.parse(end, TIME_FMT);
        } catch (DateTimeParseException e) {
            System.out.println("Invalid date or time format. Use YYYY-MM-DD and HH:MM.");
            this.date = null;
            this.startTime = null;
            this.endTime = null;
        }
    }

    //get set
    public LocalTime getStartTimeObject() { return startTime; }
    public void setStartTime(LocalTime startTime) { this.startTime = startTime; }

    public LocalTime getEndTimeObject() { return endTime; }
    public void setEndTime(LocalTime endTime) { this.endTime = endTime; }

    public LocalDate getDateObject() { return date; }
    public void setDate(LocalDate date) { this.date = date; }


    public String getDate() {
        return date != null ? date.format(DATE_FMT) : "Invalid Date";
    }

    public String getStartTime() {
        return startTime != null ? startTime.format(TIME_FMT) : "Invalid Time";
    }

    public String getEndTime() {
        return endTime != null ? endTime.format(TIME_FMT) : "Invalid Time";
    }


    public boolean isValid() {
        return date != null && startTime != null && endTime != null;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null || getClass() != obj.getClass()) return false;
        DutySchedule other = (DutySchedule) obj;
        return date != null && date.equals(other.date) &&
                startTime != null && startTime.equals(other.startTime) &&
                endTime != null && endTime.equals(other.endTime);
    }

    @Override
    public int hashCode() {
        return java.util.Objects.hash(date, startTime, endTime);
    }

    @Override
    public String toString() {
        if (date == null || startTime == null || endTime == null) {
            return "Invalid DutySchedule";
        }
        return date.format(DATE_FMT) + " " +
                startTime.format(TIME_FMT) + " - " +
                endTime.format(TIME_FMT);
    }
}
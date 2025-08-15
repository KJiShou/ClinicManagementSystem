package entity;

import java.time.LocalTime;

public class DutySchedule {
    private LocalTime startTime;
    private LocalTime endTime;
    private int day;
    private int month;
    private int year;

    public DutySchedule(LocalTime startTime, LocalTime endTime, int day, int month, int year) {
        this.startTime = startTime;
        this.endTime = endTime;
        this.day = day;
        this.month = month;
        this.year = year;
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

    public int getDay() {
        return day;
    }

    public void setDay(int day) {
        this.day = day;
    }

    public int getMonth() {
        return month;
    }

    public void setMonth(int month) {
        this.month = month;
    }

    public int getYear() {
        return year;
    }

    public void setYear(int year) {
        this.year = year;
    }

    @Override
    public String toString() {
        return String.format("%02d/%02d/%d %s - %s",
                day, month, year, startTime, endTime);
    }


}

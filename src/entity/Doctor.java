package entity;

import java.util.UUID;

public class Doctor {
    private UUID id;
    private String name;
    private String description;
    private String[] dutySchedule;
    private boolean isAvailable;
    private int amountDuty;

    // constructor
    public Doctor(UUID id, String name, String description, boolean isAvailable) {
        this.id = id;
        this.name = name;
        this.description = description;
        this.dutySchedule = new String[15];
        this.isAvailable = isAvailable;
        this.amountDuty = 0;
    }

    // get, set
    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String[] getDutySchedule() {
        return dutySchedule;
    }

    public void setDutySchedule(String[] dutySchedule) {
        this.dutySchedule = dutySchedule;
    }

    public int getAmountDuty() {
        return amountDuty;
    }

    public void setAmountDuty(int amountDuty) {
        this.amountDuty = amountDuty;
    }

    public boolean isAvailable() {
        return isAvailable;
    }

    public void setAvailable(boolean available) {
        isAvailable = available;
    }

    public void addDuty(String duty) {
        if (amountDuty < dutySchedule.length) {
            dutySchedule[amountDuty++] = duty;
        } else {
            System.out.println("Duty schedule is full for " + name);
        }
    }

    @Override
    public String toString() {
        return "Doctor ID: " + id +
                "\nName: " + name +
                "\nDescription: " + description +
                "\nAvailable: " + isAvailable +
                "\nDuty Count: " + amountDuty;
    }
}

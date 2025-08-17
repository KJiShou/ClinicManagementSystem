package control;

import adt.*;
import boundary.DutyScheduleUI;
import entity.Doctor;
import entity.DutySchedule;

import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.UUID;

public class DutyScheduleControl {
    private HashedDictionary<UUID, ListInterface<DutySchedule>> schedules;
    private ListInterface<Doctor> doctors;
    private DutyScheduleUI ui;
    private static final DateTimeFormatter DATE_FORMAT = DateTimeFormatter.ofPattern("yyyy-MM-dd");
    private static final DateTimeFormatter TIME_FORMAT = DateTimeFormatter.ofPattern("HH:mm");

    public DutyScheduleControl(ListInterface<Doctor> doctors, HashedDictionary<UUID, ListInterface<DutySchedule>> schedules) {
        this.doctors = doctors;
        this.schedules = schedules != null ? schedules : new HashedDictionary<>();
        this.ui = new DutyScheduleUI();
    }

    public void main() {
        if (doctors == null || doctors.isEmpty()) {
            ui.displayMessage("Error: No doctors data available. Please load doctor data first.");
            return;
        }

        while (true) {
            try {
                Integer choice = ui.mainMenu();
                if (choice == null) continue;

                switch (choice) {
                    case 1:
                        addDuty();
                        break;
                    case 2:
                        updateDuty();
                        break;
                    case 3:
                        deleteDuty();
                        break;
                    case 4:
                        viewDoctorSchedule();
                        break;
                    case 999:
                        System.out.println("Exiting Duty Schedule Management...");
                        return;
                    default:
                        ui.displayMessage("Invalid choice. Please try again.");
                }
            } catch (Exception e) {
                ui.displayMessage("An error occurred: " + e.getMessage());
            }
        }
    }

    private void addDuty() {
        try {
            Doctor doctor = ui.selectDoctor(doctors);
            if (doctor == null) return;

            DutySchedule newSchedule = ui.getDutyScheduleInput();
            if (newSchedule == null || !isValidSchedule(newSchedule)) {
                ui.displayMessage("Failed to create schedule due to invalid input.");
                return;
            }

            //check time conflict
            if (hasTimeConflict(doctor.getUserID(), newSchedule)) {
                ui.displayMessage("Schedule conflict detected! Doctor already has a duty during this time period.");
                return;
            }

            ListInterface<DutySchedule> doctorSchedules = schedules.getValue(doctor.getUserID());
            if (doctorSchedules == null) {
                doctorSchedules = new ArrayList<>();
            }

            doctorSchedules.add(newSchedule);
            schedules.add(doctor.getUserID(), doctorSchedules);
            ui.displayMessage("Schedule added successfully for " + doctor.getName());

        } catch (Exception e) {
            ui.displayMessage("Error adding schedule: " + e.getMessage());
        }
    }

    private void updateDuty() {
        try {
            Doctor doctor = ui.selectDoctor(doctors);
            if (doctor == null) return;

            ListInterface<DutySchedule> doctorSchedules = schedules.getValue(doctor.getUserID());
            if (doctorSchedules == null || doctorSchedules.isEmpty()) {
                ui.displayMessage("No schedules found for " + doctor.getName());
                return;
            }

            int idx = ui.selectScheduleFromList(doctor, doctorSchedules);
            if (idx == -1) return;

            DutySchedule newSchedule = ui.getDutyScheduleInput();
            if (newSchedule == null || !isValidSchedule(newSchedule)) {
                ui.displayMessage("Failed to update schedule due to invalid input.");
                return;
            }

            //check time conflict for update and except the update schedule
            if (hasTimeConflictExcluding(doctor.getUserID(), newSchedule, idx)) {
                ui.displayMessage("Schedule conflict detected! Doctor already has a duty during this time period.");
                return;
            }

            doctorSchedules.set(idx, newSchedule);
            ui.displayMessage("Schedule updated successfully.");

        } catch (Exception e) {
            ui.displayMessage("Error updating schedule: " + e.getMessage());
        }
    }

    private void deleteDuty() {
        try {
            Doctor doctor = ui.selectDoctor(doctors);
            if (doctor == null) return;

            ListInterface<DutySchedule> doctorSchedules = schedules.getValue(doctor.getUserID());
            if (doctorSchedules == null || doctorSchedules.isEmpty()) {
                ui.displayMessage("No schedules found for " + doctor.getName());
                return;
            }

            int idx = ui.selectScheduleFromList(doctor, doctorSchedules);
            if (idx == -1) return;

            doctorSchedules.remove(idx);
            ui.displayMessage("Schedule deleted successfully.");

        } catch (Exception e) {
            ui.displayMessage("Error deleting schedule: " + e.getMessage());
        }
    }

    private void viewDoctorSchedule() {
        try {
            Doctor doctor = ui.selectDoctor(doctors);
            if (doctor == null) return;

            ListInterface<DutySchedule> doctorSchedules = schedules.getValue(doctor.getUserID());
            if (doctorSchedules == null || doctorSchedules.isEmpty()) {
                ui.displayMessage("No schedules found for Dr. " + doctor.getName());
            } else {
                ui.viewDoctorSchedule(doctor, doctorSchedules);
            }

        } catch (Exception e) {
            ui.displayMessage("Error viewing schedule: " + e.getMessage());
        }
    }

    // check format correct not
    private boolean isValidSchedule(DutySchedule schedule) {
        try {
            //check date
            LocalDate date = LocalDate.parse(schedule.getDate(), DATE_FORMAT);

            //check tiem
            LocalTime startTime = LocalTime.parse(schedule.getStartTime(), TIME_FORMAT);
            LocalTime endTime = LocalTime.parse(schedule.getEndTime(), TIME_FORMAT);

            //check end is before start or not
            if (!endTime.isAfter(startTime)) {
                ui.displayMessage("End time must be after start time.");
                return false;
            }

            return true;
        } catch (DateTimeParseException e) {
            ui.displayMessage("Invalid date/time format. Please use YYYY-MM-DD for date and HH:mm for time.");
            return false;
        }
    }

    //check time conflict
    private boolean hasTimeConflict(UUID doctorId, DutySchedule newSchedule) {
        return hasTimeConflictExcluding(doctorId, newSchedule, -1);
    }

    private boolean hasTimeConflictExcluding(UUID doctorId, DutySchedule newSchedule, int excludeIndex) {
        ListInterface<DutySchedule> existingSchedules = schedules.getValue(doctorId);
        if (existingSchedules == null || existingSchedules.isEmpty()) {
            return false;
        }

        try {
            LocalDate newDate = LocalDate.parse(newSchedule.getDate(), DATE_FORMAT);
            LocalTime newStartTime = LocalTime.parse(newSchedule.getStartTime(), TIME_FORMAT);
            LocalTime newEndTime = LocalTime.parse(newSchedule.getEndTime(), TIME_FORMAT);

            for (int i = 0; i < existingSchedules.size(); i++) {
                if (i == excludeIndex) continue; //skip updating schedule

                DutySchedule existing = existingSchedules.get(i);
                LocalDate existingDate = LocalDate.parse(existing.getDate(), DATE_FORMAT);

                //check same day only
                if (!newDate.equals(existingDate)) continue;

                LocalTime existingStartTime = LocalTime.parse(existing.getStartTime(), TIME_FORMAT);
                LocalTime existingEndTime = LocalTime.parse(existing.getEndTime(), TIME_FORMAT);

                //time conflict
                if (isTimeOverlapping(newStartTime, newEndTime, existingStartTime, existingEndTime)) {
                    return true;
                }
            }
        } catch (DateTimeParseException e) {
            return true; //if conflict then stop
        }

        return false;
    }

    //check two time schedule conflict
    private boolean isTimeOverlapping(LocalTime start1, LocalTime end1, LocalTime start2, LocalTime end2) {
        return start1.isBefore(end2) && start2.isBefore(end1);
    }

}
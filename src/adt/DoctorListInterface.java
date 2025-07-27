package adt;

import entity.Doctor;

public interface DoctorListInterface {

    void addDoctor(Doctor doctor);

    Doctor getDoctor(int x);

    boolean removeDoctor(int x);

    boolean replaceDoctor(int x, Doctor newDoctor);

    int getTotalDoctors();

    boolean isEmpty();

    void clearAllDoctors();

    String[] getDutySchedule();
}


package adt;

import java.util.ArrayList;

import entity.Doctor;

public interface ListInterface {

    void addDoctor(Doctor doctor);

    Doctor getDoctor(int x);

    boolean removeDoctor(int x);

    boolean replaceDoctor(int x, Doctor newDoctor);

    int getTotalDoctors();

    boolean isEmpty();

    void clearAllDoctors();

    String[] getDutySchedule();
}


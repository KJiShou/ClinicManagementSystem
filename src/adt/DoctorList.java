package adt;

import entity.Doctor;

public class DoctorList implements ListInterface<Doctor> {
    private Doctor[] doctorArray;   //array to store doctor
    private static final int  MAX_SIZE = 100; //store 100 max doctor
    private int size = 0; //track array hv how many doctor

    public DoctorList() {
        doctorArray = new Doctor[MAX_SIZE];
        size = 0;
    }

    @Override
    public void add(Doctor doctor) {
        if (size >= MAX_SIZE) {
            System.out.println("Doctor list is full.");
            return;
        }
        doctorArray[size++] = doctor;
    }

    @Override
    public Doctor get(int index) { //get number (index=1) doctor
        if (index < 0 || index >= size) {
            System.out.println("Invalid index, please try again.");
            return null;
        }
        return doctorArray[index];
    }

    @Override
    public boolean remove(int index) {
        if (index < 0 || index >= size) {
            System.out.println("Invalid index, please try again.");
            return false;
        }
        // shift left
        for (int i = index; i < size - 1; i++) {
            doctorArray[i] = doctorArray[i + 1]; //bring behind i to in front: 1 = 2 place2 goto place1
        }
        doctorArray[--size] = null; //let empty space become null
        return true;
    }

    @Override
    public boolean replace(int index, Doctor newElement) {
        if (index < 0 || index >= size) {
            System.out.println("Invalid index.");
            return false;
        }
        doctorArray[index] = newElement;
        return true;
    }

    @Override
    public int getTotal() {
        return size;
    }

    @Override
    public boolean isEmpty() {
        return size == 0;
    }

    @Override
    public void clearAll() {
        for (int i = 0; i < size; i++) {
            doctorArray[i] = null;
        }
        size = 0;
    }

    public String[] getDutySchedule() {
        String[] dutyList = new String[size];
        for (int i = 0; i < size; i++) {
            dutyList[i] = String.join(", ", doctorArray[i].getDutySchedule());
        }
        return dutyList;
    }





}

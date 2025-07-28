package adt;

import entity.Patient;

public class PatientQueue {
    private static final int MAX_SIZE = 100;
    private Patient[] queue;
    private int first;
    private int last;
    private int size;

    public PatientQueue() {
        queue = new Patient[MAX_SIZE];
        first = 0;
        last = -1;
        size = 0;
    }

    // Add a patient to the queue
    public void enqueue(Patient patient) {
        if (size == MAX_SIZE) {
            System.out.println("Queue is full. Cannot add more patients.");
            return;
        }
        last = (last + 1) % MAX_SIZE;
        queue[last] = patient;
        size++;
        System.out.println("Patient added to the queue.");
    }

    // Remove patient in the queue
    public Patient dequeue() {
        if (isEmpty()) {
            System.out.println("Queue is empty. No patient to serve.");
            return null;
        }
        Patient patient = queue[first];
        first = (first + 1) % MAX_SIZE;
        size--;
        return patient;
    }

    // View the next patient
    public Patient peek() {
        if (isEmpty()) {
            return null;
        }
        return queue[first];
    }

    // Check if queue is empty
    public boolean isEmpty() {
        return size == 0;
    }

    // Get total number of patients in queue
    public int size() {
        return size;
    }

    // Display all patients in queue
    public void displayQueue() {
        if (isEmpty()) {
            System.out.println("The patient queue is empty.");
            return;
        }

        System.out.println("Current Patient Queue");
        for (int i = 0; i < size; i++) {
            int index = (first + i) % MAX_SIZE;
            System.out.println(queue[index]);
        }
    }

    // Clear the queue
    public void clearQueue() {
        first = 0;
        last = -1;
        size = 0;
        System.out.println("The patient queue has been cleared.");
    }
}

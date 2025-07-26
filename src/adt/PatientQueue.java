package adt;

import entity.Patient;
import java.util.LinkedList;
import java.util.Queue;

public class PatientQueue {
    private final Queue<Patient> queue = new LinkedList<>();

    // Add a patient to the queue
    public void enqueue(Patient patient) {
        queue.offer(patient);
        System.out.println("Patient is added to the queue.");
    }

    // Remove and wait for the next patient
    public Patient dequeue() {
        if (queue.isEmpty()) {
            System.out.println("Queue is empty. No patient to serve.");
            return null;
        }
        return queue.poll();
    }

    // Peek the next patient
    public Patient peek() {
        return queue.peek();
    }

    // Check if the queue is empty
    public boolean isEmpty() {
        return queue.isEmpty();
    }

    // Get the number of patients in the queue
    public int size() {
        return queue.size();
    }

    // Display all patients in the queue
    public void displayQueue() {
        if (queue.isEmpty()) {
            System.out.println("The patient queue is empty.");
        } else {
            System.out.println("=== Current Patient Queue ===");
            for (Patient patient : queue) {
                System.out.println(patient);
            }
        }
    }

    // Clear the entire queue
    public void clearQueue() {
        queue.clear();
        System.out.println("The patient queue has been cleared.");
    }
}

package adt;

import entity.Patient;
import java.io.Serializable;
import java.util.NoSuchElementException;

public class LinkedQueue implements QueueInterface<Patient>, Serializable {
    private Node first;
    private Node last;
    private int size;

    public LinkedQueue() {
        first = null;
        last = null;
        size = 0;
    }

    @Override
    public void enqueue(Patient newEntry) {
        Node newNode = new Node(newEntry);

        if (isEmpty()) {
            first = newNode;
        } else {
            last.next = newNode;
        }

        last = newNode;
        size++;
    }

    @Override
    public Patient dequeue() {
        if (isEmpty())
            throw new NoSuchElementException("Queue is empty");

        Patient frontPatient = first.data;
        first = first.next;

        if (first == null)
            last = null;

        size--;
        return frontPatient;
    }

    @Override
    public Patient peek() {
        if (isEmpty())
            throw new NoSuchElementException("Queue is empty");

        return first.data;
    }

    @Override
    public boolean isEmpty() {
        return first == null;
    }

    @Override
    public void clear() {
        first = null;
        last = null;
        size = 0;
    }

    public int size() {
        return size;
    }

    public void display() {
        System.out.println("Current Patient Queue:");
        Node current = first;
        while (current != null) {
            System.out.println(current.data);
            current = current.next;
        }
    }

    private static class Node implements Serializable {
        private Patient data;
        private Node next;

        private Node(Patient data) {
            this.data = data;
            this.next = null;
        }
    }
}

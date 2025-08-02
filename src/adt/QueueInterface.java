package adt;

public interface QueueInterface<T> {
    void enqueue(T newEntry);
    T dequeue();
    T peek();
    boolean isEmpty();
    void clear();
}

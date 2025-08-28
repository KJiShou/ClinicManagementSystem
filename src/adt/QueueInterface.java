// Ng Zhe Wei
package adt;

import java.util.NoSuchElementException;

public interface QueueInterface<T> {
    boolean isEmpty();
    int size();

    void enqueue(T item);

    /**
     * @return the front element
     * @throws NoSuchElementException if the queue is empty
     */
    T dequeue();

    /**
     * @return the front element without removing it
     * @throws NoSuchElementException if the queue is empty
     */
    T peek();

    void clear();
}

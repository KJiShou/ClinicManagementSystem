package adt;

import entity.pharmacyManagement.Medicine;
import entity.pharmacyManagement.MedicineKey;

import java.util.Iterator;
import java.util.NoSuchElementException;

/**
 * A generic, dynamic array implementation of the ListADT interface.
 */
public class ArrayList<T> implements ListInterface<T>, Iterable<T> {
    private T[] elements;
    private int size;
    private static final int DEFAULT_CAPACITY = 10;

    /**
     * Default constructor - creates empty ArrayList with default capacity
     */
    @SuppressWarnings("unchecked")
    public ArrayList() {
        elements = (T[]) new Object[DEFAULT_CAPACITY];
        size = 0;
    }

    /**
     * Constructor with initial capacity
     */
    @SuppressWarnings("unchecked")
    public ArrayList(int initialCapacity) {
        if (initialCapacity < 0) {
            throw new IllegalArgumentException("Initial capacity cannot be negative: " + initialCapacity);
        }
        elements = (T[]) new Object[Math.max(initialCapacity, DEFAULT_CAPACITY)];
        size = 0;
    }

    @SuppressWarnings("unchecked")
    public ArrayList(ArrayList<? extends T> other) {
        if (other == null) {
            elements = (T[]) new Object[DEFAULT_CAPACITY];
            size = 0;
        } else {
            int capacity = Math.max(other.size(), DEFAULT_CAPACITY);
            elements = (T[]) new Object[capacity];
            size = other.size();
            // Copy elements from the other ArrayList
            for (int i = 0; i < size; i++) {
                elements[i] = other.get(i);
            }
        }
    }

    @Override
    public boolean isEmpty() {
        return size == 0;
    }

    @Override
    public int size() {
        return size;
    }

    @Override
    public void add(T item) {
        ensureCapacity();
        elements[size++] = item;
    }

    @Override
    public void add(int index, T item) {
        checkIndexForAdd(index);
        ensureCapacity();
        System.arraycopy(elements, index, elements, index + 1, size - index);
        elements[index] = item;
        size++;
    }

    @Override
    public T get(int index) {
        checkIndex(index);
        return elements[index];
    }

    @Override
    public T set(int index, T item) {
        checkIndex(index);
        T old = elements[index];
        elements[index] = item;
        return old;
    }

    @Override
    public T remove(int index) {
        checkIndex(index);
        T removed = elements[index];
        int numMoved = size - index - 1;
        if (numMoved > 0) {
            System.arraycopy(elements, index + 1, elements, index, numMoved);
        }
        elements[--size] = null;
        return removed;
    }

    @Override
    public void clear() {
        for (int i = 0; i < size; i++) {
            elements[i] = null;
        }
        size = 0;
    }

    @Override
    public boolean contains(T item) {
        return indexOf(item) >= 0;
    }

    @Override
    public int indexOf(T item) {
        for (int i = 0; i < size; i++) {
            if (elements[i] == null ? item == null : elements[i].equals(item)) {
                return i;
            }
        }
        return -1;
    }

    /**
     * Double the array size when capacity is reached.
     */
    @SuppressWarnings("unchecked")
    private void ensureCapacity() {
        if (size == elements.length) {
            T[] newArray = (T[]) new Object[elements.length * 2];
            System.arraycopy(elements, 0, newArray, 0, size);
            elements = newArray;
        }
    }

    private void checkIndex(int index) {
        if (index < 0 || index >= size) {
            throw new IndexOutOfBoundsException("Index: " + index + ", Size: " + size);
        }
    }

    private void checkIndexForAdd(int index) {
        if (index < 0 || index > size) {
            throw new IndexOutOfBoundsException("Index: " + index + ", Size: " + size);
        }
    }
    /**
     * Returns an iterator over the elements in this list.
     */
    @Override
    public Iterator<T> iterator() {
        return new ArrayListIterator();
    }

    /**
     * Inner class implementing Iterator for ArrayList
     */
    private class ArrayListIterator implements Iterator<T> {
        private int currentIndex = 0;

        @Override
        public boolean hasNext() {
            return currentIndex < size;
        }

        @Override
        public T next() {
            if (!hasNext()) {
                throw new NoSuchElementException();
            }
            return elements[currentIndex++];
        }

        @Override
        public void remove() {
            if (currentIndex == 0) {
                throw new IllegalStateException("next() has not been called yet");
            }
            ArrayList.this.remove(--currentIndex);
        }
    }
}

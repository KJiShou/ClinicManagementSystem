package adt;

import java.util.Iterator;
import java.util.NoSuchElementException;
import java.util.function.Consumer;
import java.util.function.UnaryOperator;

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
            // Custom copy elements from the other ArrayList
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
        // Replacing System.arraycopy
        for (int i = size; i > index; i--) {
            elements[i] = elements[i - 1];
        }
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
        // Replacing System.arraycopy
        for (int i = index; i < size - 1; i++) {
            elements[i] = elements[i + 1];
        }
        elements[--size] = null; // Clear the last element
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
            // Replacing System.arraycopy
            for (int i = 0; i < size; i++) {
                newArray[i] = elements[i];
            }
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
        private boolean canRemove = false; // To ensure next() is called before remove()

        @Override
        public boolean hasNext() {
            return currentIndex < size;
        }

        @Override
        public T next() {
            if (!hasNext()) {
                throw new NoSuchElementException();
            }
            canRemove = true;
            return elements[currentIndex++];
        }

        @Override
        public void remove() {
            if (!canRemove) {
                throw new IllegalStateException("next() has not been called yet or remove() already called");
            }
            ArrayList.this.remove(--currentIndex); // Remove the element that was last returned by next()
            canRemove = false; // Reset for the next call to next()
        }
    }

    @Override
    public int lastIndexOf(T item) {
        for (int i = size - 1; i >= 0; i--) {
            if (elements[i] == null ? item == null : elements[i].equals(item)) {
                return i;
            }
        }
        return -1;
    }

    @Override
    public boolean remove(T item) {
        int idx = indexOf(item);
        if (idx >= 0) {
            remove(idx);
            return true;
        }
        return false;
    }

    @Override
    public boolean addAll(Iterable<? extends T> items) {
        boolean changed = false;
        if (items == null) return false;
        for (T it : items) {
            add(it);
            changed = true;
        }
        return changed;
    }

    @Override
    public Object[] toArray() {
        Object[] arr = new Object[size];
        // Replacing System.arraycopy
        for (int i = 0; i < size; i++) {
            arr[i] = elements[i];
        }
        return arr;
    }

    @SuppressWarnings("unchecked")
    @Override
    public T[] toArray(T[] a) {
        if (a.length < size) {
            // create a new array of the same runtime type and copy elements
            T[] newArray = (T[]) createArrayFromClass(a.getClass().getComponentType(), size);
            for (int i = 0; i < size; i++) {
                newArray[i] = elements[i];
            }
            return newArray;
        }
        // Replacing System.arraycopy
        for (int i = 0; i < size; i++) {
            a[i] = elements[i];
        }
        if (a.length > size) a[size] = null; // List contract
        return a;
    }

    // Helper to create a new array of a specific component type
    @SuppressWarnings("unchecked")
    private T[] createArrayFromClass(Class<?> componentType, int length) {
        return (T[]) java.lang.reflect.Array.newInstance(componentType, length);
    }


    @Override
    public ListInterface<T> subList(int fromIndex, int toIndex) {
        if (fromIndex < 0 || toIndex > size || fromIndex > toIndex) {
            throw new IndexOutOfBoundsException(
                    "fromIndex=" + fromIndex + ", toIndex=" + toIndex + ", size=" + size);
        }
        ArrayList<T> sub = new ArrayList<>(toIndex - fromIndex);
        for (int i = fromIndex; i < toIndex; i++) sub.add(elements[i]);
        return sub;
    }

    // Custom Comparator interface if java.util.Comparator is disallowed
    public interface CustomComparator<T> {
        int compare(T o1, T o2);
    }

    // Replacing java.util.Arrays.sort with a simple Bubble Sort
    // Note: Bubble Sort is O(n^2), so for large lists, performance will be poor.
    // A more efficient sort like Merge Sort or Quick Sort would require more code.
    @Override
    public void sort(java.util.Comparator<? super T> c) { // Kept original signature for ListInterface compatibility
        // If CustomComparator is required, change the signature and implement below
        // Example: public void sort(CustomComparator<? super T> c) { ... }

        for (int i = 0; i < size - 1; i++) {
            for (int j = 0; j < size - 1 - i; j++) {
                // If elements[j] is greater than elements[j+1] according to comparator
                if (c.compare(elements[j], elements[j + 1]) > 0) {
                    // Swap elements[j] and elements[j+1]
                    T temp = elements[j];
                    elements[j] = elements[j + 1];
                    elements[j + 1] = temp;
                }
            }
        }
    }

    public void forEach(Consumer<? super T> action) {
        if (action == null) throw new NullPointerException("action");
        for (int i = 0; i < size; i++) action.accept(elements[i]);
    }

    public void replaceAll(UnaryOperator<T> operator) {
        if (operator == null) throw new NullPointerException("operator");
        for (int i = 0; i < size; i++) elements[i] = operator.apply(elements[i]);
    }

    @Override
    public void ensureCapacity(int minCapacity) {
        if (minCapacity <= elements.length) return;
        growTo(minCapacity);
    }

    @Override
    public void trimToSize() {
        if (elements.length == size) return;
        @SuppressWarnings("unchecked")
        T[] newArray = (T[]) new Object[Math.max(size, DEFAULT_CAPACITY)];
        for (int i = 0; i < size; i++) {
            newArray[i] = elements[i];
        }
        elements = newArray;
    }

// ---------- Small internal helpers ----------

    @SuppressWarnings("unchecked")
    private void growTo(int minCapacity) {
        int newCap = elements.length;
        while (newCap < minCapacity) {
            newCap = newCap + (newCap >> 1); // ~1.5x
            if (newCap < 0) { // overflow guard
                newCap = Integer.MAX_VALUE;
                break;
            }
        }
        T[] newArray = (T[]) new Object[newCap];
        for (int i = 0; i < size; i++) {
            newArray[i] = elements[i];
        }
        elements = newArray;
    }

}

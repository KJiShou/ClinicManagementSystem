package adt;

public interface ListInterface<T> {
    boolean isEmpty();
    int size();

    void add(T item);
    void add(int index, T item);

    T get(int index);
    T set(int index, T item);

    T remove(int index);

    void clear();

    boolean contains(T item);
    int indexOf(T item);
}


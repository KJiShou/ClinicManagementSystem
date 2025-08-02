package adt;

public interface ListInterface<T> {

    void add(T newElement);

    T get(int x);

    boolean remove(int x);

    boolean replace(int x, T newElement);

    int getTotal();

    boolean isEmpty();

    void clearAll();

}


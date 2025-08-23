package adt;

import java.util.Comparator;
import java.util.function.Consumer;
import java.util.function.UnaryOperator;

public interface ListInterface<T> extends Iterable<T> {
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

    // --- New, commonly expected methods ---
    int lastIndexOf(T item);
    boolean remove(T item);
    boolean addAll(Iterable<? extends T> items);

    Object[] toArray();
    T[] toArray(T[] a);

    ListInterface<T> subList(int fromIndex, int toIndex);
    void sort(Comparator<? super T> c);

    void ensureCapacity(int minCapacity);
    void trimToSize();

    void forEach(Consumer<? super T> action);
    void replaceAll(UnaryOperator<T> operator);
}

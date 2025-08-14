package adt;

public interface ConsultationInterface<T> {
    public boolean add(T newEntry);
    public boolean add(T newPosition, T newEntry);
    public boolean remove(T givenPosition);
    public void clear();
    public boolean replace(int givenPosition, T newEntry);
    public T getEntry(int givenPosition);
    public boolean contains(T anEntry);
    public int getNumberOfEntries();
    public boolean isEmpty();
    public boolean isFull();
}

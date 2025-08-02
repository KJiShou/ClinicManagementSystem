package adt;

public class ConsultationList implements ConsultationInterface<Object> {
    private Object[] list;
    private int numberOfEntries;
    private static final int DEFAULT_CAPACITY = 25;
    private static final int MAX_CAPACITY = 10000;

    @Override
    public boolean add(Object newEntry) {
        return false;
    }

    @Override
    public boolean add(Object newPosition, Object newEntry) {
        return false;
    }

    @Override
    public boolean remove(Object givenPosition) {
        return false;
    }

    @Override
    public void clear() {

    }

    @Override
    public boolean replace(int givenPosition, Object newEntry) {
        return false;
    }

    @Override
    public Object getEntry(int givenPosition) {
        return null;
    }

    @Override
    public boolean contains(Object anEntry) {
        return false;
    }

    @Override
    public int getNumberOfEntries() {
        return 0;
    }

    @Override
    public boolean isEmpty() {
        return false;
    }

    @Override
    public boolean isFull() {
        return false;
    }
}

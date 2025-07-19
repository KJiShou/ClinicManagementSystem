package adt;

/**
 * An dictionary interface for the
 *
 * @author Kong Ji Shou
 * @version 1.0
 *
 * @param <K> key
 * @param <V> value
 */
public interface DictionaryInterface<K, V> {

    /**
     * Task: Adds a new entry to the dictionary.
     * If the given search key already exists in the dictionary, replaces the corresponding value.
     *
     * @param key an object search key of the new entry.
     * @param value an object associated with the search key.
     * @return either null if the new entry was added to the dictionary or
     * the value that was associated with key if that value was replaced.
     */
    V add(K key, V value);

    /**
     * Task: Removes a specific entry from the dictionary.
     *
     * @param key an object search key of the entry to be removed.
     * @return either the value was associated with the search key or null if no such object exists.
     */
    V remove(K key);

    /**
     * Task: Retrieves the value associated with a given search key.
     *
     * @param key an object search key of the entry to be retrieved.
     * @return either the value that is associated with the search key or null if no such object exists.
     */
    V getValue(K key);

    /**
     * Task: Sees whether a specific entry is in the dictionary.
     *
     * @param key an object search key of the desired entry.
     * @return true if key is associated with an entry in the dictionary.
     */
    boolean contains(K key);

    /**
     * Task: Sees whether the dictionary is empty.
     *
     * @return true if the dictionary is empty.
     */
    boolean isEmpty();

    /**
     * Task: Sees whether the dictionary is full.
     *
     * @return true if the dictionary is full.
     */
    boolean isFull();

    /**
     * Task: Gets the size of the dictionary.
     *
     * @return the number of entries (key-value paris) currently in the dictionary.
     */
    int getSize();

    /**
     * Task: Removes all entries from the dictionary.
     */
    void clear();
}

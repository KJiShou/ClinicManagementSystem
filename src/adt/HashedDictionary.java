package adt;

import java.io.Serial;
import java.io.Serializable;
import java.util.Iterator;
import java.util.NoSuchElementException;

/**
 * A hash‐table based dictionary keyed by {@code K}, mapping to values of type {@code V}.
 * Uses separate chaining and resizes itself when load‐factor exceeds threshold.
 *
 * @param <K> the type of keys maintained by this dictionary
 * @param <V> the type of mapped values
 */
public class HashedDictionary<K, V>
        implements DictionaryInterface<K, V>,
        Serializable,
        Iterable<Entry<K, V>> {

    @Serial
    private static final long serialVersionUID = 1L;

    /** A single chain node; made static to avoid capturing outer instance. */
    private static class Node<K, V> implements Serializable {
        @Serial
        private static final long serialVersionUID = 1L;
        K key;
        V value;
        Node<K, V> next;

        Node(K k, V v, Node<K, V> n) {
            key   = k;
            value = v;
            next  = n;
        }
    }

    private Node<K, V>[] table;
    private int          size;
    private final double loadFactorThreshold;

    private static final int    DEFAULT_CAPACITY    = 11;
    private static final double DEFAULT_LOAD_FACTOR = 0.75;

    @SuppressWarnings("unchecked")
    public HashedDictionary() {
        this(DEFAULT_CAPACITY, DEFAULT_LOAD_FACTOR);
    }

    @SuppressWarnings("unchecked")
    public HashedDictionary(int initialCapacity, double loadFactorThreshold) {
        if (initialCapacity <= 0)
            throw new IllegalArgumentException("Capacity must be > 0");
        if (loadFactorThreshold <= 0 || loadFactorThreshold >= 1)
            throw new IllegalArgumentException("Load factor must be (0,1)");

        int prime = getNextPrime(initialCapacity);
        table = (Node<K, V>[]) new Node[prime];
        this.loadFactorThreshold = loadFactorThreshold;
        this.size = 0;
    }

    @Override
    public V add(K key, V value) {
        if (key == null) throw new IllegalArgumentException("Key cannot be null");
        if (getLoadFactor() > loadFactorThreshold) rehash();

        int idx = getHashIndex(key);
        for (Node<K, V> cur = table[idx]; cur != null; cur = cur.next) {
            if (cur.key.equals(key)) {
                V old = cur.value;
                cur.value = value;
                return old;
            }
        }

        table[idx] = new Node<>(key, value, table[idx]);
        size++;
        return null;
    }

    @Override
    public V remove(K key) {
        if (key == null) return null;
        int idx = getHashIndex(key);
        Node<K, V> prev = null, cur = table[idx];
        while (cur != null && !cur.key.equals(key)) {
            prev = cur;
            cur  = cur.next;
        }
        if (cur == null) return null;

        if (prev == null)        table[idx] = cur.next;
        else                     prev.next = cur.next;
        size--;
        return cur.value;
    }

    @Override
    public V getValue(K key) {
        if (key == null) return null;
        int idx = getHashIndex(key);
        for (Node<K, V> cur = table[idx]; cur != null; cur = cur.next) {
            if (cur.key.equals(key)) return cur.value;
        }
        return null;
    }

    @Override
    public boolean contains(K key) {
        return getValue(key) != null;
    }

    @Override
    public boolean isEmpty() {
        return size == 0;
    }

    @Override
    public boolean isFull() {
        return getLoadFactor() > loadFactorThreshold;
    }

    @Override
    public int getSize() {
        return size;
    }

    @Override
    public void clear() {
        // remove all chains without using java.util.Arrays
        for (int i = 0; i < table.length; i++) {
            table[i] = null;
        }
        size = 0;
    }

    /**
     * @return a dynamic array of all keys in this dictionary
     */
    public ArrayList<K> keyList() {
        ArrayList<K> keys = new ArrayList<>();
        for (Node<K, V> bucket : table) {
            for (Node<K, V> cur = bucket; cur != null; cur = cur.next) {
                keys.add(cur.key);
            }
        }
        return keys;
    }

    /**
     * @return a dynamic array of all values in this dictionary
     */
    public ArrayList<V> valueList() {
        ArrayList<V> vals = new ArrayList<>();
        for (Node<K, V> bucket : table) {
            for (Node<K, V> cur = bucket; cur != null; cur = cur.next) {
                vals.add(cur.value);
            }
        }
        return vals;
    }

    public ArrayList<Entry<K, V>> entryList() {
        ArrayList<Entry<K, V>> entries = new ArrayList<>();
        for (int i = 0; i < table.length; i++) {
            Node<K, V> bucket = table[i];
            for (Node<K, V> cur = bucket; cur != null; cur = cur.next) {
                entries.add((Entry<K, V>) cur);
            }
        }
        return entries;
    }

    private static class SimpleEntry<K, V> implements Entry<K, V> {
        private final K key;
        private final V value;

        SimpleEntry(K key, V value) {
            this.key   = key;
            this.value = value;
        }

        @Override public K getKey()   { return key; }
        @Override public V getValue() { return value; }
        @Override public V setValue(V v) {
            throw new UnsupportedOperationException("Immutable entry");
        }
    }

    @Override
    public Iterator<Entry<K, V>> iterator() {
        return new Iterator<>() {
            int bucketIdx = 0;
            Node<K, V> nextNode = null;

            private void advance() {
                while (nextNode == null && bucketIdx < table.length) {
                    nextNode = table[bucketIdx++];
                }
            }

            @Override
            public boolean hasNext() {
                advance();
                return nextNode != null;
            }

            @Override
            public Entry<K, V> next() {
                advance();
                if (nextNode == null) throw new NoSuchElementException();
                Node<K, V> curr = nextNode;
                nextNode = curr.next;
                return new SimpleEntry<>(curr.key, curr.value);
            }
        };
    }

    // ——— Utilities ———

    public double getLoadFactor() {
        return (double) size / table.length;
    }

    public int getCapacity() {
        return table.length;
    }

    @SuppressWarnings("unchecked")
    private void rehash() {
        Node<K, V>[] old = table;
        int newCap = getNextPrime(old.length * 2);
        table = (Node<K, V>[]) new Node[newCap];
        size = 0;
        for (Node<K, V> bucket : old) {
            for (Node<K, V> cur = bucket; cur != null; cur = cur.next) {
                add(cur.key, cur.value);
            }
        }
    }

    private int getHashIndex(K key) {
        int idx = key.hashCode() % table.length;
        return idx < 0 ? idx + table.length : idx;
    }

    private int getNextPrime(int n) {
        if (n % 2 == 0) n++;
        while (!isPrime(n)) n += 2;
        return n;
    }

    private boolean isPrime(int x) {
        if (x <= 1) return false;
        if (x <= 3) return true;
        if (x % 2 == 0) return false;
        for (int i = 3; i * i <= x; i += 2) {
            if (x % i == 0) return false;
        }
        return true;
    }
}

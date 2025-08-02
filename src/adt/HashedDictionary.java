package adt;

import java.io.Serial;
import java.io.Serializable;
import java.util.*;

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
        Iterable<Map.Entry<K, V>> {

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

    private static final int    DEFAULT_CAPACITY   = 11;
    private static final double DEFAULT_LOAD_FACTOR = 0.75;

    /** Constructs with default capacity (11) and load factor (0.75). */
    public HashedDictionary() {
        this(DEFAULT_CAPACITY, DEFAULT_LOAD_FACTOR);
    }

    /**
     * Constructs with specified initial capacity and load factor.
     * @param initialCapacity desired minimum bucket count
     * @param loadFactorThreshold when size/capacity exceeds this, we rehash
     */
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

    /** {@inheritDoc} */
    @Override
    public V add(K key, V value) {
        if (key == null) throw new IllegalArgumentException("Key cannot be null");
        // Rehash if load factor exceeded
        if (getLoadFactor() > loadFactorThreshold) rehash();

        int idx = getHashIndex(key);
        for (Node<K, V> cur = table[idx]; cur != null; cur = cur.next) {
            if (cur.key.equals(key)) {
                V old = cur.value;
                cur.value = value;
                return old;
            }
        }
        // Insert at head for O(1) chaining
        table[idx] = new Node<>(key, value, table[idx]);
        size++;
        return null;
    }

    /** {@inheritDoc} */
    @Override
    public V remove(K key) {
        if (key == null) return null;
        int idx = getHashIndex(key);
        Node<K, V> prev = null, cur = table[idx];
        while (cur != null && !cur.key.equals(key)) {
            prev = cur; cur = cur.next;
        }
        if (cur == null) return null;
        // unlink
        if (prev == null) table[idx] = cur.next;
        else             prev.next = cur.next;
        size--;
        return cur.value;
    }

    /** {@inheritDoc} */
    @Override
    public V getValue(K key) {
        if (key == null) return null;
        int idx = getHashIndex(key);
        for (Node<K, V> cur = table[idx]; cur != null; cur = cur.next) {
            if (cur.key.equals(key)) return cur.value;
        }
        return null;
    }

    /** {@inheritDoc} */
    @Override
    public boolean contains(K key) {
        return getValue(key) != null;
    }

    /** {@inheritDoc} */
    @Override
    public boolean isEmpty() {
        return size == 0;
    }

    /** Now returns true when loadFactor > threshold. */
    @Override
    public boolean isFull() {
        return getLoadFactor() > loadFactorThreshold;
    }

    /** {@inheritDoc} */
    @Override
    public int getSize() {
        return size;
    }

    /** {@inheritDoc} */
    @Override
    public void clear() {
        Arrays.fill(table, null);
        size = 0;
    }

    // ——— New utility methods ———

    /** @return current load factor = size / capacity */
    public double getLoadFactor() {
        return (double) size / table.length;
    }

    /** @return number of buckets */
    public int getCapacity() {
        return table.length;
    }

    /** @return a Set of all keys in this dictionary */
    public Set<K> keySet() {
        Set<K> keys = new HashSet<>(size);
        for (Node<K, V> bucket : table)
            for (Node<K, V> cur = bucket; cur != null; cur = cur.next)
                keys.add(cur.key);
        return keys;
    }

    /** @return a Collection of all values in this dictionary */
    public Collection<V> values() {
        List<V> vals = new ArrayList<>(size);
        for (Node<K, V> bucket : table)
            for (Node<K, V> cur = bucket; cur != null; cur = cur.next)
                vals.add(cur.value);
        return vals;
    }

    /**
     * Allows for‐each iteration over entries. Example:
     * <code>for (Map.Entry&lt;K, V&gt; e : dict) { … }</code>
     */
    @Override
    public Iterator<Map.Entry<K, V>> iterator() {
        return new Iterator<>() {
            int bucketIdx = 0;
            Node<K, V> nextNode = null;

            private void advanceToNext() {
                while ((nextNode == null) && bucketIdx < table.length) {
                    nextNode = table[bucketIdx++];
                }
            }

            @Override
            public boolean hasNext() {
                advanceToNext();
                return nextNode != null;
            }

            @Override
            public Map.Entry<K, V> next() {
                advanceToNext();
                if (nextNode == null) throw new NoSuchElementException();
                Node<K, V> curr = nextNode;
                nextNode = nextNode.next; // move along chain
                return new AbstractMap.SimpleImmutableEntry<>(curr.key, curr.value);
            }
        };
    }

    // ——— Internal helpers ———

    /** Double the bucket count (to next prime) and re-insert all entries. */
    @SuppressWarnings("unchecked")
    private void rehash() {
        Node<K, V>[] oldTable = table;
        int newCapacity = getNextPrime(oldTable.length * 2);
        table = (Node<K, V>[]) new Node[newCapacity];
        size = 0;
        for (Node<K, V> bucket : oldTable)
            for (Node<K, V> cur = bucket; cur != null; cur = cur.next)
                add(cur.key, cur.value);
    }

    /** Normalize and non-negative bucket index. */
    private int getHashIndex(K key) {
        int idx = key.hashCode() % table.length;
        return idx < 0 ? idx + table.length : idx;
    }

    /** Find the next prime ≥ n (odd stepping). */
    private int getNextPrime(int n) {
        if (n % 2 == 0) n++;
        while (!isPrime(n)) n += 2;
        return n;
    }

    /** Simple primality test. */
    private boolean isPrime(int x) {
        if (x <= 1) return false;
        if (x <= 3) return true;
        if (x % 2 == 0) return false;
        for (int i = 3; i * i <= x; i += 2)
            if (x % i == 0) return false;
        return true;
    }
}

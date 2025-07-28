package adt;

import java.io.Serializable;

public class HashedDictionary<K, V> implements DictionaryInterface<K, V>, Serializable {
    private Node<K, V>[] hashedDictionary;
    private int numberOfEntries;
    private static final int DEFAULT_INITIAL_CAPACITY = 11;
    private static final double MAX_LOAD_FACTOR = 0.9;

    public HashedDictionary() {
        this(DEFAULT_INITIAL_CAPACITY);
    }

    public HashedDictionary(int tableSize) {
        int primeSize = getNextPrime(tableSize);

        hashedDictionary = new Node[primeSize];
        numberOfEntries = 0;
    }

    // get Node data in string
    public String toString() {
        StringBuilder output = new StringBuilder();
        for (int i = 0; i < hashedDictionary.length; i++) {
            if (hashedDictionary[i] == null) {
                output.append("No Data\n");
            } else {
                Node<K, V> currentNode = hashedDictionary[i];
                while (currentNode != null) {
                    output.append(currentNode.key).append(currentNode.value).append(",");
                    currentNode = currentNode.nextNode;
                }

                output.append("\n");
            }
            output.append("\n");
        }
        return output.toString();
    }

    @Override
    public V add(K key, V value) {
        V oldValue = null;

        if(isFull()) {
            rehash();
        }

        int index = getHashIndex(key);

        if (hashedDictionary[index] == null) {
            // no node in this index
            hashedDictionary[index] = new Node<K, V>(key, value);
            numberOfEntries++;
        } else {
            Node<K, V> currentNode = hashedDictionary[index];
            Node<K, V> nodeBefore = null;

            while (currentNode != null && !key.equals(currentNode.key)) {
                nodeBefore = currentNode;
                currentNode = currentNode.nextNode;
            }

            if (currentNode == null) {
                Node<K, V> newNode = new Node<K, V>(key, value);
                nodeBefore.nextNode = newNode;
                numberOfEntries++;
            }else {
                oldValue = currentNode.value;
                currentNode.value = value;
            }
        }
        return oldValue;
    }

    @Override
    public V remove(K key) {
        V removedValue = null;

        int index = getHashIndex(key);

        // search chain beginning at hashedDictionary[index] for a node that contains key
        Node<K, V> nodeBefore = null;
        Node<K, V> currentNode = hashedDictionary[index];

        while ((currentNode != null) && !key.equals(currentNode.key)) {
            nodeBefore = currentNode;
            currentNode = currentNode.nextNode;
        }

        if (currentNode != null) {
            // key found, get value for return and then remove node
            removedValue = currentNode.value;

            if (nodeBefore == null) {
                hashedDictionary[index] = currentNode.nextNode;
            } else {
                nodeBefore.nextNode = currentNode.nextNode;
            }

            numberOfEntries--;
        }
        return removedValue;
    }

    @Override
    public V getValue(K key) {
        V result = null;
        int index = getHashIndex(key);

        Node<K, V> currentNode = hashedDictionary[index];

        while ((currentNode != null) && !key.equals(currentNode.key)) {
            currentNode = currentNode.nextNode;
        }

        if (currentNode != null) {
            result = currentNode.value;
        }

        return result;
    }

    private void rehash() {
        Node<K, V>[] oldHashedDictionary = hashedDictionary;
        int oldSize = oldHashedDictionary.length;
        int newSize = getNextPrime(oldSize + oldSize);

        hashedDictionary = new Node[newSize];
        numberOfEntries = 0;

        for (int i = 0; i < oldSize; i++) {
            Node<K, V> currentNode = oldHashedDictionary[i];

            while (currentNode != null) {
                add(currentNode.key, currentNode.value);
                currentNode = currentNode.nextNode;
            }
        }
    }

    @Override
    public void clear() {
        for (int index = 0; index < hashedDictionary.length; index++) {
            hashedDictionary[index] = null;
        }
    }

    /**
     * @return true if number of entries is more than MAX_LOAD_FACTOR, else false
     */
    private boolean isHashedDictionaryTooFull() {
        return numberOfEntries > MAX_LOAD_FACTOR * hashedDictionary.length;
    }

    // the following methods that appear between the dashed lines
    // are the same as for open addressing
    // ----------------------------------------------------------
    @Override
    public boolean contains(K key) {
        return getValue(key) != null;
    }

    @Override
    public boolean isEmpty() {
        return numberOfEntries == 0;
    }

    @Override
    public boolean isFull() {
        return false;
    }

    @Override
    public int getSize() {
        return numberOfEntries;
    }

    private int getHashIndex(K key) {
        int hashIndex = key.hashCode() % hashedDictionary.length;
        if (hashIndex < 0) {
            hashIndex = hashIndex + hashedDictionary.length;
        }
        return hashIndex;
    }

    // get the next prime number
    private int getNextPrime(int number) {
        if (number % 2 == 0) number++;

        while (!isPrime(number)) {
            number += 2;
        }

        return number;
    }

    // to check the number is a prime number or not
    private boolean isPrime(int number) {
        if (number <= 1) return false;
        if (number == 2) return true;
        if (number % 2 == 0) return false;

        for (int i = 3; i <= Math.sqrt(number); i += 2) {
            if (number % i == 0) return false;
        }
        return true;
    }
    // ----------------------------------------------------------

    // Private inner class Node as the data types for the hashed dictionary
    private class Node<K, V> implements java.io.Serializable {
        private K key;
        private V value;
        private Node<K, V> nextNode;

        // constructor without nextNode
        private Node(K searchKey, V searchValue) {
            this.key = searchKey;
            this.value = searchValue;
            this.nextNode = null;
        }

        // constructor with nextNode
        private Node(K searchKey, V searchValue, Node<K, V> nextNode) {
            this.key = searchKey;
            this.value = searchValue;
            this.nextNode = nextNode;
        }
    }
}

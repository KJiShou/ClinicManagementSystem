package adt;

import java.io.Serializable;

// test
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

        // setter for Node key
        private void setKey(K key) {
            this.key = key;
        }

        // setter for Node value
        private void setValue(V value) {
            this.value = value;
        }

        // setter for next Node
        private void setNextNode(Node<K, V> nextNode) {
            this.nextNode = nextNode;
        }

        // getter for Node key
        private K getKey() {
            return key;
        }

        // getter for Node value
        private V getValue() {
            return value;
        }

        // getter for next Node
        private Node<K, V> getNextNode() {
            return nextNode;
        }
    }

    @Override
    public V add(K key, V value) {
        V oldValue = null;
        return null;
    }


    @Override
    public V remove(K key) {
        return null;
    }

    @Override
    public V getValue(K key) {
        return null;
    }

    @Override
    public boolean contains(K key) {
        return false;
    }

    @Override
    public boolean isEmpty() {
        return false;
    }

    @Override
    public boolean isFull() {
        return false;
    }

    @Override
    public int getSize() {
        return 0;
    }

    @Override
    public void clear() {
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
                add(currentNode.getKey(), currentNode.getValue());
                currentNode = currentNode.getNextNode();
            }
        }
    }

    /**
     * @return true if number of entries is more than MAX_LOAD_FACTOR, else false
     */
    private boolean isHashedDictionaryTooFull() {
        return numberOfEntries > MAX_LOAD_FACTOR * hashedDictionary.length;
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

    // get Node data in string
    public String toString() {
        StringBuilder output = new StringBuilder();
        for (int i = 0; i < hashedDictionary.length; i++) {
            if (hashedDictionary[i] == null) {
                output.append("No Data\n");
            } else {
                Node<K, V> currentNode = hashedDictionary[i];
                while (currentNode != null) {
                    output.append(currentNode.getKey()).append(currentNode.getValue()).append(",");
                    currentNode = currentNode.getNextNode();
                }

                output.append("\n");
            }
            output.append("\n");
        }
        return output.toString();
    }
}

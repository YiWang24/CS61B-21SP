package hashmap;

import java.util.*;

/**
 * A hash table-backed Map implementation. Provides amortized constant time
 * access to elements via get(), remove(), and put() in the best case.
 * <p>
 * Assumes null keys will never be inserted, and does not resize down upon remove().
 *
 * @author YOUR NAME HERE
 */
public class MyHashMap<K, V> implements Map61B<K, V> {

    private int initialSize;
    private double loadFactor = 0.75;
    private int size;

    /**
     * Protected helper class to store key/value pairs
     * The protected qualifier allows subclass access
     */
    protected class Node {
        K key;
        V value;

        Node(K k, V v) {
            key = k;
            value = v;
        }
    }

    private class MyHashMapIterator implements Iterator<K> {
        private int bucketIndex = 0;
        private Iterator<Node> bucketIterator;

        public MyHashMapIterator() {
            moveToNext();
        }

        @Override
        public boolean hasNext() {
            return bucketIterator != null && bucketIterator.hasNext();
        }

        @Override
        public K next() {
            if (!hasNext()) {
                throw new NoSuchElementException();
            }
            K key = bucketIterator.next().key;
            if (!bucketIterator.hasNext()) {
                moveToNext();
            }
            return key;
        }

        private void moveToNext() {
            while (bucketIndex < buckets.length && (buckets[bucketIndex] == null || buckets[bucketIndex].isEmpty())) {
                bucketIndex++;
            }
            if (bucketIndex < buckets.length) {
                bucketIterator = buckets[bucketIndex].iterator();
                bucketIndex++;
            } else {
                bucketIterator = null;
            }
        }
    }

    /* Instance Variables */
    private Collection<Node>[] buckets;
    // You should probably define some more!

    /**
     * Constructors
     */
    public MyHashMap() {
        this(16);
    }

    public MyHashMap(int initialSize) {
        this.initialSize = initialSize;
        buckets = createTable(initialSize);
        size = 0;
    }

    /**
     * MyHashMap constructor that creates a backing array of initialSize.
     * The load factor (# items / # buckets) should always be <= loadFactor
     *
     * @param initialSize initial size of backing array
     * @param maxLoad     maximum load factor
     */
    public MyHashMap(int initialSize, double maxLoad) {
        this(initialSize);
        this.loadFactor = maxLoad;
    }

    /**
     * Returns a new node to be placed in a hash table bucket
     */
    private Node createNode(K key, V value) {
        return new Node(key, value);
    }

    /**
     * Returns a data structure to be a hash table bucket
     * <p>
     * The only requirements of a hash table bucket are that we can:
     * 1. Insert items (`add` method)
     * 2. Remove items (`remove` method)
     * 3. Iterate through items (`iterator` method)
     * <p>
     * Each of these methods is supported by java.util.Collection,
     * Most data structures in Java inherit from Collection, so we
     * can use almost any data structure as our buckets.
     * <p>
     * Override this method to use different data structures as
     * the underlying bucket type
     * <p>
     * BE SURE TO CALL THIS FACTORY METHOD INSTEAD OF CREATING YOUR
     * OWN BUCKET DATA STRUCTURES WITH THE NEW OPERATOR!
     */
    protected Collection<Node> createBucket() {
        return new LinkedList<>();
    }

    /**
     * Returns a table to back our hash table. As per the comment
     * above, this table can be an array of Collection objects
     * <p>
     * BE SURE TO CALL THIS FACTORY METHOD WHEN CREATING A TABLE SO
     * THAT ALL BUCKET TYPES ARE OF JAVA.UTIL.COLLECTION
     *
     * @param tableSize the size of the table to create
     */
    private Collection<Node>[] createTable(int tableSize) {
        Collection<Node>[] table = new Collection[tableSize];
        for (int i = 0; i < tableSize; i++) {
            table[i] = createBucket();
        }
        return table;
    }


    // Your code won't compile until you do so!


    @Override
    public void clear() {
        buckets = createTable(16);
        size = 0;
    }

    @Override
    public boolean containsKey(K key) {
        if (key == null) {
            throw new IllegalArgumentException();
        }
        return get(key) != null;
    }

    @Override
    public V get(K key) {
        if (key == null) {
            throw new IllegalArgumentException();
        }
        int i = hash(key);

        if (buckets[i] != null) {
            for (Node node : buckets[i]) {
                if (node.key.equals(key)) {
                    return node.value;
                }
            }
        }

        return null;
    }


    @Override
    public int size() {
        return size;
    }

    @Override
    public void put(K key, V value) {
        if (key == null) {
            throw new IllegalArgumentException();
        }
        if ((double) size / initialSize > loadFactor) {
            resize(2 * buckets.length);
        }
        int i = hash(key);
        if (buckets[i] == null) {
            Node node = createNode(key, value);
            buckets[i].add(node);
            size = size + 1;
            return;
        }
        for (Node node : buckets[i]) {
            if (node.key.equals(key)) {
                node.value = value;
                return;
            }
        }

        buckets[i].add(createNode(key, value));
        size = size + 1;


    }

    @Override
    public Set<K> keySet() {
        HashSet<K> ks = new HashSet<>();
        for (Collection<Node> bucket : buckets) {
            if (bucket != null) {
                for (Node node : bucket) {
                    if (node.key != null) {
                        ks.add(node.key);
                    }
                }
            }

        }
        return ks;
    }

    @Override
    public V remove(K key) {
        int i = hash(key);
        if (buckets[i] != null) {
            for (Node node : buckets[i]) {
                if (node.key.equals(key)) {
                    V value = node.value;
                    buckets[i].remove(node);
                    return value;
                }
            }
        }
        return null;
    }

    @Override
    public V remove(K key, V value) {
        int i = hash(key);
        if (buckets[i] != null) {
            for (Node node : buckets[i]) {
                if (node.key.equals(key) && node.value == value) {
                    V remove = node.value;
                    buckets[i].remove(node);
                    return remove;
                }
            }
        }
        return null;
    }

    @Override
    public Iterator<K> iterator() {
        return new MyHashMapIterator();
    }

    private int hash(K key) {
        int h = key.hashCode();
        h ^= (h >>> 20) ^ (h >>> 12) ^ (h >>> 7) ^ (h >>> 4);
        return h & (buckets.length - 1);
    }


    private void resize(int capacity) {
        Collection<Node>[] newBucket = createTable(capacity);

        for (Collection<Node> bucket : buckets) {
            if (bucket != null) {
                for (Node node : bucket) {
                    int newIndex = hash(node.key);
                    newBucket[newIndex].add(node);
                }
            }
        }

        this.initialSize = capacity;
        buckets = newBucket;
    }
}

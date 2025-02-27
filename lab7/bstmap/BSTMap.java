package bstmap;

import java.util.Iterator;
import java.util.Set;

/**
 * @author WY
 * @version 1.0
 **/

public class BSTMap<K extends Comparable<K>, V> implements Map61B<K, V> {

    private class Entry {
        private K key;
        private V value;
        private Entry left, right;

        Entry(K key, V value) {
            this.key = key;
            this.value = value;

        }
    }

    private Entry root;
    private int size;

    @Override
    public void clear() {
        size = 0;
        root = null;
    }

    @Override
    public boolean containsKey(K key) {
        if(root == null) {
            return false;
        }
        return get(root, key).key != null;
    }

    @Override
    public V get(K key) {
        if (key == null) {
            throw new IllegalArgumentException("key is null");
        }
        if (root == null) {
            return null;
        }
        Entry entry = get(root, key);
        return entry.value;
    }

    private Entry get(Entry entry, K k) {
        int cmp = k.compareTo(entry.key);
        if (cmp < 0) {
            return get(entry.left, k);
        } else if (cmp > 0) {
            return get(entry.right, k);
        } else {
            return entry;
        }
    }

    @Override
    public int size() {
        return size;
    }

    private Entry put(Entry entry, K k, V v) {
        if (entry == null) {
            size = size + 1;
            return new Entry(k, v);
        }
        int cmp = k.compareTo(entry.key);
        if (cmp < 0) {
            entry.left = put(entry.left, k, v);
        } else if (cmp > 0) {
            entry.right = put(entry.right, k, v);
        } else {
            entry.value = v;
        }
        return entry;
    }

    @Override
    public void put(K key, V value) {
        if (key == null) {
            throw new IllegalArgumentException("key is null");
        }
        root = put(root, key, value);
    }


    public Set<K> keySet() {
        throw new UnsupportedOperationException();
    }


    public V remove(K key) {
        throw new UnsupportedOperationException();
    }


    public V remove(K key, V value) {
        throw new UnsupportedOperationException();
    }


    public Iterator<K> iterator() {
        throw new UnsupportedOperationException();
    }


    public void printInOrder() {
    }
}

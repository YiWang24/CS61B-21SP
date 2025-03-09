package bstmap;

import java.util.*;

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

    private class BSTMapIterator implements Iterator<K> {
        private Stack<Entry> stack;

        public BSTMapIterator() {
            stack = new Stack<>();
            pushLeft(root);
        }

        private void pushLeft(Entry entry) {
            while (entry != null) {
                stack.push(entry);
                entry = entry.left;
            }
        }

        @Override
        public boolean hasNext() {
            return !stack.isEmpty();
        }

        @Override
        public K next() {
            if (!hasNext()) {
                throw new NoSuchElementException();
            }
            Entry entry = stack.pop();
            pushLeft(entry.right);
            return entry.key;
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
        return get(root, key) != null;
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
        return (entry == null) ? null : entry.value;
    }

    private Entry get(Entry entry, K k) {
        if (entry == null) {
            return null;
        }
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
        Set<K> keys = new HashSet<>();
        inOrder(root, keys);
        return keys;
    }

    private void inOrder(Entry entry, Set<K> keys) {
        if (entry == null) {
            return;
        }
        inOrder(entry.left, keys);
        keys.add(entry.key);
        inOrder(entry.right, keys);
    }


    private Entry min(Entry entry) {
        while (entry.left != null) {
            entry = entry.left;
        }
        return entry;
    }

    private Entry findEntry(Entry entry, K key) {
        if (entry == null) {
            return null;
        }
        int cmp = key.compareTo(entry.key);
        if (cmp < 0) {
            return findEntry(entry.left, key);
        } else if (cmp > 0) {
            return findEntry(entry.right, key);
        }
        return entry;
    }

    private Entry remove(Entry entry, K key) {
        if (entry == null) {
            return null;
        }
        int cmp = key.compareTo(entry.key);
        if (cmp < 0) {
            entry.left = remove(entry.left, key);
        } else if (cmp > 0) {
            entry.right = remove(entry.right, key);
        } else {
            if (entry.left == null && entry.right == null) {
                return null;
            }
            if (entry.left == null) {
                return entry.right;
            }
            if (entry.right == null) {
                return entry.left;
            }
            Entry successor = min(entry.right);
            entry.key = successor.key;
            entry.value = successor.value;
            entry.right = remove(entry.right, successor.key);
        }
        return entry;
    }

    public V remove(K key) {
        if (key == null) {
            throw new IllegalArgumentException("key is null");
        }
        Entry removed = findEntry(root, key);
        if (removed == null) {
            return null;
        }
        root = remove(root, key);
        size = size - 1;
        return removed.value;
    }


    public V remove(K key, V value) {
        if (key == null) {
            throw new IllegalArgumentException("key is null");
        }
        Entry removed = findEntry(root, key);
        if (removed == null) {
            return null;
        } else if (!removed.value.equals(value)) {
            return null;
        }
        root = remove(root, key);
        size = size - 1;
        return removed.value;

    }


    public Iterator<K> iterator() {
        return new BSTMapIterator();
    }


    public void printInOrder() {
        printInOrder(root);
    }

    private void printInOrder(Entry entry) {
        if (entry == null) {
            return;
        }

        printInOrder(entry.left);
        System.out.println(entry.key + " -> " + entry.value);
        printInOrder(entry.right);


    }
}

package deque;

import java.util.Iterator;

/**
 * @author WY
 * @version 1.0
 **/

public class ArrayDeque<T> implements Deque<T>,Iterable<T> {
    private T[] items;
    private int size;
    private int capacity;
    private int nextFirst;
    private int nextLast;

    public ArrayDeque() {
        items = (T[]) new Object[8];
        this.capacity = items.length;
        nextFirst = capacity- 1;
        nextLast = 0;
        size = 0;
    }
    private void resize(int capacity) {
        T[] newItems = (T[]) new Object[capacity];
        for (int i = 1; i <= size; i++) {
            newItems[i] = items[(++nextFirst)%this.capacity];
        }
        this.capacity = capacity;
        nextFirst = 0;
        nextLast = size+1;
        items = newItems;
    }

    @Override
    public Iterator<T> iterator() {
        return null;
    }

    @Override
    public void addFirst(T item) {
        if(items.length == size) resize(capacity*2);
        items[nextFirst] = item;
        size++;
        nextFirst = nextFirst == 0 ? capacity - 1 : nextFirst -1;

    }

    @Override
    public void addLast(T item) {
        items[nextLast] = item;
        size++;
        nextLast++;

    }

    @Override
    public boolean isEmpty() {
        return size == 0;
    }

    @Override
    public int size() {
        return size;
    }

    @Override
    public String printDeque() {
        return "";
    }

    @Override
    public T removeFirst() {
        return null;
    }

    @Override
    public T removeLast() {
        return null;
    }

    @Override
    public T get(int index) {
        return null;
    }
}

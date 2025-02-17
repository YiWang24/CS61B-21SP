package deque;

import java.util.Iterator;
import java.util.Objects;


/**
 * @author WY
 * @version 1.0
 **/

public class ArrayDeque<T> implements Deque<T>, Iterable<T> {

    private class ArrayDequeIterator implements Iterator<T> {
        int first = (nextFirst + 1) % items.length;

        @Override
        public boolean hasNext() {
            return items[first] != null;
        }

        @Override
        public T next() {
            T t = items[first];
            first = (first + 1) % items.length;
            return t;
        }
    }

    private T[] items;
    private int size;
    private int nextFirst;
    private int nextLast;

    public ArrayDeque() {
        items = (T[]) new Object[8];
        nextFirst = items.length - 1;
        nextLast = 0;
        size = 0;
    }

    private void resize(int capacity) {
        T[] a = (T[]) new Object[capacity];
        for (int i = 0; i < size; i++) {
            a[i] = (T) items[(nextFirst + 1 + i) % items.length];
        }
        items = a;
        nextFirst = items.length - 1;
        nextLast = size;
    }

    @Override
    public Iterator<T> iterator() {
        return new ArrayDequeIterator();
    }

    @Override
    public void addFirst(T item) {
        if (size == items.length) {
            resize(items.length * 2);
        }
        items[nextFirst] = item;
        nextFirst = nextFirst == 0 ? items.length - 1 : nextFirst - 1;
        size++;

    }

    @Override
    public void addLast(T item) {
        if (size == items.length) {
            resize(items.length * 2);
        }
        items[nextLast] = item;
        nextLast = (nextLast + 1) % items.length;
        size++;

    }


    @Override
    public int size() {
        return size;
    }

    @Override
    public void printDeque() {
        for (int i = 0; i < size - 1; i++) {
            System.out.print(items[(nextFirst + 1 + i) % items.length] + ",");
        }
        System.out.print(items[(nextFirst + size) % items.length]);
    }

    @Override
    public T removeFirst() {
        if (size == 0) {
            return null;
        }

        int first = (nextFirst + 1) % items.length;
        T item = items[first];
        items[first] = null;
        size--;
        nextFirst = first;

        if (items.length > 16 && size < items.length / 4) {
            resize(items.length / 2);
        }
        return item;
    }

    @Override
    public T removeLast() {
        if (size == 0) {
            return null;
        }

        int last = nextLast == 0 ? items.length - 1 : nextLast - 1;
        T item = items[last];
        items[last] = null;
        size--;
        nextLast = last;
        if (items.length > 16 && size < items.length / 4) {
            resize(items.length / 2);
        }

        return item;

    }

    @Override
    public T get(int index) {
        int i = (nextFirst + index + 1) % items.length;
        return items[i];
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }

        if (!(o instanceof ArrayDeque)) {
            return false;
        }

        ArrayDeque<?> that = (ArrayDeque<?>) o;

        if (size != that.size()) {
            return false;
        }

        Iterator<?> thisIterator = this.iterator();
        Iterator<?> thatIterator = that.iterator();
        while (thisIterator.hasNext()) {
            Object a = thisIterator.next();
            Object b = thatIterator.next();
            if (!Objects.equals(a, b)) {
                return false;
            }
        }

        return true;

    }

}

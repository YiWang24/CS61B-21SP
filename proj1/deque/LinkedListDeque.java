package deque;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Objects;

/**
 * @author WY
 * @version 1.0
 **/

public class LinkedListDeque<T> implements Deque<T>, Iterable<T> {
    private class Node {
        public T data;
        public Node prev;
        public Node next;

        public Node(T data, Node prev, Node next) {
            this.data = data;
            this.prev = prev;
            this.next = next;

        }
    }

    private class LinkedListIterator implements Iterator<T> {
        private Node current  = sentinel.next;

        @Override
        public boolean hasNext() {
            return current !=sentinel;
        }

        @Override
        public T next() {
            T data = current.data;
            current = current.next;
            return data;
        }
    }

    private int size;
    private Node sentinel;


    @Override
    public Iterator<T> iterator() {
        return new LinkedListIterator();
    }


    public LinkedListDeque() {
        size = 0;
        sentinel = new Node(null, null, null);
        sentinel.next = sentinel;
        sentinel.prev = sentinel;
    }

    @Override
    public void addFirst(T item) {
        Node newNode = new Node(item, sentinel, sentinel.next);
        sentinel.next.prev = newNode;
        sentinel.next = newNode;
        size += 1;
    }

    @Override
    public void addLast(T item) {
        Node newNode = new Node(item, sentinel.prev, sentinel);
        sentinel.prev.next = newNode;
        sentinel.prev = newNode;
        size += 1;


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
        List<String> list = new ArrayList<>();
        for (int i = 0; i < size; i++) {
            list.add(Objects.toString(get(i),"null"));
        }
        return String.join(",", list);
    }

    @Override
    public T removeFirst() {
        if (size == 0) {
            return null;
        }
        Node removed = sentinel.next;
        sentinel.next = removed.next;
        removed.next.prev = sentinel;
        size -= 1;
        return removed.data;

    }

    @Override
    public T removeLast() {
        if (size == 0) {
            return null;
        }
        Node removed = sentinel.prev;
        sentinel.prev = removed.prev;
        removed.prev.next = sentinel;
        size -= 1;
        return removed.data;
    }

    @Override
    public T get(int index) {
        if (index < 0 || index >= size) {
            return null;
        }
        Node current = sentinel.next;
        for (int i = 0; i < index; i++) {
            current = current.next;
        }
        return current.data;
    }

    public T getRecursive(int index) {
        if (index < 0 || index >= size) {
            return null;
        }
        return getRecursive(index, sentinel.next);
    }

    public T getRecursive(int index, Node node) {
        if (index == 0) {
            return node.data;
        }
        return getRecursive(index - 1, node.next);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        LinkedListDeque<?> that = (LinkedListDeque<?>) o;
        if (size != that.size) return false;
        for (int i = 0; i < size; i++) {
            if (!this.get(i).equals(that.get(i))) return false;
        }
        return true;

    }


}

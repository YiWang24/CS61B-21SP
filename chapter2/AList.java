/**
 * Array based list.
 *
 * @author Josh Hug
 */

public class AList {
    /**
     * Creates an empty list.
     */
    int[] list;
    int size;

    public AList() {
        list = new int[100];
        size = 0;
    }

    /**
     * Inserts X into the back of the list.
     */
    public void addLast(int x) {
        size++;
        list[list.length - 1] = x;
    }

    /**
     * Returns the item from the back of the list.
     */
    public int getLast() {
        return list[list.length - 1];
    }

    /**
     * Gets the ith item in the list (0 is the front).
     */
    public int get(int i) {
        return list[i];
    }

    /**
     * Returns the number of items in the list.
     */
    public int size() {
        return size;
    }

    /**
     * Deletes item from back of the list and
     * returns deleted item.
     */
    public int removeLast() {
        size--;
        list[list.length - 1] = 0;
        return list[list.length - 1];
    }
} 
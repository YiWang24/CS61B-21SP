import java.util.*;

public class ArraySet<T> implements Iterable<T> {
    private T[] items;
    private int size; // the next item to be added will be at position size

    private class ArraySetIterator implements Iterator<T> {
        private int position;
        public ArraySetIterator() {
            position = 0;
        }
        @Override
        public boolean hasNext() {
            return position<size;
        }

        @Override
        public T next() {
            return items[position++];
        }
    }

    @Override
    public Iterator<T> iterator() {
        return new ArraySetIterator();
    }

    public ArraySet() {
        items = (T[]) new Object[100];
        size = 0;
    }

    public static <Glerp> ArraySet<Glerp> of(Glerp... stuff) {
        ArraySet<Glerp> set = new ArraySet<Glerp>();
        for (Glerp g : stuff) {
            set.add(g);
        }
        return set;
    }

    /* Returns true if this map contains a mapping for the specified key.
     */
    public boolean contains(T x) {

        for (int i = 0; i < size; i += 1) {
            if(items[i] == null) return false;
            if (items[i].equals(x)) {
                return true;
            }
        }
        return false;
    }

    /* Associates the specified value with the specified key in this map. */
    public void add(T x) {

        if (x == null || contains(x)) {
            return;
        }
        items[size] = x;
        size += 1;
    }

    /* Returns the number of key-value mappings in this map. */
    public int size() {
        return size;
    }

//    @Override
//    public String toString() {
//        String returenString = "{";
//        for (int i = 0; i < size - 1; i++) {
//            returenString += items[i].toString() + ", ";
//        }
//        returenString += items[size-1].toString();
//        returenString += "}";
//        return returenString;
//    }
    @Override
    public String toString() {
        List<String> listOfItems = new ArrayList<String>();
        for(T x:this){
            listOfItems.add(x.toString());
        }
        return String.join(", ", listOfItems);
    }
    @Override
    public boolean equals(Object obj) {
        if(this == obj) return true;
        if(obj == null) return false;
        if(obj.getClass() != this.getClass()) return false;
        if (!(obj instanceof ArraySet)) return false;
        ArraySet<T> other = (ArraySet<T>) obj;
        if (size != other.size) return false;
        for(T item : this) {
            if(! other.contains(item)) return false;
        }
        return true;
    }

    public static void main(String[] args) {
        ArraySet<String> s = new ArraySet<>();
        s.add(null);
        s.add("horse");
        s.add("fish");
        s.add("house");
//        s.add("fish");
        Iterator<String>  seer = s.iterator();
        while(seer.hasNext()) {
            System.out.println(seer.next());
        }
        for(String i : s){
            System.out.println(i);
        }
        System.out.println(s.toString());
        System.out.println(s.contains("horse"));        
        System.out.println(s.size());       
    }


    /* Also to do:
    1. Make ArraySet implement the Iterable<T> interface.
    2. Implement a toString method.
    3. Implement an equals() method.
    */
}

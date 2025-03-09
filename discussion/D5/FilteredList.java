package D5;

import java.util.Iterator;
import java.util.List;
import java.util.NoSuchElementException;

/**
 * @author WY
 * @version 1.0
 **/

public class FilteredList<T> implements Iterable<T> {
    List<T> list;
    Predicate<T> predicate;
    public FilteredList(List<T> list, Predicate<T> filter) {
        this.list = list;
        this.predicate = filter;
    }

    @Override
    public Iterator<T> iterator() {
        return new FilteredListIterator();
    }

    private class FilteredListIterator implements Iterator<T> {
        int index;
        public FilteredListIterator() {
            index = 0;
            moveIndex();
        }
        @Override
        public boolean hasNext() {
            return index < list.size();
        }

        @Override
        public T next() {
            if(!hasNext()) {
                throw new NoSuchElementException();
            }
            T answer = list.get(index);
            index++;
            moveIndex();
            return answer;
        }
        private void moveIndex() {
            while(hasNext() && !predicate.test(list.get(index))) {
                index++;
            }
        }
    }

}

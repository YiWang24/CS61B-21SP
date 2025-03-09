package D5;

import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.NoSuchElementException;

/**
 * @author WY
 * @version 1.0
 **/

public class IteratorOfIterators implements Iterator<Integer> {
    LinkedList<Iterator<Integer>> iterators;
    public IteratorOfIterators(List<Iterator<Integer>> a){
        iterators = new LinkedList<>();
        //add all the iterator into list
        for(Iterator<Integer> iterator : a){
            if(iterator.hasNext()){
                iterators.add(iterator);
            }
        }
    }

    @Override
    public boolean hasNext() {
        return !iterators.isEmpty();
    }

    @Override
    public Integer next() {
        if(!hasNext()){
            throw new NoSuchElementException();
        }

        // take out the first iterator and remove the iterator
        Iterator<Integer> iterator = iterators.removeFirst();
        //take out the first value
        int ans = iterator.next();

        //if the first iterator still have value, move to the last of list
        if(iterator.hasNext()){
            iterators.addLast(iterator);
        }
        return ans;
    }
}

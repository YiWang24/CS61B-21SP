package deque;

import java.util.Comparator;

/**
 * @author WY
 * @version 1.0
 **/

public class MaxArrayDeque<T> extends ArrayDeque<T> {

    private final Comparator<T> comparator;
    public MaxArrayDeque(   Comparator<T> comparator){
        super();
        this.comparator = comparator;
    }
    public T max(){
        if(isEmpty()){
            return null;
        }
        return max(comparator);
    }
    public T max(Comparator<T> c){
        if(isEmpty()){
            return null;
        }
        T maxItem = get(0);
        for(int i = 1; i < size(); i++){
            T current = get(i);
            if(c.compare(current, maxItem) > 0){
                maxItem = current;
            }
        }
        return maxItem;

    }


}

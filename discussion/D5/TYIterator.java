package D5;

/**
 * @author WY
 * @version 1.0
 **/

public class TYIterator extends OHIterator{
    public TYIterator(OHRequest queue){
        super(queue);
    }

    @Override
    public OHRequest next() {
        OHRequest result = super.next();
        if(result!=null && result.description.contains("thank u")){
            result = super.next();
        }
        return result;
    }
}

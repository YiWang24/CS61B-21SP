package D3;

/**
 * @author WY
 * @version 1.0
 **/

public class SLList {
    private static class Node{
        int item;
        Node next;
    }
    Node sentinel;

    public SLList(){
        this.sentinel = new Node();
    }

    public int findFirst(int n){
        return findFirstHelper(n,0,sentinel.next);

    }

    private int findFirstHelper(int n, int index, Node curr){
        if(curr == null){
            return -1;
        }
        if(curr.item == n){
            return index;
        } else{
            return findFirstHelper(n,index+1,curr.next);
        }

    }

}


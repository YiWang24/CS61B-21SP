/**
 * @author WY
 * @version 1.0
 **/

public class IntList {
    public int first;
    public IntList next;

    public IntList(int first, IntList next) {
        this.first = first;
        this.next = next;
    }

    public int size() {
        if (next == null) {
            return 1;
        }
        return 1 + this.next.size();
    }

    public IntList addFirst(int value) {
        return new IntList(value, this);
    }

    public int iterativeSize() {
        IntList p = this;
        int totalSize = 0;
        while (p != null) {
            totalSize += 1;
            p = p.next;
        }
        return totalSize;
    }

    public int get(int i){
        if(i == 0){
            return first;
        }
        return next.get(i-1);
    }

    public static IntList incrList(IntList L, int x){
        if(L == null){
            return null;
        }
        return new IntList(L.first + x, incrList(L.next, x));

    }

    public static IntList dincrList(IntList L, int x){
        if(L == null){
            return null;
        }
        L.first += x;
        dincrList(L.next,x);
        return L;
    }

    @Override
    public String toString() {
        if (next == null) {
            return "(" + first + ")";
        }
        return "(" + first + ", " + next.toString().substring(1);
    }
//    public String toString() {
//        StringBuilder sb = new StringBuilder();
//        sb.append("[");
//        IntList p = this;
//        while (p != null) {
//            sb.append(p.first);
//            if(p.next != null) {
//                sb.append(", ");
//            }
//            p = p.next;
//        }
//        sb.append("]");
//        return sb.toString();
//    }

    public static void main(String[] args) {
//        IntList L = new IntList(5,null);
//        L.next = new IntList(10,null);
//        L.next.next = new IntList(15,null);

        IntList L = new IntList(15, null);
        L = new IntList(10, L);
        L = new IntList(5, L);
        System.out.println(L.size());
        System.out.println(L.iterativeSize());
        System.out.println(L.get(0) );
        System.out.println(L.get(1) );
        System.out.println(L.get(2) );

        IntList incrList = L.incrList(L, 1);
        System.out.println(incrList);
        IntList dincrList = L.dincrList(L, 1);
        System.out.println(dincrList);
    }
}

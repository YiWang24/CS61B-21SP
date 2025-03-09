package D3;

/**
 * @author WY
 * @version 1.0
 **/

public class IntList {
    public int first;
    public IntList rest;
    public IntList(int f, IntList r) {
        this.first = f;
        this.rest = r;
    }
    public static IntList list(int... items) {
        if (items.length == 0) return null;
        IntList head = new IntList(items[0], null);
        IntList current = head;
        for (int i = 1; i < items.length; i++) {
            current.rest = new IntList(items[i], null);
            current = current.rest;
        }
        return head;
    }
    public void add(int value) {
        IntList current = this;
        while (current.rest != null) {
            current = current.rest; // 找到链表的最后一个节点
        }
        current.rest = new IntList(value, null); // 添加新节点
    }


    public static void evenOdd(IntList lst) {
        if (lst == null || lst.rest == null) {
            return;
        }
        IntList oddList = lst.rest;
        IntList second = lst.rest;
        while (lst.rest != null && oddList.rest != null) {
            lst.rest = lst.rest.rest;
            oddList.rest = oddList.rest.rest;
            lst = lst.rest;
            oddList = oddList.rest;
        }
        lst.rest = second;
    }

    public static IntList reverse(IntList lst) {
        if (lst == null || lst.rest == null) {
            return lst;
        }

        IntList prev = null;
        while(lst != null) {
            IntList next = lst.rest;
            lst.rest = prev;
            prev = lst;
            lst = next;
        }
        return prev;


    }

    public static IntList[] partition(IntList L, int k) {
        IntList[] array = new IntList[k];
        int index = 0;
        L = reverse(L);
        while(L != null){
           IntList prevAtIndex = array[index];
           IntList next = L.rest;
           array[index] = L;
           array[index].rest = prevAtIndex;
           L= next;
           index = (index + 1) % array.length;
        }
        return array;
    }
}

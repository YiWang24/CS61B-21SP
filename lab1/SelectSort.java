import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * @author WY
 * @version 1.0
 **/

public class SelectSort {
    public static void testSort() {
        ArrayList<Integer> list = new ArrayList(Arrays.asList(6, 3, 7, 2, 8, 1));
        ArrayList<Integer> expected = new ArrayList(Arrays.asList(1, 2, 3, 6, 7, 8));
        Sort.sort2(list,0);
        ;


        org.junit.Assert.assertArrayEquals(expected.toArray(), list.toArray());


    }

    public static void main(String[] args) {
        testSort();
    }
}


class Sort {
    public static void sort(ArrayList<Integer> arr) {

        for (int i = 0; i < arr.size() - 1; i++) {

            int minIndex = i;
            for (int j = i + 1; j < arr.size(); j++) {
                if (arr.get(j) < arr.get(minIndex)) {
                    minIndex = j;
                }
            }
            int temp = arr.get(i);
            arr.set(i, arr.get(minIndex));
            arr.set(minIndex, temp);


        }

    }

    public static void sort2(ArrayList<Integer> arr, int start) {

        if (start == arr.size()) {
            return;
        }

        int minIndex = start;

        for (int j = start + 1; j < arr.size(); j++) {
            if (arr.get(j) < arr.get(minIndex)) {
                minIndex = j;
            }
        }
        int temp = arr.get(start);
        arr.set(start, arr.get(minIndex));
        arr.set(minIndex, temp);


        sort2(arr,start+1);

    }


}

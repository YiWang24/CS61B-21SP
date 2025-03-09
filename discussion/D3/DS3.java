package D3;

import org.junit.Test;

import static D3.FillGrid.fillGrid;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;

/**
 * @author WY
 * @version 1.0
 *
 **/
 
public class DS3 {

    /** Fill the lower-left triangle of S with elements of LL and the
     * upper-right triangle of S with elements of UR (from left-to
     * right, top-to-bottom in each case). Assumes that S is square and
     * LL and UR have at least sufficient elements. */

    @Test
    public void FillGridTest() {
        int[] LL = {1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 0, 0};
        int[] UR = {11, 12, 13, 14, 15, 16, 17, 18, 19, 20};
        int[][] S = {
                {0, 0, 0, 0, 0},
                {0, 0, 0, 0, 0},
                {0, 0, 0, 0, 0},
                {0, 0, 0, 0, 0},
                {0, 0, 0, 0, 0}
        };
        fillGrid(LL, UR, S);
        for (int i = 0; i < S.length; i++) {
            for (int j = 0; j < S[i].length; j++) {
                System.out.print(S[i][j] + "  ");
            }
            System.out.println();
        }
    }

    @Test
    public void testAdd() {
        IntList lst = new IntList(1, null);
        lst.add(2);
        lst.add(3);
        lst.add(4);
        lst.add(5);

        assertEquals(1, lst.first);
        assertEquals(2, lst.rest.first);
        assertEquals(3, lst.rest.rest.first);
        assertEquals(4, lst.rest.rest.rest.first);
        assertEquals(5, lst.rest.rest.rest.rest.first);
    }

    // **测试 reverse 方法**
    @Test
    public void testReverse() {
        IntList lst = IntList.list(1, 2, 3, 4, 5);
        lst = IntList.reverse(lst);

        assertEquals(5, lst.first);
        assertEquals(4, lst.rest.first);
        assertEquals(3, lst.rest.rest.first);
        assertEquals(2, lst.rest.rest.rest.first);
        assertEquals(1, lst.rest.rest.rest.rest.first);
        assertNull(lst.rest.rest.rest.rest.rest);
    }

    // **测试 evenOdd 方法**
    @Test
    public void testEvenOdd() {
        IntList lst = IntList.list(0, 3, 1, 4, 2, 5);
        IntList.evenOdd(lst);

        assertEquals(0, lst.first);
        assertEquals(1, lst.rest.first);
        assertEquals(2, lst.rest.rest.first);
        assertEquals(3, lst.rest.rest.rest.first);
        assertEquals(4, lst.rest.rest.rest.rest.first);
        assertEquals(5, lst.rest.rest.rest.rest.rest.first);
    }

    // **测试 partition 方法**
    @Test
    public void testPartition() {
        IntList lst = IntList.list(1, 2, 3, 4, 5, 6);
        int k = 2;
        IntList[] partitioned = IntList.partition(lst, k);

        // 第一部分
        assertEquals(2, partitioned[0].first);
        assertEquals(4, partitioned[0].rest.first);
        assertEquals(6, partitioned[0].rest.rest.first);
        assertNull(partitioned[0].rest.rest.rest);

        // 第二部分
        assertEquals(1, partitioned[1].first);
        assertEquals(3, partitioned[1].rest.first);
        assertEquals(5, partitioned[1].rest.rest.first);
        assertNull(partitioned[1].rest.rest.rest);
    }
}



package D3;

/**
 * @author WY
 * @version 1.0
 **/

public class FillGrid {
    public static void fillGrid(int[] LL, int[] UR, int[][] S) {
        int N = S.length;
        int kL, kR;
        kL = kR = 0;
        for (int i = 0; i < N; i += 1) {
//            for (int j = 0; j < N; j += 1) {
//                if(i > j){
//                    S[i][j] = LL[kL++];
//                }else if (i < j){
//                    S[i][j] = UR[kR++];
//                } else{
//                    S[i][j] = 0;
//                }
//            }
            System.arraycopy(LL, kL, S[i], 0, i);
            System.arraycopy(UR, kR, S[i], i + 1, N - i - 1);
            kL += i;
            kR += N - i - 1;
        }
    }


}



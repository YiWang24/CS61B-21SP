package jointSet;

/**
 * @author WY
 * @version 1.0
 **/

public class QuickUnionDS implements DisJointSets{
    private int[] parent;

    public QuickUnionDS(int N) {
        parent = new int[N];
        for (int i = 0; i < N; i++) {
            parent[i] = i;
        }
    }

    private int find(int p){
        while(parent[p] >= 0){
            p = parent[p];
        }
        return p;
    }


    @Override
    public void connect(int p, int q) {
        int i = find(p);
        int j = find(q);
        parent[i] = j;
    }

    @Override
    public boolean isConnected(int p, int q) {
        return find(p) == find(q);
    }
}

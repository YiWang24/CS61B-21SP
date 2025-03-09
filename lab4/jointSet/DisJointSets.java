package jointSet;

public interface DisJointSets {
    void connect(int p,int q);

    boolean isConnected(int p,int q);
}

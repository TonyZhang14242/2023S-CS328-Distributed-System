package test;

import myrmi.Remote;

public interface Test extends Remote {
    public int addInt(int a, int b);

    public int addInt();

    public void modify();

    public String hello();
}

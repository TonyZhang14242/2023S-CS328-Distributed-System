package test;

import myrmi.exception.RemoteException;
import myrmi.server.UnicastRemoteObject;

public class TestImpl2 extends UnicastRemoteObject implements Test{
    int a = 0;
    int b = 0;

    protected TestImpl2() throws RemoteException {
    }

    protected TestImpl2(int port) throws RemoteException {

    }

    @Override
    public int addInt(int a, int b) {
        return a+b;
    }

    public int addInt(){
        return addInt(this.a, this.b);
    }


    @Override
    public void modify() {
        System.out.println("Welcome from TestImpl2");
        this.a = 4;
        this.b = 5;
    }

    @Override
    public String hello() {
        return "Hello From TestImpl2!";
    }
}

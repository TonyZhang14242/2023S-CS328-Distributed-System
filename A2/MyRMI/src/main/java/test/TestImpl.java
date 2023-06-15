package test;

import myrmi.exception.RemoteException;
import myrmi.server.UnicastRemoteObject;

public class TestImpl extends UnicastRemoteObject implements Test {

    protected TestImpl() throws RemoteException {
    }

    protected TestImpl(int port) throws  RemoteException {
    }

    public int addInt(int a, int b)
    {
        return a+b;
    }

    public int addInt(){
        return 0;
    }

    public void modify(){

    }

    @Override
    public String hello() {
        return "Hello from TestImpl!";
    }
}

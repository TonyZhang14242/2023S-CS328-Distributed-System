package test;

import myrmi.Remote;
import myrmi.exception.AlreadyBoundException;
import myrmi.exception.RemoteException;
import myrmi.registry.LocateRegistry;
import myrmi.registry.Registry;
import myrmi.server.UnicastRemoteObject;

public class Server {
    public static void main(String[] args) {
        try {
            Registry registry = LocateRegistry.createRegistry();
            Remote test = new TestImpl();
            Remote test2 = new TestImpl2();
            Remote stub = UnicastRemoteObject.exportObject(test);
            Remote stub2 = UnicastRemoteObject.exportObject(test2);
            registry.bind("test", stub);
            registry.bind("test2", stub2);
            //Remote remote = UnicastRemoteObject.exportObject(r);
        } catch (RemoteException e) {
            e.printStackTrace();
        } catch (AlreadyBoundException e) {
            e.printStackTrace();
        }

    }
}

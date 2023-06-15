package myrmi.registry;

import myrmi.Remote;
import myrmi.exception.AlreadyBoundException;
import myrmi.exception.RemoteException;
import myrmi.exception.NotBoundException;

import java.util.HashMap;

public interface Registry extends Remote {
    int REGISTRY_PORT = 11099;
    HashMap<String, Remote> bindings = new HashMap<>();

    public Remote lookup(String name) throws RemoteException, NotBoundException;

    public void bind(String name, Remote obj) throws RemoteException, AlreadyBoundException;

    public void unbind(String name) throws RemoteException, NotBoundException;

    public void rebind(String name, Remote obj) throws RemoteException;

    public String[] list() throws RemoteException;


}

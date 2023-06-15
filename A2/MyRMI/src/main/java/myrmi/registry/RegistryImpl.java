package myrmi.registry;

import myrmi.Remote;
import myrmi.exception.AlreadyBoundException;
import myrmi.exception.NotBoundException;
import myrmi.exception.RemoteException;
import myrmi.server.Skeleton;
import sun.reflect.generics.reflectiveObjects.NotImplementedException;

import java.net.InetAddress;
import java.util.*;

public class RegistryImpl implements Registry {
    private final HashMap<String, Remote> bindings = new HashMap<>();

    /**
     * Construct a new RegistryImpl
     * and create a skeleton on given port
     **/
    public RegistryImpl(int port) throws RemoteException {
        Skeleton skeleton = new Skeleton(this, "127.0.0.1", port, 0);
        skeleton.start();
    }


    public Remote lookup(String name) throws RemoteException, NotBoundException {
        System.out.printf("RegistryImpl: lookup(%s)\n", name);
        //TODO: implement method here
        if (bindings.containsKey(name))
            return bindings.get(name);
        else throw new NotBoundException();
        //throw new NotImplementedException();
    }

    public void bind(String name, Remote obj) throws RemoteException, AlreadyBoundException {
        System.out.printf("RegistryImpl: bind(%s)\n", name);
        if (bindings.containsKey(name)){
            throw new AlreadyBoundException();
        }
        else {
            bindings.put(name, obj);
        }

        //TODO: implement method here
        //throw new NotImplementedException();

    }

    public void unbind(String name) throws RemoteException, NotBoundException {
        System.out.printf("RegistryImpl: unbind(%s)\n", name);
        if (bindings.containsKey(name)){
            bindings.remove(name);
        }
        else {
            throw new NotBoundException();
        }
        //TODO: implement method here
        //throw new NotImplementedException();

    }

    public void rebind(String name, Remote obj) throws RemoteException {
        System.out.printf("RegistryImpl: rebind(%s)\n", name);
        bindings.put(name, obj);
        //TODO: implement method here
        //throw new NotImplementedException();
    }

    public String[] list() throws RemoteException {
        //TODO: implement method here
        List<String> l = new ArrayList<>();
        Set<Map.Entry<String, Remote>> entry = bindings.entrySet();
        entry.forEach(e -> l.add(e.getKey()));
        String[] s = (String[]) l.toArray(new String[l.size()]);
        return s;
        //throw new NotImplementedException();
    }

    public static void main(String args[]) {
        final int regPort = (args.length >= 1) ? Integer.parseInt(args[0])
                : Registry.REGISTRY_PORT;
        RegistryImpl registry;
        try {
            registry = new RegistryImpl(regPort);
        } catch (RemoteException e) {
            System.exit(1);
        }

        System.out.printf("RMI Registry is listening on port %d\n", regPort);

    }
}

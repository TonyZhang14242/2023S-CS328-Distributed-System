package myrmi.registry;

import myrmi.Remote;
import myrmi.exception.RemoteException;

import java.lang.reflect.Proxy;

public class LocateRegistry {
    public static Registry getRegistry() {
        return getRegistry("127.0.0.1", Registry.REGISTRY_PORT);
    }

    /**
     * returns a stub of remote registry
     */
    public static Registry getRegistry(String host, int port) {
        if (port <= 0) {
            port = Registry.REGISTRY_PORT;
        }
        if (host == null || host.length() == 0) {
            try {
                host = java.net.InetAddress.getLocalHost().getHostAddress();
            } catch (Exception e) {
                host = "";
            }
        }
        Remote stub = (Remote) Proxy.newProxyInstance(Registry.class.getClassLoader(), new Class<?>[]{Registry.class}, new RegistryStubInvocationHandler(host, port));

        return (Registry) stub;
    }

    public static Registry createRegistry() throws RemoteException {
        return createRegistry(Registry.REGISTRY_PORT);
    }

    /**
     * create a registry locally
     * but we still need to wrap around the lookup() method
     */
    public static Registry createRegistry(int port) throws RemoteException {
        //TODO: Notice here the registry can only bind to 127.0.0.1, can you extend that?
        if (port == 0) {
            port = Registry.REGISTRY_PORT;
        }
        Registry registry = new RegistryImpl(port);
        return (Registry) Proxy.newProxyInstance(Registry.class.getClassLoader(), new Class<?>[]{Registry.class}, new RegistryStubInvocationHandler("127.0.0.1", port));
    }
}

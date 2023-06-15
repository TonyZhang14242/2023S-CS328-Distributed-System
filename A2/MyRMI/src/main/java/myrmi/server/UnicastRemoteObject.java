package myrmi.server;

import myrmi.Remote;
import myrmi.exception.RemoteException;
import sun.reflect.generics.reflectiveObjects.NotImplementedException;

import java.lang.Thread;


public class UnicastRemoteObject implements Remote, java.io.Serializable {
    int port;

    protected UnicastRemoteObject() throws RemoteException {
        this(0);
    }

    protected UnicastRemoteObject(int port) throws RemoteException {
        this.port = port;
        exportObject(this, port);
    }

    public static Remote exportObject(Remote obj) throws RemoteException {
        return exportObject(obj, 0);
    }

    public static Remote exportObject(Remote obj, int port) throws RemoteException {
        return exportObject(obj, "127.0.0.1", port);
    }

    /**
     * 1. create a skeleton of the given object ``obj'' and bind with the address ``host:port''
     * 2. return a stub of the object ( Util.createStub() )
     **/
    public static Remote exportObject(Remote obj, String host, int port) throws RemoteException {
        //TODO: finish here
        int objkey = obj.hashCode();
        Class[] objInterface = obj.getClass().getInterfaces();
        String interfaceName = (objInterface.length >=1)? objInterface[0].getName():"Remote";
        //System.out.println(interfaceName);
        Skeleton skeleton = new Skeleton(obj, host, port, objkey);//Create a Skeleton object and start it to listen for incoming requests.
        skeleton.start();
        try {
            Thread.sleep(1);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }//Wait for one millisecond after starting the Skeleton to ensure that it has fully started and is listening for requests.
        int remoteport = skeleton.getPort();
        RemoteObjectRef objectRef = new RemoteObjectRef(host, remoteport, objkey, interfaceName);
        return Util.createStub(objectRef);//Create and return a local proxy to the remote object using the RemoteObjectRef.

        //throw new NotImplementedException();
    }
}

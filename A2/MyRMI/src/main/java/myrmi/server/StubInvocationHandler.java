package myrmi.server;

import myrmi.exception.RemoteException;
import myrmi.serializable.MsgFromSkeleton;
import myrmi.serializable.MsgFromStub;
import sun.reflect.generics.reflectiveObjects.NotImplementedException;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.net.Socket;

public class StubInvocationHandler implements InvocationHandler, Serializable {
    private String host;
    private int port;
    private int objectKey;

    public StubInvocationHandler(String host, int port, int objectKey) {
        this.host = host;
        this.port = port;
        this.objectKey = objectKey;
        System.out.printf("Stub created to %s:%d, object key = %d\n", host, port, objectKey);
    }

    public StubInvocationHandler(RemoteObjectRef ref) {
        this(ref.getHost(), ref.getPort(), ref.getObjectKey());
    }

    @Override
    public Object invoke(Object proxy, Method method, Object[] args) throws RemoteException, IOException, ClassNotFoundException, Throwable {
        /*TODO: implement stub proxy invocation handler here
         *  You need to do:
         * 1. connect to remote skeleton, send method and arguments
         * 2. get result back and return to caller transparently
         * */
        Object result = null;
        Socket client = new Socket(host, port);

        MsgFromStub msgFromStub = new MsgFromStub(method.getName(), args, objectKey, method.getParameterTypes());
        ObjectOutputStream objectOutputStream = new ObjectOutputStream(client.getOutputStream());
        objectOutputStream.writeObject(msgFromStub); //send to Skeleton
        objectOutputStream.flush();

        ObjectInputStream objectInputStream = new ObjectInputStream(client.getInputStream());
        Object readObj = objectInputStream.readObject();// read from Skeleton

        MsgFromSkeleton msgFromSkeleton = (MsgFromSkeleton) readObj;
        result = msgFromSkeleton.getResult();
        return result;

        //throw new NotImplementedException();

    }

}

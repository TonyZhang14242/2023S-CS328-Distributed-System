package myrmi.server;

import myrmi.Remote;
import sun.reflect.generics.reflectiveObjects.NotImplementedException;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Proxy;

public class Util {


    public static Remote createStub(RemoteObjectRef ref) {
        //TODO: finish here, instantiate an StubInvocationHandler for ref and then return a stub
        Class objClass;
        Class[] interfaces;
        try {
            objClass = Class.forName(ref.getInterfaceName());
        } catch (ClassNotFoundException e) {
            //throw new RuntimeException(e);
            objClass = Remote.class;
        }
        interfaces = new Class[]{objClass};
        StubInvocationHandler stubInvocationHandler = new StubInvocationHandler(ref);
        return (Remote) Proxy.newProxyInstance(objClass.getClassLoader(), interfaces, stubInvocationHandler);
        //throw new NotImplementedException();
    }


}

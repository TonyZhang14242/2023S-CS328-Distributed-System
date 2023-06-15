package test;

import myrmi.exception.NotBoundException;
import myrmi.registry.LocateRegistry;
import myrmi.registry.Registry;

import java.io.IOException;

public class Client {
    public static void main(String[] args) throws NotBoundException, IOException {
        Registry registry = LocateRegistry.getRegistry();
        Test test = (Test) registry.lookup("test");
        Test test2 = (Test) registry.lookup("test2");

        System.out.println(test.hello());
        System.out.println(test2.hello());
        System.out.println(test2.addInt(30, 8));
        System.out.println(test2.addInt());
        test2.modify();
        System.out.println(test2.addInt());
        System.out.println(test.getClass());
    }

}

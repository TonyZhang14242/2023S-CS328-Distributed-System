package myrmi.server;

import myrmi.Remote;
import sun.reflect.generics.reflectiveObjects.NotImplementedException;

import java.io.IOException;
import java.net.*;

public class Skeleton extends Thread {
    static final int BACKLOG = 5;
    private Remote remoteObj;

    private String host;
    private int port;
    private int objectKey;

    public int getPort() {
        return port;
    }

    public void setPort(int port){
        this.port = port;
    }

    public Skeleton(Remote remoteObj, RemoteObjectRef ref) {
        this(remoteObj, ref.getHost(), ref.getPort(), ref.getObjectKey());
    }

    public Skeleton(Remote remoteObj, String host, int port, int objectKey) {
        super();
        this.remoteObj = remoteObj;
        this.host = host;
        this.port = port;
        this.objectKey = objectKey;
        this.setDaemon(false);
    }

    @Override
    public void run() {
        /*TODO: implement method here
         * You need to:
         * 1. create a server socket to listen for incoming connections
         * 2. use a handler thread to process each request (use SkeletonReqHandler)
         *  */
        try {
            InetAddress addr = InetAddress.getByName(host);
            ServerSocket socket = new ServerSocket(port, BACKLOG, addr);
            setPort(socket.getLocalPort());
            while (true){
                Socket accSocket = socket.accept();
                Thread thread = new SkeletonReqHandler(accSocket, remoteObj, objectKey);
                thread.start();
            }


        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        //throw new NotImplementedException();

    }
}

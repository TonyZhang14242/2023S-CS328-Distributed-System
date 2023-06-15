package myrmi.server;

import myrmi.Remote;

import java.io.Serializable;

public class RemoteObjectRef implements Serializable, Remote {
    private String host;
    private int port;
    private int objectKey;
    private String interfaceName;

    public String getHost() {
        return host;
    }

    public int getPort() {
        return port;
    }

    public int getObjectKey() {
        return objectKey;
    }

    public String getInterfaceName() {
        return interfaceName;
    }

    public void setPort(int port) {
        this.port = port;
    }

    public RemoteObjectRef(String host, int port, int objectKey, String interfaceName) {
        this.host = host;
        this.port = port;
        this.objectKey = objectKey;
        this.interfaceName = interfaceName;
    }

    public RemoteObjectRef(RemoteObjectRef ref) {
        this.host = ref.host;
        this.port = ref.port;
        this.objectKey = ref.objectKey;
        this.interfaceName = ref.interfaceName;
    }


    @Override
    public String toString() {
        return "RemoteObjectRef{" +
                "host='" + host + '\'' +
                ", port=" + port +
                ", objectKey=" + objectKey +
                ", interfaceName='" + interfaceName + '\'' +
                '}';
    }
}

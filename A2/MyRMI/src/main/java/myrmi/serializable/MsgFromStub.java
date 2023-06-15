package myrmi.serializable;

import java.io.Serializable;

public class MsgFromStub implements Serializable {

    private String method;
    private Object[] args;

    private Class<?>[] argsType;

    private int objectKey;

    public MsgFromStub(Object[] args, int objectKey) {
        this.args = args;
        this.objectKey = objectKey;
    }

    public MsgFromStub(String method, Object[] args, int objectKey, Class<?>[] argsType) {
        this.method = method;
        this.args = args;
        this.objectKey = objectKey;
        this.argsType = argsType;
    }

    public Class<?>[] getArgsType() {
        return argsType;
    }

    public void setArgsType(Class<?>[] argsType) {
        this.argsType = argsType;
    }

    public String getMethod() {
        return method;
    }

    public void setMethod(String method) {
        this.method = method;
    }

    public Object[] getArgs() {
        return args;
    }

    public void setArgs(Object[] args) {
        this.args = args;
    }

    public int getObjectKey() {
        return objectKey;
    }

    public void setObjectKey(int objectKey) {
        this.objectKey = objectKey;
    }
}

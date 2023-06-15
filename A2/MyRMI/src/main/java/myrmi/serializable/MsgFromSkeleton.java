package myrmi.serializable;

import java.io.Serializable;

public class MsgFromSkeleton implements Serializable {
    private Object result;
    private int status;
    private int objectKey;

    public MsgFromSkeleton(int objectKey) {
        this.objectKey = objectKey;
    }

    public MsgFromSkeleton(Object result, int status, int objectKey) {
        this.result = result;
        this.status = status;
        this.objectKey = objectKey;
    }

    public Object getResult() {
        return result;
    }

    public int getStatus() {
        return status;
    }

    public int getObjectKey() {
        return objectKey;
    }

    public void setResult(Object result) {
        this.result = result;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public void setObjectKey(int objectKey) {
        this.objectKey = objectKey;
    }
}

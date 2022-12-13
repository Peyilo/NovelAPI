package org.anvei.novel.sfacg.gson.beans;

public class Status {

    public int httpCode;

    public int errorCode;

    public int msgType;

    public String msg;

    @Override
    public String toString() {
        return "Status{" +
                "httpCode=" + httpCode +
                ", errorCode=" + errorCode +
                ", msgType=" + msgType +
                ", msg='" + msg + '\'' +
                '}';
    }
}

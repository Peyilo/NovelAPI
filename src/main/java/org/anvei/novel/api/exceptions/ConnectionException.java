package org.anvei.novel.api.exceptions;

/**
 * 向服务器发起请求，如何请求失败，将会抛出该异常
 */
public class ConnectionException extends IllegalStateException {

    public ConnectionException() {
    }

}

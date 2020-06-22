package com.skyruler.socketclient.exception;


/**
 * @author zhonglunshun
 */
public class ConnectionException extends Exception {
    public ConnectionException() {
        super();
    }

    public ConnectionException(String message) {
        super(message);
    }

    public ConnectionException(String message, Throwable cause) {
        super(message, cause);
    }

    public ConnectionException(Throwable cause) {
        super(cause);
    }

}

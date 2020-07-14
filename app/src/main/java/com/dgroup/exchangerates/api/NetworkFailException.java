package com.dgroup.exchangerates.api;

/**
 * Network connection fails
 */
public class NetworkFailException extends Exception {

    public NetworkFailException() {
        super();
    }

    public NetworkFailException(String detailMessage) {
        super(detailMessage);
    }
}

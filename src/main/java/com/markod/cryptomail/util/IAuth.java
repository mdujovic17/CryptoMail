package com.markod.cryptomail.util;

public interface IAuth<T, U> extends IService<T> {
    int connect();
    int disconnect();
    boolean isConnected();
    U getConnector();
}

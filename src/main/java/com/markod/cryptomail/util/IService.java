package com.markod.cryptomail.util;

public interface IService<T> {
    T getService();
    void resetService();
}

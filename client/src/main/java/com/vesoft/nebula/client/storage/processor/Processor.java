package com.vesoft.nebula.client.storage.processor;

public interface Processor<T> {
    public void processor(T response);
}

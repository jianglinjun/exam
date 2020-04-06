package com.jlj.exam.http;

public interface HttpCallback<T> {
    void onStart();
    void onEnd();
    void onSuccess(T t);
    void onFail(String failMsg);
    void onError(Exception ex);
}

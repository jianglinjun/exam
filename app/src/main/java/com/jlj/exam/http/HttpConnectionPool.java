package com.jlj.exam.http;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

class HttpConnectionPool {
    private static ExecutorService executorService = Executors.newSingleThreadExecutor();

    public static void exec(Runnable runnable){
        executorService.execute(runnable);
    }
}

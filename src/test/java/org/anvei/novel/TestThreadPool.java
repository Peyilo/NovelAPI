package org.anvei.novel;

import org.junit.Test;

import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

public class TestThreadPool {

    AtomicInteger count = new AtomicInteger(0);

    @Test
    public void test1() throws InterruptedException {
        ExecutorService threadPool = Executors.newCachedThreadPool();
        for (int j = 0; j < 100; j++) {
            threadPool.submit(() -> {
                int i = count.getAndAdd(1);
                System.out.println("i = " + i);
            });
        }
        threadPool.awaitTermination(1000, TimeUnit.MILLISECONDS);
        threadPool.shutdown();
        System.out.println("count of list: ");
    }

}

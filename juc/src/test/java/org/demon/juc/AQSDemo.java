package org.demon.juc;

import org.junit.jupiter.api.Test;

import java.util.concurrent.BrokenBarrierException;
import java.util.concurrent.CyclicBarrier;
import java.util.concurrent.Semaphore;

/**
 * @author demon
 * @version 1.0.0
 */
public class AQSDemo {

    @Test
    public void test() {
        CyclicBarrier cyclicBarrier = new CyclicBarrier(10);
        for (int i = 0; i < 10; i++) {
            new Thread(() -> {
                try {
                    System.out.println(Thread.currentThread() + "开始");
                    cyclicBarrier.await();
                    System.out.println(Thread.currentThread() + "结束");
                } catch (InterruptedException | BrokenBarrierException e) {
                    e.printStackTrace();
                }
            }).start();
        }
    }

    @Test
    public void test2() throws InterruptedException {
        Semaphore semaphore = new Semaphore(5);
        for (int i = 0; i < 10; i++) {
            new Thread(() -> {
                try {
                    semaphore.acquire();

                    System.out.println(Thread.currentThread() + "开始");

                    Thread.sleep(1000);

                    System.out.println(Thread.currentThread() + "结束");

                    semaphore.release();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }).start();
        }

        Thread.sleep(10000);

    }
}

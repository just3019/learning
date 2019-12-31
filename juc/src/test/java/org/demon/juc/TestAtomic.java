package org.demon.juc;

import lombok.SneakyThrows;
import org.junit.jupiter.api.Test;

import java.util.concurrent.atomic.AtomicInteger;

/**
 * @author demon
 * @version 1.0.0
 */
public class TestAtomic {

    @SneakyThrows
    @Test
    public void test() {
        AtomicDemo ad = new AtomicDemo();

        for (int i = 0; i < 10; i++) {
            new Thread(ad).start();
        }

        Thread.sleep(1000);
    }
}

class AtomicDemo implements Runnable {

    private AtomicInteger serialNumber = new AtomicInteger(0);

    @Override
    public void run() {
        try {
            Thread.sleep(200);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        System.out.println(Thread.currentThread().getName() + ":" + getSerialNumber());
    }

    public int getSerialNumber() {
        return serialNumber.getAndIncrement();
    }
}

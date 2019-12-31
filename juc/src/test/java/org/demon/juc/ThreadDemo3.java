package org.demon.juc;

import org.junit.jupiter.api.Test;

/**
 * @author demon
 * @version 1.0.0
 */
public class ThreadDemo3 {

    @Test
    public void test() throws InterruptedException {
        Thread thread = new Thread(() -> {
            int i = 0, j = 0;
            i++;
            try {
                Thread.sleep(2000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            j++;
            System.out.println(i + " " + j);
        });
        thread.start();
        Thread.sleep(1000);
        thread.interrupt();
    }

}

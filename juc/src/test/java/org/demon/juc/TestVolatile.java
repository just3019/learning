package org.demon.juc;

import lombok.Getter;
import org.junit.jupiter.api.Test;

/**
 * @author demon
 * @version 1.0.0
 */
public class TestVolatile {

    @Test
    public void test() {
        ThreadDemo td = new ThreadDemo();
        new Thread(td).start();
        while (true) {
            if (td.isFlag()) {
                System.out.println("-------------");
                break;
            }
        }
    }
}

class ThreadDemo implements Runnable {
    /**
     * 提供内存可见性的flag参数。
     */
    @Getter
    private volatile boolean flag = false;

    @Override
    public void run() {
        try {
            Thread.sleep(200);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        flag = true;
        System.out.println("flag=" + flag);
    }
}

package org.demon.juc;

import org.junit.jupiter.api.Test;
import sun.misc.Unsafe;

import java.lang.reflect.Field;

/**
 * @author demon
 * @version 1.0.0
 */
public class TestUnsafe {

    private volatile int i = 0;

    private static Unsafe unsafe;

    private static long iOffset;

    static {
        try {
            Field field = Unsafe.class.getDeclaredField("theUnsafe");
            field.setAccessible(true);
            unsafe = (Unsafe) field.get(null);
            //利用unsafe，通过属性的偏移量定位到内存中对象具体属性的地址
            iOffset = unsafe.objectFieldOffset(TestUnsafe.class.getDeclaredField("i"));
        } catch (NoSuchFieldException | IllegalAccessException e) {
            e.printStackTrace();
        }
    }

    public void add() {
        int current;
        //cas   i++
        do {
            current = unsafe.getIntVolatile(this, iOffset);
        } while (!unsafe.compareAndSwapInt(this, iOffset, current, current + 1));


    }

    @Test
    public void test() throws InterruptedException {
        TestUnsafe testUnsafe = new TestUnsafe();
        for (int i = 0; i < 2; i++) {
            new Thread(() -> {
                for (int j = 0; j < 10000; j++) {
                    add();
                }
            }).start();
        }

        Thread.sleep(2000);
        System.out.println(i);

    }
}

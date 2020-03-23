package org.demon.reference;

import org.junit.jupiter.api.Test;

import java.lang.ref.PhantomReference;
import java.lang.ref.ReferenceQueue;
import java.lang.ref.SoftReference;
import java.lang.ref.WeakReference;
import java.util.WeakHashMap;

/**
 * @author demon
 * @version 1.0.0
 */
public class ReferenceTest {

    @Test
    public void soft() {
        Object o1 = new Object();
        SoftReference<Object> softReference = new SoftReference<>(o1);
        System.out.println(softReference.get());
        o1 = null;

        try {
            byte[] b = new byte[1024 * 1024 * 10];
        } finally {
            System.out.println(softReference.get());
        }
    }

    @Test
    public void weak() {
        Object o1 = new Object();
        WeakReference<Object> weakReference = new WeakReference<>(o1);
        System.out.println(weakReference.get());
        o1 = null;
        System.gc();
        try {
            byte[] b = new byte[1024 * 1024];
        } finally {
            System.out.println(weakReference.get());
        }
    }

    @Test
    public void weakHashMap() {
        WeakHashMap<String, Object> weakHashMap = new WeakHashMap<>();
        weakHashMap.put("1", "1");
        weakHashMap.put(new String("2"), "2");
        String key = new String("3");
        String value = "3";
        weakHashMap.put(key, value);
        System.out.println(weakHashMap);
        key = null;
        System.out.println(weakHashMap);

        System.gc();
        System.out.println(weakHashMap);

    }

    @Test
    public void phantom() throws InterruptedException {
        Object o1 = new Object();
        ReferenceQueue<Object> referenceQueue = new ReferenceQueue<>();
        PhantomReference<Object> phantomReference = new PhantomReference<>(o1, referenceQueue);

        o1 = null;
        System.gc();
        Thread.sleep(500);

        System.out.println(phantomReference.get());
        System.out.println(referenceQueue.poll());

    }


    @Test
    public void oom() {
        while (true) {
            new Thread(()->{
                try {
                    Thread.sleep(10000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }).start();
        }
    }
}

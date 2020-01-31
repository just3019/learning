package org.demon;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import java.util.Random;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

/**
 * @author demon
 * @version 1.0.0
 */
@SpringBootApplication
public class PerformanceApplication {

    public static void main(String[] args) {
        SpringApplication.run(PerformanceApplication.class, args);
        Executors.newScheduledThreadPool(1).scheduleAtFixedRate(() -> new Thread(() -> {
            for (int i = 0; i < 1000; i++) {
                byte[] tmp = new byte[1024 * 512];
                try {
                    Thread.sleep(new Random().nextInt(100));
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }).start(), 100, 100, TimeUnit.MILLISECONDS);
    }
}

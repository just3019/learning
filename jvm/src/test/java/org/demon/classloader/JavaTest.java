package org.demon.classloader;

import java.io.IOException;

/**
 * @author demon
 * @version 1.0.0
 */
public class JavaTest {


    public static void main(String[] args) throws IOException, InterruptedException {
        System.out.println("启动");
        while (true) {
            byte[] b = new byte[1024*100];
            Thread.sleep(100);
        }
    }
}

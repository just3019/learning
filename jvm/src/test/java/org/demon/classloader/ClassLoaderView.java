package org.demon.classloader;

import java.io.IOException;

/**
 * @author demon
 * @version 1.0.0
 */
public class ClassLoaderView {

    public static void main(String[] args) throws ClassNotFoundException, IOException {
        System.out.println("核心类加载器:" + ClassLoaderView.class.getClassLoader().loadClass("java.lang.String").getClassLoader());
        System.out.println("拓展类库加载器:" + ClassLoaderView.class.getClassLoader().loadClass("com.sun.nio.zipfs.ZipUtils").getClassLoader());
        System.out.println("应用程序加载器:" + ClassLoaderView.class.getClassLoader());
        System.out.println("应用程序加载器的父类:" + ClassLoaderView.class.getClassLoader().getParent());
        System.in.read();
    }
}

package org.demon.nio;

import java.nio.ByteBuffer;

public class BufferDemo {

    public static void main(String[] args) {
        ByteBuffer byteBuffer = ByteBuffer.allocate(4);
        ByteBuffer direct = ByteBuffer.allocateDirect(10);
    }
}

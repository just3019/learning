package org.demon.nio;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.SocketChannel;
import java.util.Scanner;

public class NioClient {

    public static void main(String[] args) throws IOException {
        SocketChannel socketChannel = SocketChannel.open();
        socketChannel.configureBlocking(false);
        socketChannel.connect(new InetSocketAddress("127.0.0.1", 8080));
        while (!socketChannel.finishConnect()) {
            Thread.yield();
        }
        Scanner scanner = new Scanner(System.in);
        System.out.println("请输入：");
        String msg = scanner.nextLine();
        ByteBuffer byteBuffer = ByteBuffer.wrap(msg.getBytes());
        while (byteBuffer.hasRemaining()) {
            socketChannel.write(byteBuffer);
        }

        System.out.println("收到响应");
        ByteBuffer response = ByteBuffer.allocate(1024);
        while (socketChannel.isOpen() && socketChannel.read(response) != -1) {
            if (response.position() > 0) break;
        }
        response.flip();
        byte[] content = new byte[response.limit()];
        response.get(content);
        System.out.println("响应内容：\n\r" + new String(content));
        scanner.close();
        socketChannel.close();

    }
}

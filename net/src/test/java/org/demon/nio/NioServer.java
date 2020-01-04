package org.demon.nio;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;

public class NioServer {

    public static void main(String[] args) throws IOException {

        ServerSocketChannel serverSocketChannel = ServerSocketChannel.open();
        serverSocketChannel.configureBlocking(false);//设置非阻塞
        serverSocketChannel.socket().bind(new InetSocketAddress(8080));//绑定端口
        System.out.println("启动成功");
        while (true) {
            SocketChannel socketChannel = serverSocketChannel.accept();
            if (socketChannel == null) continue;

            System.out.println("建立新的连接：" + socketChannel.getRemoteAddress());
            socketChannel.configureBlocking(false);//设置非阻塞
            ByteBuffer byteBuffer = ByteBuffer.allocate(1024);
            while (socketChannel.isOpen() && socketChannel.read(byteBuffer) != -1) {
                //判断数据读取有没有结束
                if (byteBuffer.position() > 0) break;
            }
            if (byteBuffer.position() == 0) continue;
            byteBuffer.flip();
            byte[] content = new byte[byteBuffer.limit()];
            byteBuffer.get(content);

            System.out.println(new String(content));

            String response = "HTTP/1.1 200 OK\r\n"+"Content-Length: 11\r\n\r\n"+"Hello world";
            ByteBuffer buffer = ByteBuffer.wrap(response.getBytes());
            while (buffer.hasRemaining()) {
                socketChannel.write(buffer);
            }

        }
    }
}

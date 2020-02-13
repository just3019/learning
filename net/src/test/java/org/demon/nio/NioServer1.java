package org.demon.nio;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class NioServer1 {

    /**
     * 已建立连接的集合
     */
    private static List<SocketChannel> channels = new ArrayList<>();

    public static void main(String[] args) throws IOException {

        ServerSocketChannel serverSocketChannel = ServerSocketChannel.open();
        serverSocketChannel.configureBlocking(false);//设置非阻塞
        serverSocketChannel.socket().bind(new InetSocketAddress(8080));//绑定端口
        System.out.println("启动成功");
        while (true) {
            SocketChannel socketChannel = serverSocketChannel.accept(); //非阻塞，获取tcp通道
            //如果获取到socketChannel，则加入预设集合中
            if (socketChannel != null) {
                System.out.println("建立新的连接：" + socketChannel.getRemoteAddress());
                socketChannel.configureBlocking(false);//设置非阻塞
                channels.add(socketChannel);
            }
            Iterator<SocketChannel> iterator = channels.iterator();
            while (iterator.hasNext()) {
                SocketChannel ch = iterator.next();
                ByteBuffer byteBuffer = ByteBuffer.allocate(1024);
                //如果没有数据，那就之后处理。
                if (ch.read(byteBuffer) == 0) continue;
                while (ch.isOpen() && ch.read(byteBuffer) != -1) {
                    //判断数据读取有没有结束，如果position超过0标识已经有请求过来
                    if (byteBuffer.position() > 0) break;
                }
                byteBuffer.flip();
                byte[] content = new byte[byteBuffer.limit()];
                byteBuffer.get(content);
                System.out.println(new String(content));
                String response = "HTTP/1.1 200 OK\r\n" + "Content-Length: 11\r\n\r\n" + "Hello world";
                ByteBuffer buffer = ByteBuffer.wrap(response.getBytes());
                while (buffer.hasRemaining()) {
                    ch.write(buffer);//非阻塞
                }
                iterator.remove();
            }
        }
    }
}

package org.demon.nio;


import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.util.Iterator;
import java.util.Set;

/**
 * @author demon
 * @version 1.0.0
 */
public class NioServer2 {


    public static void main(String[] args) throws IOException {
        //1.创建ServerSocketChannel
        ServerSocketChannel serverSocketChannel = ServerSocketChannel.open();
        serverSocketChannel.configureBlocking(false);

        //2.构建选择器
        Selector selector = Selector.open();
        SelectionKey selectionKey = serverSocketChannel.register(selector, 0, serverSocketChannel);
        selectionKey.interestOps(SelectionKey.OP_ACCEPT);

        serverSocketChannel.socket().bind(new InetSocketAddress(8080));
        System.out.println("启动成功");

        while (true) {
            selector.select();

            Set<SelectionKey> selectionKeys = selector.selectedKeys();

            Iterator<SelectionKey> iter = selectionKeys.iterator();
            while (iter.hasNext()) {
                SelectionKey key = iter.next();
                iter.remove();

                if (key.isAcceptable()) {
                    ServerSocketChannel server = (ServerSocketChannel) key.attachment();
                    SocketChannel client = server.accept();
                    client.configureBlocking(false);
                    client.register(selector, SelectionKey.OP_READ, client);
                    System.out.println("收到新连接：" + client.getRemoteAddress());
                }

                if (key.isReadable()) {
                    SocketChannel client = (SocketChannel) key.attachment();
                    ByteBuffer request = ByteBuffer.allocate(1024);
                    while (client.isOpen() && client.read(request) != -1) {
                        if (request.position() > 0) break;
                    }
                    if (request.position() == 0) continue;
                    request.flip();
                    byte[] content = new byte[request.limit()];
                    request.get(content);
                    System.out.println(new String(content));
                    System.out.println("收到来自：" + client.getRemoteAddress());

                    String response = "HTTP/1.1 200 OK\r\n"+"Content-Length: 11\r\n\r\n"+"Hello world";
                    ByteBuffer buffer = ByteBuffer.wrap(response.getBytes());
                    while (buffer.hasRemaining()) {
                        client.write(buffer);
                    }
                }


            }

        }

    }
}

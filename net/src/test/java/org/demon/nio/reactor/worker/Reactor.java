package org.demon.nio.reactor.worker;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.util.Set;

/**
 * @author demon
 * @version 1.0.0
 */
public class Reactor implements Runnable {

    final Selector selector;

    final ServerSocketChannel serverSocketChannel;

    /**
     * 初始化
     */
    public Reactor(int port) throws IOException {
        selector = Selector.open();
        serverSocketChannel = ServerSocketChannel.open();
        serverSocketChannel.socket().bind(new InetSocketAddress(port));//绑定端口
        serverSocketChannel.configureBlocking(false);//设置非阻塞
        SelectionKey sk = serverSocketChannel.register(selector, SelectionKey.OP_ACCEPT);
        sk.attach(new Acceptor());
        System.out.println("启动成功");

    }

    @Override
    public void run() {
        System.out.println("reactor run");
        while (!Thread.interrupted()) {
            try {
                int num = selector.select();
                System.out.println("==="+num);
            } catch (IOException e) {
                e.printStackTrace();
            }
            Set<SelectionKey> selected = selector.selectedKeys();
            for (SelectionKey selectionKey : selected) {
                dispatch(selectionKey);
            }
            selected.clear();
        }
    }

    void dispatch(SelectionKey k) {
        Runnable r = (Runnable) k.attachment();
        if (r != null) {
            r.run();
        }
    }


    class Acceptor implements Runnable {

        @Override
        public void run() {
            try {
                SocketChannel c = serverSocketChannel.accept();
                if (c != null) {
                    System.out.println("收到新连接：" + c.getRemoteAddress());
                    new Handler(selector, c);
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }


    public static void main(String[] args) throws IOException {
        Reactor reactor = new Reactor(8080);
        reactor.run();
    }
}

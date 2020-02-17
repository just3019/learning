package org.demon.nio.reactor.multi;

import java.io.IOException;
import java.net.InetSocketAddress;
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
public class Reactor implements Runnable {

    Selector[] selectors = new Selector[4];
    int next = 0;

    final ServerSocketChannel serverSocketChannel;

    /**
     * 初始化
     */
    public Reactor(int port) throws IOException {
        serverSocketChannel = ServerSocketChannel.open();
        serverSocketChannel.socket().bind(new InetSocketAddress(port));//绑定端口
        serverSocketChannel.configureBlocking(false);//设置非阻塞
        for (int i = 0; i < selectors.length; i++) {
            selectors[i] = Selector.open();
            SelectionKey sk = serverSocketChannel.register(selectors[i], SelectionKey.OP_ACCEPT);
            sk.attach(new Acceptor());
            System.out.println("初始化第" + i + "个selector");
        }
        System.out.println("启动成功");

    }

    @Override
    public void run() {
        try {
            while (!Thread.interrupted()) {
                for (int i = 0; i < selectors.length; i++) {
                    selectors[i].select(1000);//多个selector的话，必须设置超时时间，不然一直阻塞后，后续绑定其他select的操作就会执行不到
                    Set<SelectionKey> selected = selectors[i].selectedKeys();
                    Iterator<SelectionKey> it = selected.iterator();
                    while (it.hasNext())
                        dispatch(it.next());
                    selected.clear();
                }

            }
        } catch (IOException e) {
            e.printStackTrace();
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
        public synchronized void run() {
            try {
                SocketChannel c = serverSocketChannel.accept();
                if (c != null) {
                    System.out.println("收到新连接：" + c.getRemoteAddress());
                    new Handler(selectors[next], c);
                    if (++next == selectors.length) next = 0;
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }


    public static void main(String[] args) throws IOException {
        new Thread(new Reactor(8080)).start();
    }
}

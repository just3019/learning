package org.demon.nio.reactor.basic;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.SocketChannel;

/**
 * @author demon
 * @version 1.0.0
 */
public class NoStateHandler implements Runnable {

    final SocketChannel socket;
    final SelectionKey sk;
    ByteBuffer input = ByteBuffer.allocate(1024);
    ByteBuffer output = ByteBuffer.allocate(1024);

    NoStateHandler(Selector sel, SocketChannel c) throws IOException {
        socket = c;
        c.configureBlocking(false);
        // Optionally try first read now
        sk = socket.register(sel, 0);
        sk.attach(this);
        sk.interestOps(SelectionKey.OP_READ);
        sel.wakeup();
    }

    /**
     * 判断是否读取完毕
     */
    boolean inputIsComplete() throws IOException {
        while (socket.isOpen() && socket.read(input) != -1) {
            if (input.position() > 0) return true;
        }
        return false;
    }


    @Override
    public void run() {
        try {
            socket.read(input);
            if (inputIsComplete()) {
                process();
                sk.attach(new Sender());
                sk.interestOps(SelectionKey.OP_WRITE);
                sk.selector().wakeup();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    private void process() {
        input.flip();
        byte[] content = new byte[input.limit()];
        input.get(content);
        System.out.println(new String(content));
        try {
            System.out.println("收到来自：" + socket.getRemoteAddress());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    class Sender implements Runnable {

        @Override
        public void run() {
            try {
                String response = "HTTP/1.1 200 OK\r\n" + "Content-Length: 11\r\n\r\n" + "Hello world";
                output = ByteBuffer.wrap(response.getBytes());
                socket.write(output);
                sk.cancel();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }


}

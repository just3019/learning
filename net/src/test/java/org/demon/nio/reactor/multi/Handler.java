package org.demon.nio.reactor.multi;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.SocketChannel;
import java.util.concurrent.Executors;

/**
 * @author demon
 * @version 1.0.0
 */
public class Handler implements Runnable {

    final SocketChannel socket;
    final SelectionKey sk;
    ByteBuffer input = ByteBuffer.allocate(1024);
    ByteBuffer output = ByteBuffer.allocate(1024);
    static final int READING = 0, SENDING = 1, PROCESSING = 3;
    int state = READING;


    Handler(Selector sel, SocketChannel c) throws IOException {
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
            if (state == READING) read();
            else if (state == SENDING) send();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void send() throws IOException {
        String response = "HTTP/1.1 200 OK\r\n" + "Content-Length: 11\r\n\r\n" + "Hello world";
        output = ByteBuffer.wrap(response.getBytes());
        socket.write(output);
        sk.cancel();
    }


    private void read() throws IOException {
        socket.read(input);
        if (inputIsComplete()) {
            state = PROCESSING;
            Executors.newCachedThreadPool().execute(new Processer());
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

    class Processer implements Runnable {
        @Override
        public void run() {
            process();
            state = SENDING;
            sk.interestOps(SelectionKey.OP_WRITE);
            sk.selector().wakeup();
        }
    }


}

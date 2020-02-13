package org.demon.bio;

import org.junit.jupiter.api.Test;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.nio.charset.StandardCharsets;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * @author demon
 * @version 1.0.0
 */
public class BIOServer2 {

    private static ExecutorService threadPool = Executors.newCachedThreadPool();


    @Test
    public void test() throws IOException {
        ServerSocket serverSocket = new ServerSocket(8080);
        System.out.println("服务端启动");
        while (!serverSocket.isClosed()) {
            Socket request = serverSocket.accept();//线程阻塞
            System.out.println("接收到新连接：" + request.toString());

            threadPool.execute(() -> {
                try (InputStream inputStream = request.getInputStream();
                     BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream,
                             StandardCharsets.UTF_8));
                     OutputStream outputStream = request.getOutputStream()) {
                    String msg;
                    while ((msg = reader.readLine()) != null) {//线程阻塞
                        if (msg.length() == 0) {
                            break;
                        }
                        System.out.println(msg);
                    }
                    System.out.println("收到数据，来自：" + request.toString());
                    //响应结果
                    outputStream.write("HTTP/1.1 200 OK \r\n".getBytes());
                    outputStream.write("Content-Length:11\r\n\r\n".getBytes());
                    outputStream.write("Hello World".getBytes());
                    outputStream.flush();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            });

        }
    }
}

package org.demon;

import org.junit.jupiter.api.Test;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.ServerSocket;
import java.net.Socket;
import java.nio.charset.StandardCharsets;

/**
 * @author demon
 * @version 1.0.0
 */
public class BIOServer {

    @Test
    public void test() throws IOException {
        ServerSocket serverSocket = new ServerSocket(8080);
        System.out.println("服务端启动");
        while (!serverSocket.isClosed()) {

            Socket request = serverSocket.accept();
            System.out.println("接收到新连接：" + request.toString());
            try (InputStream inputStream = request.getInputStream();
                 BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream,
                         StandardCharsets.UTF_8));) {
                String msg;
                while ((msg = reader.readLine()) != null) {
                    if (msg.length() == 0) {
                        break;
                    }
                    System.out.println(msg);
                }
                System.out.println("收到数据，来自：" + request.toString());
            } catch (Exception e) {
                e.printStackTrace();
            }

        }
    }
}

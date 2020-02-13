package org.demon.bio;

import java.io.IOException;
import java.io.OutputStream;
import java.net.Socket;
import java.nio.charset.StandardCharsets;
import java.util.Scanner;

/**
 * @author demon
 * @version 1.0.0
 */
public class BIOClient {


    public static void main(String[] args) throws IOException {
        Socket socket = new Socket("localhost", 8080);
        OutputStream outputStream = socket.getOutputStream();
        Scanner scanner = new Scanner(System.in);
        System.out.println("请输入：");
        String msg = scanner.nextLine();
        outputStream.write(msg.getBytes(StandardCharsets.UTF_8));
//        scanner.close();
//        socket.close();
    }
}

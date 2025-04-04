package com.remaslover;


import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.SocketChannel;

public class TcpEchoClient {
    public static void main(String[] args) {
        try (SocketChannel socketChannel = SocketChannel.open()) {
            socketChannel.connect(new InetSocketAddress("127.0.0.1", 7));
            System.out.println("Подключение к серверу");
            ByteBuffer buffer = ByteBuffer.allocate(128);

            try (BufferedReader reader = new BufferedReader(new InputStreamReader(System.in))) {
                while (true) {
                    System.out.print("Введите сообщение: ");
                    String line = reader.readLine();

                    buffer.clear();
                    buffer.put(line.getBytes());
                    buffer.flip();
                    socketChannel.write(buffer);
                    buffer.clear();
                    int bytesRead = socketChannel.read(buffer);

                    if (bytesRead == -1) {
                        System.err.println("Сервер закрыл подключение");
                        break;
                    }

                    buffer.flip();
                    int remainElements = buffer.remaining();
                    byte[] response = new byte[remainElements];
                    buffer.get(response);
                    String bytesInString = new String(response);
                    System.out.println("Ответ от сервера: " + bytesInString);

                    if (line.equalsIgnoreCase("quit") || line.equalsIgnoreCase("q")) {
                        break;
                    }
                }
            } catch (IOException e) {
                System.err.println("Ошибка чтения из консоли: " + e.getMessage());
            }
        } catch (IOException e) {
            System.err.println("Ошибка подключения к серверу: " + e.getMessage());
        }
    }
}

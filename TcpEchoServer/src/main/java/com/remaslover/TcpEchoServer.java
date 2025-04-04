package com.remaslover;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class TcpEchoServer {


    private static final int PORT = 7;
    private static final int THREAD_COUNT = 4;
    private static final ExecutorService threadPool = Executors.newFixedThreadPool(THREAD_COUNT);

    public static void main(String[] args) {
        try (ServerSocketChannel serverSocketChannel = ServerSocketChannel.open()) {
            serverSocketChannel.bind(new InetSocketAddress(PORT));
            serverSocketChannel.configureBlocking(true);
            System.out.println("Сервер стартует на порту: " + PORT);

            while (true) {
                try {
                    SocketChannel accepted = serverSocketChannel.accept();
                    System.out.println("Клиент подключился: " + accepted.getRemoteAddress());

                    threadPool.submit(() -> {
                        ByteBuffer buffer = ByteBuffer.allocate(128);

                        try (accepted) {
                            while (true) {
                                buffer.clear();
                                int bytesRead = accepted.read(buffer);

                                if (bytesRead == -1) {
                                    System.out.println("Клиент отключился: " + accepted.getRemoteAddress());
                                    break;
                                }

                                buffer.flip();
                                accepted.write(buffer);
                                System.out.println("Сообщение отправлено клиенту");
                            }
                        } catch (IOException e) {
                            System.err.println("Ошибка обработки клиента: " + e.getMessage());
                        }
                    });
                } catch (IOException e) {
                    System.err.println("Ошибка подключения клиента: " + e.getMessage());
                }
            }
        } catch (IOException e) {
            System.err.println("Ошибка запуска сервера: " + e.getMessage());
        } finally {
            threadPool.shutdown();
        }
    }
}
package server;

import java.io.IOException;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Scanner;

public class Main {

    public static void main(String[] args) {
        ServerSocket serverSocket = null;
        Socket socket = null;

        try {
            serverSocket = new ServerSocket(8189);
            System.out.println("Сервер запущен, ожидание клиентов");
            socket = serverSocket.accept(); //сервер ждет подключений
            System.out.println("Клиент подключился");
            Scanner in = new Scanner(socket.getInputStream());
            Scanner scanner = new Scanner(System.in);
            PrintWriter printWriter = new PrintWriter(socket.getOutputStream());
            new Thread(new Runnable() {
                @Override
                public void run() {
                    while (true) {
                        if(in.hasNext()){
                            System.out.println(in.nextLine());
                        }
                        if(scanner.hasNext()){
                            printWriter.println("Echo: " + scanner.nextLine());
                            printWriter.flush();
                        }
                    }
                }
            }).start();

        } catch (IOException e) {
            System.out.println("Ошибка инициализации");
        } finally {
            try {
                serverSocket.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}

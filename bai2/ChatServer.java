package bai2;

import java.io.*;
import java.net.*;
import java.util.ArrayList;
import java.util.List;

public class ChatServer {
    private static final List<ClientHandler> clientHandlers = new ArrayList<>();

    public static void main(String[] args) {
        try {
            ServerSocket serverSocket = new ServerSocket(8888);
            System.out.println("Server đang chạy...");

            while (true) {
                Socket clientSocket = serverSocket.accept();
                System.out.println("Client mới đã kết nối: " + clientSocket);
                ClientHandler clientHandler = new ClientHandler(clientSocket);
                clientHandlers.add(clientHandler); // Thêm client handler vào danh sách
                clientHandler.start();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    static class ClientHandler extends Thread {
        private Socket clientSocket;
        private PrintWriter out;
        private BufferedReader in;
        private String username;

        public ClientHandler(Socket socket) {
            this.clientSocket = socket;
        }

        public void run() {
            try {
                out = new PrintWriter(clientSocket.getOutputStream(), true);
                in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));

                out.println("Nhập tên của bạn:");
                username = in.readLine();
                System.out.println(username + " đã tham gia cuộc trò chuyện.");

                String message;
                while ((message = in.readLine()) != null) {
                    System.out.println(username + ": " + message);
                    // Phát đi tin nhắn đến tất cả client
                    for (ClientHandler client : clientHandlers) {
                        if (client != this) {
                            client.sendMessage(username + ": " + message);
                        }
                    }
                }
                clientSocket.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        public void sendMessage(String message) {
            out.println(message);
        }
    }
}

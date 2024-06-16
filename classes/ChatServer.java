import java.io.*;
import java.net.*;
import java.util.*;

public class ChatServer {
    private static final int PORT = 12345;
    public static Set<ClientHandler> clientHandlers = new HashSet<>();
    private static AdminHandler adminHandler;

    public static void main(String[] args) {
        System.out.println("Chat server started...");
        try (ServerSocket serverSocket = new ServerSocket(PORT)) {
            new Thread(() -> {
                try {
                    adminHandler = new AdminHandler();
                    adminHandler.start();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }).start();

            while (true) {
                Socket clientSocket = serverSocket.accept();
                ClientHandler clientHandler = new ClientHandler(clientSocket);
                clientHandlers.add(clientHandler);
                new Thread(clientHandler).start();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void sendMessageToAdmin(String message, ClientHandler clientHandler) {
        if (adminHandler != null) {
            adminHandler.sendMessage(clientHandler.getClientName() + ": " + message);
        }
    }

    public static void sendMessageToClient(String clientName, String message) {
        for (ClientHandler client : clientHandlers) {
            if (client.getClientName().equals(clientName)) {
                client.sendMessage("Admin: " + message);
                break;
            }
        }
    }

    public static void removeClient(ClientHandler clientHandler) {
        clientHandlers.remove(clientHandler);
    }
}

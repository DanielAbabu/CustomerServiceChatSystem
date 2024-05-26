import java.io.*;
import java.net.*;

public class AdminHandler {
    private PrintWriter out;
    private BufferedReader in;
    private BufferedReader stdIn;

    public AdminHandler() throws IOException {
        stdIn = new BufferedReader(new InputStreamReader(System.in));
    }

    public void start() {
        System.out.println("Admin started...");
        try {
            while (true) {
                System.out.print("Enter client name to respond: ");
                String clientName = stdIn.readLine();
                System.out.print("Enter message to " + clientName + ": ");
                String message = stdIn.readLine();
                sendMessageToClient(clientName, message);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void sendMessage(String message, ClientHandler clientHandler) {
        System.out.println("Message from " + clientHandler.getClientName() + ": " + message);
    }

    private void sendMessageToClient(String clientName, String message) {
        for (ClientHandler client : ChatServer.clientHandlers) {
            if (client.getClientName().equals(clientName)) {
                client.sendMessage("Admin: " + message);
                break;
            }
        }
    }
}

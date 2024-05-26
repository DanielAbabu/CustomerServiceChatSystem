import java.io.*;
import java.net.*;

public class ClientHandler implements Runnable {
    private Socket socket;
    private PrintWriter out;
    private BufferedReader in;
    private String clientName;

    public ClientHandler(Socket socket) {
        this.socket = socket;
    }

    @Override
    public void run() {
        try {
            in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            out = new PrintWriter(socket.getOutputStream(), true);

            out.println("Enter your name: ");
            clientName = in.readLine();
            System.out.println(clientName + " connected.");

            String message;
            while ((message = in.readLine()) != null) {
                System.out.println("Received from " + clientName + ": " + message);
                if (message.startsWith("@admin")) {
                    ChatServer.sendMessageToAdmin(clientName + ": " + message.substring(6), this);
                } else {
                    ChatServer.broadcastMessage(clientName + ": " + message, this);
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                socket.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
            ChatServer.removeClient(this);
            System.out.println(clientName + " disconnected.");
        }
    }

    public void sendMessage(String message) {
        out.println(message);
    }

    public String getClientName() {
        return clientName;
    }
}

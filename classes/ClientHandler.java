import java.io.*;
import java.net.*;

public class ClientHandler implements Runnable {
    private Socket socket;
    private PrintWriter out;
    private BufferedReader in;
    private String clientName;

    public ClientHandler(Socket socket) {
        this.socket = socket;
        try {
            out = new PrintWriter(socket.getOutputStream(), true);
            in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void run() {
        try {
            clientName = in.readLine();
            String message;
            while ((message = in.readLine()) != null) {
                ChatServer.sendMessageToAdmin(message, this);
                broadcastMessage(clientName + ": " + message);
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            closeConnection();
        }
    }

    public String getClientName() {
        return clientName;
    }

    public void sendMessage(String message) {
        out.println(message);
    }

    private void broadcastMessage(String message) {
        for (ClientHandler client : ChatServer.clientHandlers) {
            if (client != this) {
                client.sendMessage(message);
            }
        }
    }

    private void closeConnection() {
        try {
            ChatServer.removeClient(this);
            socket.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}

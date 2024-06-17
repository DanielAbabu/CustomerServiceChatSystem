import java.io.*;
import java.net.Socket;
import java.sql.*;

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
            if (authenticateUser(clientName)) {
                String message;
                while ((message = in.readLine()) != null) {
                    saveMessageToDatabase(clientName, message);
                    ChatServer.sendMessageToAdmin(message, this);
                    broadcastMessage(clientName + ": " + message);
                }
            } else {
                out.println("Authentication failed. Disconnecting...");
                socket.close();
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            closeConnection();
        }
    }

    private boolean authenticateUser(String username) {
        try (Connection connection = DatabaseUtil.getConnection();) {
            PreparedStatement statement = connection.prepareStatement("SELECT id FROM users WHERE username = ?");
            statement.setString(1, username);
            ResultSet resultSet = statement.executeQuery();
            if (resultSet.next()) {
                System.out.println(resultSet);
                System.out.println("llll");
                // PreparedStatement stmt = connection.prepareStatement("SELECT id FROM users
                // WHERE username = ?");
                // stmt.setString(1, username);
                // ResultSet sst = statement.executeQuery();
                return true;
            } else {
                try (PreparedStatement insertStatement = connection
                        .prepareStatement("INSERT INTO users (username) VALUES (?)")) {
                    insertStatement.setString(1, username);
                    insertStatement.executeUpdate();
                    return true;
                }
            }
        } catch (SQLException e) {
            System.out.println(e);
            return false;
        }
    }

    private void saveMessageToDatabase(String username, String message) {
        try (Connection connection = DatabaseUtil.getConnection();
                PreparedStatement statement = connection.prepareStatement(
                        "INSERT INTO messages (user_id, message) VALUES ((SELECT id FROM users WHERE username = ?), ?)")) {
            statement.setString(1, username);
            statement.setString(2, message);
            statement.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
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

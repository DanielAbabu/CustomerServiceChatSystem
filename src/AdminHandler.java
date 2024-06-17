import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class AdminHandler {
    private JTextField userField;
    private JFrame frame;
    private JTextArea messageArea;
    private JTextField messageField;
    private JButton sendButton;

    public AdminHandler() {
        frame = new JFrame("Admin Console");
        frame.setSize(700, 400);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        messageArea = new JTextArea(20, 40);
        messageArea.setEditable(false);
        messageField = new JTextField(30);
        sendButton = new JButton("Send");
        userField = new JTextField(15);

        JPanel panel = new JPanel(new BorderLayout());
        panel.add(new JScrollPane(messageArea), BorderLayout.CENTER);

        JPanel inputPanel = new JPanel();
        inputPanel.add(messageField);
        inputPanel.add(sendButton);
        inputPanel.add(new JLabel("To User:"));
        inputPanel.add(userField);
        panel.add(inputPanel, BorderLayout.SOUTH);

        frame.getContentPane().add(panel);
        frame.setVisible(true);

        sendButton.addActionListener(new SendButtonListener());
    }

    public void start() {
        System.out.println("Admin console started...");
    }

    public void sendMessage(String message) {
        messageArea.append(message + "\n");
    }

    private boolean authenticateUser(String username) {
        try (Connection connection = DatabaseUtil.getConnection();) {
            PreparedStatement statement = connection.prepareStatement("SELECT id FROM users WHERE username = ?");
            statement.setString(1, username);
            ResultSet resultSet = statement.executeQuery();
            if (resultSet.next()) {
                return true;
            } else {
                return false;
            }
        } catch (SQLException e) {
            System.out.println(e);
            return false;
        }
    }

    private class SendButtonListener implements ActionListener {
        @Override
        public void actionPerformed(ActionEvent e) {
            String username = userField.getText().trim();
            String message = messageField.getText().trim();

            if (!username.isEmpty() && !message.isEmpty()) {
                if (authenticateUser(username)) {

                    ChatServer.sendMessageToClient(username, message);
                    messageArea.append("You to " + username + ": " + message + "\n");
                    messageField.setText("");
                    userField.setText("");
                } else {
                    JOptionPane.showMessageDialog(frame, "There is no user by this name.");
                }
            } else {
                JOptionPane.showMessageDialog(frame, "Enter both username and message");
            }
        }
    }
}

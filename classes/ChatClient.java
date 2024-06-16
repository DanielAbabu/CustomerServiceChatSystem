import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.*;
import java.net.*;

public class ChatClient {
    private static final String SERVER_ADDRESS = "localhost";
    private static final int SERVER_PORT = 12345;

    private JFrame frame;
    private JTextArea messageArea;
    private JTextField textField;
    private JTextField nameField;
    private JButton sendButton;
    private JButton connectButton;
    private PrintWriter out;
    private BufferedReader in;
    private Socket socket;

    public ChatClient() {
        frame = new JFrame("Chat Client");
        frame.setSize(500, 500);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        JPanel panel = new JPanel(new BorderLayout());
        messageArea = new JTextArea(20, 40);
        messageArea.setEditable(false);
        panel.add(new JScrollPane(messageArea), BorderLayout.CENTER);

        JPanel inputPanel = new JPanel();
        nameField = new JTextField(20);
        connectButton = new JButton("Connect");
        inputPanel.add(nameField);
        inputPanel.add(connectButton);
        panel.add(inputPanel, BorderLayout.NORTH);

        JPanel messagePanel = new JPanel();
        textField = new JTextField(30);
        sendButton = new JButton("Send");
        sendButton.setEnabled(false);
        messagePanel.add(textField);
        messagePanel.add(sendButton);
        panel.add(messagePanel, BorderLayout.SOUTH);

        frame.getContentPane().add(panel);
        frame.setVisible(true);

        connectButton.addActionListener(new ConnectButtonListener());
        sendButton.addActionListener(new SendButtonListener());
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(ChatClient::new);
    }

    private void connectToServer(String username) {
        try {
            socket = new Socket(SERVER_ADDRESS, SERVER_PORT);
            out = new PrintWriter(socket.getOutputStream(), true);
            in = new BufferedReader(new InputStreamReader(socket.getInputStream()));

            out.println(username); // Send the username to the server

            Thread readThread = new Thread(() -> {
                String serverMessage;
                try {
                    while ((serverMessage = in.readLine()) != null) {
                        messageArea.append(serverMessage + "\n");
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
            });
            readThread.start();

            sendButton.setEnabled(true);
        } catch (IOException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(frame, "Unable to connect to the server.", "Error",
                    JOptionPane.ERROR_MESSAGE);
        }
    }

    private class ConnectButtonListener implements ActionListener {
        @Override
        public void actionPerformed(ActionEvent e) {
            String username = nameField.getText().trim();
            if (!username.isEmpty()) {
                connectToServer(username);
                nameField.setEditable(false);
                connectButton.setEnabled(false);
            } else {
                JOptionPane.showMessageDialog(frame, "Enter a username");
            }
        }
    }

    private class SendButtonListener implements ActionListener {
        @Override
        public void actionPerformed(ActionEvent e) {
            String message = textField.getText().trim();
            if (!message.isEmpty()) {
                out.println(message);
                messageArea.append("You: " + message + "\n");
                textField.setText("");
            } else {
                JOptionPane.showMessageDialog(frame, "Enter a message");
            }
        }
    }
}

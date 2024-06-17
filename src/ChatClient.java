import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.*;
import java.net.Socket;

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

    private class ConnectButtonListener implements ActionListener {
        @Override
        public void actionPerformed(ActionEvent e) {
            String username = nameField.getText().trim();
            if (!username.isEmpty()) {
                try {
                    socket = new Socket(SERVER_ADDRESS, SERVER_PORT);
                    out = new PrintWriter(socket.getOutputStream(), true);
                    in = new BufferedReader(new InputStreamReader(socket.getInputStream()));

                    out.println(username);
                    new Thread(new IncomingReader()).start();

                    nameField.setEditable(false);
                    connectButton.setEnabled(false);
                    sendButton.setEnabled(true);
                } catch (IOException ex) {
                    ex.printStackTrace();
                }
            }
        }
    }

    private class SendButtonListener implements ActionListener {
        @Override
        public void actionPerformed(ActionEvent e) {
            String message = textField.getText();
            if (!message.isEmpty()) {
                out.println(message);
                messageArea.append("You: " + message + "\n");
                textField.setText("");
            }
        }
    }

    private class IncomingReader implements Runnable {
        @Override
        public void run() {
            String message;
            try {
                while ((message = in.readLine()) != null) {
                    messageArea.append(message + "\n");
                }
            } catch (IOException ex) {
                ex.printStackTrace();
            }
        }
    }
}

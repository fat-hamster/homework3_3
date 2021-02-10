package Client;

import Server.Message;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.io.IOException;

public class MyClient extends JFrame {

    private final ServerService serverService;
    private final LocalHistory localHistory;


    public MyClient() {
        super("Simple Chat");

        localHistory = new LocalHistory();
        serverService = new SocketServerService();
        serverService.openConnection();

        setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        setLayout(new FlowLayout());
        setBounds(400, 400, 520, 350);
        setResizable(false);


        JTextArea mainChat = new JTextArea();
        mainChat.setSize(100, 400);
        mainChat.setColumns(45);
        mainChat.setRows(15);
        mainChat.setFocusable(false);
        mainChat.setEditable(false);
        mainChat.setBorder(BorderFactory.createEtchedBorder());

        JTextField myMessage = new JTextField();
        myMessage.setSize(100, 400);
        myMessage.setColumns(45);
        myMessage.setBorder(BorderFactory.createEtchedBorder());

        Label loginLabel = new Label("Login");
        JTextField login = new JTextField();
        login.setColumns(10);
        Label passwordLabel = new Label("Password");
        JPasswordField password = new JPasswordField();
        password.setColumns(10);

        JLabel status = new JLabel("Status: Не авторизован");

        JButton send = new JButton("Send");
        send.setSize(50, 200);

        send.addActionListener(actionEvent -> sendAuth(loginLabel, login, passwordLabel, password,
                send, mainChat, myMessage, status));

        myMessage.addKeyListener(new KeyAdapter() {
            @Override
            public void keyPressed(KeyEvent e) {
                if(e.getKeyCode() == KeyEvent.VK_ENTER) {
                    sendMessage(myMessage);
                }
            }
        });

        JButton clear = new JButton("Exit");
        clear.setSize(50, 200);
        clear.addActionListener(actionEvent -> exitButton());

        add(status);

        add(mainChat);
        add(myMessage);

        add(loginLabel);
        add(login);
        add(passwordLabel);
        add(password);

        add(send);
        add(clear);

        myMessage.setEditable(false);
        if(!serverService.getError().isEmpty()) {
            mainChat.append(serverService.getError());
        }
    }

    private void sendAuth(Label loginLabel, JTextField login, Label passwordLabel, JPasswordField password,
                          JButton send, JTextArea mainChat, JTextField myMessage, JLabel status) {
        System.out.println("Попытка авторизации");

        if(login.getText().isEmpty() || String.valueOf(password.getPassword()).isEmpty()) {
            mainChat.append("System: Необходимо авторизоваться!\n");
            return;
        }

        try {
            serverService.authentication(login.getText(), String.valueOf(password.getPassword()));
        } catch (IOException e) {
            e.printStackTrace();
        }
        if(serverService.isConnected()) {
            loginLabel.setVisible(false);
            login.setVisible(false);
            passwordLabel.setVisible(false);
            password.setVisible(false);
            myMessage.setEditable(true);
            status.setText("Status: Авторизован");
            for(ActionListener listener : send.getActionListeners()) {
                send.removeActionListener(listener);
            }

            send.addActionListener(actionEvent -> sendMessage(myMessage));
            mainChat.append(localHistory.readHistory(100));

            new Thread(() -> {
                while (true) {
                    printToUI(mainChat, serverService.readMessages());
                }
            }).start();
        } else if (!serverService.getError().isEmpty()) {
            mainChat.append(serverService.getError());
        } else {
            mainChat.append("System: Авторизация не прошла\n");
        }
    }

    private void exitButton() {
        localHistory.close();
        serverService.closeConnection();
    }

    private void sendMessage(JTextField myMessage) {
        if(!myMessage.getText().isEmpty()) {
            serverService.sendMessage(myMessage.getText());
            myMessage.setText("");
        }
    }

    private void printToUI(JTextArea mainChat, Message message) {
        if(message.getMessage() != null) {
            String msg = ((message.getNick() == null) ? "Server" : message.getNick()) + ": " + message.getMessage();
            mainChat.append(msg + '\n');
            localHistory.writeHistory(msg);
        } else {
            mainChat.append("System: Что-то пошло не так\n");
        }
    }
}

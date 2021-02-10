package Client;

import Server.AuthMessage;
import Server.Message;
import Server.MyServer;
import com.google.gson.Gson;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;

public class SocketServerService implements ServerService {
    private Socket socket;
    private DataInputStream dataInputStream;
    private DataOutputStream dataOutputStream;
    private boolean isConnected = false;
    private String error = "";

    @Override
    public String getError() {
        return error;
    }

    @Override
    public boolean isConnected() {
        return this.isConnected;
    }

    @Override
    public void openConnection() {
        try {
            socket = new Socket("localhost", MyServer.PORT);
            dataInputStream = new DataInputStream(socket.getInputStream());
            dataOutputStream = new DataOutputStream(socket.getOutputStream());
        } catch (IOException e) {
            error = "Ошибка соединения с сервером";
        }
    }

    public void authentication(String login, String password) {
        AuthMessage authMessage = new AuthMessage();
        authMessage.setLogin(login);
        authMessage.setPassword(password);
        try {
            dataOutputStream.writeUTF(new Gson().toJson(authMessage));
            dataOutputStream.flush();
            authMessage = new Gson().fromJson(dataInputStream.readUTF(), AuthMessage.class);
        }catch (IOException e) {
            System.out.println("IOException");
            error = "System: Authentication timeout";
            closeConnection();
        }

        if(authMessage.isAuthenticated()) {
            isConnected = true;
        }
    }

    @Override
    public void closeConnection() {
        try {
            if(dataOutputStream != null) {
                dataOutputStream.close();
            }
            if(dataInputStream != null) {
                dataInputStream.close();
            }
            if(socket != null) {
                socket.close();
            }
        } catch (IOException ignore) {}
        System.exit(0);
    }

    @Override
    public void sendMessage(String message) {
        Message msg = new Message();
        msg.setMessage(message);
        try {
            dataOutputStream.writeUTF(new Gson().toJson(msg));
        } catch (IOException e) {
            e.printStackTrace();
        }
        if("/end".equals(message)) {
            closeConnection();
        }
    }

    @Override
    public Message readMessages() {
        if(!isConnected) {
            return null;
        }
        try {
            return new Gson().fromJson(dataInputStream.readUTF(), Message.class);
        } catch (IOException ignore) {
            return null;
        }
    }
}

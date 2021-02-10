package Server;

public interface AuthService {
    void start();
    void stop();
    String getNickByLoginAndPass(String login, String password);
    boolean changeNick(String oldNick, String newNick);
    String getError();
}

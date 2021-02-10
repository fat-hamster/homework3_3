package Server;

public class BaseAuthService implements AuthService {

    private final DBAccess dbAccess;

    public BaseAuthService() {
        dbAccess = new DBAccess();
    }

    @Override
    public void start() {
        dbAccess.connect();
        System.out.println("Сервис авторизации запущен");
    }

    @Override
    public void stop() {
        dbAccess.closeConnection();
        System.out.println("Сервис авторизации остановлен");
    }

    @Override
    public String getNickByLoginAndPass(String login, String password) {
        return dbAccess.getNick(login, password);
    }

    @Override
    public boolean changeNick(String oldNick, String newNick) {
        return dbAccess.changeNick(oldNick, newNick);
    }

    @Override
    public String getError() {
        return dbAccess.getError();
    }
}

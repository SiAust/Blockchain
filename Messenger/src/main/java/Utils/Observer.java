package Utils;

public interface Observer {
    void responseNotification(String string);
    void connectionNotification(boolean isConnected);
    void keyNotification(String key);
}
package Utils;

public interface Observable {
    void addObserver(Observer observer);
    void removeObserver(Observer observer);
    void notifyServerResponse(String serverMessage);
    void notifyConnectionStatus(boolean bool);
    void notifyKeyResponse(boolean hasKey);
    void notifyMsgID(int id);
    void notifyAccountsJSON(String JSON);
}

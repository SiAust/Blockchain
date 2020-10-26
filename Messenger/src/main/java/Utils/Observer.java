package Utils;

public interface Observer {
    void responseNotification(String string);
    void connectionNotification(boolean isConnected);
    void keyNotification(boolean hasKey);
    void updatedMsgID(int id);
    void updateAccountsJSON(String accountsJSON);
}

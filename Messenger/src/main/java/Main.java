import View.ClientView;
import Utils.ClientServer;
import Controller.Controller;
import model.Message;

public class Main {

    public static void main(String[] args) {
        Message model = new Message();
        ClientView view = new ClientView();

        Controller controller = new Controller(model, view);
        controller.init();
    }
}

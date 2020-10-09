import View.ClientView;
import Controller.Controller;
import model.Message;

public class Main {

    public static void main(String[] args) {
        // todo pass args to model for host and port
        Message model = new Message(); // fixme pass null to Controller? Or?
        ClientView view = new ClientView();

        Controller controller = new Controller(model, view);
        controller.init();
    }
}

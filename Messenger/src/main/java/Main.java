import Controller.Controller;
import View.ClientView;
import model.TransactionRequest;

public class Main {

    public static void main(String[] args) {
        // todo pass args to model for host and port
        TransactionRequest model = new TransactionRequest(); // fixme pass null to Controller? Or?
        ClientView view = new ClientView();

        Controller controller = new Controller(model, view);
        controller.init();
    }
}

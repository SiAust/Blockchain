import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.net.UnknownHostException;

/** Client connects to a server that is generating blocks for a blockchain. The client
 * sends messages which will be inserted into the blocks as they are generated.
 * */
public class Client {

    public static void main(String[] args) {
        /* For the moment host and port are hard coded */
//        if (args.length != 2) {
//            System.err.println(
//                    "Usage: java Client <host name> <port number>");
//            System.exit(1);
//        }

        String hostName = "localhost";
        int portNumber = 8080;

        try (
                Socket echoSocket = new Socket(hostName, portNumber);
                PrintWriter out = new PrintWriter(echoSocket.getOutputStream(), true);
                BufferedReader in = new BufferedReader(new InputStreamReader(echoSocket.getInputStream()));
                BufferedReader stdIn = new BufferedReader(new InputStreamReader(System.in))
        ) {
            String serverResponse;
            while (true) {
                if (in.ready()) {
                    serverResponse = in.readLine();
                    if (serverResponse.equals("-1")) {
                        System.out.println("Server connection closed");
                        break;
                    }
                    System.out.println("Server received message: " + serverResponse);
                }
                if (stdIn.ready()) {
                    out.println(stdIn.readLine());
                }
            }
        } catch (UnknownHostException e) {
            System.err.println("Don't know about host " + hostName);
            System.exit(1);
        } catch (IOException e) {
            System.err.println("Couldn't get I/O for the connection to " +
                    hostName);
            System.exit(1);
        }
    }
}
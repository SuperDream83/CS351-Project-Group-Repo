import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.Scanner;

public class Client {

    Client() {

    }

    public void runClient() throws IOException {
        Socket socket = new Socket("127.0.0.1", 11000);
        PrintWriter printWriter = new PrintWriter(socket.getOutputStream(), true);
        BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()));

        Menu menu = new Menu(in, printWriter);
        menu.displayMenu();
        socket.close();
        System.exit(0);

    }

    public static void main(String[] args) throws IOException {
        Client client = new Client();
        client.runClient();
    }

}

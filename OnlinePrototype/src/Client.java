import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Scanner;

public class Client {

    Client() {

    }

    public void runClient() throws IOException {
        Socket socket = new Socket("127.0.0.1", 11000);
        PrintWriter printWriter = new PrintWriter(socket.getOutputStream(), true);
        Scanner scanner = new Scanner(System.in);
        BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
        System.out.print("Please enter your username: ");
        String username = scanner.nextLine();
        System.out.print("Please enter your password: ");
        String password = scanner.nextLine();

        printWriter.println("LOGIN" + "|" + username + "|" + password);

        while (socket.isConnected()) {
            System.out.println("Welcome to Aidans Chat Site!");
            System.out.println("Option 1: View online users");
            System.out.println("Option 2: View my account");
            System.out.println("Option 3: Send Money to User");
            System.out.println("Option 4: Exit");
            System.out.print(" >>> ");
            int choice = Integer.parseInt(scanner.nextLine());

            if (choice == 1) {
                printWriter.println("VIEW_ONLINE_USERS");
                String output = in.readLine();
                String[] splitOut = output.split("-");
                for (String line : splitOut) {
                    System.out.println(line);
                }

            } else if (choice == 2) {
                printWriter.println("VIEW_USER_ACCOUNT");
                String output = in.readLine();
                String[] splitOut = output.split("-");
                for (String line : splitOut) {
                    System.out.println(line);
                }

            } else if (choice == 3) {
                System.out.print("Enter user you wish to send money too: ");
                String recipient = scanner.nextLine().trim();
                System.out.print("Enter amount you wish to send: ");
                String amount = scanner.nextLine().trim();

                printWriter.println("SEND_MONEY_TO_USER" + "|" + recipient + "|" + amount + "|" + username);

                String output = in.readLine();
                System.out.println(output);

            } else {
                printWriter.println("LOG_OFF" +  "|" + username);
                socket.close();
                System.out.println("Goodbye!");
                System.exit(0);
            }


        }
    }

    public static void main(String[] args) throws IOException {
        Client client = new Client();
        client.runClient();
    }

}

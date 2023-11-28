import javax.swing.*;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.Scanner;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.LinkedBlockingQueue;

public class Client {

    private Socket socket;
    private BufferedReader in;
    private PrintWriter printWriter;
    private Menu menu;

    private BlockingQueue<Runnable> taskQueue;

    Client() {
        try {
            this.socket = new Socket("127.0.0.1", 11001);
            this.printWriter = new PrintWriter(socket.getOutputStream(), true);
            this.in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            this.menu = new Menu(in, printWriter);
            this.taskQueue = new LinkedBlockingQueue<>();
        } catch (IOException e) {
            e.printStackTrace();
        }


    }

    public Runnable runClient(BlockingQueue<Runnable> taskQueue) throws IOException {
        return (new Runnable() {
            @Override
            public void run() {
                menu.displayMenu(taskQueue);
                try {
                    socket.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
                System.exit(0);
            }
        });
    }

    public Runnable listenForMessage() {
        return (new Runnable() {
            @Override
            public void run() {
                String msgFromServer = "";

                while (socket.isConnected()) {
                    try {
                        msgFromServer = in.readLine();


                        // Conditional for Balance Update
                        if (msgFromServer.contains("BALANCE_UPDATE")) {

                            String[] data = msgFromServer.split("\\|");

                            String sender = data[1].trim();
                            String amount = data[2].trim();

                            JOptionPane.showMessageDialog(null, "You have received: " + amount + " from " + sender);

                            // Conditional for Account Login
                        } else if (msgFromServer.contains("SERVER_DISCONNECT")) {
                            JOptionPane.showMessageDialog(null, "Warning: Server is shutting down...");
                            try {
                                Thread.sleep(100);
                            } catch (InterruptedException e) {
                                e.printStackTrace();
                            }

                            System.exit(0);
                        } else if (msgFromServer.contains("SUCCESSFUL_LOGON")) {
                            // Balance is 0 on client side as actual balance is kept on online user list on server

                            String finalMsgFromServer1 = msgFromServer;
                            taskQueue.add(() -> {
                                String[] data = finalMsgFromServer1.split("\\|");

                                String username = data[1].trim();
                                String password = data[2].trim();

                                menu.setAccount(new Account(username, password, 0));
                                System.out.println("Log in Successful! Welcome " + username + "!");
                            });
                        } else if (msgFromServer.equals("LOGON_FAILED")) {
                            taskQueue.add(() -> {
                                System.out.println("Login Unsuccessful - Please try again!");
                            });
                            // Conditional for Account Registration
                        } else if (msgFromServer.equals("REGISTER_FAILED")) {
                            taskQueue.add(() -> {
                                System.out.println("Error: Username already exits! Please pick a unique username.");
                            });
                        } else if (msgFromServer.contains("ACCOUNT_CREATED")) {
                            String finalMsgFromServer3 = msgFromServer;
                            taskQueue.add(() -> {
                                String[] data = finalMsgFromServer3.split("\\|");

                                String username = data[1].trim();
                                String password = data[2].trim();

                                menu.setAccount(new Account(username, password, 0));
                                System.out.println("Account Created! Welcome " + username + "!");
                            });
                        } else if (msgFromServer.contains("VIEW_ONLINE_USERS")) {
                            String finalMsgFromServer4 = msgFromServer;
                            taskQueue.add(() -> {
                                String[] data = finalMsgFromServer4.split("\\|");

                                String onlineUsers = data[1].trim();

                                String[] splitOut = onlineUsers.split("-");
                                for (String line : splitOut) {
                                    System.out.println(line);
                                }
                            });
                        } else if (msgFromServer.contains("VIEW_USER_ACCOUNT")) {
                            String finalMsgFromServer2 = msgFromServer;
                            taskQueue.add(() -> {
                                String[] data = finalMsgFromServer2.split("\\|");

                                String userAccount = data[1].trim();

                                String[] splitOut = userAccount.split("-");
                                for (String line : splitOut) {
                                    System.out.println(line);
                                }
                            });
                        } else if (msgFromServer.contains("SEND_MONEY_TO_USER")) {
                            String finalMsgFromServer5 = msgFromServer;
                            taskQueue.add(() -> {
                                String[] data = finalMsgFromServer5.split("\\|");

                                String msg = data[1].trim();

                                System.out.println(msg);
                            });
                        } else if (msgFromServer.contains("VIEW_MARKETPLACE")) {
                            String finalMsgFromServer6 = msgFromServer;
                            taskQueue.add(() -> {

                                String[] data = finalMsgFromServer6.split("\\|");

                                String marketplaceView = data[1];

                                String[] splitOut = marketplaceView.split("-");
                                for (String line : splitOut) {
                                    System.out.println(line);
                                }
                            });
                        } else if (msgFromServer.contains("BUY_ITEM")) {
                            String finalMsgFromServer7 = msgFromServer;
                            taskQueue.add(() -> {

                                String[] data = finalMsgFromServer7.split("\\|");

                                String buyMsg = data[1];

                                System.out.println(buyMsg);
                            });
                        } else if (msgFromServer.contains("SELL_ITEM")) {
                            String finalMsgFromServer8 = msgFromServer;
                            taskQueue.add(() -> {

                                String[] data = finalMsgFromServer8.split("\\|");

                                String buyMsg = data[1];

                                System.out.println(buyMsg);
                            });
                        }
                    } catch (IOException e) {
                        e.printStackTrace();
                        break;
                    }
                }
            }
        });
    }

    public static void main(String[] args) throws IOException {
        Client client = new Client();

        Thread listenForMsg = new Thread(client.listenForMessage());
        Thread runClient = new Thread(client.runClient(client.taskQueue));

        listenForMsg.start();
        runClient.start();

    }


}

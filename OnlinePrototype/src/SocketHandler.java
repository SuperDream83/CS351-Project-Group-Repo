import java.io.IOException;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Scanner;

public class SocketHandler implements Runnable {

    Socket socket;
    Account userAccount;
    public static ArrayList<Account> onlineUsers = new ArrayList<>();


    SocketHandler(Socket socket) throws IOException {
        this.socket = socket;

    }

    @Override
    public void run() {
        try {
            Scanner scanner = new Scanner(socket.getInputStream());
            PrintWriter out = new PrintWriter(socket.getOutputStream(), true);
            String msg = "";
            while (true) {
                msg = scanner.nextLine();

                // Note this is more of a register function
                // Real login will parse CSV
                // Real Register will parse CSV to check if account already exists
                // before adding new account
                if (msg.contains("LOGIN")) {
                    String[] data = msg.split("\\|");

                    String username = data[1].trim();
                    String password = data[2].trim();

                    userAccount = new Account(username, password);
                    onlineUsers.add(userAccount);


                } else if (msg.contains("VIEW_ONLINE_USERS")) {
                    StringBuilder stringToSend = new StringBuilder();
                    for (Account account : onlineUsers) {
                        stringToSend.append("Username: ").append(account.getUserName()).append("-")
                                .append("  Balance: ").append(account.getBalance()).append("-");
                    }
                    out.println(stringToSend);
                } else if (msg.contains("VIEW_USER_ACCOUNT")) {
                    StringBuilder accountToSend = new StringBuilder();
                    accountToSend.append("Username: ").append(userAccount.getUserName()).append("-")
                            .append("  Balance: ").append(userAccount.getBalance()).append("-");
                    out.println(accountToSend);
                } else if (msg.contains("SEND_MONEY_TO_USER")) {

                    String[] data = msg.split("\\|");

                    String recipient = data[1].trim();
                    Integer amount = Integer.parseInt(data[2].trim());
                    String sender = data[3].trim();
                    boolean transactionFlag = true;
                    boolean senderFlag = false;
                    boolean recipientFlag = false;
                    int existingSenderBal = 0;
                    int existingRecipientBal = 0;

                    while (transactionFlag){

                        int count  = 0;
                        for (Account account : onlineUsers){
                            if (account.getUserName().equals(sender)){
                                existingSenderBal = account.getBalance();
                                senderFlag = true;
                            } else if (account.getUserName().equals(recipient)){
                                existingRecipientBal = account.getBalance();
                                recipientFlag = true;
                            }
                        }

                        if (senderFlag && !recipientFlag){
                            out.println("ERROR: We couldn't find the recipient!");
                            break;
                        } else if (!senderFlag && recipientFlag){
                            out.println("ERROR: We couldn't find the sender!");
                            break;
                        }

                        synchronized (this) {
                            // Update receiver Account
                            for (Account account : onlineUsers) {
                                if (account.getUserName().equals(recipient)) {
                                    Account recipientAccount = account;
                                    recipientAccount.incrementBalance(amount);
                                    onlineUsers.remove(account);
                                    onlineUsers.add(recipientAccount);
                                    break;
                                }
                            }

                            // Update Sender account
                            for (Account account : onlineUsers) {
                                if (account.getUserName().equals(sender)) {
                                    Account senderAccount = account;
                                    senderAccount.decrementBalance(amount);
                                    onlineUsers.remove(account);
                                    onlineUsers.add(senderAccount);
                                    break;
                                }

                            }
                        }

                        int updatedSenderBal = existingSenderBal - amount;
                        int updatedRecipientBal = existingRecipientBal + amount;

                        boolean senderBalVal = false;
                        boolean recipientBalVal = false;

                        // Need to figure out if this is necessary
                        count = 0;
                        for (Account account : onlineUsers){
                            if (account.getUserName().equals(sender)){
                                if (account.getBalance() == updatedSenderBal){
                                    senderBalVal = true;
                                    break;
                                } else if (count == onlineUsers.size()){
                                    account.setBalance(existingSenderBal);
                                } else {
                                    count++;
                                }

                            }
                        }

                        // Need to figure out if this is necessary
                        count = 0;
                        for (Account account : onlineUsers){
                            if (account.getUserName().equals(recipient)){
                                if (account.getBalance() == updatedRecipientBal){
                                    recipientBalVal = true;
                                    break;
                                } else if (count == onlineUsers.size()){
                                    account.setBalance(existingRecipientBal);
                                } else {
                                    count++;
                                }

                            }
                        }

                        if (senderBalVal && recipientBalVal){
                            out.println("SUCCESS");
                        } else {
                            out.println("ERROR: General Error");
                        }
                        transactionFlag = false;

                    }


                } else if (msg.contains("LOG_OFF")) {
                    String[] data = msg.split("\\|");
                    synchronized (this) {
                        onlineUsers.removeIf(account -> account.getUserName().equals(data[1]));
                    }
                }


            }
        } catch (IOException e) {
            e.printStackTrace();
        }


    }
}

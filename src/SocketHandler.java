import java.io.IOException;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Scanner;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

public class SocketHandler implements Runnable {

    Socket socket;
    Account userAccount;
    private static Map<Account, Socket> onlineUsersMap = new HashMap<>(); // <Account, client Socket> HashMap
    public static Marketplace marketplace = new Marketplace();


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

                if (msg.contains("LOGIN")) {
                    String[] data = msg.split("\\|");

                    String username = data[1].trim();
                    String password = data[2].trim();

                    userAccount = CsvUtils.getAccount(username, password);

                    if (userAccount != null) {
                        out.println("SUCCESSFUL_LOGON" + "|" + username + "|" + password);
                        onlineUsersMap.put(userAccount, socket);
                        out.flush();
                    } else {
                        out.println("LOGON_FAILED");
                        out.flush();
                    }

                } else if (msg.contains("REGISTER")) {
                    String[] data = msg.split("\\|");

                    String username = data[1].trim();
                    String password = data[2].trim();

                    if (CsvUtils.checkAccountExists(username)) {
                        out.println("REGISTER_FAILED");
                        out.flush();
                    } else {
                        userAccount = new Account(username, password, 1000);

                        CsvUtils.saveToCSV(userAccount);
                        onlineUsersMap.put(userAccount, socket);
                        out.println("ACCOUNT_CREATED" + "|" + username + "|" + password);
                        out.flush();
                    }

                    // handles the viewing of all current online users
                    // NOTE: IP if only there for testing purposes - needs removed
                } else if (msg.contains("VIEW_ONLINE_USERS")) {
                    StringBuilder stringToSend = new StringBuilder();
                    for (Account account : onlineUsersMap.keySet()) {
                        stringToSend.append("Username: ").append(account.getUserName()).append("-")
                                .append("  Balance: ").append(account.getBalance()).append("-")
                                .append("    IP: ").append(onlineUsersMap.get(account).getRemoteSocketAddress().toString()).append("-");
                    }
                    out.println("VIEW_ONLINE_USERS" + "|" + stringToSend);
                    out.flush();
                    // View client account
                } else if (msg.contains("VIEW_USER_ACCOUNT")) {
                    out.println("VIEW_USER_ACCOUNT" + "|" + userAccount.printInventory());
                    out.flush();
                    //Send money to another account
                } else if (msg.contains("SEND_MONEY_TO_USER")) {

                    String[] data = msg.split("\\|");

                    String recipient = data[1].trim();
                    int amount = Integer.parseInt(data[2].trim());
                    String sender = data[3].trim();

                    boolean senderFlag = false;
                    boolean recipientFlag = false;

                    for (Account account : onlineUsersMap.keySet()) {
                        // Verify sender exists and get current balance
                        if (account.getUserName().equals(sender)) {
                            senderFlag = true;
                            // Verify recipient exists and get current balance
                        } else if (account.getUserName().equals(recipient)) {
                            recipientFlag = true;
                        }
                    }

                    // Validate both user and sender are confirmed on list of online users
                    //recipient not found
                    if (senderFlag && !recipientFlag) {
                        out.println("SEND_MONEY_TO_USER" + "|" + "ERROR: We couldn't find the recipient!");
                        out.flush();
                        break;
                        // Sender not found
                    } else if (!senderFlag && recipientFlag) {
                        out.println("SEND_MONEY_TO_USER" + "|" + "ERROR: We couldn't find the sender!");
                        out.flush();
                        break;
                        // neither found
                    } else if (!senderFlag && !recipientFlag) {
                        out.println("SEND_MONEY_TO_USER" + "|" + "ERROR: Transaction cancelled!");
                        out.flush();
                        break;
                    }

                    // Update receiver Account
                    for (Account account : onlineUsersMap.keySet()) {
                        if (account.getUserName().equals(recipient)) {
                            // get the recipient account
                            // get the socket of the client associated with the account
                            Socket cliSocket = onlineUsersMap.get(account);
                            account.incrementBalance(amount);
                            onlineUsersMap.remove(account);
                            onlineUsersMap.put(account, cliSocket);

                            // Code for creating connection and writing to sender client socket

                            // Creates a temp PW that will write to recipient client
                            PrintWriter specificPW = new PrintWriter(cliSocket.getOutputStream(), true);
                            // Sends message to recipient client that informs of balance update
                            specificPW.println("BALANCE_UPDATE" + "|" + sender + "|" + amount);
                            //specificPW.close();
                            break;
                        }
                    }

                    // Update Sender account
                    for (Account account : onlineUsersMap.keySet()) {
                        if (account.getUserName().equals(sender)) {
                            account.decrementBalance(amount);
                            onlineUsersMap.remove(account);
                            onlineUsersMap.put(account, socket);
                            break;
                        }
                    }


                    out.println("SEND_MONEY_TO_USER" + "|" + "SUCCESS: " + amount + " sent to " + recipient);
                    out.flush();


                } else if (msg.contains("VIEW_MARKETPLACE")) {
                    out.println("VIEW_MARKETPLACE" + "|" + marketplace.view());
                    out.flush();
                } else if (msg.contains("BUY_ITEM")) {
                    String[] data = msg.split("\\|");

                    String itemName = data[1].trim();
                    Integer quantity = Integer.parseInt(data[2].trim());

                    MarketItem item = marketplace.findItem(itemName);
                    if (item == null) {
                        out.println("BUY_ITEM" + "|" + "Item not found in the marketplace.");
                        out.flush();
                        break;
                    }

                    if (quantity <= 0 || item.getQuantity() < quantity) {
                        out.println("BUY_ITEM" + "|" + "Insufficient quantity available.");
                        out.flush();
                        break;
                    }

                    int totalCost = item.getBuyPrice() * quantity;
                    if (userAccount.getBalance() < totalCost) {
                        out.println("BUY_ITEM" + "|" + "Insufficient balance to complete the transaction.");
                        out.flush();
                        break;
                    }

                    // Processing the transaction
                    marketplace.removeFromInventory(itemName, quantity); // Remove from marketplace
                    userAccount.addToInventory(itemName, quantity); // Add to user's inventory
                    userAccount.decrementBalance(totalCost); // Deduct the cost from the user's balance

                    out.println("BUY_ITEM" + "|" + "Purchase successful!");
                    out.flush();


                } else if (msg.contains("SELL_ITEM")) {
                    String[] data = msg.split("\\|");

                    String itemName = data[1].trim();
                    Integer quantityToSell = Integer.parseInt(data[2].trim());

                    Integer itemQuantity = userAccount.getInventory().get(itemName);
                    if (itemQuantity == null || itemQuantity == 0) {
                        out.println("SELL_ITEM" + "|" + "Item not found in your inventory.");
                        out.flush();
                        break;
                    }

                    if (quantityToSell <= 0 || quantityToSell > itemQuantity) {
                        out.println("SELL_ITEM" + "|" + "Insufficient quantity to sell.");
                        out.flush();
                        break;
                    }

                    // Proceed with the selling process
                    userAccount.removeFromInventory(itemName, quantityToSell);

                    // Update marketplace inventory
                    marketplace.addToInventory(itemName, quantityToSell);

                    // Calculate the total earnings from the sale
                    MarketItem marketItem = marketplace.findItem(itemName);
                    int sellPrice = marketItem != null ? marketItem.getSellPrice() : 0;
                    int totalEarnings = sellPrice * quantityToSell;

                    // Update the user's balance
                    userAccount.incrementBalance(totalEarnings);

                    out.println("SELL_ITEM" + "|" + "Sale successful! You earned " + totalEarnings);
                    out.flush();


                } else if (msg.contains("LOG_OFF")) {
                    String[] data = msg.split("\\|");
                    for (Account account : onlineUsersMap.keySet()) {
                        if (account.getUserName().equals(data[1])) {
                            CsvUtils.updateUserBalance(userAccount);
                            onlineUsersMap.remove(account);
                        }
                    }
                }


            }
        } catch (IOException e) {
            e.printStackTrace();
        }


    }
}

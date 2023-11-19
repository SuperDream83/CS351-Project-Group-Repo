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
    public static ArrayList<Account> onlineUsers = new ArrayList<>();
    private static Map<Account, String> onlineUsersMap = new HashMap<>(); // <Account, IP> HashMap
    private static ArrayList<Socket> clientSockets = new ArrayList<>();
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


                // Note this is more of a register function
                // Real login will parse CSV
                // Real Register will parse CSV to check if account already exists
                // before adding new account
                if (msg.contains("LOGIN")) {
                    String[] data = msg.split("\\|");

                    String username = data[1].trim();
                    String password = data[2].trim();

                    userAccount = CsvUtils.getAccount(username, password);

                    if (userAccount != null) {
                        out.println("SUCCESSFUL_LOGON" + "|" + username + "|" + password);
                        onlineUsers.add(userAccount);
                        onlineUsersMap.put(userAccount, socket.getRemoteSocketAddress().toString());
                        clientSockets.add(socket);
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
                        onlineUsers.add(userAccount);
                        onlineUsersMap.put(userAccount, socket.getRemoteSocketAddress().toString());
                        clientSockets.add(socket);
                        out.println("ACCOUNT_CREATED" + "|" + username + "|" + password);
                        out.flush();
                    }


                } else if (msg.contains("VIEW_ONLINE_USERS")) {
                    StringBuilder stringToSend = new StringBuilder();
                    for (Account account : onlineUsers) {
                        stringToSend.append("Username: ").append(account.getUserName()).append("-")
                                .append("  Balance: ").append(account.getBalance()).append("-")
                                .append("    IP: ").append(onlineUsersMap.get(account)).append("-");
                    }
                    out.println("VIEW_ONLINE_USERS" + "|" + stringToSend);
                    out.flush();
                } else if (msg.contains("VIEW_USER_ACCOUNT")) {
                    out.println("VIEW_USER_ACCOUNT" + "|" + userAccount.printInventory());
                    out.flush();
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

                    while (transactionFlag) {

                        int count = 0;
                        for (Account account : onlineUsersMap.keySet()) {
                            if (account.getUserName().equals(sender)) {
                                existingSenderBal = account.getBalance();
                                senderFlag = true;
                            } else if (account.getUserName().equals(recipient)) {
                                existingRecipientBal = account.getBalance();
                                recipientFlag = true;
                            }
                        }

                        if (senderFlag && !recipientFlag) {
                            out.println("SEND_MONEY_TO_USER" + "|" + "ERROR: We couldn't find the recipient!");
                            out.flush();
                            break;
                        } else if (!senderFlag && recipientFlag) {
                            out.println("SEND_MONEY_TO_USER" + "|" + "ERROR: We couldn't find the sender!");
                            out.flush();
                            break;
                        }

                        // Update receiver Account
                        for (Account account : onlineUsersMap.keySet()) {
                            if (account.getUserName().equals(recipient)) {
                                Account recipientAccount = account;
                                String recipientIP = onlineUsersMap.get(account);
                                recipientAccount.incrementBalance(amount);
                                onlineUsersMap.remove(account);
                                onlineUsersMap.put(recipientAccount, socket.getRemoteSocketAddress().toString());
                                for (Socket cliSocket : clientSockets) {
                                    if (cliSocket.getRemoteSocketAddress().toString().equals(recipientIP)) {
                                        // Creates a temp PW that will write to recipient client
                                        PrintWriter specificPW = new PrintWriter(cliSocket.getOutputStream(), true);
                                        // Sends message to recipient client that informs of balance update
                                        specificPW.println("BALANCE_UPDATE" + "|" + sender + "|" + amount);
                                        // closes temp PW
                                        specificPW.flush();
                                    }
                                }
                                break;
                            }
                        }

                        // Update Sender account
                        for (Account account : onlineUsersMap.keySet()) {
                            if (account.getUserName().equals(sender)) {
                                Account senderAccount = account;
                                senderAccount.decrementBalance(amount);
                                onlineUsersMap.remove(account);
                                onlineUsersMap.put(senderAccount, socket.getRemoteSocketAddress().toString());
                                break;
                            }

                        }


                        int updatedSenderBal = existingSenderBal - amount;
                        int updatedRecipientBal = existingRecipientBal + amount;

                        boolean senderBalVal = false;
                        boolean recipientBalVal = false;

                        // Need to figure out if this is necessary
                        count = 0;
                        for (Account account : onlineUsersMap.keySet()) {
                            if (account.getUserName().equals(sender)) {
                                if (account.getBalance() == updatedSenderBal) {
                                    senderBalVal = true;
                                    break;
                                } else if (count == onlineUsersMap.size()) {
                                    account.setBalance(existingSenderBal);
                                } else {
                                    count++;
                                }

                            }
                        }

                        // Need to figure out if this is necessary
                        count = 0;
                        for (Account account : onlineUsersMap.keySet()) {
                            if (account.getUserName().equals(recipient)) {
                                if (account.getBalance() == updatedRecipientBal) {
                                    recipientBalVal = true;
                                    break;
                                } else if (count == onlineUsersMap.size()) {
                                    account.setBalance(existingRecipientBal);
                                } else {
                                    count++;
                                }

                            }
                        }

                        if (senderBalVal && recipientBalVal) {
                            out.println("SEND_MONEY_TO_USER" + "|" + "SUCCESS: " + amount + " sent to " + recipient);
                            out.flush();
                        } else {
                            out.println("ERROR: General Error");
                            out.flush();
                        }
                        transactionFlag = false;

                    }


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

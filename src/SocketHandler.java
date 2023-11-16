import java.io.IOException;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Scanner;

public class SocketHandler implements Runnable {

    Socket socket;
    Account userAccount;
    public static ArrayList<Account> onlineUsers = new ArrayList<>();
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
                        out.println("SUCCESSFUL_LOGON");
                        onlineUsers.add(userAccount);
                    } else {
                        out.println("LOGON_FAILED");
                    }

                } else if (msg.contains("REGISTER")) {
                    String[] data = msg.split("\\|");

                    String username = data[1].trim();
                    String password = data[2].trim();

                    if (CsvUtils.checkAccountExists(username)){
                        out.println("Error: Username already exits! Please pick a unique username.");
                    } else {
                        userAccount = new Account(username, password, 1000);

                        CsvUtils.saveToCSV(userAccount);
                        onlineUsers.add(userAccount);
                        out.println("Account Created! Welcome " + username + "!");
                    }


                } else if (msg.contains("VIEW_ONLINE_USERS")) {
                    StringBuilder stringToSend = new StringBuilder();
                    for (Account account : onlineUsers) {
                        stringToSend.append("Username: ").append(account.getUserName()).append("-")
                                .append("  Balance: ").append(account.getBalance()).append("-");
                    }
                    out.println(stringToSend);
                } else if (msg.contains("VIEW_USER_ACCOUNT")) {
                    out.println(userAccount.printInventory());
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
                        for (Account account : onlineUsers) {
                            if (account.getUserName().equals(sender)) {
                                existingSenderBal = account.getBalance();
                                senderFlag = true;
                            } else if (account.getUserName().equals(recipient)) {
                                existingRecipientBal = account.getBalance();
                                recipientFlag = true;
                            }
                        }

                        if (senderFlag && !recipientFlag) {
                            out.println("ERROR: We couldn't find the recipient!");
                            break;
                        } else if (!senderFlag && recipientFlag) {
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
                        for (Account account : onlineUsers) {
                            if (account.getUserName().equals(sender)) {
                                if (account.getBalance() == updatedSenderBal) {
                                    senderBalVal = true;
                                    break;
                                } else if (count == onlineUsers.size()) {
                                    account.setBalance(existingSenderBal);
                                } else {
                                    count++;
                                }

                            }
                        }

                        // Need to figure out if this is necessary
                        count = 0;
                        for (Account account : onlineUsers) {
                            if (account.getUserName().equals(recipient)) {
                                if (account.getBalance() == updatedRecipientBal) {
                                    recipientBalVal = true;
                                    break;
                                } else if (count == onlineUsers.size()) {
                                    account.setBalance(existingRecipientBal);
                                } else {
                                    count++;
                                }

                            }
                        }

                        if (senderBalVal && recipientBalVal) {
                            out.println("SUCCESS: " + amount + " sent to " + recipient);
                        } else {
                            out.println("ERROR: General Error");
                        }
                        transactionFlag = false;

                    }


                } else if (msg.contains("VIEW_MARKETPLACE")){
                    out.println(marketplace.view());
                } else if (msg.contains("BUY_ITEM")) {
                    String[] data = msg.split("\\|");

                    String itemName = data[1].trim();
                    Integer quantity = Integer.parseInt(data[2].trim());

                    MarketItem item = marketplace.findItem(itemName);
                    if (item == null) {
                        out.println("Item not found in the marketplace.");
                        break;
                    }

                    if (quantity <= 0 || item.getQuantity() < quantity) {
                        out.println("Insufficient quantity available.");
                        break;
                    }

                    int totalCost = item.getBuyPrice() * quantity;
                    if (userAccount.getBalance() < totalCost) {
                        out.println("Insufficient balance to complete the transaction.");
                        break;
                    }

                    // Processing the transaction
                    marketplace.removeFromInventory(itemName, quantity); // Remove from marketplace
                    userAccount.addToInventory(itemName, quantity); // Add to user's inventory
                    userAccount.decrementBalance(totalCost); // Deduct the cost from the user's balance

                    out.println("Purchase successful!");


                } else if (msg.contains("SELL_ITEM")) {
                    String[] data = msg.split("\\|");

                    String itemName = data[1].trim();
                    Integer quantityToSell = Integer.parseInt(data[2].trim());

                    Integer itemQuantity = userAccount.getInventory().get(itemName);
                    if (itemQuantity == null || itemQuantity == 0) {
                        out.println("Item not found in your inventory.");
                        break;
                    }

                    if (quantityToSell <= 0 || quantityToSell > itemQuantity) {
                        out.println("Insufficient quantity to sell.");
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

                    out.println("Sale successful! You earned " + totalEarnings);


                } else if (msg.contains("LOG_OFF")) {
                    String[] data = msg.split("\\|");
                    synchronized (this) {
                        for (Account account : onlineUsers){
                            if (account.getUserName().equals(data[1])){
                                CsvUtils.updateUserBalance(userAccount);
                            }
                        }
                        onlineUsers.removeIf(account -> account.getUserName().equals(data[1]));
                    }
                }


            }
        } catch (IOException e) {
            e.printStackTrace();
        }


    }
}

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;
import java.util.concurrent.locks.ReentrantReadWriteLock;

public class CsvUtils {

    private static final ReentrantReadWriteLock rwLock = new ReentrantReadWriteLock();

    // Adjust the paths if necessary
    static String accountsFilepath   = "Resources/accounts.csv";
    static String userInventoryFilepath = "Resources/userInventory.csv";

    public static boolean checkAccountExists(String username) {

        try (BufferedReader reader = new BufferedReader(new FileReader(accountsFilepath))) {
            String line;
            reader.readLine(); // Skip header line
            while ((line = reader.readLine()) != null) {
                String[] parts = line.split(",");
                if (parts.length == 3) {
                    if (parts[0].equals(username)) {
                        return true; // Account name found already
                    }
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        } catch (NumberFormatException e) {
            System.out.println("Error parsing balance");
            e.printStackTrace();
        }
        return false; // Account not found or error occurred
    }

    public static List<MarketItem> loadMarketItems(List<MarketItem> inventory, File marketFile) {

        try (BufferedReader reader = new BufferedReader(new FileReader(marketFile))) {
            String line;
            reader.readLine(); // Skip header line
            while ((line = reader.readLine()) != null) {
                String[] parts = line.split(",");
                if (parts.length == 4) {
                    String item = parts[0];
                    int quantity = Integer.parseInt(parts[1]);
                    int buyPrice = Integer.parseInt(parts[2]);
                    int sellPrice = Integer.parseInt(parts[3]);
                    MarketItem marketItem = new MarketItem(item, quantity, buyPrice, sellPrice);
                    inventory.add(marketItem);
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        } catch (NumberFormatException e) {
            System.out.println("Error parsing integer from market.csv");
            e.printStackTrace();
        }
        return inventory;
    }

    public static void saveAccountToCSV(Account accountToSave, File accountsFile) {
        File csvOutputFile = accountsFile;
        try (PrintWriter pw = new PrintWriter(new FileWriter(csvOutputFile, true))) {
            String processedUsername = escapeSpecialCharacters(accountToSave.getUserName());
            String processedPass = escapeSpecialCharacters(accountToSave.getPassword());
            String accountData = processedUsername + "," + processedPass + "," + accountToSave.getBalance();
            pw.write(accountData + "\n");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static String escapeSpecialCharacters(String data) {
        String escapedData = data.replaceAll("\\R", " ");
        if (data.contains(",") || data.contains("\"") || data.contains("'")) {
            data = data.replace("\"", "\"\"");
            escapedData = "\"" + data + "\"";
        }
        return escapedData;
    }

    public static void updateUserBalance(Account account, File accountsFile) {
        rwLock.writeLock().lock();
        File oldFile = accountsFile; // assuming accountsFilename is a static variable
        File updatedFile = new File("tempAccounts.csv");

        try (
                Scanner scanner = new Scanner(accountsFile);
                FileWriter fw = new FileWriter(updatedFile, true);
                BufferedWriter bw = new BufferedWriter(fw);
                PrintWriter pw = new PrintWriter(bw)
        ) {
            scanner.useDelimiter("[,\\r]"); // Delimiter includes newline

            while (scanner.hasNextLine()) {
                String line = scanner.nextLine();
                String[] line_parts = line.split(",");
                String username = line_parts[0];
                String password = line_parts[1];
                String balance = line_parts[2];

                if (username.equals(account.getUserName())) {
                    pw.println(username + "," + password + "," + account.getBalance().toString());
                } else {
                    pw.println(username + "," + password + "," + balance);
                }
            }

            pw.flush();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
            return; // Early return on exception
        } catch (IOException e) {
            e.printStackTrace();
            return; // Early return on exception
        }

        // Replace old file with updated file
        if (oldFile.delete()) {
            if (!updatedFile.renameTo(accountsFile)) {
                System.err.println("Could not rename updated file to " + accountsFile);
            }
        } else {
            System.err.println("Could not delete old file");
        }
        rwLock.writeLock().unlock();
    }

    // Returns a list of all users on the system
    public static ArrayList<Account> getAccounts(File accountsFile) {
        ArrayList<Account> accounts = new ArrayList<>();

        try (BufferedReader reader = new BufferedReader(new FileReader(accountsFile))) {
            String line;
            reader.readLine(); // Skip header line
            while ((line = reader.readLine()) != null) {
                String[] parts = line.split(",");
                if (parts.length == 3) {
                    Account account = new Account(parts[0], parts[1], Integer.parseInt(parts[2]));

                    InventoryUtils.readInventoryForAccount(account, new File(userInventoryFilepath));

                    accounts.add(account);
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        } catch (NumberFormatException e) {
            System.out.println("Error parsing balance");
            e.printStackTrace();
        }

        return accounts;
    }

    // Saves all changes made to the Marketplace during server uptime to the market.csv
    public static void saveMarketItems(List<MarketItem> inventory, File marketFile) {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(marketFile))) {
            writer.write("item,quantity,buyPrice,sellPrice\n"); // Writing the header

            for (MarketItem item : inventory) {
                writer.write(item.getItem() + "," + item.getQuantity() + ","
                        + item.getBuyPrice() + "," + item.getSellPrice() + "\n");
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}

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

public class CsvUtils {

    private static String tempAccountsFilePath = "Resources/tempAccounts.csv";
    static String accountsFilename = "Resources/accounts.csv";
    static String marketFilename = "Resources/market.csv";

    public static Account getAccount(String username, String password) {

        try (BufferedReader reader = new BufferedReader(new FileReader(accountsFilename))) {
            String line;
            reader.readLine(); // Skip header line
            while ((line = reader.readLine()) != null) {
                String[] parts = line.split(",");
                if (parts.length == 3) {
                    if (parts[0].equals(username) && parts[1].equals(password)) {
                        Account account = new Account(parts[0], parts[1], Integer.parseInt(parts[2]));
                        return account;
                    }
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        } catch (NumberFormatException e) {
            System.out.println("Error parsing balance");
            e.printStackTrace();
        }
        return null; // Account not found or error occurred
    }

    public static boolean checkAccountExists(String username) {

        try (BufferedReader reader = new BufferedReader(new FileReader(accountsFilename))) {
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

    public static List<MarketItem> loadMarketItems(List<MarketItem> inventory) {

        try (BufferedReader reader = new BufferedReader(new FileReader(marketFilename))) {
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

    public static void saveToCSV(Account accountToSave) {
        File csvOutputFile = new File(accountsFilename);
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

    public static void updateUserBalance(Account account) {
        File oldFile = new File(accountsFilename); // assuming accountsFilename is a static variable
        File updatedFile = new File(tempAccountsFilePath);

        try (
                Scanner scanner = new Scanner(new File(accountsFilename));
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
            if (!updatedFile.renameTo(new File(accountsFilename))) {
                System.err.println("Could not rename updated file to " + accountsFilename);
            }
        } else {
            System.err.println("Could not delete old file");
        }
    }


    // Returns a list of all users on the system
    public static ArrayList<Account> getAccounts() {
        ArrayList<Account> accounts = new ArrayList<>();

        try (BufferedReader reader = new BufferedReader(new FileReader(accountsFilename))) {
            String line;
            reader.readLine(); // Skip header line
            while ((line = reader.readLine()) != null) {
                String[] parts = line.split(",");
                if (parts.length == 3) {
                    accounts.add(new Account(parts[0], parts[1], Integer.parseInt(parts[2])));
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

}

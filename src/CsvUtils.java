import java.io.*;
import java.util.List;

public class CsvUtils {

    static String accountsFilename = "C:\\Users\\Aidan\\Documents\\Uni Work\\Year 3\\CS351\\Group Project\\OnlineSystem\\Resources\\accounts.csv"; // Adjust the path if necessary
    static String marketFilename = "C:\\Users\\Aidan\\Documents\\Uni Work\\Year 3\\CS351\\Group Project\\OnlineSystem\\Resources\\market.csv"; // Adjust the path if necessary

    public static Account getAccount(String username, String password) {

        try (BufferedReader reader = new BufferedReader(new FileReader(accountsFilename))) {
            String line;
            reader.readLine(); // Skip header line
            while ((line = reader.readLine()) != null) {
                String[] parts = line.split(",");
                if (parts.length == 3) {
                    if (parts[0].equals(username) && parts[1].equals(password)) {
                        Account account = new Account(parts[0],parts[1],Integer.parseInt(parts[2]));
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
}

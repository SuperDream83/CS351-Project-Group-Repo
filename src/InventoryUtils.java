import java.io.*;
import java.util.*;
import java.util.concurrent.locks.ReentrantReadWriteLock;

public class InventoryUtils {

    private static final ReentrantReadWriteLock rwLock = new ReentrantReadWriteLock();

    public static void updateInventoryInCSV(Account account, File inventoryFile) {
        rwLock.writeLock().lock(); // Allows multiple threads to read the data simultaneously but not write to the file
        try {
            List<String> lines = new ArrayList<>();
            String line;

            // Read the existing file and keep lines that are not related to the current account
            try (BufferedReader br = new BufferedReader(new FileReader(inventoryFile))) {
                while ((line = br.readLine()) != null) {
                    if (!line.startsWith(account.getUserName() + ",")) {
                        lines.add(line);
                    }
                }
            } catch (IOException e) {
                e.printStackTrace();
            }

            // Add the current account's inventory to the list
            for (Map.Entry<String, Integer> entry : account.getInventory().entrySet()) {
                lines.add(account.getUserName() + "," + entry.getKey() + "," + entry.getValue());
            }

            // Write everything back to the CSV file
            try (BufferedWriter bw = new BufferedWriter(new FileWriter(inventoryFile))) {
                for (String l : lines) {
                    bw.write(l);
                    bw.newLine();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        } finally {
            rwLock.writeLock().unlock();
        }

    }

    // Method for reading CSV file and adding each of the Account's items to Inventory.
    public static void readInventoryForAccount(Account account, File inventoryFile) {
        try (BufferedReader br = new BufferedReader(new FileReader(inventoryFile))) {
            String line;
            while ((line = br.readLine()) != null) {
                String[] parts = line.split(",");
                if (parts.length == 3 && parts[0].equals(account.getUserName())) {
                    String itemName = parts[1];
                    int quantity = Integer.parseInt(parts[2]);
                    account.addToInventory(itemName, quantity);
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        } catch (NumberFormatException e) {
            System.out.println("Error parsing quantity");
            e.printStackTrace();
        }
    }
}
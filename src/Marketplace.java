import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

public class Marketplace {

    private File marketFilepath = new File("Resources/market.csv");

    private List<MarketItem> inventory;

    private Scanner scanner = new Scanner(System.in);
    private CsvUtils csvUtils;

    public Marketplace() {
        inventory = new ArrayList<>();
        CsvUtils.loadMarketItems(inventory, marketFilepath);
    }

    public Marketplace(File filepath) {
        inventory = new ArrayList<>();
        CsvUtils.loadMarketItems(inventory, filepath);
    }

    public List<MarketItem> getInventory() {
        return inventory;
    }

    public String view() {
        StringBuilder sb = new StringBuilder();
        sb.append(String.format("%-15s %-10s %-10s %-10s -", "Item", "Quantity", "Buy Price", "Sell Price"));

        for (MarketItem item : inventory) {
            sb.append(String.format("%-15s %-10d %-10d %-10d -",
                    item.getItem(),
                    item.getQuantity(),
                    item.getBuyPrice(),
                    item.getSellPrice()));
        }

        return sb.toString();
    }

    public MarketItem findItem(String itemName) {
        for (MarketItem item : inventory) {
            if (item.getItem().equalsIgnoreCase(itemName)) {
                return item;
            }
        }
        return null; // Item not found
    }

    public boolean addToInventory(String itemName, int quantity) {
        MarketItem item = findItem(itemName);

        synchronized (this) {
            if (item != null) {
                item.setQuantity(item.getQuantity() + quantity);
                return true;
            } else {
                System.out.println("Item not found.");
                return false;
            }
        }
    }

    public boolean removeFromInventory(String itemName, int quantity) {
        MarketItem item = findItem(itemName);

        synchronized (this) {
            if (item != null) {
                if (item.getQuantity() >= quantity) {
                    item.setQuantity(item.getQuantity() - quantity);
                    return true;
                } else {
                    System.out.println("There is no enough quantity to subtract.");
                }
            } else {
                System.out.println("Item not found.");
            }

            return false;
        }
        // Consider removing the item from the list if quantity becomes 0
    }

}

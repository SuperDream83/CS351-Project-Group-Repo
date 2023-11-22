import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

public class Marketplace {

    private List<MarketItem> inventory;

    private Scanner scanner = new Scanner(System.in);
    private CsvUtils csvUtils;

    public Marketplace() {
        inventory = new ArrayList<>();
        CsvUtils.loadMarketItems(inventory);
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

    public void addToInventory(String itemName, int quantity) {
        MarketItem item = findItem(itemName);

        synchronized (this) {
            if (item != null) {
                item.setQuantity(item.getQuantity() + quantity);
            } else {
                System.out.println("Item not found.");
            }
        }
    }

    public void removeFromInventory(String itemName, int quantity) {
        MarketItem item = findItem(itemName);

        synchronized (this) {
            if (item != null) {
                if (item.getQuantity() >= quantity) {
                    item.setQuantity(item.getQuantity() - quantity);
                } else {
                    System.out.println("There is no enough quantity to subtract.");
                }
            } else {
                System.out.println("Item not found.");
            }
        }
        // Consider removing the item from the list if quantity becomes 0
    }

    public void saveInventory() {
        CsvUtils.saveMarketItems(this.inventory);
    }

}

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

public class Account implements Serializable {

    private String userName;
    private String password;
    private Integer balance;
    private Map<String, Integer> inventory; // Inventory HashMap

    public Account(String userName, String password, int balance) {
        this.userName = userName;
        this.password = password;
        this.balance = balance;
        this.inventory = new HashMap<>(); // Init inventory
    }

    public String getUserName() {
        return userName;
    }

    public String getPassword() {
        return password;
    }

    public Integer getBalance() {
        return balance;
    }

    public void setBalance(Integer balance) {
        synchronized (this) {
            this.balance = balance;
        }
    }

    public void incrementBalance(Integer amount) {
        synchronized (this) {
            this.setBalance(balance + amount);
        }
    }

    public boolean decrementBalance(Integer amount) {
        synchronized (this) {
            if (balance >= amount) {
                this.setBalance(balance - amount);
                return true;
            } else {
                System.out.println("Cannot decrement amount, there is no enough balance");
                return false;
            }
        }
    }

    public void transferBalance(Integer amount, Account toAccount) {
        synchronized (this) {
            if (this.decrementBalance(amount)) {
                toAccount.incrementBalance(amount);
            } else {
                System.out.println("Cannot transfer amount");
            }
        }
    }

    // Inventory stuff

    public void addToInventory(String item, int quantity) {
        // Check if the item exists and get the current quantity, default to 0 if not present
        int currentQuantity = this.inventory.getOrDefault(item, 0);
        // Update the inventory with the new total quantity
        this.inventory.put(item, currentQuantity + quantity);
    }

    public void removeFromInventory(String item, int quantity) {
        if (this.inventory.containsKey(item)) {
            int currentQuantity = this.inventory.get(item);
            int newQuantity = Math.max(currentQuantity - quantity, 0);
            if (newQuantity > 0) {
                this.inventory.put(item, newQuantity);
            } else {
                this.inventory.remove(item); // Remove the item if quantity becomes 0
            }
        }
    }

    public int getInventoryQuantity(String item) {
        return this.inventory.getOrDefault(item, 0);
    }

    public Map<String, Integer> getInventory() {
        return new HashMap<>(inventory); // Return a copy to protect internal map
    }

    public String printInventory() {
        StringBuilder inventoryString = new StringBuilder("Your balance: " + getBalance() + "-"
                + "Your inventory: " + "-");

        for (Map.Entry<String, Integer> entry : inventory.entrySet()) {
            if (entry.getValue() > 0) {
                inventoryString.append(entry.getValue()).append(" ").append(entry.getKey()).append("-");
            }
        }

        inventoryString.append("-");

        return inventoryString.toString();
    }

    @Override
    public String toString() {
        return "Account: " +
                "userName='" + userName + '\'' +
                ", password='" + password + '\'' +
                ", balance=" + balance;
    }

}

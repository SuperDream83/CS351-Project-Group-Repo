public class MarketItem {
    private String item;
    private int quantity;
    private int buyPrice;
    private int sellPrice;

    // Constructor
    public MarketItem(String item, int quantity, int buyPrice, int sellPrice) {
        this.item = item;
        this.quantity = quantity;
        this.buyPrice = buyPrice;
        this.sellPrice = sellPrice;
    }

    // Getters and Setters
    public String getItem() {
        return item;
    }

    public void setItem(String item) {
        this.item = item;
    }

    public int getQuantity() {
        return quantity;
    }

    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }

    public int getBuyPrice() {
        return buyPrice;
    }

    public void setBuyPrice(int buyPrice) {
        this.buyPrice = buyPrice;
    }

    public int getSellPrice() {
        return sellPrice;
    }

    public void setSellPrice(int sellPrice) {
        this.sellPrice = sellPrice;
    }

    // toString method
    @Override
    public String toString() {
        return "MarketItem{" +
                "item='" + item + '\'' +
                ", quantity=" + quantity +
                ", buyPrice=" + buyPrice +
                ", sellPrice=" + sellPrice +
                '}';
    }
}

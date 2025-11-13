import java.io.*;
import java.time.LocalDateTime;


public class ItemQuantity implements Serializable {
    @Serial
    private static final long serialVersionUID = 1L;

    // Association references
    private MenuItem menuItem;

    
    private int quantity;
    private double itemTotal;
    private String specialRequests; 
    private LocalDateTime servedTimestamp; 

    
    public ItemQuantity() {}

    
    public ItemQuantity(MenuItem menuItem, int quantity) {
        setMenuItem(menuItem);
        setQuantity(quantity);
        calculateItemTotal();
    }

    
    public MenuItem getMenuItem() { return menuItem; }
    public int getQuantity() { return quantity; }
    public double getItemTotal() { return itemTotal; }
    public String getSpecialRequests() { return specialRequests; }
    public LocalDateTime getServedTimestamp() { return servedTimestamp; }

    
    public void setMenuItem(MenuItem menuItem) {
        if (menuItem == null) {
            throw new IllegalArgumentException("Menu item cannot be null");
        }
        this.menuItem = menuItem;
        calculateItemTotal();
    }

    public void setQuantity(int quantity) {
        if (quantity <= 0) {
            throw new IllegalArgumentException("Quantity must be greater than zero");
        }
        this.quantity = quantity;
        calculateItemTotal();
    }

    
    public void setSpecialRequests(String specialRequests) {
        if (specialRequests != null && !specialRequests.trim().isEmpty()) {
            this.specialRequests = specialRequests.trim();
        } else {
            this.specialRequests = null;
        }
    }

    public void setServedTimestamp(LocalDateTime servedTimestamp) {
        
        this.servedTimestamp = servedTimestamp;
    }


    private void calculateItemTotal() {
        if (menuItem != null) {
            this.itemTotal = menuItem.calculatePriceWithTax() * quantity;
        }
    }

    // Mark as served
    public void markAsServed() {
        this.servedTimestamp = LocalDateTime.now();
    }

    @Override
    public String toString() {
        return String.format("ItemQuantity[%s x%d = %.2f PLN, specialReqs=%s]",
            menuItem != null ? menuItem.getName() : "null", quantity, itemTotal,
            specialRequests != null ? specialRequests : "none");
    }
}

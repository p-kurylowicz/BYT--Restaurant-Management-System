import java.io.*;


public class ItemQuantity implements Serializable {
    @Serial
    private static final long serialVersionUID = 1L;

    // Association references
    private MenuItem menuItem;


    private int quantity;
    private String specialRequests; 

    
    public ItemQuantity() {}


    public ItemQuantity(MenuItem menuItem, int quantity) {
        setMenuItem(menuItem);
        setQuantity(quantity);
    }


    public ItemQuantity(MenuItem menuItem, int quantity, String specialRequests) {
        setMenuItem(menuItem);
        setQuantity(quantity);
        setSpecialRequests(specialRequests);
    }


    public MenuItem getMenuItem() { return menuItem; }
    public int getQuantity() { return quantity; }

    /**
     * Derived attribute - calculates item total from menu item price and quantity
     */
    public double getItemTotal() {
        if (menuItem == null) {
            return 0.0;
        }
        return menuItem.calculatePriceWithTax() * quantity;
    }

    public String getSpecialRequests() { return specialRequests; }


    public void setMenuItem(MenuItem menuItem) {
        if (menuItem == null) {
            throw new IllegalArgumentException("Menu item cannot be null");
        }
        this.menuItem = menuItem;
    }

    public void setQuantity(int quantity) {
        if (quantity <= 0) {
            throw new IllegalArgumentException("Quantity must be greater than zero");
        }
        this.quantity = quantity;
    }


    public void setSpecialRequests(String specialRequests) {
        if (specialRequests == null) {
            this.specialRequests = null;
            return;
        }
        if (specialRequests.trim().isEmpty()) {
            throw new IllegalArgumentException("Special requests cannot be empty or whitespace-only");
        }
        this.specialRequests = specialRequests.trim();
    }

    /**
     * Mark as served - placeholder for future implementation.
     * TODO: Implement served tracking logic
     */
    public void markAsServed() {
        // Placeholder - no timestamp tracking for now
    }

    @Override
    public String toString() {
        return String.format("ItemQuantity[%s x%d = %.2f PLN, specialReqs=%s]",
            menuItem != null ? menuItem.getName() : "null", quantity, getItemTotal(),
            specialRequests != null ? specialRequests : "none");
    }
}

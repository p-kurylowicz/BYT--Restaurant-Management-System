import java.io.*;


public class ItemQuantity implements Serializable {
    @Serial
    private static final long serialVersionUID = 1L;

    private int quantity;
    private String specialRequests; 


    public ItemQuantity() {}


    public ItemQuantity(int quantity) {
        setQuantity(quantity);
    }


    public ItemQuantity(int quantity, String specialRequests) {
        setQuantity(quantity);
        setSpecialRequests(specialRequests);
    }


    public int getQuantity() { return quantity; }


    /**
     * Calculates the total for the request
     * TODO: Implement when OrderRequest association is added
     */
    public double getRequestTotal() {
        // Placeholder
        return 0.0;
    }

    public String getSpecialRequests() { return specialRequests; }


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
        return String.format("ItemQuantity[quantity=%d, total=%.2f PLN, specialReqs=%s]",
            quantity, getRequestTotal(),
            specialRequests != null ? specialRequests : "none");
    }
}

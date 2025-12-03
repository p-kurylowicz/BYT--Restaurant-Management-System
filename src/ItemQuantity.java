import java.io.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Association Class: ItemQuantity connects OrderRequest to MenuItem
 * Represents the many-to-many relationship with attributes (quantity, specialRequests, servedTimestamp)
 * Multiplicity: OrderRequest (0..*) <-> (0..*) MenuItem
 */
public class ItemQuantity implements Serializable {
    @Serial
    private static final long serialVersionUID = 1L;

    // Class extent to track all ItemQuantity instances
    private static List<ItemQuantity> allItemQuantities = new ArrayList<>();

    // Association references (both sides of the association)
    private OrderRequest orderRequest;
    private MenuItem menuItem;

    // Association attributes
    private int quantity;
    // Optional attribute
    private String specialRequests;
    // Optional attribute - timestamp when item was served
    private LocalDateTime servedTimestamp; 


    /**
     * Private constructor - use static factory method create() instead.
     * This ensures proper association management.
     */
    private ItemQuantity(OrderRequest orderRequest, MenuItem menuItem, int quantity, String specialRequests) {
        if (orderRequest == null) {
            throw new IllegalArgumentException("OrderRequest cannot be null - ItemQuantity must connect to an OrderRequest");
        }
        if (menuItem == null) {
            throw new IllegalArgumentException("MenuItem cannot be null - ItemQuantity must connect to a MenuItem");
        }

        this.orderRequest = orderRequest;
        this.menuItem = menuItem;
        setQuantity(quantity);
        setSpecialRequests(specialRequests);
        this.servedTimestamp = null;

        // Add to class extent
        allItemQuantities.add(this);
    }

    /**
     * Static factory method to create ItemQuantity and establish reverse connections.
     * This is the ONLY way to create an ItemQuantity instance.
     *
     * @param orderRequest The OrderRequest this item belongs to
     * @param menuItem The MenuItem being ordered
     * @param quantity The quantity ordered (must be > 0)
     * @param specialRequests Optional special requests (can be null)
     * @return The created ItemQuantity instance
     * @throws IllegalArgumentException if orderRequest or menuItem is null, or quantity <= 0
     */
    public static ItemQuantity create(OrderRequest orderRequest, MenuItem menuItem, int quantity, String specialRequests) {
        // Create the ItemQuantity instance
        ItemQuantity itemQuantity = new ItemQuantity(orderRequest, menuItem, quantity, specialRequests);

        // Establish reverse connections on both sides
        orderRequest.addItemQuantity(itemQuantity);
        menuItem.addItemQuantity(itemQuantity);

        return itemQuantity;
    }

    /**
     * Convenience factory method without special requests.
     */
    public static ItemQuantity create(OrderRequest orderRequest, MenuItem menuItem, int quantity) {
        return create(orderRequest, menuItem, quantity, null);
    }


    public OrderRequest getOrderRequest() { return orderRequest; }
    public MenuItem getMenuItem() { return menuItem; }
    public int getQuantity() { return quantity; }
    public String getSpecialRequests() { return specialRequests; }
    public LocalDateTime getServedTimestamp() { return servedTimestamp; }

    public double getRequestTotal() {
        if (menuItem == null) return 0.0;
        return menuItem.calculatePriceWithTax() * quantity;
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

    public void markAsServed() {
        this.servedTimestamp = LocalDateTime.now();
    }

    public void delete() {
        if (orderRequest != null) {
            orderRequest.removeItemQuantity(this);
        }
        if (menuItem != null) {
            menuItem.removeItemQuantity(this);
        }
        allItemQuantities.remove(this);

        this.orderRequest = null;
        this.menuItem = null;
    }

    public static List<ItemQuantity> getAllItemQuantities() {
        return Collections.unmodifiableList(allItemQuantities);
    }

    public static void clearExtent() {
        allItemQuantities.clear();
    }

    @Override
    public String toString() {
        String itemName = menuItem != null ? menuItem.getName() : "Unknown";
        String reqId = orderRequest != null ? orderRequest.getRequestId().substring(0, 8) : "Unknown";
        return String.format("ItemQuantity[item=%s, qty=%d, total=%.2f PLN, request=%s..., served=%s]",
            itemName, quantity, getRequestTotal(), reqId,
            servedTimestamp != null ? "Yes" : "No");
    }
}

import java.io.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Association Class: ItemQuantity connects OrderRequest to MenuItem
 * Represents the many-to-many relationship with attributes (quantity, specialRequests, servedTimestamp)
 * Multiplicity: OrderRequest (0..*) <-> (0..*) MenuItem
 *
 * NOTE: This is a REGULAR association class (NOT {Bag}).
 * Each OrderRequest-MenuItem pair can have only ONE ItemQuantity.
 * Attempting to create a duplicate will throw IllegalStateException.
 */
public class ItemQuantity implements Serializable {
    @Serial
    private static final long serialVersionUID = 1L;


    private static final List<ItemQuantity> allItemQuantities = new ArrayList<>();


    private OrderRequest orderRequest;
    private MenuItem menuItem;
    private int quantity;
    private String specialRequests;
    private LocalDateTime servedTimestamp; 



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

        addItemQuantityToExtent();
    }



    public static ItemQuantity create(OrderRequest orderRequest, MenuItem menuItem, int quantity, String specialRequests) {
        if (orderRequest == null) {
            throw new IllegalArgumentException("OrderRequest cannot be null");
        }
        if (menuItem == null) {
            throw new IllegalArgumentException("MenuItem cannot be null");
        }

        // Check for existing connection
        for (ItemQuantity existing : allItemQuantities) {
            if (existing.getOrderRequest() == orderRequest && existing.getMenuItem() == menuItem) {
                throw new IllegalStateException(
                    "ItemQuantity already exists for this OrderRequest and MenuItem combination. " +
                    "To modify quantity, update the existing ItemQuantity instead of creating a new one."
                );
            }
        }

        ItemQuantity itemQuantity = new ItemQuantity(orderRequest, menuItem, quantity, specialRequests);

        // Establish reverse connections on both sides
        orderRequest.addItemQuantity(itemQuantity);
        menuItem.addItemQuantity(itemQuantity);

        return itemQuantity;
    }


    public static ItemQuantity create(OrderRequest orderRequest, MenuItem menuItem, int quantity) {
        return create(orderRequest, menuItem, quantity, null);
    }

    public void delete() {
        if (orderRequest != null) {
            orderRequest.removeItemQuantity(this);
        }
        if (menuItem != null) {
            menuItem.removeItemQuantity(this);
        }
        deleteItemQuantityFromExtent();

        this.orderRequest = null;
        this.menuItem = null;
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


    private void addItemQuantityToExtent() {
        if (!allItemQuantities.contains(this)) {
            allItemQuantities.add(this);
        }
    }

    private void deleteItemQuantityFromExtent() {
        if (!allItemQuantities.remove(this)) {
            throw new IllegalStateException("ItemQuantity not found in extent - cannot delete");
        }
    }

    public static List<ItemQuantity> getAllItemQuantitiesFromExtent() {
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

import java.io.Serial;
import java.io.Serializable;
import java.time.LocalTime;

/**
 * Component class for Takeaway orders.
 * Cannot exist without an Order (composition constraint).
 * Implements ITakeaway interface to define contract.
 */
public class Takeaway implements Serializable, ITakeaway {
    @Serial
    private static final long serialVersionUID = 1L;

    // Reverse reference - component cannot exist without parent
    private final Order order;

    private LocalTime collectionTime;
    private boolean wasPickedUp;

    // Package-private constructor - only Order can create Takeaway
    Takeaway(Order order) {
        if (order == null) {
            throw new IllegalArgumentException(
                "Order cannot be null - Takeaway cannot exist without Order");
        }
        this.order = order;
        this.wasPickedUp = false;
    }

    Takeaway(Order order, LocalTime collectionTime) {
        this(order);
        setCollectionTime(collectionTime);
    }

    // Getter for parent Order
    public Order getOrder() {
        return order;
    }

    public LocalTime getCollectionTime() { return collectionTime; }
    public boolean getWasPickedUp() { return wasPickedUp; }

    public void setCollectionTime(LocalTime collectionTime) {
        if (collectionTime == null) {
            throw new IllegalArgumentException("Collection time cannot be null");
        }
        this.collectionTime = collectionTime;
    }

    public void setWasPickedUp(boolean wasPickedUp) {
        this.wasPickedUp = wasPickedUp;
    }

    public void markAsPickedUp() {
        this.wasPickedUp = true;
    }

    /**
     * Confirms the takeaway order.
     * Handles takeaway-specific confirmation logic.
     */
    public void confirmTakeawayOrder() {
        if (order.getStatus() != OrderStatus.ACTIVE) {
            throw new IllegalStateException(
                "Can only confirm active takeaway orders");
        }
        // Confirmation logic: verify payment, notify kitchen, send confirmation
        System.out.println("Takeaway order confirmed for collection at: " + collectionTime);
    }

    @Override
    public String toString() {
        return String.format("Takeaway[collectionTime=%s, wasPickedUp=%s]",
                collectionTime, wasPickedUp);
    }
}

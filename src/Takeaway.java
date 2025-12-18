import java.io.Serial;
import java.io.Serializable;
import java.time.LocalTime;


public class Takeaway implements Serializable, ITakeaway {
    @Serial
    private static final long serialVersionUID = 1L;

    
    private final Order order;

    private LocalTime collectionTime;
    private boolean wasPickedUp;

    //only Order can create Takeaway
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

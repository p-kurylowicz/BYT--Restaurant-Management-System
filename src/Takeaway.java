import java.io.Serial;
import java.time.LocalTime;

public class Takeaway extends Order {
    @Serial
    private static final long serialVersionUID = 1L;


    private LocalTime collectionTime;
    private boolean wasPickedUp;

    public Takeaway(Customer customer) {
        super(customer);
        this.wasPickedUp = false;
    }

    public Takeaway(Customer customer, LocalTime collectionTime) {
        super(customer);
        setCollectionTime(collectionTime);
        this.wasPickedUp = false;
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

    // Mark as picked up
    public void markAsPickedUp() {
        if (getStatus() != OrderStatus.COMPLETED) {
            throw new IllegalStateException("Order must be completed before marking as picked up");
        }
        this.wasPickedUp = true;
    }

    @Override
    public String toString() {
        return String.format("Takeaway[collectionTime=%s, wasPickedUp=%s, %s]",
            collectionTime, wasPickedUp, super.toString());
    }
}

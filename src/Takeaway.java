import java.io.Serial;
import java.time.LocalTime;

public class Takeaway implements OrderRole {
    @Serial
    private static final long serialVersionUID = 1L;

    private LocalTime collectionTime;
    private boolean wasPickedUp;

    public Takeaway() {
        this.wasPickedUp = false;
    }

    public Takeaway(LocalTime collectionTime) {
        setCollectionTime(collectionTime);
        this.wasPickedUp = false;
    }

    @Override
    public OrderKind kind() {
        return OrderKind.TAKEAWAY;
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

    @Override
    public String toString() {
        return String.format("Takeaway[collectionTime=%s, wasPickedUp=%s]",
                collectionTime, wasPickedUp);
    }
}

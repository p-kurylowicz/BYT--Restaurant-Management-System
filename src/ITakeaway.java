import java.time.LocalTime;

/**
 * Interface defining the contract for Takeaway orders.
 * Similar to IStudent in the guide's Person/Teacher/Student example.
 */
public interface ITakeaway {
    LocalTime getCollectionTime();
    void setCollectionTime(LocalTime collectionTime);

    boolean getWasPickedUp();
    void setWasPickedUp(boolean wasPickedUp);
    void markAsPickedUp();

    void confirmTakeawayOrder();

    Order getOrder();
}

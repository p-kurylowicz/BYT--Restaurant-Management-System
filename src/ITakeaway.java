import java.time.LocalTime;


public interface ITakeaway {
    LocalTime getCollectionTime();
    void setCollectionTime(LocalTime collectionTime);

    boolean getWasPickedUp();
    void setWasPickedUp(boolean wasPickedUp);
    void markAsPickedUp();

    void confirmTakeawayOrder();

    Order getOrder();
}

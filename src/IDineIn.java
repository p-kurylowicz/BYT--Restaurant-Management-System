import java.time.LocalDateTime;
import java.util.List;


public interface IDineIn {
    LocalDateTime getServingStartTime();
    void setServingStartTime(LocalDateTime servingStartTime);

    Reservation getReservation();
    void setReservation(Reservation reservation);

    List<Table> getTables();
    void addTable(Table table);
    void releaseTables();

    Order getOrder();
}

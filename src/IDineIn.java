import java.time.LocalDateTime;
import java.util.List;

/**
 * Interface defining the contract for DineIn orders.
 * Similar to ITeacher in the guide's Person/Teacher/Student example.
 */
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

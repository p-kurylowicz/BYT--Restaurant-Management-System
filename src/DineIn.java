import java.io.Serial;
import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;


public class DineIn implements Serializable, IDineIn {
    @Serial
    private static final long serialVersionUID = 1L;

   
    private final Order order;

    private LocalDateTime servingStartTime;
    private final List<Table> tables;
    private Reservation reservation;

    //only Order can create DineIn
    DineIn(Order order) {
        if (order == null) {
            throw new IllegalArgumentException(
                "Order cannot be null - DineIn cannot exist without Order");
        }
        this.order = order;
        this.tables = new ArrayList<>();
        this.servingStartTime = LocalDateTime.now();
        this.reservation = null;
    }

    DineIn(Order order, Reservation reservation) {
        this(order);
        setReservation(reservation);
    }


    public Order getOrder() {
        return order;
    }

    public LocalDateTime getServingStartTime() { return servingStartTime; }
    public Reservation getReservation() { return reservation; }

    public List<Table> getTables() {
        return Collections.unmodifiableList(tables);
    }

    public void setServingStartTime(LocalDateTime servingStartTime) {
        if (servingStartTime == null) {
            throw new IllegalArgumentException("Serving start time cannot be null");
        }
        this.servingStartTime = servingStartTime;
    }

    public void setReservation(Reservation reservation) {
        this.reservation = reservation;
    }

    public void addTable(Table table) {
        if (table == null) {
            throw new IllegalArgumentException("Table cannot be null");
        }
        if (!tables.contains(table)) {
            tables.add(table);
            table.changeStatus(TableStatus.OCCUPIED);
        }
    }

    public void releaseTables() {
        for (Table table : tables) {
            table.changeStatus(TableStatus.AVAILABLE);
        }
        tables.clear();
    }

    @Override
    public String toString() {
        return String.format("DineIn[servingStartTime=%s, tables=%d, hasReservation=%s]",
                servingStartTime, tables.size(), reservation != null);
    }
}

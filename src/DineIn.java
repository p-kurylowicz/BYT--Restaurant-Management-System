import java.io.Serial;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class DineIn extends Order {
    @Serial
    private static final long serialVersionUID = 1L;


    private LocalDateTime servingStartTime;

    
    private List<Table> tables;
    private Reservation reservation;

    public DineIn(Customer customer) {
        super(customer);
        this.tables = new ArrayList<>();
        this.servingStartTime = LocalDateTime.now();
        this.reservation = null;
    }

    public DineIn(Customer customer, Reservation reservation) {
        super(customer);
        this.tables = new ArrayList<>();
        this.servingStartTime = LocalDateTime.now();
        setReservation(reservation);
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
    }

    @Override
    public void completeOrder() {
        super.completeOrder();
        releaseTables();
    }

    @Override
    public String toString() {
        return String.format("DineIn[servingStartTime=%s, tables=%d, hasReservation=%s, %s]",
            servingStartTime, tables.size(), reservation != null, super.toString());
    }
}

import java.io.*;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.*;

public class Reservation implements Serializable {
    @Serial
    private static final long serialVersionUID = 1L;

    public static final int CANCELLATION_WINDOW_HOURS = 4;

    private static List<Reservation> allReservations = new ArrayList<>();

    private LocalDate date;
    private LocalTime time;
    private int size;
    private ReservationStatus status;
    private final Set<String> specialRequests;
    
    // Aggregation: Reservation -> Table (0..1)
    private Table assignedTable;

    // Qualified Association: Reservation -> Customer (1, mandatory)
    private Customer customer;

    // Package-private no-arg constructor for serialization only
    Reservation() {
        this.specialRequests = new HashSet<>();
    }

    public Reservation(LocalDate date, LocalTime time, int size, Customer customer) {
        this.specialRequests = new HashSet<>();
        if (customer == null) {
            throw new IllegalArgumentException("Customer cannot be null - Reservation must have a Customer (mandatory 1)");
        }
        setDate(date);
        setTime(time);
        setSize(size);
        this.customer = customer;
        this.status = ReservationStatus.PENDING;
        addReservation(this);

        // Establish reverse connection in Customer's qualified map
        customer.addReservation(this);
    }

    public Customer getCustomer() { return customer; }

    public void setCustomer(Customer newCustomer) {
        if (newCustomer == null) {
            throw new IllegalArgumentException("Customer cannot be null - Reservation must have a Customer (mandatory 1)");
        }

        if (this.customer != newCustomer) {
            if (this.customer != null) {
                this.customer.removeReservation(this);
            }
            this.customer = newCustomer;
            newCustomer.addReservation(this);
        }
    }

    public LocalDate getDate() { return date; }
    public LocalTime getTime() { return time; }
    public int getSize() { return size; }
    public ReservationStatus getStatus() { return status; }
    public Table getAssignedTable() { return assignedTable; }

    public Set<String> getSpecialRequests() {
        return Collections.unmodifiableSet(specialRequests);
    }

    public void setDate(LocalDate date) {
        if (date == null) {
            throw new IllegalArgumentException("Date cannot be null");
        }
        if (date.isBefore(LocalDate.now())) {
            throw new IllegalArgumentException("Reservation date cannot be in the past");
        }

        // Handle qualifier update in the Customer's map
        if (this.customer != null && this.date != null && !date.equals(this.date)) {
            LocalDateTime oldKey = LocalDateTime.of(this.date, this.time);
            LocalDateTime newKey = LocalDateTime.of(date, this.time);
            this.customer.updateReservationKey(this, oldKey, newKey);
        }

        this.date = date;
    }

    public void setTime(LocalTime time) {
        if (time == null) {
            throw new IllegalArgumentException("Time cannot be null");
        }

        // Handle qualifier update in the Customer's map
        if (this.customer != null && this.time != null && !time.equals(this.time)) {
            LocalDateTime oldKey = LocalDateTime.of(this.date, this.time);
            LocalDateTime newKey = LocalDateTime.of(this.date, time);
            this.customer.updateReservationKey(this, oldKey, newKey);
        }

        this.time = time;
    }

    public void setSize(int size) {
        if (size <= 0) {
            throw new IllegalArgumentException("Party size must be greater than zero");
        }
        this.size = size;
    }

    public void assignTable(Table table) {
        if (table == null) {
            throw new IllegalArgumentException("Table cannot be null");
        }
        
        if (table.getCapacity() < this.size) {
            throw new IllegalArgumentException(
                String.format("Table capacity (%d) is insufficient for party size (%d)", 
                    table.getCapacity(), this.size));
        }
        
        if (this.assignedTable != null && this.assignedTable != table) {
            Table oldTable = this.assignedTable;
            this.assignedTable = null;
            oldTable.removeReservation(this);
        }
        
        this.assignedTable = table;
        
        // Reverse connection: add this reservation to the table
        if (!table.getReservations().contains(this)) {
            table.addReservation(this);
        }
    }

    public void removeTable() {
        if (this.assignedTable != null) {
            Table table = this.assignedTable;
            this.assignedTable = null;
            
            // Reverse connection: remove this reservation from the table
            if (table.getReservations().contains(this)) {
                table.removeReservation(this);
            }
        }
    }

    public void addSpecialRequest(String request) {
        if (request == null || request.trim().isEmpty()) {
            throw new IllegalArgumentException("Special request cannot be null or empty");
        }
        specialRequests.add(request.trim());
    }

    public void removeSpecialRequest(String request) {
        specialRequests.remove(request);
    }

    public void clearSpecialRequests() {
        specialRequests.clear();
    }

    public boolean canBeCancelled() {
        LocalDateTime reservationDateTime = LocalDateTime.of(date, time);
        LocalDateTime now = LocalDateTime.now();
        long minutesUntilReservation = ChronoUnit.MINUTES.between(now, reservationDateTime);
        long requiredMinutes = CANCELLATION_WINDOW_HOURS * 60;
        return minutesUntilReservation >= requiredMinutes;
    }

    public void cancelReservation() {
        if (!canBeCancelled()) {
            throw new IllegalStateException(
                String.format("Cannot cancel reservation within %d hours of scheduled time", 
                    CANCELLATION_WINDOW_HOURS));
        }
        
        // Remove table assignment when cancelling
        if (this.assignedTable != null) {
            removeTable();
        }
        
        this.status = ReservationStatus.CANCELLED;
    }

    public void confirmReservation() {
        if (this.status != ReservationStatus.PENDING) {
            throw new IllegalStateException("Only pending reservations can be confirmed");
        }
        this.status = ReservationStatus.CONFIRMED;
    }

    public void changeReservationStatus(ReservationStatus newStatus) {
        if (newStatus == null) {
            throw new IllegalArgumentException("Status cannot be null");
        }
        this.status = newStatus;
    }

    private static void addReservation(Reservation reservation) {
        if (reservation == null) {
            throw new IllegalArgumentException("Reservation cannot be null");
        }
        allReservations.add(reservation);
    }

    public static List<Reservation> getAllReservations() {
        return Collections.unmodifiableList(allReservations);
    }

    public static void clearExtent() {
        allReservations.clear();
    }

    public static void saveExtent(String filename) throws IOException {
        String filepath = PersistenceConfig.getDataFilePath(filename);
        try (ObjectOutputStream out = new ObjectOutputStream(new FileOutputStream(filepath))) {
            out.writeObject(allReservations);
        }
    }

    @SuppressWarnings("unchecked")
    public static boolean loadExtent(String filename) {
        String filepath = PersistenceConfig.getDataFilePath(filename);
        try (ObjectInputStream in = new ObjectInputStream(new FileInputStream(filepath))) {
            allReservations = (List<Reservation>) in.readObject();
            return true;
        } catch (IOException | ClassNotFoundException e) {
            allReservations.clear();
            return false;
        }
    }

    @Override
    public String toString() {
        return String.format("Reservation[date=%s, time=%s, size=%d, status=%s, table=%s, specialRequests=%d]",
            date, time, size, status, 
            assignedTable != null ? assignedTable.getNumber() : "none", 
            specialRequests.size());
    }
}

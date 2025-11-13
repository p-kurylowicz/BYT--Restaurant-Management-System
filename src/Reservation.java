import java.io.*;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class Reservation implements Serializable {
    @Serial
    private static final long serialVersionUID = 1L;

    
    public static final int CANCELLATION_WINDOW_HOURS = 4;

    
    private static List<Reservation> allReservations = new ArrayList<>();

    
    private LocalDate date;
    private LocalTime time;
    private int size;
    private ReservationStatus status;
    private String specialRequests; 

    
    private Customer customer;
    private List<Table> tables;

    
    public Reservation() {
        this.tables = new ArrayList<>();
    }

    
    public Reservation(Customer customer, LocalDate date, LocalTime time, int size) {
        this.tables = new ArrayList<>();
        setCustomer(customer);
        setDate(date);
        setTime(time);
        setSize(size);
        this.status = ReservationStatus.PENDING;
        addReservation(this);
    }

    
    public Customer getCustomer() { return customer; }
    public LocalDate getDate() { return date; }
    public LocalTime getTime() { return time; }
    public int getSize() { return size; }
    public ReservationStatus getStatus() { return status; }
    public String getSpecialRequests() { return specialRequests; }

    public List<Table> getTables() {
        return Collections.unmodifiableList(tables);
    }

    
    public void setCustomer(Customer customer) {
        if (customer == null) {
            throw new IllegalArgumentException("Customer cannot be null");
        }
        this.customer = customer;
    }

    public void setDate(LocalDate date) {
        if (date == null) {
            throw new IllegalArgumentException("Date cannot be null");
        }
        if (date.isBefore(LocalDate.now())) {
            throw new IllegalArgumentException("Reservation date cannot be in the past");
        }
        this.date = date;
    }

    public void setTime(LocalTime time) {
        if (time == null) {
            throw new IllegalArgumentException("Time cannot be null");
        }
        this.time = time;
    }

    public void setSize(int size) {
        if (size <= 0) {
            throw new IllegalArgumentException("Party size must be greater than zero");
        }
        this.size = size;
    }

    
    public void setSpecialRequests(String specialRequests) {
        
        if (specialRequests != null && !specialRequests.trim().isEmpty()) {
            this.specialRequests = specialRequests.trim();
        } else {
            this.specialRequests = null;
        }
    }


    public boolean canBeCancelled() {
        LocalDateTime reservationDateTime = LocalDateTime.of(date, time);
        LocalDateTime now = LocalDateTime.now();
        // Use minutes for more accurate comparison
        long minutesUntilReservation = ChronoUnit.MINUTES.between(now, reservationDateTime);
        long requiredMinutes = CANCELLATION_WINDOW_HOURS * 60;
        return minutesUntilReservation >= requiredMinutes;
    }

    // Cancel reservation
    public void cancelReservation() {
        if (!canBeCancelled()) {
            throw new IllegalStateException(
                String.format("Cannot cancel reservation within %d hours of scheduled time", CANCELLATION_WINDOW_HOURS));
        }
        this.status = ReservationStatus.CANCELLED;
        // Free up tables
        for (Table table : tables) {
            table.changeStatus(TableStatus.AVAILABLE);
        }
    }

    // Confirm reservation
    public void confirmReservation() {
        if (this.status != ReservationStatus.PENDING) {
            throw new IllegalStateException("Only pending reservations can be confirmed");
        }
        this.status = ReservationStatus.CONFIRMED;
    }

    // Change reservation status
    public void changeReservationStatus(ReservationStatus newStatus) {
        if (newStatus == null) {
            throw new IllegalArgumentException("Status cannot be null");
        }
        this.status = newStatus;
    }


    public void addTable(Table table) {
        if (table == null) {
            throw new IllegalArgumentException("Table cannot be null");
        }
        if (!tables.contains(table)) {
            tables.add(table);
        }
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
        return String.format("Reservation[customer=%s, date=%s, time=%s, size=%d, status=%s, tables=%d]",
            customer.getName(), date, time, size, status, tables.size());
    }
}

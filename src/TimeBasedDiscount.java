import java.io.Serial;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;


public class TimeBasedDiscount extends Discount {
    @Serial
    private static final long serialVersionUID = 1L;


    private static List<TimeBasedDiscount> allTimeBasedDiscounts = new ArrayList<>();


    private LocalDate date;
    private LocalTime time;


    protected TimeBasedDiscount() {
        super();
    }

    public TimeBasedDiscount(String code, LocalDate date, LocalTime time) {
        super(code);
        setDate(date);
        setTime(time);
        addToExtent(this);
    }


    private static void addToExtent(TimeBasedDiscount discount) {
        if (discount != null) {
            allTimeBasedDiscounts.add(discount);
        }
    }

    public static List<TimeBasedDiscount> getAllTimeBasedDiscounts() {
        return Collections.unmodifiableList(allTimeBasedDiscounts);
    }


    public LocalDate getDate() {
        return date;
    }

    public void setDate(LocalDate date) {
        if (date == null) {
            throw new IllegalArgumentException("Date cannot be null");
        }
        this.date = date;
    }

    public LocalTime getTime() {
        return time;
    }

    public void setTime(LocalTime time) {
        if (time == null) {
            throw new IllegalArgumentException("Time cannot be null");
        }
        this.time = time;
    }

    // Class extent persistence
    public static void clearExtent() {
        allTimeBasedDiscounts.clear();
    }

    public static void saveExtent(String filename) throws java.io.IOException {
        String filepath = PersistenceConfig.getDataFilePath(filename);
        try (java.io.ObjectOutputStream out = new java.io.ObjectOutputStream(new java.io.FileOutputStream(filepath))) {
            out.writeObject(allTimeBasedDiscounts);
        }
    }

    @SuppressWarnings("unchecked")
    public static boolean loadExtent(String filename) {
        String filepath = PersistenceConfig.getDataFilePath(filename);
        try (java.io.ObjectInputStream in = new java.io.ObjectInputStream(new java.io.FileInputStream(filepath))) {
            allTimeBasedDiscounts = (List<TimeBasedDiscount>) in.readObject();
            return true;
        } catch (java.io.IOException | ClassNotFoundException e) {
            allTimeBasedDiscounts.clear();
            return false;
        }
    }

    @Override
    public boolean validateDiscount(Order order) {
        // Placeholder implementation
        return true;
    }

    @Override
    public String toString() {
        return String.format("TimeBasedDiscount[code=%s, date=%s, time=%s]",
                           getCode(), date, time);
    }
}

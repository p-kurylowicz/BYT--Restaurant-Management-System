import java.io.Serial;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Discount valid only at specific dates and times.
 */
public class TimeBasedDiscount extends Discount {
    @Serial
    private static final long serialVersionUID = 1L;

    // Class extent
    private static List<TimeBasedDiscount> allTimeBasedDiscounts = new ArrayList<>();

    // Attributes
    private LocalDate date;
    private LocalTime time;

    // Constructors
    protected TimeBasedDiscount() {
        super();
    }

    public TimeBasedDiscount(String code, LocalDate date, LocalTime time) {
        super(code);
        setDate(date);
        setTime(time);
        addToExtent(this);
    }

    // Class extent management
    private static void addToExtent(TimeBasedDiscount discount) {
        if (discount != null) {
            allTimeBasedDiscounts.add(discount);
        }
    }

    public static List<TimeBasedDiscount> getAllTimeBasedDiscounts() {
        return Collections.unmodifiableList(allTimeBasedDiscounts);
    }

    // Getters and setters
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

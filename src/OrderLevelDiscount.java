import java.io.Serial;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.*;

public class OrderLevelDiscount extends Discount {
    @Serial
    private static final long serialVersionUID = 1L;

    private static List<OrderLevelDiscount> allOrderLevelDiscounts = new ArrayList<>();

    private double discountPercentage;

    protected OrderLevelDiscount() {
        super();
    }

    public OrderLevelDiscount(String code, double discountPercentage) {
        super(code);
        setDiscountPercentage(discountPercentage);
        addToExtent(this);
    }

    public OrderLevelDiscount(String code, LocalDate date, LocalTime time, double discountPercentage) {
        super(code);
        setTimeBasedComponent(date, time);
        setDiscountPercentage(discountPercentage);
        addToExtent(this);
    }

    public OrderLevelDiscount(String code, double minAmount, int minQuantity, double discountPercentage) {
        super(code);
        setVolumeComponent(minAmount, minQuantity);
        setDiscountPercentage(discountPercentage);
        addToExtent(this);
    }

    public OrderLevelDiscount(String code, LocalDate date, LocalTime time,
                             double minAmount, int minQuantity, double discountPercentage) {
        super(code);
        setTimeBasedComponent(date, time);
        setVolumeComponent(minAmount, minQuantity);
        setDiscountPercentage(discountPercentage);
        addToExtent(this);
    }

    @Override
    public boolean isOrderLevel() {
        return true;
    }

    @Override
    public boolean isItemLevel() {
        return false;
    }

    @Override
    public double getDiscountPercentage() {
        return discountPercentage;
    }

    @Override
    public void setDiscountPercentage(double percentage) {
        if (percentage < 0 || percentage > 100) {
            throw new IllegalArgumentException("Discount percentage must be between 0 and 100");
        }
        this.discountPercentage = percentage;
    }

    @Override
    public Set<String> getApplicableItems() {
        throw new UnsupportedOperationException("Order-level discounts do not have applicable items");
    }

    @Override
    public void addApplicableItem(String item) {
        throw new UnsupportedOperationException("Order-level discounts do not have applicable items");
    }

    @Override
    public void removeApplicableItem(String item) {
        throw new UnsupportedOperationException("Order-level discounts do not have applicable items");
    }

    private static void addToExtent(OrderLevelDiscount discount) {
        if (discount != null) {
            allOrderLevelDiscounts.add(discount);
        }
    }

    public static List<OrderLevelDiscount> getAllOrderLevelDiscounts() {
        return Collections.unmodifiableList(allOrderLevelDiscounts);
    }

    public static void clearExtent() {
        allOrderLevelDiscounts.clear();
    }

    public static void saveExtent(String filename) throws java.io.IOException {
        String filepath = PersistenceConfig.getDataFilePath(filename);
        try (java.io.ObjectOutputStream out = new java.io.ObjectOutputStream(
                new java.io.FileOutputStream(filepath))) {
            out.writeObject(allOrderLevelDiscounts);
        }
    }

    @SuppressWarnings("unchecked")
    public static boolean loadExtent(String filename) {
        String filepath = PersistenceConfig.getDataFilePath(filename);
        try (java.io.ObjectInputStream in = new java.io.ObjectInputStream(
                new java.io.FileInputStream(filepath))) {
            allOrderLevelDiscounts = (List<OrderLevelDiscount>) in.readObject();
            return true;
        } catch (java.io.IOException | ClassNotFoundException e) {
            allOrderLevelDiscounts.clear();
            return false;
        }
    }

    @Override
    public String toString() {
        return String.format("OrderLevelDiscount[code=%s, percentage=%.2f%%, timeBased=%b, volumeBased=%b]",
            getCode(), discountPercentage, isTimeBased(), isVolumeBased());
    }
}

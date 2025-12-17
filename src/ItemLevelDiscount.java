import java.io.Serial;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.*;

public class ItemLevelDiscount extends Discount {
    @Serial
    private static final long serialVersionUID = 1L;

    private static List<ItemLevelDiscount> allItemLevelDiscounts = new ArrayList<>();

    private final Set<String> applicableItems;

    protected ItemLevelDiscount() {
        super();
        this.applicableItems = new HashSet<>();
    }

    public ItemLevelDiscount(String code, Set<String> applicableItems) {
        super(code);
        this.applicableItems = new HashSet<>();
        if (applicableItems != null) {
            for (String item : applicableItems) {
                addApplicableItem(item);
            }
        }
        addToExtent(this);
    }

    public ItemLevelDiscount(String code, LocalDate date, LocalTime time, Set<String> applicableItems) {
        super(code);
        setTimeBasedComponent(date, time);
        this.applicableItems = new HashSet<>();
        if (applicableItems != null) {
            for (String item : applicableItems) {
                addApplicableItem(item);
            }
        }
        addToExtent(this);
    }

    public ItemLevelDiscount(String code, double minAmount, int minQuantity, Set<String> applicableItems) {
        super(code);
        setVolumeComponent(minAmount, minQuantity);
        this.applicableItems = new HashSet<>();
        if (applicableItems != null) {
            for (String item : applicableItems) {
                addApplicableItem(item);
            }
        }
        addToExtent(this);
    }

    public ItemLevelDiscount(String code, LocalDate date, LocalTime time,
                            double minAmount, int minQuantity, Set<String> applicableItems) {
        super(code);
        setTimeBasedComponent(date, time);
        setVolumeComponent(minAmount, minQuantity);
        this.applicableItems = new HashSet<>();
        if (applicableItems != null) {
            for (String item : applicableItems) {
                addApplicableItem(item);
            }
        }
        addToExtent(this);
    }

    @Override
    public boolean isOrderLevel() {
        return false;
    }

    @Override
    public boolean isItemLevel() {
        return true;
    }

    @Override
    public double getDiscountPercentage() {
        throw new UnsupportedOperationException("Item-level discounts do not have a discount percentage");
    }

    @Override
    public void setDiscountPercentage(double percentage) {
        throw new UnsupportedOperationException("Item-level discounts do not have a discount percentage");
    }

    @Override
    public Set<String> getApplicableItems() {
        return Collections.unmodifiableSet(applicableItems);
    }

    @Override
    public void addApplicableItem(String item) {
        if (item == null || item.trim().isEmpty()) {
            throw new IllegalArgumentException("Applicable item cannot be null or empty");
        }
        applicableItems.add(item.trim());
    }

    @Override
    public void removeApplicableItem(String item) {
        applicableItems.remove(item);
    }

    private static void addToExtent(ItemLevelDiscount discount) {
        if (discount != null) {
            allItemLevelDiscounts.add(discount);
        }
    }

    public static List<ItemLevelDiscount> getAllItemLevelDiscounts() {
        return Collections.unmodifiableList(allItemLevelDiscounts);
    }

    public static void clearExtent() {
        allItemLevelDiscounts.clear();
    }

    public static void saveExtent(String filename) throws java.io.IOException {
        String filepath = PersistenceConfig.getDataFilePath(filename);
        try (java.io.ObjectOutputStream out = new java.io.ObjectOutputStream(
                new java.io.FileOutputStream(filepath))) {
            out.writeObject(allItemLevelDiscounts);
        }
    }

    @SuppressWarnings("unchecked")
    public static boolean loadExtent(String filename) {
        String filepath = PersistenceConfig.getDataFilePath(filename);
        try (java.io.ObjectInputStream in = new java.io.ObjectInputStream(
                new java.io.FileInputStream(filepath))) {
            allItemLevelDiscounts = (List<ItemLevelDiscount>) in.readObject();
            return true;
        } catch (java.io.IOException | ClassNotFoundException e) {
            allItemLevelDiscounts.clear();
            return false;
        }
    }

    @Override
    public String toString() {
        return String.format("ItemLevelDiscount[code=%s, items=%s, timeBased=%b, volumeBased=%b]",
            getCode(), applicableItems, isTimeBased(), isVolumeBased());
    }
}

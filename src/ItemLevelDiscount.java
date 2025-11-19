import java.io.Serial;
import java.util.*;


public class ItemLevelDiscount extends Discount {
    @Serial
    private static final long serialVersionUID = 1L;


    private static List<ItemLevelDiscount> allItemLevelDiscounts = new ArrayList<>();

    // Multi-value attribute
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


    private static void addToExtent(ItemLevelDiscount discount) {
        if (discount != null) {
            allItemLevelDiscounts.add(discount);
        }
    }

    public static List<ItemLevelDiscount> getAllItemLevelDiscounts() {
        return Collections.unmodifiableList(allItemLevelDiscounts);
    }


    public Set<String> getApplicableItems() {
        return Collections.unmodifiableSet(applicableItems);
    }

    public void addApplicableItem(String item) {
        if (item == null || item.trim().isEmpty()) {
            throw new IllegalArgumentException("Applicable item cannot be null or empty");
        }
        applicableItems.add(item.trim());
    }

    public void removeApplicableItem(String item) {
        applicableItems.remove(item);
    }


    public static void clearExtent() {
        allItemLevelDiscounts.clear();
    }

    public static void saveExtent(String filename) throws java.io.IOException {
        String filepath = PersistenceConfig.getDataFilePath(filename);
        try (java.io.ObjectOutputStream out = new java.io.ObjectOutputStream(new java.io.FileOutputStream(filepath))) {
            out.writeObject(allItemLevelDiscounts);
        }
    }

    @SuppressWarnings("unchecked")
    public static boolean loadExtent(String filename) {
        String filepath = PersistenceConfig.getDataFilePath(filename);
        try (java.io.ObjectInputStream in = new java.io.ObjectInputStream(new java.io.FileInputStream(filepath))) {
            allItemLevelDiscounts = (List<ItemLevelDiscount>) in.readObject();
            return true;
        } catch (java.io.IOException | ClassNotFoundException e) {
            allItemLevelDiscounts.clear();
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
        return String.format("ItemLevelDiscount[code=%s, applicableItems=%s]",
                           getCode(), applicableItems);
    }
}

import java.io.Serial;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Discount applied to specific menu items.
 */
public class ItemLevelDiscount extends Discount {
    @Serial
    private static final long serialVersionUID = 1L;

    // Class extent
    private static List<ItemLevelDiscount> allItemLevelDiscounts = new ArrayList<>();

    // Multi-value attribute
    private List<String> applicableItems;

    // Constructors
    protected ItemLevelDiscount() {
        super();
        this.applicableItems = new ArrayList<>();
    }

    public ItemLevelDiscount(String code, List<String> applicableItems) {
        super(code);
        this.applicableItems = new ArrayList<>();
        if (applicableItems != null) {
            for (String item : applicableItems) {
                addApplicableItem(item);
            }
        }
        addToExtent(this);
    }

    // Class extent management
    private static void addToExtent(ItemLevelDiscount discount) {
        if (discount != null) {
            allItemLevelDiscounts.add(discount);
        }
    }

    public static List<ItemLevelDiscount> getAllItemLevelDiscounts() {
        return Collections.unmodifiableList(allItemLevelDiscounts);
    }

    // Multi-value attribute management
    public List<String> getApplicableItems() {
        return Collections.unmodifiableList(applicableItems);
    }

    public void addApplicableItem(String item) {
        if (item == null || item.trim().isEmpty()) {
            throw new IllegalArgumentException("Applicable item cannot be null or empty");
        }
        if (!applicableItems.contains(item.trim())) {
            applicableItems.add(item.trim());
        }
    }

    public void removeApplicableItem(String item) {
        applicableItems.remove(item);
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

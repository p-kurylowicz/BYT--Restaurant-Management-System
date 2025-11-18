import java.io.Serial;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Discount applied at the order level based on a percentage.
 */
public class OrderLevelDiscount extends Discount {
    @Serial
    private static final long serialVersionUID = 1L;

    // Class extent
    private static List<OrderLevelDiscount> allOrderLevelDiscounts = new ArrayList<>();

    // Attributes
    private double discountPercentage;

    // Constructors
    protected OrderLevelDiscount() {
        super();
    }

    public OrderLevelDiscount(String code, double discountPercentage) {
        super(code);
        setDiscountPercentage(discountPercentage);
        addToExtent(this);
    }

    // Class extent management
    private static void addToExtent(OrderLevelDiscount discount) {
        if (discount != null) {
            allOrderLevelDiscounts.add(discount);
        }
    }

    public static List<OrderLevelDiscount> getAllOrderLevelDiscounts() {
        return Collections.unmodifiableList(allOrderLevelDiscounts);
    }

    // Getters and setters
    public double getDiscountPercentage() {
        return discountPercentage;
    }

    public void setDiscountPercentage(double discountPercentage) {
        if (discountPercentage < 0 || discountPercentage > 100) {
            throw new IllegalArgumentException("Discount percentage must be between 0 and 100");
        }
        this.discountPercentage = discountPercentage;
    }

    @Override
    public boolean validateDiscount(Order order) {
        // Placeholder implementation
        return true;
    }

    @Override
    public String toString() {
        return String.format("OrderLevelDiscount[code=%s, percentage=%.2f%%]",
                           getCode(), discountPercentage);
    }
}

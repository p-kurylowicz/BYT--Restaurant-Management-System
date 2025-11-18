import java.io.Serial;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Discount based on minimum order amount or quantity.
 */
public class VolumeDiscount extends Discount {
    @Serial
    private static final long serialVersionUID = 1L;

    // Class extent
    private static List<VolumeDiscount> allVolumeDiscounts = new ArrayList<>();

    // Attributes
    private double minAmount;
    private int minQuantity;

    // Constructors
    protected VolumeDiscount() {
        super();
    }

    public VolumeDiscount(String code, double minAmount, int minQuantity) {
        super(code);
        setMinAmount(minAmount);
        setMinQuantity(minQuantity);
        addToExtent(this);
    }

    // Class extent management
    private static void addToExtent(VolumeDiscount discount) {
        if (discount != null) {
            allVolumeDiscounts.add(discount);
        }
    }

    public static List<VolumeDiscount> getAllVolumeDiscounts() {
        return Collections.unmodifiableList(allVolumeDiscounts);
    }

    // Getters and setters
    public double getMinAmount() {
        return minAmount;
    }

    public void setMinAmount(double minAmount) {
        if (minAmount < 0) {
            throw new IllegalArgumentException("Minimum amount cannot be negative");
        }
        this.minAmount = minAmount;
    }

    public int getMinQuantity() {
        return minQuantity;
    }

    public void setMinQuantity(int minQuantity) {
        if (minQuantity < 0) {
            throw new IllegalArgumentException("Minimum quantity cannot be negative");
        }
        this.minQuantity = minQuantity;
    }

    @Override
    public boolean validateDiscount(Order order) {
        // Placeholder implementation
        return true;
    }

    @Override
    public String toString() {
        return String.format("VolumeDiscount[code=%s, minAmount=%.2f, minQuantity=%d]",
                           getCode(), minAmount, minQuantity);
    }
}

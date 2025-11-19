import java.io.Serial;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;


public class VolumeDiscount extends Discount {
    @Serial
    private static final long serialVersionUID = 1L;


    private static List<VolumeDiscount> allVolumeDiscounts = new ArrayList<>();


    private double minAmount;
    private int minQuantity;


    protected VolumeDiscount() {
        super();
    }

    public VolumeDiscount(String code, double minAmount, int minQuantity) {
        super(code);
        setMinAmount(minAmount);
        setMinQuantity(minQuantity);
        addToExtent(this);
    }


    private static void addToExtent(VolumeDiscount discount) {
        if (discount != null) {
            allVolumeDiscounts.add(discount);
        }
    }

    public static List<VolumeDiscount> getAllVolumeDiscounts() {
        return Collections.unmodifiableList(allVolumeDiscounts);
    }


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

    // Class extent persistence
    public static void clearExtent() {
        allVolumeDiscounts.clear();
    }

    public static void saveExtent(String filename) throws java.io.IOException {
        String filepath = PersistenceConfig.getDataFilePath(filename);
        try (java.io.ObjectOutputStream out = new java.io.ObjectOutputStream(new java.io.FileOutputStream(filepath))) {
            out.writeObject(allVolumeDiscounts);
        }
    }

    @SuppressWarnings("unchecked")
    public static boolean loadExtent(String filename) {
        String filepath = PersistenceConfig.getDataFilePath(filename);
        try (java.io.ObjectInputStream in = new java.io.ObjectInputStream(new java.io.FileInputStream(filepath))) {
            allVolumeDiscounts = (List<VolumeDiscount>) in.readObject();
            return true;
        } catch (java.io.IOException | ClassNotFoundException e) {
            allVolumeDiscounts.clear();
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
        return String.format("VolumeDiscount[code=%s, minAmount=%.2f, minQuantity=%d]",
                           getCode(), minAmount, minQuantity);
    }
}

import java.io.Serial;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;


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


    private static void addToExtent(OrderLevelDiscount discount) {
        if (discount != null) {
            allOrderLevelDiscounts.add(discount);
        }
    }

    public static List<OrderLevelDiscount> getAllOrderLevelDiscounts() {
        return Collections.unmodifiableList(allOrderLevelDiscounts);
    }


    public double getDiscountPercentage() {
        return discountPercentage;
    }

    public void setDiscountPercentage(double discountPercentage) {
        if (discountPercentage < 0 || discountPercentage > 100) {
            throw new IllegalArgumentException("Discount percentage must be between 0 and 100");
        }
        this.discountPercentage = discountPercentage;
    }


    public static void clearExtent() {
        allOrderLevelDiscounts.clear();
    }

    public static void saveExtent(String filename) throws java.io.IOException {
        String filepath = PersistenceConfig.getDataFilePath(filename);
        try (java.io.ObjectOutputStream out = new java.io.ObjectOutputStream(new java.io.FileOutputStream(filepath))) {
            out.writeObject(allOrderLevelDiscounts);
        }
    }

    @SuppressWarnings("unchecked")
    public static boolean loadExtent(String filename) {
        String filepath = PersistenceConfig.getDataFilePath(filename);
        try (java.io.ObjectInputStream in = new java.io.ObjectInputStream(new java.io.FileInputStream(filepath))) {
            allOrderLevelDiscounts = (List<OrderLevelDiscount>) in.readObject();
            return true;
        } catch (java.io.IOException | ClassNotFoundException e) {
            allOrderLevelDiscounts.clear();
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
        return String.format("OrderLevelDiscount[code=%s, percentage=%.2f%%]",
                           getCode(), discountPercentage);
    }
}

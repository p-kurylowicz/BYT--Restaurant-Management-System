import java.io.Serial;
import java.io.Serializable;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.*;

/**
 * Discount Basis): {complete, overlapping} - COMPOSITION
 * (Discount Application): {complete, disjoint} - INHERITANCE
 */
public abstract class Discount implements Serializable {
    @Serial
    private static final long serialVersionUID = 1L;

    private static List<Discount> allDiscounts = new ArrayList<>();

    private String code;
    protected TimeBasedDiscountComponent timeBasedComponent;
    protected VolumeDiscountComponent volumeComponent;

    private static class TimeBasedDiscountComponent implements Serializable {
        @Serial
        private static final long serialVersionUID = 1L;

        private LocalDate date;
        private LocalTime time;

        public TimeBasedDiscountComponent(LocalDate date, LocalTime time) {
            setDate(date);
            setTime(time);
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

        @Override
        public String toString() {
            return String.format("TimeBasedComponent[date=%s, time=%s]", date, time);
        }
    }

    private static class VolumeDiscountComponent implements Serializable {
        @Serial
        private static final long serialVersionUID = 1L;

        private double minAmount;
        private int minQuantity;

        public VolumeDiscountComponent(double minAmount, int minQuantity) {
            setMinAmount(minAmount);
            setMinQuantity(minQuantity);
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

        @Override
        public String toString() {
            return String.format("VolumeComponent[minAmount=%.2f, minQuantity=%d]", minAmount, minQuantity);
        }
    }

    protected Discount() {}

    protected Discount(String code) {
        setCode(code);
        addToExtent(this);
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        if (code == null || code.trim().isEmpty()) {
            throw new IllegalArgumentException("Discount code cannot be null or empty");
        }
        this.code = code.trim();
    }

    public boolean isTimeBased() {
        return timeBasedComponent != null;
    }

    public boolean isVolumeBased() {
        return volumeComponent != null;
    }

    protected void setTimeBasedComponent(LocalDate date, LocalTime time) {
        this.timeBasedComponent = new TimeBasedDiscountComponent(date, time);
    }

    protected void setVolumeComponent(double minAmount, int minQuantity) {
        this.volumeComponent = new VolumeDiscountComponent(minAmount, minQuantity);
    }

    public abstract boolean isOrderLevel();
    public abstract boolean isItemLevel();

    public LocalDate getDate() {
        if (!isTimeBased()) {
            throw new IllegalStateException("This discount is not time-based");
        }
        return timeBasedComponent.getDate();
    }

    public void setDate(LocalDate date) {
        if (!isTimeBased()) {
            throw new IllegalStateException("This discount is not time-based");
        }
        timeBasedComponent.setDate(date);
    }

    public LocalTime getTime() {
        if (!isTimeBased()) {
            throw new IllegalStateException("This discount is not time-based");
        }
        return timeBasedComponent.getTime();
    }

    public void setTime(LocalTime time) {
        if (!isTimeBased()) {
            throw new IllegalStateException("This discount is not time-based");
        }
        timeBasedComponent.setTime(time);
    }

    public double getMinAmount() {
        if (!isVolumeBased()) {
            throw new IllegalStateException("This discount is not volume-based");
        }
        return volumeComponent.getMinAmount();
    }

    public void setMinAmount(double minAmount) {
        if (!isVolumeBased()) {
            throw new IllegalStateException("This discount is not volume-based");
        }
        volumeComponent.setMinAmount(minAmount);
    }

    public int getMinQuantity() {
        if (!isVolumeBased()) {
            throw new IllegalStateException("This discount is not volume-based");
        }
        return volumeComponent.getMinQuantity();
    }

    public void setMinQuantity(int minQuantity) {
        if (!isVolumeBased()) {
            throw new IllegalStateException("This discount is not volume-based");
        }
        volumeComponent.setMinQuantity(minQuantity);
    }

    public abstract double getDiscountPercentage();
    public abstract void setDiscountPercentage(double percentage);
    public abstract Set<String> getApplicableItems();
    public abstract void addApplicableItem(String item);
    public abstract void removeApplicableItem(String item);

    public boolean validateDiscount(Order order) {
        if (order == null) {
            return false;
        }

        if (isTimeBased()) {
            LocalDate orderDate = order.getDate();
            if (orderDate == null || !orderDate.equals(getDate())) {
                return false;
            }
        }

        if (isVolumeBased()) {
            double orderTotal = order.getSubtotal();
            if (orderTotal < getMinAmount()) {
                return false;
            }
        }

        return true;
    }

    public String getDiscountDescription() {
        StringBuilder desc = new StringBuilder();
        desc.append("Discount[").append(code).append("]: ");

        List<String> basisAspects = new ArrayList<>();
        if (isTimeBased()) {
            basisAspects.add("Time-based(" + getDate() + " " + getTime() + ")");
        }
        if (isVolumeBased()) {
            basisAspects.add("Volume-based(min $" + getMinAmount() + ", qty " + getMinQuantity() + ")");
        }
        desc.append(String.join(" + ", basisAspects));

        desc.append(" -> ");
        if (isOrderLevel()) {
            desc.append("Order-level(").append(getDiscountPercentage()).append("%)");
        } else {
            desc.append("Item-level(items: ").append(getApplicableItems()).append(")");
        }

        return desc.toString();
    }

    private static void addToExtent(Discount discount) {
        if (discount != null) {
            allDiscounts.add(discount);
        }
    }

    public static List<Discount> getAllDiscounts() {
        return Collections.unmodifiableList(allDiscounts);
    }

    public static List<Discount> getTimeBasedDiscounts() {
        return allDiscounts.stream()
            .filter(Discount::isTimeBased)
            .toList();
    }

    public static List<Discount> getVolumeBasedDiscounts() {
        return allDiscounts.stream()
            .filter(Discount::isVolumeBased)
            .toList();
    }

    public static List<Discount> getOrderLevelDiscounts() {
        return allDiscounts.stream()
            .filter(Discount::isOrderLevel)
            .toList();
    }

    public static List<Discount> getItemLevelDiscounts() {
        return allDiscounts.stream()
            .filter(Discount::isItemLevel)
            .toList();
    }

    public static List<Discount> getTimeAndVolumeDiscounts() {
        return allDiscounts.stream()
            .filter(d -> d.isTimeBased() && d.isVolumeBased())
            .toList();
    }

    public static void clearExtent() {
        allDiscounts.clear();
    }

    public static void saveExtent(String filename) throws java.io.IOException {
        String filepath = PersistenceConfig.getDataFilePath(filename);
        try (java.io.ObjectOutputStream out = new java.io.ObjectOutputStream(
                new java.io.FileOutputStream(filepath))) {
            out.writeObject(allDiscounts);
        }
    }

    @SuppressWarnings("unchecked")
    public static boolean loadExtent(String filename) {
        String filepath = PersistenceConfig.getDataFilePath(filename);
        try (java.io.ObjectInputStream in = new java.io.ObjectInputStream(
                new java.io.FileInputStream(filepath))) {
            allDiscounts = (List<Discount>) in.readObject();
            return true;
        } catch (java.io.IOException | ClassNotFoundException e) {
            allDiscounts.clear();
            return false;
        }
    }

    @Override
    public String toString() {
        return getDiscountDescription();
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null || getClass() != obj.getClass()) return false;
        Discount other = (Discount) obj;
        return Objects.equals(code, other.code);
    }

    @Override
    public int hashCode() {
        return Objects.hash(code);
    }
}

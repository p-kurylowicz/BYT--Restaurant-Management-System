import java.io.Serial;

public class PercentageDiscount extends Discount {
    @Serial
    private static final long serialVersionUID = 1L;

    private double percentage; // e.g., 0.10 for 10%

    public PercentageDiscount() {
        super();
    }

    public PercentageDiscount(String code, double percentage) {
        super(code, 0); // discount amount calculated dynamically
        setPercentage(percentage);
    }

    public double getPercentage() { return percentage; }

    public void setPercentage(double percentage) {
        if (percentage < 0 || percentage > 1) {
            throw new IllegalArgumentException("Percentage must be between 0 and 1");
        }
        this.percentage = percentage;
    }

    @Override
    public boolean validateDiscount(Order order) {

        return order != null && order.getTotal() > 0;
    }

    public double calculateDiscount(double orderTotal) {
        return orderTotal * percentage;
    }

    @Override
    public String toString() {
        return String.format("PercentageDiscount[code=%s, percentage=%.0f%%]",
            getCode(), percentage * 100);
    }
}

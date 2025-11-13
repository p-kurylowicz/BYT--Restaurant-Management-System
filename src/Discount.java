import java.io.Serial;
import java.io.Serializable;

// Simple discount class for now
public abstract class Discount implements Serializable {
    @Serial
    private static final long serialVersionUID = 1L;

    
    private String code;
    private double discountAmount;

    
    protected Discount() {}

    
    protected Discount(String code, double discountAmount) {
        setCode(code);
        setDiscountAmount(discountAmount);
    }

    
    public String getCode() { return code; }
    public double getDiscountAmount() { return discountAmount; }

    
    public void setCode(String code) {
        if (code == null || code.trim().isEmpty()) {
            throw new IllegalArgumentException("Discount code cannot be null or empty");
        }
        this.code = code.trim();
    }

    public void setDiscountAmount(double discountAmount) {
        if (discountAmount < 0) {
            throw new IllegalArgumentException("Discount amount cannot be negative");
        }
        this.discountAmount = discountAmount;
    }

    // Abstract method to validate discount
    public abstract boolean validateDiscount(Order order);

    @Override
    public String toString() {
        return String.format("Discount[code=%s, amount=%.2f]", code, discountAmount);
    }
}

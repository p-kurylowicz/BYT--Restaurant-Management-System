import java.io.Serial;
import java.io.Serializable;


public abstract class Discount implements Serializable {
    @Serial
    private static final long serialVersionUID = 1L;


    private String code;


    protected Discount() {}


    protected Discount(String code) {
        setCode(code);
    }


    public String getCode() { return code; }


    public void setCode(String code) {
        if (code == null || code.trim().isEmpty()) {
            throw new IllegalArgumentException("Discount code cannot be null or empty");
        }
        this.code = code.trim();
    }

    // Abstract method to validate discount
    public abstract boolean validateDiscount(Order order);

    @Override
    public String toString() {
        return String.format("Discount[code=%s]", code);
    }
}

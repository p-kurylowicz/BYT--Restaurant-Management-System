import java.io.Serial;

public class Cash extends Payment {
    @Serial
    private static final long serialVersionUID = 1L;


    private double amountTendered;
    private double changeGiven;


    public Cash() {
        super();
    }


    public Cash(double amountPayed, double amountTendered) {
        super(amountPayed);
        setAmountTendered(amountTendered);
        calculateChange();
    }

    
    public double getAmountTendered() { return amountTendered; }
    public double getChangeGiven() { return changeGiven; }

    
    public void setAmountTendered(double amountTendered) {
        if (amountTendered < getAmountPayed()) {
            throw new IllegalArgumentException("Amount tendered must be greater than or equal to amount payed");
        }
        this.amountTendered = amountTendered;
        calculateChange();
    }

    public void setChangeGiven(double changeGiven) {
        if (changeGiven < 0) {
            throw new IllegalArgumentException("Change given cannot be negative");
        }
        this.changeGiven = changeGiven;
    }


    private void calculateChange() {
        this.changeGiven = amountTendered - getAmountPayed();
    }

    @Override
    public String toString() {
        return String.format("Cash[tendered=%.2f, change=%.2f, %s]",
            amountTendered, changeGiven, super.toString());
    }
}

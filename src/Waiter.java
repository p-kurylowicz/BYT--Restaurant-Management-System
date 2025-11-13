import java.io.Serial;
import java.time.LocalDate;

public class Waiter extends Employee {
    @Serial
    private static final long serialVersionUID = 1L;


    private String section;
    private double tipTotal;

    
    public Waiter() {
        super();
    }

    
    public Waiter(String name, String email, String phone, LocalDate hireDate,
                 double hourlyRate, String section) {
        super(name, email, phone, hireDate, hourlyRate);
        setSection(section);
        this.tipTotal = 0.0;
    }

    
    public String getSection() { return section; }
    public double getTipTotal() { return tipTotal; }

    
    public void setSection(String section) {
        if (section == null || section.trim().isEmpty()) {
            throw new IllegalArgumentException("Section cannot be null or empty");
        }
        this.section = section.trim();
    }

    public void setTipTotal(double tipTotal) {
        if (tipTotal < 0) {
            throw new IllegalArgumentException("Tip total cannot be negative");
        }
        this.tipTotal = tipTotal;
    }


    public void addTip(double tip) {
        if (tip < 0) {
            throw new IllegalArgumentException("Tip cannot be negative");
        }
        this.tipTotal += tip;
    }


    public void clearTableAssignments() {
       // Placeholder
    }

    @Override
    public String toString() {
        return String.format("Waiter[%s, section=%s, tipTotal=%.2f, %s]",
            getName(), section, tipTotal, super.toString());
    }
}

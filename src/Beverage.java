import java.io.Serial;
import java.util.Set;

public class Beverage extends MenuItem {
    @Serial
    private static final long serialVersionUID = 1L;

    // Optional attribute
    private Double alcoholPercentage;


    public Beverage() {
        super();
    }

    public Beverage(String name, String description, double price, String image,
                    String nationalOrigin, NutritionalInfo nutritionalInfo, Double alcoholPercentage) {
        super(name, description, price, image, nationalOrigin, nutritionalInfo);
        setAlcoholPercentage(alcoholPercentage);
    }

    public Beverage(String name, String description, double price, String image,
                    String nationalOrigin, NutritionalInfo nutritionalInfo,
                    Set<String> allergens, Double alcoholPercentage) {
        super(name, description, price, image, nationalOrigin, nutritionalInfo, allergens);
        setAlcoholPercentage(alcoholPercentage);
    }


    public Double getAlcoholPercentage() {
        return alcoholPercentage;
    }


    public void setAlcoholPercentage(Double alcoholPercentage) {
        if (alcoholPercentage != null && (alcoholPercentage < 0 || alcoholPercentage > 100)) {
            throw new IllegalArgumentException("Alcohol percentage must be between 0 and 100");
        }
        this.alcoholPercentage = alcoholPercentage;
    }

    public boolean isAlcoholic() {
        return alcoholPercentage != null && alcoholPercentage > 0;
    }

    @Override
    public String toString() {
        String alcoholStr = alcoholPercentage != null ? String.format("%.1f%%", alcoholPercentage) : "N/A";
        return String.format("Beverage[%s, alcohol=%s, %s, %s]",
                getName(), alcoholStr, getNutritionalInfo(), super.toString());
    }
}

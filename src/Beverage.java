import java.io.Serial;
import java.util.List;

public class Beverage extends MenuItem {
    @Serial
    private static final long serialVersionUID = 1L;


    private double alcoholPercentage;


    public Beverage() {
        super();
    }

    public Beverage(String name, String description, double price, String image,
                   String nationalOrigin, NutritionalInfo nutritionalInfo,
                   List<Ingredient> ingredients, double alcoholPercentage) {
        super(name, description, price, image, nationalOrigin, nutritionalInfo, ingredients);
        setAlcoholPercentage(alcoholPercentage);
    }


    public double getAlcoholPercentage() {
        return alcoholPercentage;
    }


    public void setAlcoholPercentage(double alcoholPercentage) {
        if (alcoholPercentage < 0 || alcoholPercentage > 100) {
            throw new IllegalArgumentException("Alcohol percentage must be between 0 and 100");
        }
        this.alcoholPercentage = alcoholPercentage;
    }

    public boolean isAlcoholic() {
        return alcoholPercentage > 0;
    }

    @Override
    public String toString() {
        return String.format("Beverage[%s, alcohol=%.1f%%, %s, %s]",
            getName(), alcoholPercentage, getNutritionalInfo(), super.toString());
    }
}

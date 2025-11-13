import java.io.Serial;
import java.io.Serializable;


public class NutritionalInfo implements Serializable {
    @Serial
    private static final long serialVersionUID = 1L;

    private double calories;
    private double protein;
    private double carbs;
    private double fats;
    private double fiber;

    
    public NutritionalInfo() {}

    public NutritionalInfo(double calories, double protein, double carbs, double fats, double fiber) {
        if (calories < 0 || protein < 0 || carbs < 0 || fats < 0 || fiber < 0) {
            throw new IllegalArgumentException("Nutritional values cannot be negative");
        }
        this.calories = calories;
        this.protein = protein;
        this.carbs = carbs;
        this.fats = fats;
        this.fiber = fiber;
    }

    
    public double getCalories() { return calories; }
    public double getProtein() { return protein; }
    public double getCarbs() { return carbs; }
    public double getFats() { return fats; }
    public double getFiber() { return fiber; }


    public void setCalories(double calories) {
        if (calories < 0) throw new IllegalArgumentException("Calories cannot be negative");
        this.calories = calories;
    }
    public void setProtein(double protein) {
        if (protein < 0) throw new IllegalArgumentException("Protein cannot be negative");
        this.protein = protein;
    }
    public void setCarbs(double carbs) {
        if (carbs < 0) throw new IllegalArgumentException("Carbs cannot be negative");
        this.carbs = carbs;
    }
    public void setFats(double fats) {
        if (fats < 0) throw new IllegalArgumentException("Fats cannot be negative");
        this.fats = fats;
    }
    public void setFiber(double fiber) {
        if (fiber < 0) throw new IllegalArgumentException("Fiber cannot be negative");
        this.fiber = fiber;
    }

    @Override
    public String toString() {
        return String.format("Nutritional Info: %.0f cal, %.1fg protein, %.1fg carbs, %.1fg fats, %.1fg fiber",
            calories, protein, carbs, fats, fiber);
    }
}

import java.util.List;

public class MainDish extends MenuItem {
    private static final long serialVersionUID = 1L;


    private int spiceLevel;

    
    public MainDish() {
        super();
    }


    public MainDish(String name, String description, double price, String image,
                   String nationalOrigin, NutritionalInfo nutritionalInfo, int spiceLevel) {
        super(name, description, price, image, nationalOrigin, nutritionalInfo);
        setSpiceLevel(spiceLevel);
    }

    public MainDish(String name, String description, double price, String image,
                   String nationalOrigin, NutritionalInfo nutritionalInfo,
                   List<String> allergens, int spiceLevel) {
        super(name, description, price, image, nationalOrigin, nutritionalInfo, allergens);
        setSpiceLevel(spiceLevel);
    }


    public int getSpiceLevel() {
        return spiceLevel;
    }


    public void setSpiceLevel(int spiceLevel) {
        if (spiceLevel < 0 || spiceLevel > 5) {
            throw new IllegalArgumentException("Spice level must be between 0 and 5");
        }
        this.spiceLevel = spiceLevel;
    }

    @Override
    public String toString() {
        return String.format("MainDish[%s, spiceLevel=%d, %s, %s]",
            getName(), spiceLevel, getNutritionalInfo(), super.toString());
    }
}

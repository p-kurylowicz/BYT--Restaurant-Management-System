import java.io.Serial;
import java.util.Set;

public class Dessert extends MenuItem {
    @Serial
    private static final long serialVersionUID = 1L;


    private boolean hasNuts;


    public Dessert() {
        super();
    }


    public Dessert(String name, String description, double price, String image,
                  String nationalOrigin, NutritionalInfo nutritionalInfo, boolean hasNuts) {
        super(name, description, price, image, nationalOrigin, nutritionalInfo);
        setHasNuts(hasNuts);
    }

    public Dessert(String name, String description, double price, String image,
                  String nationalOrigin, NutritionalInfo nutritionalInfo,
                  Set<String> allergens, boolean hasNuts) {
        super(name, description, price, image, nationalOrigin, nutritionalInfo, allergens);
        setHasNuts(hasNuts);
    }


    public boolean getHasNuts() {
        return hasNuts;
    }


    public void setHasNuts(boolean hasNuts) {
        this.hasNuts = hasNuts;
    }

    @Override
    public String toString() {
        return String.format("Dessert[%s, hasNuts=%s, %s, %s]",
            getName(), hasNuts ? "Yes" : "No", getNutritionalInfo(), super.toString());
    }
}

import java.io.*;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public abstract class MenuItem implements Serializable {
    @Serial
    private static final long serialVersionUID = 1L;

    
    public static final double TAX_RATE = 0.23;

    
    private static List<MenuItem> allMenuItems = new ArrayList<>();

    
    private String name;
    private String description;
    private double price;
    private String image;
    private MenuItemAvailability availability;
    private String nationalOrigin;


    private NutritionalInfo nutritionalInfo;

    
    private List<Ingredient> ingredients;

    
    protected MenuItem() {
        this.ingredients = new ArrayList<>();
    }

    
    protected MenuItem(String name, String description, double price, String image,
                      String nationalOrigin, NutritionalInfo nutritionalInfo, List<Ingredient> ingredients) {
        this.ingredients = new ArrayList<>();
        setName(name);
        setDescription(description);
        setPrice(price);
        setImage(image);
        setNationalOrigin(nationalOrigin);
        setNutritionalInfo(nutritionalInfo);
        setIngredients(ingredients);
        this.availability = MenuItemAvailability.AVAILABLE;

        addMenuItem(this);
    }

    
    public String getName() { return name; }
    public String getDescription() { return description; }
    public double getPrice() { return price; }
    public String getImage() { return image; }
    public MenuItemAvailability getAvailability() { return availability; }
    public String getNationalOrigin() { return nationalOrigin; }
    public NutritionalInfo getNutritionalInfo() { return nutritionalInfo; }

    
    public List<Ingredient> getIngredients() {
        return Collections.unmodifiableList(ingredients);
    }

    
    public void setName(String name) {
        if (name == null || name.trim().isEmpty()) {
            throw new IllegalArgumentException("Menu item name cannot be null or empty");
        }
        this.name = name.trim();
    }

    public void setDescription(String description) {
        if (description == null || description.trim().isEmpty()) {
            throw new IllegalArgumentException("Description cannot be null or empty");
        }
        this.description = description.trim();
    }

    public void setPrice(double price) {
        if (price <= 0) {
            throw new IllegalArgumentException("Price must be greater than zero");
        }
        this.price = price;
    }

    public void setImage(String image) {
        if (image == null || image.trim().isEmpty()) {
            throw new IllegalArgumentException("Image path cannot be null or empty");
        }
        this.image = image.trim();
    }

    public void setNationalOrigin(String nationalOrigin) {
        if (nationalOrigin == null || nationalOrigin.trim().isEmpty()) {
            throw new IllegalArgumentException("National origin cannot be null or empty");
        }
        this.nationalOrigin = nationalOrigin.trim();
    }

    public void setAvailability(MenuItemAvailability availability) {
        if (availability == null) {
            throw new IllegalArgumentException("Availability cannot be null");
        }
        this.availability = availability;
    }


    public void setNutritionalInfo(NutritionalInfo nutritionalInfo) {
        if (nutritionalInfo == null) {
            throw new IllegalArgumentException("Nutritional info cannot be null");
        }
        this.nutritionalInfo = nutritionalInfo;
    }

    
    public void setIngredients(List<Ingredient> ingredients) {
        if (ingredients == null || ingredients.isEmpty()) {
            throw new IllegalArgumentException("Menu item must have at least one ingredient");
        }
        for (Ingredient ingredient : ingredients) {
            if (ingredient == null) {
                throw new IllegalArgumentException("Ingredient list cannot contain null values");
            }
        }
        this.ingredients = new ArrayList<>(ingredients);
    }


    public void addIngredient(Ingredient ingredient) {
        if (ingredient == null) {
            throw new IllegalArgumentException("Ingredient cannot be null");
        }
        this.ingredients.add(ingredient);
    }


    public double calculatePriceWithTax() {
        return price * (1 + TAX_RATE);
    }


    public boolean checkAvailability() {
        for (Ingredient ingredient : ingredients) {
            if (ingredient.getCurrentStock() <= 0) {
                return false;
            }
        }
        return true;
    }


    public void recalculateAvailability() {
        if (checkAvailability()) {
            this.availability = MenuItemAvailability.AVAILABLE;
        } else {
            this.availability = MenuItemAvailability.UNAVAILABLE;
        }
    }

    public void transitionToPendingUpdate() {
        this.availability = MenuItemAvailability.PENDING_UPDATE;
    }

    
    private static void addMenuItem(MenuItem menuItem) {
        if (menuItem == null) {
            throw new IllegalArgumentException("MenuItem cannot be null");
        }
        allMenuItems.add(menuItem);
    }

    public static List<MenuItem> getAllMenuItems() {
        return Collections.unmodifiableList(allMenuItems);
    }

    public static void clearExtent() {
        allMenuItems.clear();
    }

    
    public static void saveExtent(String filename) throws IOException {
        String filepath = PersistenceConfig.getDataFilePath(filename);
        try (ObjectOutputStream out = new ObjectOutputStream(new FileOutputStream(filepath))) {
            out.writeObject(allMenuItems);
        }
    }

    @SuppressWarnings("unchecked")
    public static boolean loadExtent(String filename) {
        String filepath = PersistenceConfig.getDataFilePath(filename);
        try (ObjectInputStream in = new ObjectInputStream(new FileInputStream(filepath))) {
            allMenuItems = (List<MenuItem>) in.readObject();
            return true;
        } catch (IOException | ClassNotFoundException e) {
            allMenuItems.clear();
            return false;
        }
    }

    @Override
    public String toString() {
        return String.format("MenuItem[%s, price=%.2f PLN (%.2f with tax), origin=%s, availability=%s]",
            name, price, calculatePriceWithTax(), nationalOrigin, availability);
    }
}

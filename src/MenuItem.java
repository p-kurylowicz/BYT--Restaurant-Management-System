import java.io.*;
import java.util.*;

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
    private final List<Feedback> reviews = new ArrayList<>();

    private NutritionalInfo nutritionalInfo;

    private final Set<String> allergens;

    private final List<ItemQuantity> itemQuantities = new ArrayList<>();

    private Menu menu;

    protected MenuItem() {
        this.allergens = new HashSet<>();
    }


    protected MenuItem(String name, String description, double price, String image,
                      String nationalOrigin, NutritionalInfo nutritionalInfo) {
        this.allergens = new HashSet<>();
        setName(name);
        setDescription(description);
        setPrice(price);
        setImage(image);
        setNationalOrigin(nationalOrigin);
        setNutritionalInfo(nutritionalInfo);
        this.availability = MenuItemAvailability.AVAILABLE;

        addMenuItem(this);
    }

    protected MenuItem(String name, String description, double price, String image,
                      String nationalOrigin, NutritionalInfo nutritionalInfo, Set<String> allergens) {
        this.allergens = new HashSet<>();
        setName(name);
        setDescription(description);
        setPrice(price);
        setImage(image);
        setNationalOrigin(nationalOrigin);
        setNutritionalInfo(nutritionalInfo);
        setAllergens(allergens);
        this.availability = MenuItemAvailability.AVAILABLE;

        addMenuItem(this);
    }

    public Menu getMenu() { return menu; }

    public void setMenu(Menu newMenu) {
        if (this.menu != newMenu) {
            if (this.menu != null) {
                // Reverse: remove from old menu
                this.menu.removeMenuItem(this);
            }
            this.menu = newMenu;
            if (newMenu != null) {
                // Reverse: add to new menu
                newMenu.addMenuItem(this);
            }
        }
    }

    
    public String getName() { return name; }
    public String getDescription() { return description; }
    public double getPrice() { return price; }
    public String getImage() { return image; }
    public MenuItemAvailability getAvailability() { return availability; }
    public String getNationalOrigin() { return nationalOrigin; }
    public NutritionalInfo getNutritionalInfo() { return nutritionalInfo; }
    public List<Feedback> getReviews() {
        return Collections.unmodifiableList(reviews);
    }

    public Set<String> getAllergens() {
        return Collections.unmodifiableSet(allergens);
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

    public void addReview(Feedback review) {
        if (review == null) throw new IllegalArgumentException("Review cannot be null");
        if (review.getMenuItem() != this) review.setMenuItem(this);
        reviews.add(review);
    }


    public void setNutritionalInfo(NutritionalInfo nutritionalInfo) {
        if (nutritionalInfo == null) {
            throw new IllegalArgumentException("Nutritional info cannot be null");
        }
        this.nutritionalInfo = nutritionalInfo;
    }

    public void removeReview(Feedback review) {
        if (review == null) return;
        reviews.remove(review);
    }

    // Multi-value allergens management
    public void setAllergens(Set<String> allergens) {
        // Allergens are optional (0..*), so null or empty set is allowed
        this.allergens.clear();
        if (allergens != null && !allergens.isEmpty()) {
            for (String allergen : allergens) {
                if (allergen == null || allergen.trim().isEmpty()) {
                    throw new IllegalArgumentException("Allergen set cannot contain null or empty values");
                }
                this.allergens.add(allergen.trim());
            }
        }
    }

    public void addAllergen(String allergen) {
        if (allergen == null || allergen.trim().isEmpty()) {
            throw new IllegalArgumentException("Allergen cannot be null or empty");
        }
        allergens.add(allergen.trim());
    }

    public void removeAllergen(String allergen) {
        allergens.remove(allergen);
    }

    public void clearAllergens() {
        allergens.clear();
    }

    public List<ItemQuantity> getItemQuantities() {
        return Collections.unmodifiableList(itemQuantities);
    }

    void addItemQuantity(ItemQuantity itemQuantity) {
        if (itemQuantity == null) {
            throw new IllegalArgumentException("ItemQuantity cannot be null");
        }
        if (itemQuantities.contains(itemQuantity)) {
            return;
        }
        itemQuantities.add(itemQuantity);
    }

    void removeItemQuantity(ItemQuantity itemQuantity) {
        if (itemQuantity != null) {
            itemQuantities.remove(itemQuantity);
        }
    }

    public double calculatePriceWithTax() {
        return price * (1 + TAX_RATE);
    }

    /**
     * Check availability - placeholder for future implementation.
     * TODO: Implement availability checking logic
     */
    public boolean checkAvailability() {
        return this.availability == MenuItemAvailability.AVAILABLE;
    }

    /**
     * Recalculate availability - placeholder for future implementation.
     * TODO: Implement availability recalculation logic
     */
    public void recalculateAvailability() {
        // Placeholder - no ingredient-based recalculation for now
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
        return String.format("MenuItem[%s, price=%.2f PLN (%.2f with tax), origin=%s, availability=%s, allergens=%d]",
            name, price, calculatePriceWithTax(), nationalOrigin, availability, allergens.size());
    }
}

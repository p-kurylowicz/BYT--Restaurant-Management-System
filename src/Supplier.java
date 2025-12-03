import java.io.*;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class Supplier implements Serializable {
    @Serial
    private static final long serialVersionUID = 1L;

    private static List<Supplier> allSuppliers = new ArrayList<>();

    private String name;
    private ContactInfo contactInfo;
    private double reliabilityRating;
    private String contactPerson;
    
    // Aggregation: Supplier -> Ingredient (0..*)
    private List<Ingredient> ingredients;
    // {Bag} Association: Supplier -> SupplyLog (0..*)
    private List<SupplyLog> supplyLogs;

    public Supplier() {
        this.ingredients = new ArrayList<>();
        this.supplyLogs = new ArrayList<>();
    }

    public Supplier(String name, String phone, String email, String address, double reliabilityRating, String contactPerson) {
        this.ingredients = new ArrayList<>();
        this.supplyLogs = new ArrayList<>();
        setName(name);
        setContactInfo(new ContactInfo(phone, email, address));
        setReliabilityRating(reliabilityRating);
        setContactPerson(contactPerson);
        addSupplier(this);
    }

    public Supplier(String name, ContactInfo contactInfo, double reliabilityRating, String contactPerson) {
        this.ingredients = new ArrayList<>();
        this.supplyLogs = new ArrayList<>();
        setName(name);
        setContactInfo(contactInfo);
        setReliabilityRating(reliabilityRating);
        setContactPerson(contactPerson);
        addSupplier(this);
    }

    public String getName() { return name; }
    public ContactInfo getContactInfo() { return contactInfo; }
    public String getPhone() { return contactInfo != null ? contactInfo.getPhone() : null; }
    public String getEmail() { return contactInfo != null ? contactInfo.getEmail() : null; }
    public String getAddress() { return contactInfo != null ? contactInfo.getAddress() : null; }
    public double getReliabilityRating() { return reliabilityRating; }
    public String getContactPerson() { return contactPerson; }
    
    public List<Ingredient> getIngredients() {
        return Collections.unmodifiableList(ingredients);
    }

    public List<SupplyLog> getSupplyLogs() {
        return Collections.unmodifiableList(supplyLogs);
    }

    void addSupplyLog(SupplyLog supplyLog) {
        if (supplyLog == null) {
            throw new IllegalArgumentException("SupplyLog cannot be null");
        }
        supplyLogs.add(supplyLog);
    }

    void removeSupplyLog(SupplyLog supplyLog) {
        if (supplyLog != null) {
            supplyLogs.remove(supplyLog);
        }
    }

    public void setName(String name) {
        if (name == null || name.trim().isEmpty()) {
            throw new IllegalArgumentException("Supplier name cannot be null or empty");
        }
        this.name = name.trim();
    }

    public void setContactInfo(ContactInfo contactInfo) {
        if (contactInfo == null) {
            throw new IllegalArgumentException("Contact info cannot be null");
        }
        this.contactInfo = contactInfo;
    }

    public void updateContactInfo(String phone, String email, String address) {
        if (this.contactInfo == null) {
            this.contactInfo = new ContactInfo(phone, email, address);
        } else {
            this.contactInfo.updateContactInfo(phone, email, address);
        }
    }

    public void setReliabilityRating(double reliabilityRating) {
        if (reliabilityRating < 0 || reliabilityRating > 5) {
            throw new IllegalArgumentException("Reliability rating must be between 0 and 5");
        }
        this.reliabilityRating = reliabilityRating;
    }

    public void setContactPerson(String contactPerson) {
        if (contactPerson != null) {
            this.contactPerson = contactPerson.trim();
        } else {
            this.contactPerson = null;
        }
    }

    public void addIngredient(Ingredient ingredient) {
        if (ingredient == null) {
            throw new IllegalArgumentException("Ingredient cannot be null");
        }
        
        if (ingredients.contains(ingredient)) {
            throw new IllegalStateException("This ingredient is already supplied by this supplier");
        }
        
        ingredients.add(ingredient);
        
        if (!ingredient.getSuppliers().contains(this)) {
            ingredient.addSupplier(this);
        }
    }
    
    public void removeIngredient(Ingredient ingredient) {
        if (ingredient == null) {
            throw new IllegalArgumentException("Ingredient cannot be null");
        }
        
        // No minimum multiplicity constraint (0..*)
        if (!ingredients.contains(ingredient)) {
            throw new IllegalArgumentException("This ingredient is not supplied by this supplier");
        }
        
        // Check if ingredient would violate its minimum multiplicity (1..*)
        if (ingredient.getSuppliers().size() <= 1) {
            throw new IllegalStateException(
                "Cannot remove ingredient: it would leave the ingredient without any suppliers (violates 1..* constraint)");
        }
        
        ingredients.remove(ingredient);
        
        if (ingredient.getSuppliers().contains(this)) {
            ingredient.removeSupplier(this);
        }
    }
    
    public void clearIngredients() {
        List<Ingredient> ingredientsCopy = new ArrayList<>(ingredients);
        
        for (Ingredient ingredient : ingredientsCopy) {
            if (ingredient.getSuppliers().size() > 1) {
                removeIngredient(ingredient);
            }
        }
    }
    
    public boolean suppliesIngredient(String ingredientName) {
        for (Ingredient ingredient : ingredients) {
            if (ingredient.getName().equalsIgnoreCase(ingredientName)) {
                return true;
            }
        }
        return false;
    }

    private static void addSupplier(Supplier supplier) {
        if (supplier == null) {
            throw new IllegalArgumentException("Supplier cannot be null");
        }
        allSuppliers.add(supplier);
    }

    public static List<Supplier> getAllSuppliers() {
        return Collections.unmodifiableList(allSuppliers);
    }

    public static void clearExtent() {
        allSuppliers.clear();
    }

    public static void saveExtent(String filename) throws IOException {
        String filepath = PersistenceConfig.getDataFilePath(filename);
        try (ObjectOutputStream out = new ObjectOutputStream(new FileOutputStream(filepath))) {
            out.writeObject(allSuppliers);
        }
    }

    @SuppressWarnings("unchecked")
    public static boolean loadExtent(String filename) {
        String filepath = PersistenceConfig.getDataFilePath(filename);
        try (ObjectInputStream in = new ObjectInputStream(new FileInputStream(filepath))) {
            allSuppliers = (List<Supplier>) in.readObject();
            return true;
        } catch (IOException | ClassNotFoundException e) {
            allSuppliers.clear();
            return false;
        }
    }

    @Override
    public String toString() {
        return String.format("Supplier[%s, contact=%s, contactPerson=%s, rating=%.1f, ingredients=%d]",
            name, contactInfo, contactPerson != null ? contactPerson : "N/A", reliabilityRating, ingredients.size());
    }
}

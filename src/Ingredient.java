import java.io.*;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Set;
import java.util.HashSet;

public class Ingredient implements Serializable {
    @Serial
    private static final long serialVersionUID = 1L;

    private static List<Ingredient> allIngredients = new ArrayList<>();

    private String name;
    private String unit;
    private double currentStock;
    private double reorderPoint;
    private double costPerUnit;

    private List<SupplyLog> supplyLogs;

    // Basic Association: Ingredient -> MenuItem (1..*)
    private Set<MenuItem> menuItems;

    public Ingredient() {
        this.supplyLogs = new ArrayList<>();
        this.menuItems = new HashSet<>();
    }

    public Ingredient(String name, String unit, double currentStock, double reorderPoint, double costPerUnit) {
        this.supplyLogs = new ArrayList<>();
        this.menuItems = new HashSet<>();
        setName(name);
        setUnit(unit);
        setCurrentStock(currentStock);
        setReorderPoint(reorderPoint);
        setCostPerUnit(costPerUnit);
        addIngredient(this);
    }

    public String getName() { return name; }
    public String getUnit() { return unit; }
    public double getCurrentStock() { return currentStock; }
    public double getReorderPoint() { return reorderPoint; }
    public double getCostPerUnit() { return costPerUnit; }

    public List<SupplyLog> getSupplyLogs() {
        return Collections.unmodifiableList(supplyLogs);
    }

    public Set<MenuItem> getMenuItems() {
        return Collections.unmodifiableSet(menuItems);
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

    // Basic Association: Ingredient -> MenuItem (1..*)
    public void addMenuItem(MenuItem menuItem) {
        if (menuItem == null) {
            throw new IllegalArgumentException("MenuItem cannot be null");
        }

        if (menuItems.contains(menuItem)) {
            return;
        }

        menuItems.add(menuItem);


        if (!menuItem.getIngredients().contains(this)) {
            menuItem.addIngredient(this);
        }
    }

    public void removeMenuItem(MenuItem menuItem) {
        if (menuItem == null) {
            throw new IllegalArgumentException("MenuItem cannot be null");
        }


        if (!menuItems.contains(menuItem)) {
            throw new IllegalArgumentException("This ingredient is not part of this menu item");
        }

        // Enforce 1..* multiplicity: cannot remove last menu item
        if (menuItems.size() <= 1) {
            throw new IllegalStateException("Cannot remove the last menu item. Ingredient must be used in at least one menu item (1..*)");
        }

        menuItems.remove(menuItem);


        if (menuItem.getIngredients().contains(this)) {
            menuItem.removeIngredientDirect(this);
        }
    }


    public boolean getNeedsReorder() {
        return currentStock < reorderPoint;
    }

    public void setName(String name) {
        if (name == null || name.trim().isEmpty()) {
            throw new IllegalArgumentException("Ingredient name cannot be null or empty");
        }
        this.name = name.trim();
    }

    public void setUnit(String unit) {
        if (unit == null || unit.trim().isEmpty()) {
            throw new IllegalArgumentException("Unit cannot be null or empty");
        }
        this.unit = unit.trim();
    }

    public void setCurrentStock(double currentStock) {
        if (currentStock < 0) {
            throw new IllegalArgumentException("Current stock cannot be negative");
        }
        this.currentStock = currentStock;
    }

    public void setReorderPoint(double reorderPoint) {
        if (reorderPoint < 0) {
            throw new IllegalArgumentException("Reorder point cannot be negative");
        }
        this.reorderPoint = reorderPoint;
    }

    public void setCostPerUnit(double costPerUnit) {
        if (costPerUnit <= 0) {
            throw new IllegalArgumentException("Cost per unit must be greater than zero");
        }
        this.costPerUnit = costPerUnit;
    }

    public void updateCurrentStock(double quantity) {
        setCurrentStock(currentStock + quantity);
    }

    public void increaseStock(double quantity) {
        if (quantity <= 0) {
            throw new IllegalArgumentException("Quantity to increase must be greater than zero");
        }
        updateCurrentStock(quantity);
    }

    public void reduceStock(double quantity) {
        if (quantity <= 0) {
            throw new IllegalArgumentException("Quantity to reduce must be greater than zero");
        }
        updateCurrentStock(-quantity);
    }

    private static void addIngredient(Ingredient ingredient) {
        if (ingredient == null) {
            throw new IllegalArgumentException("Ingredient cannot be null");
        }
        allIngredients.add(ingredient);
    }

    // Package-private method to directly remove without constraint check
    void removeMenuItemDirect(MenuItem menuItem) {
        menuItems.remove(menuItem);
    }

    public static List<Ingredient> getAllIngredients() {
        return Collections.unmodifiableList(allIngredients);
    }

    public static void clearExtent() {
        allIngredients.clear();
    }

    public static void saveExtent(String filename) throws IOException {
        String filepath = PersistenceConfig.getDataFilePath(filename);
        try (ObjectOutputStream out = new ObjectOutputStream(new FileOutputStream(filepath))) {
            out.writeObject(allIngredients);
        }
    }

    @SuppressWarnings("unchecked")
    public static boolean loadExtent(String filename) {
        String filepath = PersistenceConfig.getDataFilePath(filename);
        try (ObjectInputStream in = new ObjectInputStream(new FileInputStream(filepath))) {
            allIngredients = (List<Ingredient>) in.readObject();
            return true;
        } catch (IOException | ClassNotFoundException e) {
            allIngredients.clear();
            return false;
        }
    }

    @Override
    public String toString() {
        return String.format("Ingredient[%s, stock=%.2f %s, reorderPoint=%.2f, needsReorder=%s, cost=%.2f]",
            name, currentStock, unit, reorderPoint, getNeedsReorder(), costPerUnit);
    }
}

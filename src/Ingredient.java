import java.io.*;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class Ingredient implements Serializable {
    @Serial
    private static final long serialVersionUID = 1L;

    private static List<Ingredient> allIngredients = new ArrayList<>();

    private String name;
    private String unit;
    private double currentStock;
    private double reorderPoint;
    private double costPerUnit;
    
    // Aggregation: Ingredient -> Supplier (1..*)
    private List<Supplier> suppliers;
    // {Bag} Association: Ingredient -> SupplyLog (0..*)
    private List<SupplyLog> supplyLogs;

    public Ingredient() {
        this.suppliers = new ArrayList<>();
        this.supplyLogs = new ArrayList<>();
    }

    public Ingredient(String name, String unit, double currentStock, double reorderPoint, double costPerUnit) {
        this.suppliers = new ArrayList<>();
        this.supplyLogs = new ArrayList<>();
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
    
    public List<Supplier> getSuppliers() {
        return Collections.unmodifiableList(suppliers);
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

    public void addSupplier(Supplier supplier) {
        if (supplier == null) {
            throw new IllegalArgumentException("Supplier cannot be null");
        }
        
        if (suppliers.contains(supplier)) {
            throw new IllegalStateException("This supplier is already associated with this ingredient");
        }
        
        suppliers.add(supplier);
        
        // Reverse connection: add this ingredient to the supplier
        if (!supplier.getIngredients().contains(this)) {
            supplier.addIngredient(this);
        }
    }
    
    public void removeSupplier(Supplier supplier) {
        if (supplier == null) {
            throw new IllegalArgumentException("Supplier cannot be null");
        }
        
        // Check minimum multiplicity (1..*)
        if (suppliers.size() <= 1) {
            throw new IllegalStateException(
                "Cannot remove supplier: ingredient must have at least one supplier");
        }
        
        if (!suppliers.contains(supplier)) {
            throw new IllegalArgumentException("This supplier is not associated with this ingredient");
        }
        
        suppliers.remove(supplier);
        
        if (supplier.getIngredients().contains(this)) {
            supplier.removeIngredient(this);
        }
    }
    
    public void replaceSupplier(Supplier oldSupplier, Supplier newSupplier) {
        if (oldSupplier == null || newSupplier == null) {
            throw new IllegalArgumentException("Suppliers cannot be null");
        }
        
        if (!suppliers.contains(oldSupplier)) {
            throw new IllegalArgumentException("Old supplier is not associated with this ingredient");
        }
        
        if (suppliers.contains(newSupplier)) {
            throw new IllegalStateException("New supplier is already associated with this ingredient");
        }
        
        addSupplier(newSupplier);
        
        suppliers.remove(oldSupplier);
        if (oldSupplier.getIngredients().contains(this)) {
            oldSupplier.removeIngredient(this);
        }
    }
    
    public Supplier getPrimarySupplier() {
        if (suppliers.isEmpty()) {
            return null;
        }
        
        Supplier primary = suppliers.get(0);
        for (Supplier supplier : suppliers) {
            if (supplier.getReliabilityRating() > primary.getReliabilityRating()) {
                primary = supplier;
            }
        }
        return primary;
    }
    // ============ AGGREGATION END ============

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
        return String.format("Ingredient[%s, stock=%.2f %s, reorderPoint=%.2f, needsReorder=%s, cost=%.2f, suppliers=%d]",
            name, currentStock, unit, reorderPoint, getNeedsReorder(), costPerUnit, suppliers.size());
    }
}

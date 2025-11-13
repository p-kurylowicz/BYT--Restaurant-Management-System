import java.io.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;


public class SupplyLog implements Serializable {
    @Serial
    private static final long serialVersionUID = 1L;

    
    private static List<SupplyLog> allSupplyLogs = new ArrayList<>();

    
    private Supplier supplier;
    private Ingredient ingredient;

    
    private LocalDate supplyDate;
    private double costAtSupply;
    private double quantitySupplied;

    
    public SupplyLog() {}

    
    public SupplyLog(Supplier supplier, Ingredient ingredient, LocalDate supplyDate,
                    double costAtSupply, double quantitySupplied) {
        setSupplier(supplier);
        setIngredient(ingredient);
        setSupplyDate(supplyDate);
        setCostAtSupply(costAtSupply);
        setQuantitySupplied(quantitySupplied);
        addSupplyLog(this);
    }

    
    public Supplier getSupplier() { return supplier; }
    public Ingredient getIngredient() { return ingredient; }
    public LocalDate getSupplyDate() { return supplyDate; }
    public double getCostAtSupply() { return costAtSupply; }
    public double getQuantitySupplied() { return quantitySupplied; }

    
    public void setSupplier(Supplier supplier) {
        if (supplier == null) {
            throw new IllegalArgumentException("Supplier cannot be null");
        }
        this.supplier = supplier;
    }

    public void setIngredient(Ingredient ingredient) {
        if (ingredient == null) {
            throw new IllegalArgumentException("Ingredient cannot be null");
        }
        this.ingredient = ingredient;
    }

    public void setSupplyDate(LocalDate supplyDate) {
        if (supplyDate == null) {
            throw new IllegalArgumentException("Supply date cannot be null");
        }
        if (supplyDate.isAfter(LocalDate.now())) {
            throw new IllegalArgumentException("Supply date cannot be in the future");
        }
        this.supplyDate = supplyDate;
    }

    public void setCostAtSupply(double costAtSupply) {
        if (costAtSupply < 0) {
            throw new IllegalArgumentException("Cost at supply cannot be negative");
        }
        this.costAtSupply = costAtSupply;
    }

    public void setQuantitySupplied(double quantitySupplied) {
        if (quantitySupplied <= 0) {
            throw new IllegalArgumentException("Quantity supplied must be greater than zero");
        }
        this.quantitySupplied = quantitySupplied;
    }

    // Register delivery - updates ingredient stock
    public void registerDelivery() {
        ingredient.updateCurrentStock(quantitySupplied);
    }

    
    private static void addSupplyLog(SupplyLog supplyLog) {
        if (supplyLog == null) {
            throw new IllegalArgumentException("SupplyLog cannot be null");
        }
        allSupplyLogs.add(supplyLog);
    }

    public static List<SupplyLog> getAllSupplyLogs() {
        return Collections.unmodifiableList(allSupplyLogs);
    }

    public static void clearExtent() {
        allSupplyLogs.clear();
    }

    
    public static void saveExtent(String filename) throws IOException {
        String filepath = PersistenceConfig.getDataFilePath(filename);
        try (ObjectOutputStream out = new ObjectOutputStream(new FileOutputStream(filepath))) {
            out.writeObject(allSupplyLogs);
        }
    }

    @SuppressWarnings("unchecked")
    public static boolean loadExtent(String filename) {
        String filepath = PersistenceConfig.getDataFilePath(filename);
        try (ObjectInputStream in = new ObjectInputStream(new FileInputStream(filepath))) {
            allSupplyLogs = (List<SupplyLog>) in.readObject();
            return true;
        } catch (IOException | ClassNotFoundException e) {
            allSupplyLogs.clear();
            return false;
        }
    }

    @Override
    public String toString() {
        return String.format("SupplyLog[%s supplied %.2f %s of %s on %s at %.2f per unit]",
            supplier.getName(), quantitySupplied, ingredient.getUnit(),
            ingredient.getName(), supplyDate, costAtSupply);
    }
}

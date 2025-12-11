import java.io.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

// {Bag} Association Class - allows multiple logs
public class SupplyLog implements Serializable {
    @Serial
    private static final long serialVersionUID = 1L;

    private static List<SupplyLog> allSupplyLogs = new ArrayList<>();

    private Supplier supplier;
    private Ingredient ingredient;
    private LocalDate supplyDate;
    private double costAtSupply;
    private double quantitySupplied;

    private SupplyLog(Supplier supplier, Ingredient ingredient, LocalDate supplyDate,
                     double costAtSupply, double quantitySupplied) {
        if (supplier == null) {
            throw new IllegalArgumentException("Supplier cannot be null");
        }
        if (ingredient == null) {
            throw new IllegalArgumentException("Ingredient cannot be null");
        }

        this.supplier = supplier;
        this.ingredient = ingredient;
        setSupplyDate(supplyDate);
        setCostAtSupply(costAtSupply);
        setQuantitySupplied(quantitySupplied);
        addSupplyLogToExtent(this);
    }

    // {Bag} - allows multiple logs for same pair (no duplicate checking)
    public static SupplyLog create(Supplier supplier, Ingredient ingredient, LocalDate supplyDate,
                                   double costAtSupply, double quantitySupplied) {
        SupplyLog supplyLog = new SupplyLog(supplier, ingredient, supplyDate, costAtSupply, quantitySupplied);
        // reverse connection
        supplier.addSupplyLog(supplyLog);
        ingredient.addSupplyLog(supplyLog);
        return supplyLog;
    }

    public void delete() {
        if (supplier != null) {
            supplier.removeSupplyLog(this);
        }
        if (ingredient != null) {
            ingredient.removeSupplyLog(this);
        }
        allSupplyLogs.remove(this);
        this.supplier = null;
        this.ingredient = null;
    }

    public Supplier getSupplier() { return supplier; }
    public Ingredient getIngredient() { return ingredient; }
    public LocalDate getSupplyDate() { return supplyDate; }
    public double getCostAtSupply() { return costAtSupply; }
    public double getQuantitySupplied() { return quantitySupplied; }

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

    private static void addSupplyLogToExtent(SupplyLog supplyLog) {
        if (supplyLog == null) {
            throw new IllegalArgumentException("Supply log cannot be null");
        }
        allSupplyLogs.add(supplyLog);
    }

    public static List<SupplyLog> getAllSupplyLogsFromExtent() {
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
        String supplierName = supplier != null ? supplier.getName() : "Unknown";
        String ingredientName = ingredient != null ? ingredient.getName() : "Unknown";
        return String.format("SupplyLog[supplier=%s, ingredient=%s, date=%s, quantity=%.2f, cost=%.2f]",
            supplierName, ingredientName, supplyDate, quantitySupplied, costAtSupply);
    }
}

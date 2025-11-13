import java.io.*;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class Supplier implements Serializable {
    @Serial
    private static final long serialVersionUID = 1L;

    
    private static List<Supplier> allSuppliers = new ArrayList<>();

    
    private String name;
    private String phone;
    private String email;
    private double reliabilityRating;

    
    public Supplier() {}

    
    public Supplier(String name, String phone, String email, double reliabilityRating) {
        setName(name);
        setPhone(phone);
        setEmail(email);
        setReliabilityRating(reliabilityRating);
        addSupplier(this);
    }

    
    public String getName() { return name; }
    public String getPhone() { return phone; }
    public String getEmail() { return email; }
    public double getReliabilityRating() { return reliabilityRating; }

    
    public void setName(String name) {
        if (name == null || name.trim().isEmpty()) {
            throw new IllegalArgumentException("Supplier name cannot be null or empty");
        }
        this.name = name.trim();
    }

    public void setPhone(String phone) {
        if (phone == null || phone.trim().isEmpty()) {
            throw new IllegalArgumentException("Phone cannot be null or empty");
        }
        this.phone = phone.trim();
    }

    public void setEmail(String email) {
        if (email == null || email.trim().isEmpty()) {
            throw new IllegalArgumentException("Email cannot be null or empty");
        }
        if (!email.contains("@")) {
            throw new IllegalArgumentException("Email must contain @ symbol");
        }
        this.email = email.trim();
    }

    public void setReliabilityRating(double reliabilityRating) {
        if (reliabilityRating < 0 || reliabilityRating > 5) {
            throw new IllegalArgumentException("Reliability rating must be between 0 and 5");
        }
        this.reliabilityRating = reliabilityRating;
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
        return String.format("Supplier[%s, email=%s, phone=%s, rating=%.1f]",
            name, email, phone, reliabilityRating);
    }
}

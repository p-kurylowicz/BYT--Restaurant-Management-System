import java.io.*;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class Table implements Serializable {
    @Serial
    private static final long serialVersionUID = 1L;

    
    private static List<Table> allTables = new ArrayList<>();

    
    private TableStatus status;
    private int number;
    private int capacity;
    private String section;

    
    public Table() {}

    
    public Table(int number, int capacity, String section) {
        setNumber(number);
        setCapacity(capacity);
        setSection(section);
        this.status = TableStatus.AVAILABLE;
        addTable(this);
    }

    
    public TableStatus getStatus() { return status; }
    public int getNumber() { return number; }
    public int getCapacity() { return capacity; }
    public String getSection() { return section; }

    
    public void setNumber(int number) {
        if (number <= 0) {
            throw new IllegalArgumentException("Table number must be greater than zero");
        }
        this.number = number;
    }

    public void setCapacity(int capacity) {
        if (capacity <= 0) {
            throw new IllegalArgumentException("Table capacity must be greater than zero");
        }
        this.capacity = capacity;
    }

    public void setSection(String section) {
        if (section == null || section.trim().isEmpty()) {
            throw new IllegalArgumentException("Section cannot be null or empty");
        }
        this.section = section.trim();
    }

    // Change status
    public void changeStatus(TableStatus newStatus) {
        if (newStatus == null) {
            throw new IllegalArgumentException("Status cannot be null");
        }
        this.status = newStatus;
    }

    // Reserve table
    public void reserve() {
        if (this.status != TableStatus.AVAILABLE) {
            throw new IllegalStateException("Table is not available for reservation");
        }
        this.status = TableStatus.RESERVED;
    }


    public boolean isAvailableAtTime() {
        return this.status == TableStatus.AVAILABLE;
    }

    
    private static void addTable(Table table) {
        if (table == null) {
            throw new IllegalArgumentException("Table cannot be null");
        }
        allTables.add(table);
    }

    public static List<Table> getAllTables() {
        return Collections.unmodifiableList(allTables);
    }

    public static void clearExtent() {
        allTables.clear();
    }

    
    public static void saveExtent(String filename) throws IOException {
        String filepath = PersistenceConfig.getDataFilePath(filename);
        try (ObjectOutputStream out = new ObjectOutputStream(new FileOutputStream(filepath))) {
            out.writeObject(allTables);
        }
    }

    @SuppressWarnings("unchecked")
    public static boolean loadExtent(String filename) {
        String filepath = PersistenceConfig.getDataFilePath(filename);
        try (ObjectInputStream in = new ObjectInputStream(new FileInputStream(filepath))) {
            allTables = (List<Table>) in.readObject();
            return true;
        } catch (IOException | ClassNotFoundException e) {
            allTables.clear();
            return false;
        }
    }

    @Override
    public String toString() {
        return String.format("Table[number=%d, capacity=%d, section=%s, status=%s]",
            number, capacity, section, status);
    }
}

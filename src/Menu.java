import java.io.*;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Menu implements Serializable {
    @Serial
    private static final long serialVersionUID = 1L;

    
    private static List<Menu> allMenus = new ArrayList<>();


    private String name;
    private String season;

    public Menu() {}


    public Menu(String name, String season) {
        setName(name);
        setSeason(season);
        addMenu(this);
    }

    public String getName() { return name; }
    public String getSeason() { return season; }


    public void setName(String name) {
        if (name == null || name.trim().isEmpty()) {
            throw new IllegalArgumentException("Menu name cannot be null or empty");
        }
        this.name = name.trim();
    }

    public void setSeason(String season) {
        if (season == null || season.trim().isEmpty()) {
            throw new IllegalArgumentException("Season cannot be null or empty");
        }
        this.season = season.trim();
    }

    
    private static void addMenu(Menu menu) {
        if (menu == null) {
            throw new IllegalArgumentException("Menu cannot be null");
        }
        allMenus.add(menu);
    }

    public static List<Menu> getAllMenus() {
        return Collections.unmodifiableList(allMenus);
    }

    public static void clearExtent() {
        allMenus.clear();
    }

    
    public static void saveExtent(String filename) throws IOException {
        String filepath = PersistenceConfig.getDataFilePath(filename);
        try (ObjectOutputStream out = new ObjectOutputStream(new FileOutputStream(filepath))) {
            out.writeObject(allMenus);
        }
    }

    @SuppressWarnings("unchecked")
    public static boolean loadExtent(String filename) {
        String filepath = PersistenceConfig.getDataFilePath(filename);
        try (ObjectInputStream in = new ObjectInputStream(new FileInputStream(filepath))) {
            allMenus = (List<Menu>) in.readObject();
            return true;
        } catch (IOException | ClassNotFoundException e) {
            allMenus.clear();
            return false;
        }
    }

    @Override
    public String toString() {
        return String.format("Menu[%s, season=%s]", name, season);
    }
}

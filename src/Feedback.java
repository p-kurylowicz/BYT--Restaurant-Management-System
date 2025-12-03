import java.io.*;
import java.time.LocalDateTime;
import java.util.*;

public class Feedback implements Serializable {
    @Serial
    private static final long serialVersionUID = 1L;

    private static List<Feedback> allFeedback = new ArrayList<>();

    private String title;
    private String description;
    private int rating;
    private Sensory sensoryFeedback;
    private LocalDateTime editedAt;
    private Set<String> keywords = new HashSet<>();
    private MenuItem menuItem;
    private Customer author;


    public Feedback() {}

    public Feedback(MenuItem menuItem, Customer author, String title, String description, int rating,
        Sensory sensoryFeedback, LocalDateTime editedAt,
        Set<String> keywords) {
        setAuthor(author);
        setMenuItem(menuItem); 
        setTitle(title);
        setDescription(description);
        setRating(rating);
        setSensoryFeedback(sensoryFeedback);
        setEditedAt(editedAt);
        setKeywords(keywords);
    
        addFeedback(this);
    }


    public String getTitle() { return title; }
    public String getDescription() { return description; }
    public int getRating() { return rating; }
    public Sensory getSensoryFeedback() { return sensoryFeedback; }
    public LocalDateTime getEditedAt() { return editedAt; }
    public Set<String> getKeywords() { return Collections.unmodifiableSet(keywords); }
    public MenuItem getMenuItem() { return menuItem; } 
    public Customer getAuthor() {
        return author;
    }


    public void setTitle(String title) {
        if (title == null || title.trim().isEmpty())
            throw new IllegalArgumentException("Title cannot be null or empty");
        this.title = title.trim();
    }

    public void setDescription(String description) {
        if (description == null || description.trim().isEmpty())
            throw new IllegalArgumentException("Description cannot be null or empty");
        this.description = description.trim();
    }

    public void setAuthor(Customer author) {
        if (author == null) {
            throw new IllegalArgumentException("Author cannot be null for feedback");
        }
        this.author = author;
        author.addFeedback(this);
    }

    public void setRating(int rating) {
        if (rating < 0 || rating > 5)
            throw new IllegalArgumentException("Rating must be 0–5");
        this.rating = rating;
    }

    public void setSensoryFeedback(Sensory sensoryFeedback) {
        this.sensoryFeedback = sensoryFeedback;
    }

    public void setEditedAt(LocalDateTime editedAt) {
        this.editedAt = editedAt; // optional → null allowed
    }

    public void setKeywords(Set<String> keywords) {
        if (keywords != null)
            this.keywords = new HashSet<>(keywords);
    }

    private static void addFeedback(Feedback f) {
        if (f == null)
            throw new IllegalArgumentException("Feedback cannot be null");
        allFeedback.add(f);
    }

    public static List<Feedback> getAllFeedback() {
        return Collections.unmodifiableList(allFeedback);
    }

    public static void clearExtent() {
        allFeedback.clear();
    }

    public static void saveExtent(String filename) throws IOException {
        String path = PersistenceConfig.getDataFilePath(filename);
        try (ObjectOutputStream out = new ObjectOutputStream(new FileOutputStream(path))) {
            out.writeObject(allFeedback);
        }
    }

    public void setMenuItem(MenuItem menuItem) {
        if (menuItem == null) {
            throw new IllegalArgumentException("MenuItem cannot be null for a review");
        }
        this.menuItem = menuItem;
        menuItem.addReview(this); 
    }

    @SuppressWarnings("unchecked")
    public static boolean loadExtent(String filename) {
        String path = PersistenceConfig.getDataFilePath(filename);
        try (ObjectInputStream in = new ObjectInputStream(new FileInputStream(path))) {
            allFeedback = (List<Feedback>) in.readObject();
    
            for (Feedback f : allFeedback) {
                if (f.getAuthor() != null) {
                    f.getAuthor().addFeedback(f);
                }
            }
    
            return true;
        } catch (IOException | ClassNotFoundException e) {
            allFeedback.clear();
            return false;
        }
    }


    @Override
    public String toString() {
        return "Feedback[" +
               "title=" + title +
               ", rating=" + rating +
               ", editedAt=" + editedAt +
               ", keywords=" + keywords +
               ", sensory=" + sensoryFeedback +
               "]";
    }
}

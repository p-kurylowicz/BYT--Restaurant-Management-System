import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;
import static org.junit.jupiter.api.Assertions.*;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;

@DisplayName("Feedback Attribute Tests")
class FeedbackTest {

    private Customer customer;
    private MenuItem menuItem;

    @BeforeEach
    void setUp() {
        // Clear extents
        Customer.getAllCustomers().clear();
        MenuItem.getAllMenuItems().clear();
        Feedback.getAllFeedback().clear();


        customer = new Customer("Ivan", "Petrov", "ivan@test.com", "+123456789", 
                               LocalDateTime.now().minusDays(30));
        NutritionalInfo nutritionalInfo = new NutritionalInfo(500, 20, 30, 40);
        menuItem = new Dish("Test Dish", "Delicious test dish", 25.50, "test.jpg", 
                           "Polish", nutritionalInfo, DishType.MAIN_COURSE);
    }

    @Test
    @DisplayName("Should create feedback with valid title")
    void testTitle() {
        Sensory sensory = new Sensory(65.0, "Sweet", "Aromatic");
        Set<String> keywords = new HashSet<>();
        keywords.add("delicious");

        Feedback feedback = new Feedback(menuItem, customer, "Great dish", "Very tasty", 
                                         5, sensory, null, keywords);

        assertEquals("Great dish", feedback.getTitle());
    }

    @Test
    @DisplayName("Should create feedback with valid description")
    void testDescription() {
        Sensory sensory = new Sensory(65.0, "Sweet", "Aromatic");
        Set<String> keywords = new HashSet<>();

        Feedback feedback = new Feedback(menuItem, customer, "Title", "Very tasty and well cooked", 
                                         5, sensory, null, keywords);

        assertEquals("Very tasty and well cooked", feedback.getDescription());
    }

    @Test
    @DisplayName("Should accept valid rating range 0-5")
    void testRating() {
        Sensory sensory = new Sensory(65.0, "Sweet", "Aromatic");
        Set<String> keywords = new HashSet<>();

        Feedback feedback = new Feedback(menuItem, customer, "Title", "Description", 
                                         4, sensory, null, keywords);

        assertEquals(4, feedback.getRating());
    }

    @Test
    @DisplayName("Should store sensory feedback")
    void testSensoryFeedback() {
        Sensory sensory = new Sensory(65.0, "Sweet", "Aromatic");
        Set<String> keywords = new HashSet<>();

        Feedback feedback = new Feedback(menuItem, customer, "Title", "Description", 
                                         5, sensory, null, keywords);

        assertNotNull(feedback.getSensoryFeedback());
        assertEquals(sensory, feedback.getSensoryFeedback());
        assertEquals(65.0, feedback.getSensoryFeedback().getTemperature());
        assertEquals("Sweet", feedback.getSensoryFeedback().getTaste());
        assertEquals("Aromatic", feedback.getSensoryFeedback().getSmell());
    }

    @Test
    @DisplayName("EditedAt should be optional (nullable)")
    void testEditedAt() {
        Sensory sensory = new Sensory(65.0, "Sweet", "Aromatic");
        Set<String> keywords = new HashSet<>();

        Feedback feedback = new Feedback(menuItem, customer, "Title", "Description", 
                                         5, sensory, null, keywords);

        assertNull(feedback.getEditedAt(), "EditedAt should be null initially");

        LocalDateTime editTime = LocalDateTime.now();
        feedback.setEditedAt(editTime);
        assertEquals(editTime, feedback.getEditedAt());
    }

    @Test
    @DisplayName("Should store keywords set")
    void testKeywords() {
        Sensory sensory = new Sensory(65.0, "Sweet", "Aromatic");
        Set<String> keywords = new HashSet<>();
        keywords.add("delicious");
        keywords.add("spicy");
        keywords.add("fresh");

        Feedback feedback = new Feedback(menuItem, customer, "Title", "Description", 
                                         5, sensory, null, keywords);

        assertEquals(3, feedback.getKeywords().size());
        assertTrue(feedback.getKeywords().contains("delicious"));
        assertTrue(feedback.getKeywords().contains("spicy"));
        assertTrue(feedback.getKeywords().contains("fresh"));
    }

    @Test
    @DisplayName("Should reference menu item")
    void testMenuItem() {
        Sensory sensory = new Sensory(65.0, "Sweet", "Aromatic");
        Set<String> keywords = new HashSet<>();

        Feedback feedback = new Feedback(menuItem, customer, "Title", "Description", 
                                         5, sensory, null, keywords);

        assertEquals(menuItem, feedback.getMenuItem());
    }

    @Test
    @DisplayName("Should reference author (customer)")
    void testAuthor() {
        Sensory sensory = new Sensory(65.0, "Sweet", "Aromatic");
        Set<String> keywords = new HashSet<>();

        Feedback feedback = new Feedback(menuItem, customer, "Title", "Description", 
                                         5, sensory, null, keywords);

        assertEquals(customer, feedback.getAuthor());
    }
}

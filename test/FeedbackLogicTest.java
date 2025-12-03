import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;
import static org.junit.jupiter.api.Assertions.*;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;

@DisplayName("Feedback Tests")
class FeedbackLogicTest {

    private Customer customer;
    private MenuItem menuItem;

    @BeforeEach
    void setUp() {
        Customer.clearExtent();
        MenuItem.clearExtent();
        Feedback.clearExtent();

        customer = new Customer("Ivan", "Petrov", "ivan@test.com", "+123456789",
                               LocalDateTime.now().minusDays(30));

        NutritionalInfo nutritionalInfo = new NutritionalInfo(500, 20, 30, 40, 10);
        menuItem = new MainDish("Test Dish", "Delicious test dish", 25.50, "test.jpg",
                           "Polish", nutritionalInfo, 2);
    }

    @Test
    @DisplayName("Should create feedback with valid data")
    void testCreateFeedback() {
        Feedback feedback = new Feedback(menuItem, customer, "Great!", "Very tasty", 5, null, null, null);

        assertNotNull(feedback);
        assertEquals("Great!", feedback.getTitle());
        assertEquals("Very tasty", feedback.getDescription());
        assertEquals(5, feedback.getRating());
        assertEquals(customer, feedback.getAuthor());
        assertEquals(menuItem, feedback.getMenuItem());
    }

    @Test
    @DisplayName("Should not allow null customer")
    void testNullCustomer() {
        assertThrows(IllegalArgumentException.class, () -> {
            new Feedback(menuItem, null, "Great!", "Very tasty", 5, null, null, null);
        });
    }

    @Test
    @DisplayName("Should not allow null menu item")
    void testNullMenuItem() {
        assertThrows(IllegalArgumentException.class, () -> {
            new Feedback(null, customer, "Great!", "Very tasty", 5, null, null, null);
        });
    }

    @Test
    @DisplayName("Should not allow empty title")
    void testEmptyTitle() {
        assertThrows(IllegalArgumentException.class, () -> {
            new Feedback(menuItem, customer, "", "Very tasty", 5, null, null, null);
        });
    }

    @Test
    @DisplayName("Should not allow empty description")
    void testEmptyDescription() {
        assertThrows(IllegalArgumentException.class, () -> {
            new Feedback(menuItem, customer, "Great!", "", 5, null, null, null);
        });
    }

    @Test
    @DisplayName("Should not allow rating below 0")
    void testRatingTooLow() {
        assertThrows(IllegalArgumentException.class, () -> {
            new Feedback(menuItem, customer, "Bad", "Terrible", -1, null, null, null);
        });
    }

    @Test
    @DisplayName("Should not allow rating above 5")
    void testRatingTooHigh() {
        assertThrows(IllegalArgumentException.class, () -> {
            new Feedback(menuItem, customer, "Amazing", "Best ever", 6, null, null, null);
        });
    }

    @Test
    @DisplayName("Should allow rating from 0 to 5")
    void testValidRatings() {
        for (int rating = 0; rating <= 5; rating++) {
            final int r = rating;
            assertDoesNotThrow(() -> {
                new Feedback(menuItem, customer, "Test", "Test feedback", r, null, null, null);
            });
        }
    }

    @Test
    @DisplayName("Should set keywords correctly")
    void testKeywords() {
        Set<String> keywords = new HashSet<>();
        keywords.add("delicious");
        keywords.add("spicy");
        keywords.add("authentic");

        Feedback feedback = new Feedback(menuItem, customer, "Excellent", "Amazing food", 5, null, null, keywords);

        assertEquals(3, feedback.getKeywords().size());
        assertTrue(feedback.getKeywords().contains("delicious"));
        assertTrue(feedback.getKeywords().contains("spicy"));
        assertTrue(feedback.getKeywords().contains("authentic"));
    }

    @Test
    @DisplayName("Should set edited timestamp")
    void testEditedTimestamp() {
        LocalDateTime editedTime = LocalDateTime.now();
        Feedback feedback = new Feedback(menuItem, customer, "Good", "Nice dish", 4, null, editedTime, null);

        assertEquals(editedTime, feedback.getEditedAt());
    }

    @Test
    @DisplayName("Should allow null sensory feedback")
    void testNullSensoryFeedback() {
        assertDoesNotThrow(() -> {
            new Feedback(menuItem, customer, "Good", "Nice", 4, null, null, null);
        });
    }

    @Test
    @DisplayName("Should establish reverse connection with customer")
    void testCustomerReverseConnection() {
        Feedback feedback = new Feedback(menuItem, customer, "Great", "Loved it", 5, null, null, null);

        assertTrue(customer.getFeedbacks().contains(feedback));
    }

    @Test
    @DisplayName("Should establish reverse connection with menu item")
    void testMenuItemReverseConnection() {
        Feedback feedback = new Feedback(menuItem, customer, "Great", "Loved it", 5, null, null, null);

        assertTrue(menuItem.getReviews().contains(feedback));
    }
}

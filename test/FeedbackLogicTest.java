import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;
import static org.junit.jupiter.api.Assertions.*;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.HashSet;
import java.util.Set;

@DisplayName("Review Business Logic Tests")
class ReviewTest {

    private Customer customer;
    private MenuItem menuItem;
    private Order order;

    @BeforeEach
    void setUp() {
        Customer.getAllCustomers().clear();
        MenuItem.getAllMenuItems().clear();
        Order.getAllOrders().clear();
        Review.getAllReviews().clear();


        customer = new Customer("Ivan", "Petrov", "ivan@test.com", "+123456789", 
                               LocalDateTime.now().minusDays(30));

        NutritionalInfo nutritionalInfo = new NutritionalInfo(500, 20, 30, 40);
        menuItem = new Dish("Test Dish", "Delicious test dish", 25.50, "test.jpg", 
                           "Polish", nutritionalInfo, DishType.MAIN_COURSE);

        order = new DineInOrder();
        order.setCustomer(customer);
        order.addMenuItem(menuItem);
        order.completeOrder();
    }

    @Test
    @DisplayName("Should not allow review without completed order")
    void testCannotCreateReviewWithoutOrder() {
        Customer customerWithoutOrder = new Customer("Anna", "Kowalska", "anna@test.com", 
                                                     "+987654321", LocalDateTime.now());

        assertThrows(IllegalStateException.class, () -> {
            new Review(menuItem, customerWithoutOrder, "Great!", "Very tasty", 5, null, null);
        }, "Should throw exception when customer has no completed orders for this menu item");
    }

    @Test
    @DisplayName("Should not allow review when order is not completed")
    void testCannotCreateReviewWithActiveOrder() {
        Customer newCustomer = new Customer("Maria", "Nowak", "maria@test.com", 
                                           "+111222333", LocalDateTime.now());
        Order activeOrder = new DineInOrder();
        activeOrder.setCustomer(newCustomer);
        activeOrder.addMenuItem(menuItem);

        assertThrows(IllegalStateException.class, () -> {
            new Review(menuItem, newCustomer, "Great!", "Very tasty", 5, null, null);
        }, "Should throw exception when order is not completed");
    }

    @Test
    @DisplayName("Should allow review when customer has completed order")
    void testCanCreateReviewWithCompletedOrder() {
        assertDoesNotThrow(() -> {
            Review review = new Review(menuItem, customer, "Excellent", "Best dish ever", 5, null, null);
            assertNotNull(review);
            assertEquals(customer, review.getCustomer());
            assertEquals(menuItem, review.getMenuItem());
        });
    }

    @Test
    @DisplayName("Should allow customer to review same menu item only once per order")
    void testOneReviewPerOrderPerMenuItem() {
        Review firstReview = new Review(menuItem, customer, "First", "First review", 5, null, null);
        assertNotNull(firstReview);

        assertThrows(IllegalStateException.class, () -> {
            new Review(menuItem, customer, "Second", "Second review", 4, null, null);
        }, "Should not allow multiple reviews for same menu item from same customer without new order");
    }

    @Test
    @DisplayName("Should allow customer to review different menu items in same order")
    void testMultipleReviewsForDifferentMenuItems() {
        MenuItem menuItem2 = new Dish("Second Dish", "Another dish", 20.0, "dish2.jpg",
                                     "Polish", new NutritionalInfo(400, 15, 25, 30), 
                                     DishType.APPETIZER);
        order.addMenuItem(menuItem2);

        assertDoesNotThrow(() -> {
            Review review1 = new Review(menuItem, customer, "Good", "Nice", 4, null, null);
            Review review2 = new Review(menuItem2, customer, "Great", "Excellent", 5, null, null);
            
            assertNotNull(review1);
            assertNotNull(review2);
        }, "Should allow reviews for different menu items in same order");
    }

    @Test
    @DisplayName("Should allow new review after new completed order")
    void testCanReviewAfterNewOrder() {
        Review firstReview = new Review(menuItem, customer, "First", "First review", 5, null, null);
        assertNotNull(firstReview);

        Order newOrder = new DineInOrder();
        newOrder.setCustomer(customer);
        newOrder.addMenuItem(menuItem);
        newOrder.completeOrder();

        assertDoesNotThrow(() -> {
            Review secondReview = new Review(menuItem, customer, "Second", "Second review after new order", 4, null, null);
            assertNotNull(secondReview);
        }, "Should allow review after new completed order");
    }
}

import org.junit.jupiter.api.*;
import static org.junit.jupiter.api.Assertions.*;

public class AggregationAssociationTest {

    @BeforeEach
    void setup() {
        Menu.clearExtent();
        MenuItem.clearExtent();
    }

    @Test
    @DisplayName("Aggregation: Adding menu item establishes reverse connection")
    void testAddMenuItemEstablishesReverseConnection() {
        Menu menu = new Menu("Summer Menu", "Summer");
        MainDish dish = new MainDish("Grilled Chicken", "Delicious grilled chicken", 45.0, "img.jpg",
            "Polish", new NutritionalInfo(300, 30, 10, 15, 2), 2);

        menu.addMenuItem(dish);

        assertTrue(menu.getMenuItems().contains(dish));
        assertTrue(dish.getMenus().contains(menu));
    }

    @Test
    @DisplayName("Aggregation: Adding menu establishes reverse connection")
    void testAddMenuEstablishesReverseConnection() {
        Menu menu = new Menu("Winter Menu", "Winter");
        Dessert dessert = new Dessert("Chocolate Cake", "Rich chocolate cake", 25.0, "cake.jpg",
            "French", new NutritionalInfo(450, 5, 60, 20, 1), true);

        dessert.addMenu(menu);

        assertTrue(dessert.getMenus().contains(menu));
        assertTrue(menu.getMenuItems().contains(dessert));
    }

    @Test
    @DisplayName("Aggregation: Menu can exist without menu items (0..*)")
    void testMenuCanExistWithoutMenuItems() {
        Menu menu = new Menu("Empty Menu", "Spring");

        assertNotNull(menu);
        assertEquals(0, menu.getMenuItems().size());
    }

    @Test
    @DisplayName("Aggregation: MenuItem can be in multiple menus")
    void testMenuItemSharedBetweenMenus() {
        Beverage beverage = new Beverage("Cola", "Refreshing cola", 8.0, "cola.jpg",
            "American", new NutritionalInfo(150, 0, 40, 0, 0), 0.0);
        Menu menu1 = new Menu("Lunch Menu", "All Year");
        Menu menu2 = new Menu("Dinner Menu", "All Year");
        Menu menu3 = new Menu("Kids Menu", "All Year");

        menu1.addMenuItem(beverage);
        menu2.addMenuItem(beverage);
        menu3.addMenuItem(beverage);

        assertEquals(3, beverage.getMenus().size());
        assertTrue(beverage.getMenus().contains(menu1));
        assertTrue(beverage.getMenus().contains(menu2));
        assertTrue(beverage.getMenus().contains(menu3));
    }

    @Test
    @DisplayName("Aggregation: Removing menu item removes reverse connection")
    void testRemoveMenuItemRemovesReverseConnection() {
        MainDish dish = new MainDish("Steak", "Juicy steak", 75.0, "steak.jpg",
            "American", new NutritionalInfo(500, 40, 5, 30, 1), 1);
        Menu menu1 = new Menu("Menu 1", "Spring");
        Menu menu2 = new Menu("Menu 2", "Summer");
        Menu menu3 = new Menu("Menu 3", "Fall");

        menu1.addMenuItem(dish);
        menu2.addMenuItem(dish);
        menu3.addMenuItem(dish);

        menu1.removeMenuItem(dish);

        assertFalse(menu1.getMenuItems().contains(dish));
        assertFalse(dish.getMenus().contains(menu1));
        assertTrue(dish.getMenus().contains(menu2));
        assertTrue(dish.getMenus().contains(menu3));
    }

    @Test
    @DisplayName("Aggregation: MenuItem can remove menu")
    void testMenuItemRemoveMenu() {
        Dessert dessert = new Dessert("Ice Cream", "Vanilla ice cream", 15.0, "ice.jpg",
            "Italian", new NutritionalInfo(200, 3, 30, 8, 0), false);
        Menu menu1 = new Menu("Menu A", "Summer");
        Menu menu2 = new Menu("Menu B", "Winter");

        menu1.addMenuItem(dessert);
        menu2.addMenuItem(dessert);

        dessert.removeMenu(menu1);

        assertFalse(dessert.getMenus().contains(menu1));
        assertFalse(menu1.getMenuItems().contains(dessert));
        assertTrue(dessert.getMenus().contains(menu2));
    }

    @Test
    @DisplayName("Aggregation: Cannot add duplicate menu item")
    void testCannotAddDuplicateMenuItem() {
        Menu menu = new Menu("Test Menu", "Spring");
        MainDish dish = new MainDish("Pasta", "Italian pasta", 35.0, "pasta.jpg",
            "Italian", new NutritionalInfo(400, 15, 60, 10, 3), 0);

        menu.addMenuItem(dish);

        IllegalStateException exception = assertThrows(IllegalStateException.class, () -> {
            menu.addMenuItem(dish);
        });
        assertTrue(exception.getMessage().contains("already in this menu"));
    }

    @Test
    @DisplayName("Aggregation: Cannot add null menu item")
    void testCannotAddNullMenuItem() {
        Menu menu = new Menu("Test Menu", "Summer");

        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> {
            menu.addMenuItem(null);
        });
        assertTrue(exception.getMessage().contains("MenuItem cannot be null"));
    }

    @Test
    @DisplayName("Aggregation: Cannot remove menu item not in menu")
    void testCannotRemoveMenuItemNotInMenu() {
        Menu menu = new Menu("Test Menu", "Fall");
        Beverage beverage = new Beverage("Water", "Still water", 5.0, "water.jpg",
            "Polish", new NutritionalInfo(0, 0, 0, 0, 0), 0.0);

        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> {
            menu.removeMenuItem(beverage);
        });
        assertTrue(exception.getMessage().contains("not in this menu"));
    }

    @Test
    @DisplayName("Aggregation: MenuItem can be created without menus initially")
    void testMenuItemCanBeCreatedWithoutMenus() {
        MainDish dish = new MainDish("Burger", "Beef burger", 30.0, "burger.jpg",
            "American", new NutritionalInfo(600, 25, 50, 35, 2), 1);

        assertNotNull(dish);
        assertEquals(0, dish.getMenus().size());
    }

    @Test
    @DisplayName("Aggregation (1..*): Cannot remove last menu from MenuItem")
    void testCannotRemoveLastMenuFromMenuItem() {
        Menu menu = new Menu("Test Menu", "Spring");
        MainDish dish = new MainDish("Steak", "Juicy steak", 75.0, "steak.jpg",
            "American", new NutritionalInfo(500, 40, 5, 30, 1), 1);

        menu.addMenuItem(dish);

        IllegalStateException exception = assertThrows(IllegalStateException.class, () -> {
            dish.removeMenu(menu);
        });
        assertTrue(exception.getMessage().contains("Cannot remove the last menu"));
        assertTrue(exception.getMessage().contains("1..*"));
    }

    @Test
    @DisplayName("Aggregation (1..*): Can remove menu when multiple exist")
    void testCanRemoveMenuWhenMultipleExist() {
        Menu menu1 = new Menu("Menu 1", "Spring");
        Menu menu2 = new Menu("Menu 2", "Summer");
        Dessert dessert = new Dessert("Ice Cream", "Vanilla ice cream", 15.0, "ice.jpg",
            "Italian", new NutritionalInfo(200, 3, 30, 8, 0), false);

        menu1.addMenuItem(dessert);
        menu2.addMenuItem(dessert);

        assertEquals(2, dessert.getMenus().size());

        assertDoesNotThrow(() -> {
            dessert.removeMenu(menu1);
        });

        assertEquals(1, dessert.getMenus().size());
        assertFalse(dessert.getMenus().contains(menu1));
        assertTrue(dessert.getMenus().contains(menu2));
    }

    @Test
    @DisplayName("Aggregation: Multiple menu items in one menu")
    void testMultipleMenuItemsInOneMenu() {
        Menu menu = new Menu("Full Menu", "All Year");
        MainDish dish1 = new MainDish("Dish 1", "Description 1", 40.0, "d1.jpg",
            "Polish", new NutritionalInfo(350, 25, 30, 15, 2), 1);
        MainDish dish2 = new MainDish("Dish 2", "Description 2", 45.0, "d2.jpg",
            "Italian", new NutritionalInfo(400, 20, 40, 18, 3), 2);
        Dessert dessert = new Dessert("Dessert", "Sweet dessert", 20.0, "des.jpg",
            "French", new NutritionalInfo(300, 4, 50, 10, 1), true);

        menu.addMenuItem(dish1);
        menu.addMenuItem(dish2);
        menu.addMenuItem(dessert);

        assertEquals(3, menu.getMenuItems().size());
        assertTrue(menu.getMenuItems().contains(dish1));
        assertTrue(menu.getMenuItems().contains(dish2));
        assertTrue(menu.getMenuItems().contains(dessert));
    }

    @Test
    @DisplayName("Aggregation: Reverse connection works both ways")
    void testReverseConnectionBothWays() {
        Menu menu = new Menu("Test Menu", "Summer");
        Beverage beverage = new Beverage("Juice", "Orange juice", 10.0, "juice.jpg",
            "American", new NutritionalInfo(120, 1, 28, 0, 0), 0.0);

        menu.addMenuItem(beverage);
        assertTrue(beverage.getMenus().contains(menu));

        Menu menu2 = new Menu("Another Menu", "Winter");
        beverage.addMenu(menu2);
        assertTrue(menu2.getMenuItems().contains(beverage));
    }
}

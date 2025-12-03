import org.junit.jupiter.api.*;
import static org.junit.jupiter.api.Assertions.*;

public class QualifiedAssociationTest {

    @BeforeEach
    void setup() {
        Menu.clearExtent();
        MenuItem.clearExtent();
    }

    @Test
    @DisplayName("Qualified Association: Add item to menu (Reverse Connection)")
    void testAddItemToMenu() {
        Menu menu = new Menu("Summer Menu", "Summer");
        NutritionalInfo nutrition = new NutritionalInfo(100, 10, 10, 10, 10);
        MenuItem item = new MainDish("Pasta", "Desc", 20.0, "img", "Italy", nutrition, 1);

        menu.addMenuItem(item);

        assertEquals(item, menu.getMenuItem("Pasta"));
        assertEquals(menu, item.getMenu());
    }

    @Test
    @DisplayName("Qualified Association: Set menu for item (Reverse Connection)")
    void testSetMenuForItem() {
        Menu menu = new Menu("Winter Menu", "Winter");
        NutritionalInfo nutrition = new NutritionalInfo(100, 10, 10, 10, 10);
        MenuItem item = new MainDish("Soup", "Desc", 15.0, "img", "Poland", nutrition, 1);

        item.setMenu(menu);

        assertEquals(menu, item.getMenu());
        assertEquals(item, menu.getMenuItem("Soup"));
    }

    @Test
    @DisplayName("Qualified Association: Remove item from menu")
    void testRemoveItem() {
        Menu menu = new Menu("Summer Menu", "Summer");
        NutritionalInfo nutrition = new NutritionalInfo(100, 10, 10, 10, 10);
        MenuItem item = new MainDish("Pasta", "Desc", 20.0, "img", "Italy", nutrition, 1);
        menu.addMenuItem(item);

        menu.removeMenuItem(item);

        assertNull(menu.getMenuItem("Pasta"));
        assertNull(item.getMenu());
    }

    @Test
    @DisplayName("Qualified Association: Duplicate qualifier logic")
    void testDuplicateQualifier() {
        Menu menu = new Menu("Summer Menu", "Summer");
        NutritionalInfo nutrition = new NutritionalInfo(100, 10, 10, 10, 10);
        MenuItem item1 = new MainDish("Pasta", "Desc", 20.0, "img", "Italy", nutrition, 1);
        MenuItem item2 = new MainDish("Pasta", "Desc2", 25.0, "img", "Italy", nutrition, 1);

        menu.addMenuItem(item1);
        menu.addMenuItem(item2); 
        
        assertEquals(item1, menu.getMenuItem("Pasta"));
        assertNotEquals(item2, menu.getMenuItem("Pasta"));
    }
}

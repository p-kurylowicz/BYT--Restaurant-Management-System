import org.junit.jupiter.api.Test;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

class MenuItemInheritanceTest {

    private NutritionalInfo sampleNutrition() {
        return new NutritionalInfo(200, 10, 30, 5, 3);
    }

    private Set<String> sampleAllergens() {
        Set<String> allergens = new HashSet<>();
        allergens.add("Gluten");
        allergens.add("Dairy");
        return allergens;
    }

    @Test
    void beverageShouldBeMenuItem() {
        MenuItem item = new Beverage(
                "Coffee",
                "Hot drink",
                10.0,
                "coffee.png",
                "Italy",
                sampleNutrition(),
                Double.valueOf(0.0)
        );

        assertNotNull(item);
        assertTrue(item instanceof MenuItem);
    }

    @Test
    void allMenuItemsShouldBeUsablePolymorphically() {
        MenuItem beverage = new Beverage("Tea", "Hot tea", 8.0, "tea.png", "China", sampleNutrition(), Double.valueOf(0.0));
        MenuItem mainDish = new MainDish("Pizza", "Cheese pizza", 30.0, "pizza.png", "Italy", sampleNutrition(), 2);
        MenuItem dessert = new Dessert("Cake", "Chocolate cake", 15.0, "cake.png", "France", sampleNutrition(), false);

        MenuItem[] items = {beverage, mainDish, dessert};

        for (MenuItem item : items) {
            assertNotNull(item.getName());
            assertTrue(item.getPrice() > 0);
            assertNotNull(item.getDescription());
        }
    }

    @Test
    void inheritanceHierarchyShouldBeCorrect() {
        Beverage beverage = new Beverage("Beer", "Alcoholic drink", 12.0, "beer.png", "Germany", sampleNutrition(), Double.valueOf(5.0));

        assertTrue(beverage instanceof MenuItem);
        assertTrue(beverage instanceof Beverage);
//        assertFalse(beverage instanceof Dessert);
//        assertFalse(beverage instanceof MainDish);
    }

    @Test
    void liskovSubstitutionPrincipleShouldHold() {
        MenuItem item = new Beverage("Juice", "Orange juice", 9.0, "juice.png", "Spain", sampleNutrition(), Double.valueOf(0.0));

        item.setAvailability(MenuItemAvailability.AVAILABLE);

        assertEquals(MenuItemAvailability.AVAILABLE, item.getAvailability());
    }

    @Test
    void menuItemsCollectionShouldAcceptAllSubtypes() {
        List<MenuItem> menu = new ArrayList<>();

        menu.add(new Beverage("Water", "Still water", 5.0, "water.png", "USA", sampleNutrition(), Double.valueOf(0.0)));
        menu.add(new MainDish("Burger", "Beef burger", 25.0, "burger.png", "USA", sampleNutrition(), 1));
        menu.add(new Dessert("Ice Cream", "Vanilla", 12.0, "icecream.png", "Italy", sampleNutrition(), false));

        assertEquals(3, menu.size());
    }
}

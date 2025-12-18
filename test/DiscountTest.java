import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;
import static org.junit.jupiter.api.Assertions.*;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.Set;
import java.util.HashSet;
import java.util.List;

@DisplayName("Discount Multi-Aspect Inheritance Tests")
public class DiscountTest {

    @BeforeEach
    public void setUp() {
        Discount.clearExtent();
        OrderLevelDiscount.clearExtent();
        ItemLevelDiscount.clearExtent();
    }

    @Test
    @DisplayName("1. Create Time-based Order-level discount")
    public void testTimeBasedOrderDiscount() {
        LocalDate date = LocalDate.of(2024, 12, 25);
        LocalTime time = LocalTime.of(18, 0);

        Discount discount = new OrderLevelDiscount("XMAS2024", date, time, 20.0);

        assertNotNull(discount);
        assertEquals("XMAS2024", discount.getCode());
        assertTrue(discount.isTimeBased());
        assertFalse(discount.isVolumeBased());
        assertTrue(discount.isOrderLevel());
        assertFalse(discount.isItemLevel());
        assertEquals(date, discount.getDate());
        assertEquals(time, discount.getTime());
        assertEquals(20.0, discount.getDiscountPercentage());
    }

    @Test
    @DisplayName("2. Create Time-based Item-level discount")
    public void testTimeBasedItemDiscount() {
        LocalDate date = LocalDate.of(2024, 12, 31);
        LocalTime time = LocalTime.of(23, 59);
        Set<String> items = new HashSet<>(Set.of("Pizza", "Burger"));

        Discount discount = new ItemLevelDiscount("NEWYEAR", date, time, items);

        assertNotNull(discount);
        assertTrue(discount.isTimeBased());
        assertFalse(discount.isVolumeBased());
        assertFalse(discount.isOrderLevel());
        assertTrue(discount.isItemLevel());
        assertEquals(2, discount.getApplicableItems().size());
        assertTrue(discount.getApplicableItems().contains("Pizza"));
    }

    @Test
    @DisplayName("3. Create Volume-based Order-level discount")
    public void testVolumeBasedOrderDiscount() {
        Discount discount = new OrderLevelDiscount("BULK50", 100.0, 5, 15.0);

        assertNotNull(discount);
        assertFalse(discount.isTimeBased());
        assertTrue(discount.isVolumeBased());
        assertTrue(discount.isOrderLevel());
        assertFalse(discount.isItemLevel());
        assertEquals(100.0, discount.getMinAmount());
        assertEquals(5, discount.getMinQuantity());
        assertEquals(15.0, discount.getDiscountPercentage());
    }

    @Test
    @DisplayName("4. Create Volume-based Item-level discount")
    public void testVolumeBasedItemDiscount() {
        Set<String> items = new HashSet<>(Set.of("Soda", "Juice"));
        Discount discount = new ItemLevelDiscount("DRINKS", 50.0, 10, items);

        assertNotNull(discount);
        assertFalse(discount.isTimeBased());
        assertTrue(discount.isVolumeBased());
        assertFalse(discount.isOrderLevel());
        assertTrue(discount.isItemLevel());
        assertEquals(50.0, discount.getMinAmount());
        assertEquals(10, discount.getMinQuantity());
    }

    @Test
    @DisplayName("5. Create Time + Volume Order-level discount (Overlapping)")
    public void testTimeVolumeOrderDiscount() {
        LocalDate date = LocalDate.of(2024, 11, 11);
        LocalTime time = LocalTime.of(11, 11);

        Discount discount = new OrderLevelDiscount("DOUBLE11", date, time, 200.0, 10, 25.0);

        assertNotNull(discount);
        assertTrue(discount.isTimeBased());
        assertTrue(discount.isVolumeBased());
        assertTrue(discount.isOrderLevel());
        assertFalse(discount.isItemLevel());

        assertEquals(date, discount.getDate());
        assertEquals(time, discount.getTime());
        assertEquals(200.0, discount.getMinAmount());
        assertEquals(10, discount.getMinQuantity());
        assertEquals(25.0, discount.getDiscountPercentage());
    }

    @Test
    @DisplayName("6. Create Time + Volume Item-level discount (Overlapping)")
    public void testTimeVolumeItemDiscount() {
        LocalDate date = LocalDate.of(2024, 7, 4);
        LocalTime time = LocalTime.of(12, 0);
        Set<String> items = new HashSet<>(Set.of("Hotdog", "Fries"));

        Discount discount = new ItemLevelDiscount("JULY4TH", date, time, 75.0, 3, items);

        assertNotNull(discount);
        assertTrue(discount.isTimeBased());
        assertTrue(discount.isVolumeBased());
        assertFalse(discount.isOrderLevel());
        assertTrue(discount.isItemLevel());

        assertEquals(2, discount.getApplicableItems().size());
    }

    
    @Test
    @DisplayName("Throw exception when accessing time fields on non-time-based discount")
    public void testIllegalAccessToTimeBased() {
        Discount discount = new OrderLevelDiscount("VOLUME", 100.0, 5, 15.0);

        assertThrows(IllegalStateException.class, discount::getDate);
        assertThrows(IllegalStateException.class, discount::getTime);
        assertThrows(IllegalStateException.class, () -> discount.setDate(LocalDate.now()));
        assertThrows(IllegalStateException.class, () -> discount.setTime(LocalTime.now()));
    }

    @Test
    @DisplayName("Throw exception when accessing volume fields on non-volume-based discount")
    public void testIllegalAccessToVolumeBased() {
        Discount discount = new OrderLevelDiscount("TIME", LocalDate.now(), LocalTime.now(), 10.0);

        assertThrows(IllegalStateException.class, discount::getMinAmount);
        assertThrows(IllegalStateException.class, discount::getMinQuantity);
        assertThrows(IllegalStateException.class, () -> discount.setMinAmount(50.0));
        assertThrows(IllegalStateException.class, () -> discount.setMinQuantity(3));
    }

    @Test
    @DisplayName("Throw exception when accessing order-level fields on item-level discount")
    public void testIllegalAccessToOrderLevel() {
        Discount discount = new ItemLevelDiscount("ITEM", LocalDate.now(), LocalTime.now(), Set.of("Pizza"));

        assertThrows(UnsupportedOperationException.class, discount::getDiscountPercentage);
        assertThrows(UnsupportedOperationException.class, () -> discount.setDiscountPercentage(20.0));
    }

    @Test
    @DisplayName("Throw exception when accessing item-level fields on order-level discount")
    public void testIllegalAccessToItemLevel() {
        Discount discount = new OrderLevelDiscount("ORDER", LocalDate.now(), LocalTime.now(), 15.0);

        assertThrows(UnsupportedOperationException.class, discount::getApplicableItems);
        assertThrows(UnsupportedOperationException.class, () -> discount.addApplicableItem("Pizza"));
        assertThrows(UnsupportedOperationException.class, () -> discount.removeApplicableItem("Pizza"));
    }

    @Test
    @DisplayName("Filter discounts by basis aspect - Time-based")
    public void testGetTimeBasedDiscounts() {
        Discount d1 = new OrderLevelDiscount("T1", LocalDate.now(), LocalTime.now(), 10.0);
        Discount d2 = new OrderLevelDiscount("V1", 100.0, 5, 15.0);
        Discount d3 = new OrderLevelDiscount("TV1", LocalDate.now(), LocalTime.now(), 50.0, 3, 20.0);

        List<Discount> timeBasedDiscounts = Discount.getTimeBasedDiscounts();
        assertEquals(2, timeBasedDiscounts.size());
        assertTrue(timeBasedDiscounts.contains(d1));
        assertTrue(timeBasedDiscounts.contains(d3));
        assertFalse(timeBasedDiscounts.contains(d2));
    }

    @Test
    @DisplayName("Filter discounts by basis aspect - Volume-based")
    public void testGetVolumeBasedDiscounts() {
        Discount d1 = new OrderLevelDiscount("T1", LocalDate.now(), LocalTime.now(), 10.0);
        Discount d2 = new OrderLevelDiscount("V1", 100.0, 5, 15.0);
        Discount d3 = new OrderLevelDiscount("TV1", LocalDate.now(), LocalTime.now(), 50.0, 3, 20.0);

        List<Discount> volumeBasedDiscounts = Discount.getVolumeBasedDiscounts();
        assertEquals(2, volumeBasedDiscounts.size());
        assertTrue(volumeBasedDiscounts.contains(d2));
        assertTrue(volumeBasedDiscounts.contains(d3));
        assertFalse(volumeBasedDiscounts.contains(d1));
    }

    @Test
    @DisplayName("Filter discounts by application aspect - Order-level")
    public void testGetOrderLevelDiscounts() {
        Discount d1 = new OrderLevelDiscount("T1", LocalDate.now(), LocalTime.now(), 10.0);
        Discount d2 = new ItemLevelDiscount("T2", LocalDate.now(), LocalTime.now(), Set.of("Pizza"));

        List<Discount> orderLevelDiscounts = Discount.getOrderLevelDiscounts();
        assertEquals(1, orderLevelDiscounts.size());
        assertTrue(orderLevelDiscounts.contains(d1));
        assertFalse(orderLevelDiscounts.contains(d2));
    }

    @Test
    @DisplayName("Filter discounts by application aspect - Item-level")
    public void testGetItemLevelDiscounts() {
        Discount d1 = new OrderLevelDiscount("T1", LocalDate.now(), LocalTime.now(), 10.0);
        Discount d2 = new ItemLevelDiscount("T2", LocalDate.now(), LocalTime.now(), Set.of("Pizza"));

        List<Discount> itemLevelDiscounts = Discount.getItemLevelDiscounts();
        assertEquals(1, itemLevelDiscounts.size());
        assertTrue(itemLevelDiscounts.contains(d2));
        assertFalse(itemLevelDiscounts.contains(d1));
    }

    @Test
    @DisplayName("Filter overlapping discounts (Time AND Volume)")
    public void testGetTimeAndVolumeDiscounts() {
        Discount d1 = new OrderLevelDiscount("T", LocalDate.now(), LocalTime.now(), 10.0);
        Discount d2 = new OrderLevelDiscount("V", 100.0, 5, 15.0);
        Discount d3 = new OrderLevelDiscount("TV", LocalDate.now(), LocalTime.now(), 50.0, 3, 20.0);

        List<Discount> overlappingDiscounts = Discount.getTimeAndVolumeDiscounts();
        assertEquals(1, overlappingDiscounts.size());
        assertTrue(overlappingDiscounts.contains(d3));
        assertFalse(overlappingDiscounts.contains(d1));
        assertFalse(overlappingDiscounts.contains(d2));
    }


    @Test
    @DisplayName("Get discount description for all aspects")
    public void testGetDiscountDescription() {
        Discount d1 = new OrderLevelDiscount("XMAS", LocalDate.of(2024, 12, 25), LocalTime.of(18, 0), 20.0);
        String desc1 = d1.getDiscountDescription();
        assertTrue(desc1.contains("XMAS"));
        assertTrue(desc1.contains("Time-based"));
        assertTrue(desc1.contains("Order-level"));
        assertTrue(desc1.contains("20.0%"));

        Discount d2 = new ItemLevelDiscount("COMBO", LocalDate.now(), LocalTime.now(), 100.0, 5, Set.of("Pizza"));
        String desc2 = d2.getDiscountDescription();
        assertTrue(desc2.contains("Time-based"));
        assertTrue(desc2.contains("Volume-based"));
        assertTrue(desc2.contains("Item-level"));
    }

    @Test
    @DisplayName("Modify applicable items for item-level discount")
    public void testModifyApplicableItems() {
        Set<String> initialItems = new HashSet<>(Set.of("Pizza"));
        Discount discount = new ItemLevelDiscount("TEST", LocalDate.now(), LocalTime.now(), initialItems);

        assertEquals(1, discount.getApplicableItems().size());

        discount.addApplicableItem("Burger");
        assertEquals(2, discount.getApplicableItems().size());
        assertTrue(discount.getApplicableItems().contains("Burger"));

        discount.removeApplicableItem("Pizza");
        assertEquals(1, discount.getApplicableItems().size());
        assertFalse(discount.getApplicableItems().contains("Pizza"));
    }

    @Test
    @DisplayName("Applicable items are returned as unmodifiable")
    public void testApplicableItemsUnmodifiable() {
        Discount discount = new ItemLevelDiscount("TEST", LocalDate.now(), LocalTime.now(), Set.of("Pizza"));

        Set<String> items = discount.getApplicableItems();
        assertThrows(UnsupportedOperationException.class, () -> {
            items.add("Burger");
        });
    }

  
}

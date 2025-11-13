import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.Arrays;

/**
 * Restaurant Management System - Main Demonstration
 *
 * This demonstrates the implementation of:
 * - Basic attributes (name, email, phone, etc.)
 * - Complex attributes (NutritionalInfo)
 * - Multi-value attributes (ingredients list)
 * - Static attributes (TAX_RATE, CANCELLATION_WINDOW_HOURS)
 * - Derived attributes (needsReorder, yearsOfService, isExperienced)
 * - Optional attributes (specialRequests, servedTimestamp)
 * - Class extent (static collections of all instances)
 * - Extent persistence (save/load to files)
 */
public class Main {
    public static void main(String[] args) {
        System.out.println("===== RESTAURANT MANAGEMENT SYSTEM =====");
        System.out.println("Demonstrating all required attribute types and class extent\n");

        // Load all persisted extents on application startup
        loadAllExtents();

        try {
            demonstrateCustomerManagement();
            demonstrateInventoryManagement();
            demonstrateMenuManagement();
            demonstrateEmployeeManagement();
            demonstrateReservationSystem();
            demonstrateOrderingSystem();
            demonstrateExtentPersistence();

            System.out.println("\n===== DEMONSTRATION COMPLETE =====");
            System.out.println("All attribute types successfully demonstrated:");
            System.out.println("✓ Basic attributes");
            System.out.println("✓ Complex attributes (NutritionalInfo)");
            System.out.println("✓ Multi-value attributes");
            System.out.println("✓ Static attributes (TAX_RATE, CANCELLATION_WINDOW_HOURS)");
            System.out.println("✓ Derived attributes (needsReorder, yearsOfService, isExperienced)");
            System.out.println("✓ Optional attributes");
            System.out.println("✓ Class extent and persistence");

        } catch (Exception e) {
            System.err.println("Error: " + e.getMessage());
            e.printStackTrace();
        }
    }

    private static void demonstrateCustomerManagement() {
        System.out.println("--- Customer Management (Basic Attributes) ---");

        Customer customer1 = new Customer("John", "Doe", "john.doe@email.com",
            "+48123456789", LocalDateTime.now().minusYears(2));
        Customer customer2 = new Customer("Jane", "Smith", "jane.smith@email.com",
            "+48987654321", LocalDateTime.now().minusMonths(6));

        System.out.println("Created customers: " + Customer.getAllCustomers().size());
        System.out.println("  " + customer1);
        System.out.println("  " + customer2);
        System.out.println();
    }

    private static void demonstrateInventoryManagement() {
        System.out.println("--- Inventory Management (Derived Attributes) ---");

        // Create ingredients with different stock levels
        Ingredient tomatoes = new Ingredient("Tomatoes", "kg", 50, 20, 3.50);
        Ingredient cheese = new Ingredient("Mozzarella", "kg", 15, 20, 12.00); // Below reorder point
        Ingredient flour = new Ingredient("Flour", "kg", 100, 25, 2.00);

        System.out.println("Ingredients created: " + Ingredient.getAllIngredients().size());
        System.out.println("  " + tomatoes + " - Needs reorder: " + tomatoes.getNeedsReorder());
        System.out.println("  " + cheese + " - Needs reorder: " + cheese.getNeedsReorder());
        System.out.println("  " + flour + " - Needs reorder: " + flour.getNeedsReorder());

        // Demonstrate supplier and supply log
        Supplier supplier = new Supplier("Fresh Foods Inc", "+48111222333",
            "orders@freshfoods.pl", 4.5);
        SupplyLog delivery = new SupplyLog(supplier, cheese, LocalDate.now(), 11.50, 30);
        delivery.registerDelivery();

        System.out.println("\nAfter delivery:");
        System.out.println("  " + cheese + " - Needs reorder: " + cheese.getNeedsReorder());
        System.out.println();
    }

    private static void demonstrateMenuManagement() {
        System.out.println("--- Menu Management (Complex, Multi-value, and Static Attributes) ---");

        // Get ingredients
        Ingredient tomatoes = Ingredient.getAllIngredients().get(0);
        Ingredient cheese = Ingredient.getAllIngredients().get(1);
        Ingredient flour = Ingredient.getAllIngredients().get(2);

        // Create complex attribute (NutritionalInfo)
        NutritionalInfo pizzaNutrition = new NutritionalInfo(850, 35.0, 95.0, 28.0, 6.0);

        // Create menu item with multi-value attribute (ingredients list)
        MainDish pizza = new MainDish(
            "Margherita Pizza",
            "Classic Italian pizza with tomatoes, mozzarella, and basil",
            32.00,  // Base price without tax
            "/images/margherita.jpg",
            "Italian",
            pizzaNutrition,  // Complex attribute
            Arrays.asList(tomatoes, cheese, flour),  
            2  // Spice level
        );

        System.out.println("Menu item created: " + pizza.getName());
        System.out.println("  Ingredients: " + pizza.getIngredients().size());
        System.out.println("  " + pizzaNutrition);
        System.out.println("  Base price: " + pizza.getPrice() + " PLN");

        // Demonstrate static attribute (TAX_RATE)
        System.out.println("\n  Static TAX_RATE: " + (MenuItem.TAX_RATE * 100) + "%");
        System.out.println("  Price with tax: " + pizza.calculatePriceWithTax() + " PLN");

        // Create beverage
        NutritionalInfo cokaNutrition = new NutritionalInfo(140, 0, 39.0, 0, 0);
        Ingredient water = new Ingredient("Water", "L", 500, 50, 0.10);
        Ingredient syrup = new Ingredient("Cola Syrup", "L", 80, 20, 5.50);

        Beverage cola = new Beverage(
            "Coca-Cola",
            "Classic soft drink",
            8.00,
            "/images/cola.jpg",
            "American",
            cokaNutrition,
            Arrays.asList(water, syrup),
            0.0  // No alcohol
        );

        System.out.println("\nBeverage created: " + cola.getName());
        System.out.println("  Alcoholic: " + cola.isAlcoholic());
        System.out.println("  Price with tax: " + cola.calculatePriceWithTax() + " PLN");

        // Create menu
        Menu springMenu = new Menu("Spring 2024", "Spring");
        springMenu.addMenuItem(pizza);
        springMenu.addMenuItem(cola);

        System.out.println("\nMenu '" + springMenu.getName() + "' has " +
            springMenu.getMenuItems().size() + " items");
        System.out.println();
    }

    private static void demonstrateEmployeeManagement() {
        System.out.println("--- Employee Management (Derived Attributes) ---");

        // Create employees with different hire dates
        Waiter newWaiter = new Waiter("Tom", "tom@restaurant.pl", "+48222333444",
            LocalDate.now().minusYears(2), 28.00, "Main Hall");

        Waiter experiencedWaiter = new Waiter("Sarah", "sarah@restaurant.pl", "+48333444555",
            LocalDate.now().minusYears(7), 38.00, "Terrace");

        Manager manager = new Manager("Robert", "robert@restaurant.pl", "+48444555666",
            LocalDate.now().minusYears(10), 55.00, "Front of House", 8);

        System.out.println("Employees created: " + Employee.getAllEmployees().size());
        System.out.println("  " + newWaiter.getName() +
            " - Years of service: " + newWaiter.getYearsOfService() +
            ", Experienced: " + newWaiter.getIsExperienced());
        System.out.println("  " + experiencedWaiter.getName() +
            " - Years of service: " + experiencedWaiter.getYearsOfService() +
            ", Experienced: " + experiencedWaiter.getIsExperienced());
        System.out.println("  " + manager.getName() +
            " - Years of service: " + manager.getYearsOfService() +
            ", Experienced: " + manager.getIsExperienced());
        System.out.println();
    }

    private static void demonstrateReservationSystem() {
        System.out.println("--- Reservation System (Static and Optional Attributes) ---");

        Customer customer = Customer.getAllCustomers().get(0);

        // Create tables
        Table table1 = new Table(1, 4, "Main Hall");
        Table table2 = new Table(2, 2, "Terrace");

        // Create reservation for next week (can be cancelled)
        Reservation futureReservation = new Reservation(customer,
            LocalDate.now().plusDays(7), LocalTime.of(19, 0), 4);
        futureReservation.addTable(table1);

        
        futureReservation.setSpecialRequests("Window seat, celebrating anniversary");

        System.out.println("Reservation created for " + futureReservation.getDate());
        System.out.println("  Special requests (optional): " + futureReservation.getSpecialRequests());

        // Demonstrate static attribute (CANCELLATION_WINDOW_HOURS)
        System.out.println("\n  Static CANCELLATION_WINDOW_HOURS: " +
            Reservation.CANCELLATION_WINDOW_HOURS + " hours");
        System.out.println("  Can be cancelled: " + futureReservation.canBeCancelled());

        // Create reservation within cancellation window
        Reservation soonReservation = new Reservation(customer,
            LocalDate.now(), LocalTime.now().plusHours(2), 2);
        System.out.println("\nReservation in 2 hours:");
        System.out.println("  Can be cancelled: " + soonReservation.canBeCancelled() +
            " (within " + Reservation.CANCELLATION_WINDOW_HOURS + " hour window)");
        System.out.println();
    }

    private static void demonstrateOrderingSystem() {
        System.out.println("--- Ordering System ---");

        Customer customer = Customer.getAllCustomers().get(0);

        // Create dine-in order
        DineIn order = new DineIn(customer);
        Table table = Table.getAllTables().get(0);
        table.reserve();
        order.addTable(table);

        // Create order request
        OrderRequest request = new OrderRequest();


        MenuItem pizza = MenuItem.getAllMenuItems().get(0);
        MenuItem cola = MenuItem.getAllMenuItems().get(1);

        ItemQuantity pizzaItem = new ItemQuantity(pizza, 2);
        pizzaItem.setSpecialRequests("Extra cheese, no onions"); 

        ItemQuantity colaItem = new ItemQuantity(cola, 2);

        request.addItemQuantity(pizzaItem);
        request.addItemQuantity(colaItem);
        request.confirmRequest();

        order.addOrderRequest(request);

        System.out.println("Order created: " + order);
        System.out.println("  Total: " + order.getTotal() + " PLN (including " +
            (MenuItem.TAX_RATE * 100) + "% tax)");
        System.out.println("  Items: " + order.getItems());

        // Finalize and pay
        order.finalizeOrder();

        Payment payment = new Card(order.getTotal(), "1234", "Visa");
        payment.confirmPayment();
        order.setPayment(payment);
        order.completeOrder();

        System.out.println("  Status: " + order.getStatus());
        System.out.println("  Payment: " + payment);
        System.out.println();
    }

    /**
     * Load all persisted extents on application startup
     * This ensures data persists throughout the application lifecycle
     */
    private static void loadAllExtents() {
        System.out.println("--- Loading Persisted Data ---");

        int loadedCount = 0;

        // Load all extents from persistence files
        if (Customer.loadExtent("customers.dat")) {
            System.out.println("✓ Loaded Customers: " + Customer.getAllCustomers().size());
            loadedCount++;
        }
        if (Employee.loadExtent("employees.dat")) {
            System.out.println("✓ Loaded Employees: " + Employee.getAllEmployees().size());
            loadedCount++;
        }
        if (MenuItem.loadExtent("menu_items.dat")) {
            System.out.println("✓ Loaded Menu Items: " + MenuItem.getAllMenuItems().size());
            loadedCount++;
        }
        if (Ingredient.loadExtent("ingredients.dat")) {
            System.out.println("✓ Loaded Ingredients: " + Ingredient.getAllIngredients().size());
            loadedCount++;
        }
        if (Table.loadExtent("tables.dat")) {
            System.out.println("✓ Loaded Tables: " + Table.getAllTables().size());
            loadedCount++;
        }
        if (Order.loadExtent("orders.dat")) {
            System.out.println("✓ Loaded Orders: " + Order.getAllOrders().size());
            loadedCount++;
        }
        if (OrderRequest.loadExtent("order_requests.dat")) {
            System.out.println("✓ Loaded Order Requests: " + OrderRequest.getAllOrderRequests().size());
            loadedCount++;
        }
        if (Reservation.loadExtent("reservations.dat")) {
            System.out.println("✓ Loaded Reservations: " + Reservation.getAllReservations().size());
            loadedCount++;
        }
        if (Payment.loadExtent("payments.dat")) {
            System.out.println("✓ Loaded Payments: " + Payment.getAllPayments().size());
            loadedCount++;
        }
        if (Supplier.loadExtent("suppliers.dat")) {
            System.out.println("✓ Loaded Suppliers: " + Supplier.getAllSuppliers().size());
            loadedCount++;
        }
        if (SupplyLog.loadExtent("supply_logs.dat")) {
            System.out.println("✓ Loaded Supply Logs: " + SupplyLog.getAllSupplyLogs().size());
            loadedCount++;
        }
        if (Menu.loadExtent("menus.dat")) {
            System.out.println("✓ Loaded Menus: " + Menu.getAllMenus().size());
            loadedCount++;
        }

        if (loadedCount == 0) {
            System.out.println("No persisted data found - starting with fresh data");
        } else {
            System.out.println("\n✓ Successfully loaded " + loadedCount + " extent(s) from persistent storage");
        }

        System.out.println();
    }

    private static void demonstrateExtentPersistence() {
        System.out.println("--- Extent Persistence ---");

        try {
            // Save ALL extents to files for complete persistence
            System.out.println("Saving all class extents to files...");

            Customer.saveExtent("customers.dat");
            Employee.saveExtent("employees.dat");
            MenuItem.saveExtent("menu_items.dat");
            Ingredient.saveExtent("ingredients.dat");
            Table.saveExtent("tables.dat");
            Order.saveExtent("orders.dat");
            OrderRequest.saveExtent("order_requests.dat");
            Reservation.saveExtent("reservations.dat");
            Payment.saveExtent("payments.dat");
            Supplier.saveExtent("suppliers.dat");
            SupplyLog.saveExtent("supply_logs.dat");
            Menu.saveExtent("menus.dat");

            System.out.println("✓ Saved all extents successfully");

            // Display extent counts
            System.out.println("\nClass extent counts:");
            System.out.println("  Customers: " + Customer.getAllCustomers().size());
            System.out.println("  Employees: " + Employee.getAllEmployees().size());
            System.out.println("  Menu Items: " + MenuItem.getAllMenuItems().size());
            System.out.println("  Ingredients: " + Ingredient.getAllIngredients().size());
            System.out.println("  Tables: " + Table.getAllTables().size());
            System.out.println("  Orders: " + Order.getAllOrders().size());
            System.out.println("  Order Requests: " + OrderRequest.getAllOrderRequests().size());
            System.out.println("  Reservations: " + Reservation.getAllReservations().size());
            System.out.println("  Payments: " + Payment.getAllPayments().size());
            System.out.println("  Suppliers: " + Supplier.getAllSuppliers().size());
            System.out.println("  Supply Logs: " + SupplyLog.getAllSupplyLogs().size());
            System.out.println("  Menus: " + Menu.getAllMenus().size());

        } catch (Exception e) {
            System.err.println("Error saving extents: " + e.getMessage());
        }

        System.out.println();
    }
}
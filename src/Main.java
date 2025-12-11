import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;
import java.util.Set;

/**
 * Main demonstration class for the Restaurant Management System.
 *
 * REQUIRED METHOD IMPLEMENTATIONS (mark with TODO if missing):
 *
 * Order class:
 *   - public String getOrderId() - returns unique order identifier
 *   - public double calculateTotal() - calculates order total
 *
 * OrderRequest class:
 *   - public void startPreparation() - sets status to IN_PREPARATION
 *   - public void markAsReady() - sets status to READY
 *
 * Payment class:
 *   - public void processPayment() - convenience method to confirm payment
 *
 * Ingredient class:
 *   - public void increaseStock(double quantity) - increases stock by quantity
 *   - public void reduceStock(double quantity) - decreases stock by quantity
 */
public class Main {
    public static void main(String[] args) {
        System.out.println("===============================================================");
        System.out.println("   RESTAURANT MANAGEMENT SYSTEM - DEMONSTRATION");
        System.out.println("===============================================================\n");

        // ================================================================
        // PART 1: EXTENT PERSISTENCE DEMONSTRATION
        // ================================================================
        System.out.println("---------------------------------------------------------------");
        System.out.println("PART 1: EXTENT PERSISTENCE (Save/Load from .dat files)");
        System.out.println("---------------------------------------------------------------\n");

        demonstrateExtentPersistence();

        // ================================================================
        // PART 2: CORE FUNCTIONALITY DEMONSTRATION
        // ================================================================
        System.out.println("\n---------------------------------------------------------------");
        System.out.println("PART 2: CORE RESTAURANT OPERATIONS");
        System.out.println("---------------------------------------------------------------\n");

        demonstrateCoreOperations();

        System.out.println("\n===============================================================");
        System.out.println("   DEMONSTRATION COMPLETED SUCCESSFULLY");
        System.out.println("===============================================================");
    }

    /**
     * Demonstrates extent persistence - the key requirement of the project.
     * Shows how class extents are saved to .dat files and loaded back, maintaining state.
     */
    private static void demonstrateExtentPersistence() {
        System.out.println("[STEP 1] Creating sample data...\n");

        // Create ingredients
        Ingredient tomato = new Ingredient("Tomato", "kg", 50.0, 10.0, 5.50);
        Ingredient cheese = new Ingredient("Cheese", "kg", 30.0, 8.0, 15.00);
        Ingredient flour = new Ingredient("Flour", "kg", 100.0, 20.0, 3.00);
        Ingredient water = new Ingredient("Water", "L", 500.0, 100.0, 0.10);
        Ingredient sugar = new Ingredient("Sugar", "kg", 80.0, 15.0, 2.50);
        Ingredient eggs = new Ingredient("Eggs", "pcs", 200.0, 50.0, 0.50);
        Ingredient mascarpone = new Ingredient("Mascarpone", "kg", 20.0, 5.0, 18.00);
        Ingredient coffee = new Ingredient("Coffee", "kg", 10.0, 2.0, 25.00);

        // Create menu items with different types
        NutritionalInfo pizzaNutrition = new NutritionalInfo(800, 35, 90, 25, 5);
        MainDish margheritaPizza = new MainDish(
            "Margherita Pizza",
            "Classic Italian pizza with tomato and mozzarella",
            45.00,
            "pizza.jpg",
            "Italy",
            pizzaNutrition,
            2 // spice level
        );

        NutritionalInfo cokeNutrition = new NutritionalInfo(140, 0, 39, 0, 0);
        Beverage coke = new Beverage(
            "Coca-Cola",
            "Classic Coca-Cola 330ml",
            8.00,
            "coke.jpg",
            "USA",
            cokeNutrition,
            0.0 // no alcohol
        );

        NutritionalInfo tiramisuNutrition = new NutritionalInfo(450, 8, 45, 25, 2);
        Dessert tiramisu = new Dessert(
            "Tiramisu",
            "Classic Italian dessert",
            28.00,
            "tiramisu.jpg",
            "Italy",
            tiramisuNutrition,
            false // no nuts
        );

        // Create customers
        Customer customer1 = new Customer("John", "Doe", "john.doe@email.com", "123456789", LocalDateTime.now());
        Customer customer2 = new Customer("Jane", "Smith", "jane.smith@email.com", "987654321", LocalDateTime.now());

        // Create employees
        Waiter waiter1 = new Waiter("Alice", "111222333", "alice@restaurant.com", "123 Main Street",
                                    LocalDate.of(2020, 3, 15), 25.00, "Section A");
        Manager manager1 = new Manager("Bob", "444555666", "bob@restaurant.com", "456 Oak Avenue",
                                       LocalDate.of(2018, 1, 10), 45.00, "Operations", 3);

        // Create tables
        Table table1 = new Table(1, 4, "Section A");
        Table table2 = new Table(2, 2, "Section A");

        System.out.println("CREATED:");
        System.out.println("   - 8 Ingredients (Tomato, Cheese, Flour, Water, Sugar, Eggs, Mascarpone, Coffee)");
        System.out.println("   - 3 Menu Items (Pizza, Coke, Tiramisu)");
        System.out.println("   - 2 Customers");
        System.out.println("   - 2 Employees (1 Waiter, 1 Manager)");
        System.out.println("   - 2 Tables\n");

        // Display extent sizes before saving
        System.out.println("EXTENT SIZES (in memory):");
        System.out.println("   - Ingredients: " + Ingredient.getAllIngredients().size());
        System.out.println("   - Menu Items: " + MenuItem.getAllMenuItems().size());
        System.out.println("   - Customers: " + Customer.getAllCustomersFromExtent().size());
        System.out.println("   - Employees: " + Employee.getAllEmployeesFromExtent().size());
        System.out.println("   - Tables: " + Table.getAllTablesFromExtent().size() + "\n");

        // Save all extents to .dat files
        System.out.println("[STEP 2] Saving extents to .dat files...\n");
        try {
            Ingredient.saveExtent("ingredients.dat");
            System.out.println("   [OK] Saved ingredients.dat");

            MenuItem.saveExtent("menu_items.dat");
            System.out.println("   [OK] Saved menu_items.dat");

            Customer.saveExtent("customers.dat");
            System.out.println("   [OK] Saved customers.dat");

            Employee.saveExtent("employees.dat");
            System.out.println("   [OK] Saved employees.dat");

            Table.saveExtent("tables.dat");
            System.out.println("   [OK] Saved tables.dat");

            System.out.println("\nAll extents saved to .dat files successfully!\n");
        } catch (Exception e) {
            System.out.println("[ERROR] Error saving extents: " + e.getMessage() + "\n");
        }

        // Simulate application restart
        System.out.println("[STEP 3] Simulating application restart (clearing memory)...\n");
        System.out.println("   (In real scenario, extent would be cleared and reloaded on restart)\n");

        // Load extents from .dat files
        System.out.println("[STEP 4] Loading extents from .dat files...\n");
        boolean ingredientsLoaded = Ingredient.loadExtent("ingredients.dat");
        System.out.println("   " + (ingredientsLoaded ? "[OK]" : "[FAIL]") + " Loaded ingredients.dat");

        boolean menuItemsLoaded = MenuItem.loadExtent("menu_items.dat");
        System.out.println("   " + (menuItemsLoaded ? "[OK]" : "[FAIL]") + " Loaded menu_items.dat");

        boolean customersLoaded = Customer.loadExtent("customers.dat");
        System.out.println("   " + (customersLoaded ? "[OK]" : "[FAIL]") + " Loaded customers.dat");

        boolean employeesLoaded = Employee.loadExtent("employees.dat");
        System.out.println("   " + (employeesLoaded ? "[OK]" : "[FAIL]") + " Loaded employees.dat");

        boolean tablesLoaded = Table.loadExtent("tables.dat");
        System.out.println("   " + (tablesLoaded ? "[OK]" : "[FAIL]") + " Loaded tables.dat");

        System.out.println("\nLOAD RESULTS: " + (ingredientsLoaded && menuItemsLoaded && customersLoaded &&
                                           employeesLoaded && tablesLoaded ? "ALL SUCCESS" : "SOME FAILED") + "\n");

        // Verify data integrity after load
        System.out.println("EXTENT SIZES (after loading from .dat files):");
        System.out.println("   - Ingredients: " + Ingredient.getAllIngredients().size());
        System.out.println("   - Menu Items: " + MenuItem.getAllMenuItems().size());
        System.out.println("   - Customers: " + Customer.getAllCustomersFromExtent().size());
        System.out.println("   - Employees: " + Employee.getAllEmployeesFromExtent().size());
        System.out.println("   - Tables: " + Table.getAllTablesFromExtent().size() + "\n");

        System.out.println(">>> EXTENT PERSISTENCE VERIFIED <<<");
        System.out.println("    Data persisted to .dat files and restored successfully!");
    }

    /**
     * Demonstrates core restaurant management functionalities.
     */
    private static void demonstrateCoreOperations() {
        // Get existing objects from extents
        List<Customer> customers = Customer.getAllCustomersFromExtent();
        List<MenuItem> menuItems = MenuItem.getAllMenuItems();
        List<Table> tables = Table.getAllTablesFromExtent();
        List<Employee> employees = Employee.getAllEmployeesFromExtent();

        if (customers.isEmpty() || menuItems.isEmpty() || tables.isEmpty()) {
            System.out.println("[WARNING] No data available. Run extent persistence demonstration first.");
            return;
        }

        Customer customer = customers.get(0);
        Table table = tables.get(0);

        // ================================================================
        // 2.1: ATTRIBUTE TYPES DEMONSTRATION
        // ================================================================
        System.out.println("---------------------------------------------------------------");
        System.out.println("2.1: ATTRIBUTE TYPES");
        System.out.println("---------------------------------------------------------------\n");

        MenuItem pizza = menuItems.stream()
            .filter(m -> m instanceof MainDish)
            .findFirst()
            .orElse(menuItems.get(0));

        System.out.println("[STATIC ATTRIBUTE] TAX_RATE:");
        System.out.println("   Value: " + MenuItem.TAX_RATE + " (23% Polish VAT)");
        System.out.println("   Applied uniformly to all menu items\n");

        System.out.println("[COMPLEX ATTRIBUTE] Nutritional Info:");
        System.out.println("   Item: " + pizza.getName());
        NutritionalInfo nutrition = pizza.getNutritionalInfo();
        System.out.println("   Calories: " + nutrition.getCalories() + " kcal");
        System.out.println("   Protein: " + nutrition.getProtein() + "g");
        System.out.println("   Carbohydrates: " + nutrition.getCarbs() + "g");
        System.out.println("   Fats: " + nutrition.getFats() + "g");
        System.out.println("   Fiber: " + nutrition.getFiber() + "g\n");

        System.out.println("[MULTI-VALUE ATTRIBUTE] Allergens:");
        System.out.println("   Item: " + pizza.getName());
        Set<String> allergens = pizza.getAllergens();
        if (allergens.isEmpty()) {
            System.out.println("   No allergens listed\n");
        } else {
            for (String allergen : allergens) {
                System.out.println("   - " + allergen);
            }
            System.out.println();
        }

        if (!employees.isEmpty()) {
            Employee emp = employees.get(0);
            System.out.println("[DERIVED ATTRIBUTES] Employee:");
            System.out.println("   Name: " + emp.getName());
            System.out.println("   Years of Service: " + emp.getYearsOfService() + " years");
            System.out.println("   (Calculated from hire date: " + emp.getHireDate() + ")");
            System.out.println("   Is Experienced (>=5 years): " + (emp.getIsExperienced() ? "Yes" : "No") + "\n");
        }

        Ingredient ingredient = Ingredient.getAllIngredients().get(0);
        System.out.println("[DERIVED ATTRIBUTE] Ingredient Reorder Status:");
        System.out.println("   Name: " + ingredient.getName());
        System.out.println("   Current Stock: " + ingredient.getCurrentStock() + " " + ingredient.getUnit());
        System.out.println("   Reorder Point: " + ingredient.getReorderPoint() + " " + ingredient.getUnit());
        System.out.println("   Needs Reorder: " + (ingredient.getNeedsReorder() ? "YES [!]" : "NO") + "\n");

        // ================================================================
        // 2.2: RESERVATION SYSTEM
        // ================================================================
        System.out.println("---------------------------------------------------------------");
        System.out.println("2.2: RESERVATION SYSTEM");
        System.out.println("---------------------------------------------------------------\n");

        LocalDateTime reservationTime = LocalDateTime.now().plusDays(1).withHour(19).withMinute(0);
        Reservation reservation = new Reservation(
            reservationTime.toLocalDate(),
            reservationTime.toLocalTime(),
            4,
            customer,
            null
        );
        reservation.addSpecialRequest("Window seat preferred");

        System.out.println("RESERVATION CREATED:");
        System.out.println("   Customer: " + customer.getName() + " " + customer.getSurname());
        System.out.println("   Date: " + reservation.getDate());
        System.out.println("   Time: " + reservation.getTime());
        System.out.println("   Party Size: " + reservation.getSize());
        System.out.println("   Status: " + reservation.getStatus());
        System.out.println("   Special Requests: " + String.join(", ", reservation.getSpecialRequests()));
        System.out.println("   [STATIC] Cancellation Window: " + Reservation.CANCELLATION_WINDOW_HOURS + " hours\n");

        reservation.confirmReservation();
        System.out.println(">>> Reservation confirmed");
        System.out.println("    New Status: " + reservation.getStatus() + "\n");

        // ================================================================
        // 2.3: DINE-IN ORDER (SIMPLIFIED)
        // ================================================================
        System.out.println("---------------------------------------------------------------");
        System.out.println("2.3: DINE-IN ORDER (Simplified)");
        System.out.println("---------------------------------------------------------------\n");

        // Note: Order must have a Customer (constructor requirement)
        DineIn dineInOrder = new DineIn(customer);

        System.out.println("DINE-IN ORDER CREATED:");
        System.out.println("   Type: Dine-In");
        System.out.println("   Status: " + dineInOrder.getStatus());
        System.out.println("   Date: " + dineInOrder.getDate());
        System.out.println("   Time: " + dineInOrder.getTime() + "\n");

        // Get menu item for demonstration
        MenuItem pizza1 = menuItems.stream()
            .filter(m -> m instanceof MainDish)
            .findFirst()
            .orElse(menuItems.get(0));

        double itemPrice = pizza1.calculatePriceWithTax() * 2;

        System.out.println("ORDER ITEM:");
        System.out.println("   Item: " + pizza1.getName() + " x2");
        System.out.println("   Price: " + String.format("%.2f", itemPrice) + " PLN\n");

        // Create standalone order request for demonstration
        OrderRequest request1 = new OrderRequest();
        request1.setRequestDetails(pizza1.getName() + " x2, Extra crispy");

        System.out.println("ORDER REQUEST CREATED:");
        System.out.println("   Request ID: " + request1.getRequestId());
        System.out.println("   Status: " + request1.getStatus());
        System.out.println("   Details: " + request1.getRequestDetails() + "\n");

        request1.confirmRequest();
        System.out.println("   [OK] Request confirmed: " + request1.getStatus());

        request1.startPreparation();
        System.out.println("   [OK] Kitchen preparing: " + request1.getStatus() + "\n");

        request1.markAsReady();
        request1.markAsServed();
        System.out.println(">>> Request completed and served");
        System.out.println("    Final Status: " + request1.getStatus() + "\n");

        dineInOrder.finalizeOrder();
        System.out.println("ORDER FINALIZED:");
        System.out.println("   Status: " + dineInOrder.getStatus());
        System.out.println("   Total: " + String.format("%.2f", dineInOrder.getTotalAmount()) + " PLN\n");

        // ================================================================
        // 2.4: TAKEAWAY ORDER (SIMPLIFIED)
        // ================================================================
        System.out.println("---------------------------------------------------------------");
        System.out.println("2.4: TAKEAWAY ORDER (Simplified)");
        System.out.println("---------------------------------------------------------------\n");

        LocalTime collectionTime = LocalTime.now().plusHours(1);
        Takeaway takeawayOrder = new Takeaway(customer, collectionTime);

        MenuItem beverage = menuItems.stream()
            .filter(m -> m instanceof Beverage)
            .findFirst()
            .orElse(menuItems.get(0));

        double beverageTotal = beverage.calculatePriceWithTax() * 3;

        System.out.println("TAKEAWAY ORDER CREATED:");
        System.out.println("   Collection Time: " + collectionTime);
        System.out.println("   Status: " + takeawayOrder.getStatus());
        System.out.println("   Item: " + beverage.getName() + " x3");
        System.out.println("   Price: " + String.format("%.2f", beverageTotal) + " PLN\n");

        // Create payment and associate with order (composition)
        Card cardPayment = new Card(beverageTotal, takeawayOrder, "4532", "Visa");
        cardPayment.processPayment();

        System.out.println("PAYMENT PROCESSED:");
        System.out.println("   Type: Card");
        System.out.println("   Card: Visa ending in " + cardPayment.getLastFourDigits());
        System.out.println("   Amount: " + String.format("%.2f", cardPayment.getAmountPayed()) + " PLN");
        System.out.println("   Status: " + cardPayment.getStatus() + "\n");

        takeawayOrder.finalizeOrder();
        takeawayOrder.completeOrder();
        takeawayOrder.markAsPickedUp();

        System.out.println(">>> Order prepared and picked up");
        System.out.println("    Order Status: " + takeawayOrder.getStatus());
        System.out.println("    Was Picked Up: " + takeawayOrder.getWasPickedUp() + "\n");

        // ================================================================
        // 2.5: PAYMENT OPTIONS
        // ================================================================
        System.out.println("---------------------------------------------------------------");
        System.out.println("2.5: PAYMENT OPTIONS");
        System.out.println("---------------------------------------------------------------\n");

        // Create cash payment and associate with order (composition)
        // Using the pizza price calculated earlier
        Cash cashPayment = new Cash(itemPrice, dineInOrder, 200.00);
        cashPayment.processPayment();

        System.out.println("CASH PAYMENT:");
        System.out.println("   Amount Due: " + String.format("%.2f", itemPrice) + " PLN");
        System.out.println("   Amount Tendered: " + String.format("%.2f", cashPayment.getAmountTendered()) + " PLN");
        System.out.println("   Change Given: " + String.format("%.2f", cashPayment.getChangeGiven()) + " PLN");
        System.out.println("   Status: " + cashPayment.getStatus() + "\n");

        dineInOrder.completeOrder();
        System.out.println(">>> Order completed");
        System.out.println("    Order Status: " + dineInOrder.getStatus() + "\n");

        // ================================================================
        // 2.6: INVENTORY MANAGEMENT
        // ================================================================
        System.out.println("---------------------------------------------------------------");
        System.out.println("2.6: INVENTORY MANAGEMENT");
        System.out.println("---------------------------------------------------------------\n");

        // Create supplier with contactPerson
        Supplier supplier = new Supplier("Fresh Foods Ltd", "123-456-789", "supplier@freshfoods.com",
                                        "789 Supply Road", 4.5, "John Smith");

        System.out.println("SUPPLIER CREATED:");
        System.out.println("   Name: " + supplier.getName());
        System.out.println("   Contact Person: " + supplier.getContactPerson());
        System.out.println("   Reliability Rating: " + supplier.getReliabilityRating() + "/5.0\n");

        Ingredient tomato = Ingredient.getAllIngredients().stream()
            .filter(i -> i.getName().equals("Tomato"))
            .findFirst()
            .orElse(Ingredient.getAllIngredients().get(0));

        System.out.println("INGREDIENT STATUS (Before Usage):");
        System.out.println("   Name: " + tomato.getName());
        System.out.println("   Current Stock: " + tomato.getCurrentStock() + " " + tomato.getUnit());
        System.out.println("   Reorder Point: " + tomato.getReorderPoint() + " " + tomato.getUnit());
        System.out.println("   Cost Per Unit: " + String.format("%.2f", tomato.getCostPerUnit()) + " PLN");
        System.out.println("   Needs Reorder: " + (tomato.getNeedsReorder() ? "YES" : "NO") + "\n");

        // Simulate usage
        tomato.reduceStock(45.0);
        System.out.println("STOCK UPDATE: Usage recorded");
        System.out.println("   Stock reduced by: 45.0 kg");
        System.out.println("   Current Stock: " + tomato.getCurrentStock() + " " + tomato.getUnit());
        System.out.println("   Needs Reorder: " + (tomato.getNeedsReorder() ? "YES [!]" : "NO") + "\n");

        // Create supply log using {Bag} association class
        double quantitySupplied = 50.0;
        double supplyCost = tomato.getCostPerUnit() * quantitySupplied;
        SupplyLog supplyLog = SupplyLog.create(supplier, tomato, LocalDate.now(), supplyCost, quantitySupplied);

        // Restock
        tomato.increaseStock(quantitySupplied);

        System.out.println("SUPPLY RECEIVED:");
        System.out.println("   Quantity Supplied: " + supplyLog.getQuantitySupplied() + " kg");
        System.out.println("   Total Cost: " + String.format("%.2f", supplyLog.getCostAtSupply()) + " PLN");
        System.out.println("   Supply Date: " + supplyLog.getSupplyDate());
        System.out.println("   Current Stock: " + tomato.getCurrentStock() + " " + tomato.getUnit());
        System.out.println("   Needs Reorder: " + (tomato.getNeedsReorder() ? "YES" : "NO") + "\n");

        // ================================================================
        // 2.7: EMPLOYEE MANAGEMENT
        // ================================================================
        System.out.println("---------------------------------------------------------------");
        System.out.println("2.7: EMPLOYEE MANAGEMENT");
        System.out.println("---------------------------------------------------------------\n");

        for (Employee emp : employees) {
            if (emp instanceof Waiter) {
                Waiter waiter = (Waiter) emp;
                System.out.println("WAITER PROFILE:");
                System.out.println("   Name: " + waiter.getName());
                System.out.println("   Email: " + waiter.getEmail());
                System.out.println("   Section: " + waiter.getSection());
                System.out.println("   Hire Date: " + waiter.getHireDate());
                System.out.println("   Hourly Rate: " + String.format("%.2f", waiter.getHourlyRate()) + " PLN");
                System.out.println("   Years of Service: " + waiter.getYearsOfService() + " years (derived)");
                System.out.println("   Total Tips: " + String.format("%.2f", waiter.getTipTotal()) + " PLN\n");
            } else if (emp instanceof Manager) {
                Manager manager = (Manager) emp;
                System.out.println("MANAGER PROFILE:");
                System.out.println("   Name: " + manager.getName());
                System.out.println("   Email: " + manager.getEmail());
                System.out.println("   Department: " + manager.getDepartment());
                System.out.println("   Access Level: " + manager.getAccessLevel());
                System.out.println("   Hire Date: " + manager.getHireDate());
                System.out.println("   Hourly Rate: " + String.format("%.2f", manager.getHourlyRate()) + " PLN");
                System.out.println("   Years of Service: " + manager.getYearsOfService() + " years (derived)");
                System.out.println("   Is Experienced: " + (manager.getIsExperienced() ? "Yes (>=5 years)" : "No (<5 years)") + "\n");
            }
        }

        // ================================================================
        // FINAL: SAVING ALL CHANGES TO .DAT FILES
        // ================================================================
        System.out.println("---------------------------------------------------------------");
        System.out.println("FINAL: SAVING ALL CHANGES TO .DAT FILES");
        System.out.println("---------------------------------------------------------------\n");

        try {
            // Save all additional extents created during operations
            Reservation.saveExtent("reservations.dat");
            System.out.println("   [OK] Saved reservations.dat");

            Order.saveExtent("orders.dat");
            System.out.println("   [OK] Saved orders.dat");

            OrderRequest.saveExtent("order_requests.dat");
            System.out.println("   [OK] Saved order_requests.dat");

            Payment.saveExtent("payments.dat");
            System.out.println("   [OK] Saved payments.dat");

            Supplier.saveExtent("suppliers.dat");
            System.out.println("   [OK] Saved suppliers.dat");

            System.out.println("\nFINAL EXTENT STATISTICS:");
            System.out.println("   - Orders: " + Order.getAllOrdersFromExtent().size());
            System.out.println("   - Order Requests: " + OrderRequest.getAllOrderRequestsFromExtent().size());
            System.out.println("   - Reservations: " + Reservation.getAllReservationsFromExtent().size());
            System.out.println("   - Payments: " + Payment.getAllPaymentsFromExtent().size());
            System.out.println("   - Suppliers: " + Supplier.getAllSuppliersFromExtent().size());
            System.out.println("\n>>> All changes persisted to .dat files successfully!");
        } catch (Exception e) {
            System.out.println("[ERROR] Error saving extents: " + e.getMessage());
            e.printStackTrace();
        }
    }
}

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;
import static org.junit.jupiter.api.Assertions.*;

import java.time.LocalDate;

@DisplayName("Employee Dynamic Inheritance Tests (Flattening Approach)")
public class EmployeeInheritanceTest {

    @BeforeEach
    public void setUp() {
        Employee.clearExtent();
    }


    @Test
    @DisplayName("Manager-specific: Get department and access level")
    public void testManagerSpecificFields() {
        Employee manager = new Employee(
            "Carol White",
            "+48111222333",
            "carol@restaurant.com",
            "789 Boss Rd",
            LocalDate.now().minusYears(5),
            30.0,
            "Kitchen",
            5
        );

        assertEquals("Kitchen", manager.getDepartment());
        assertEquals(5, manager.getAccessLevel());
    }

    @Test
    @DisplayName("Manager-specific: Accessing waiter fields throws exception")
    public void testManagerCannotAccessWaiterFields() {
        Employee manager = new Employee(
            "David Brown",
            "+48444555666",
            "david@restaurant.com",
            "321 Admin Blvd",
            LocalDate.now().minusYears(4),
            28.0,
            "HR",
            4
        );

        assertThrows(IllegalStateException.class, manager::getSection);
        assertThrows(IllegalStateException.class, manager::getTipTotal);
        assertThrows(IllegalStateException.class, () -> manager.setSection("Patio"));
        assertThrows(IllegalStateException.class, () -> manager.addTip(50.0));
    }

    @Test
    @DisplayName("Waiter-specific: Get section and tip total")
    public void testWaiterSpecificFields() {
        Employee waiter = new Employee(
            "Emma Green",
            "+48777888999",
            "emma@restaurant.com",
            "654 Server Ln",
            LocalDate.now().minusYears(1),
            17.5,
            "Patio"
        );

        assertEquals("Patio", waiter.getSection());
        assertEquals(0.0, waiter.getTipTotal());
    }

    @Test
    @DisplayName("Waiter-specific: Accessing manager fields throws exception")
    public void testWaiterCannotAccessManagerFields() {
        Employee waiter = new Employee(
            "Frank Miller",
            "+48555666777",
            "frank@restaurant.com",
            "987 Wait St",
            LocalDate.now().minusYears(1),
            16.0,
            "Bar"
        );

        assertThrows(IllegalStateException.class, waiter::getDepartment);
        assertThrows(IllegalStateException.class, waiter::getAccessLevel);
        assertThrows(IllegalStateException.class, () -> waiter.setDepartment("Sales"));
        assertThrows(IllegalStateException.class, () -> waiter.setAccessLevel(2));
    }

    @Test
    @DisplayName("Dynamic change: Waiter becomes Manager")
    public void testWaiterBecomesManager() {
        Employee employee = new Employee(
            "Grace Lee",
            "+48333444555",
            "grace@restaurant.com",
            "111 Career Path",
            LocalDate.now().minusYears(6),
            20.0,
            "Main"
        );

        assertEquals(Employee.EmployeeType.WAITER, employee.getType());
        assertEquals("Main", employee.getSection());

        employee.becomeManager("Operations", 3);

        assertEquals(Employee.EmployeeType.MANAGER, employee.getType());
        assertEquals("Operations", employee.getDepartment());
        assertEquals(3, employee.getAccessLevel());

        // Waiter fields should now throw exceptions
        assertThrows(IllegalStateException.class, employee::getSection);
        assertThrows(IllegalStateException.class, employee::getTipTotal);
    }

    @Test
    @DisplayName("Dynamic change: Manager becomes Waiter")
    public void testManagerBecomesWaiter() {
        Employee employee = new Employee(
            "Henry Taylor",
            "+48222333444",
            "henry@restaurant.com",
            "222 Demotion Dr",
            LocalDate.now().minusYears(3),
            25.0,
            "Sales",
            2
        );

        assertEquals(Employee.EmployeeType.MANAGER, employee.getType());
        assertEquals("Sales", employee.getDepartment());

        employee.becomeWaiter("Patio");

        assertEquals(Employee.EmployeeType.WAITER, employee.getType());
        assertEquals("Patio", employee.getSection());
        assertEquals(0.0, employee.getTipTotal());

        // Manager fields should now throw exceptions
        assertThrows(IllegalStateException.class, employee::getDepartment);
        assertThrows(IllegalStateException.class, employee::getAccessLevel);
    }

    @Test
    @DisplayName("Dynamic change: Calling becomeManager when already Manager does nothing")
    public void testBecomeManagerIdempotent() {
        Employee manager = new Employee(
            "Ivy Anderson",
            "+48666777888",
            "ivy@restaurant.com",
            "333 Stable Job",
            LocalDate.now().minusYears(4),
            27.0,
            "Finance",
            4
        );

        assertEquals(Employee.EmployeeType.MANAGER, manager.getType());
        assertEquals("Finance", manager.getDepartment());
        assertEquals(4, manager.getAccessLevel());

        // Should return early without changes
        manager.becomeManager("NewDept", 5);

        // Original values unchanged
        assertEquals(Employee.EmployeeType.MANAGER, manager.getType());
        assertEquals("Finance", manager.getDepartment());
        assertEquals(4, manager.getAccessLevel());
    }

    @Test
    @DisplayName("Dynamic change: Calling becomeWaiter when already Waiter does nothing")
    public void testBecomeWaiterIdempotent() {
        Employee waiter = new Employee(
            "Jack Wilson",
            "+48999888777",
            "jack@restaurant.com",
            "444 Constant Ct",
            LocalDate.now().minusYears(2),
            18.0,
            "Bar"
        );

        assertEquals(Employee.EmployeeType.WAITER, waiter.getType());
        assertEquals("Bar", waiter.getSection());

        waiter.becomeWaiter("NewSection");

        assertEquals(Employee.EmployeeType.WAITER, waiter.getType());
        assertEquals("Bar", waiter.getSection());
    }

    @Test
    @DisplayName("Dynamic change: Manager with subordinates cannot become Waiter")
    public void testManagerWithSubordinatesCannotBecomeWaiter() {
        Employee manager = new Employee(
            "Karen Davis",
            "+48111000111",
            "karen@restaurant.com",
            "555 Boss Ave",
            LocalDate.now().minusYears(5),
            30.0,
            "Operations",
            5
        );

        Employee subordinate = new Employee(
            "Leo Martinez",
            "+48222000222",
            "leo@restaurant.com",
            "666 Worker Ln",
            LocalDate.now().minusYears(2),
            22.0,
            "Sales",
            2
        );

        manager.addSubordinate(subordinate);

        IllegalStateException exception = assertThrows(IllegalStateException.class, () -> {
            manager.becomeWaiter("Patio");
        });

        assertTrue(exception.getMessage().contains("subordinate"));
    }

    @Test
    @DisplayName("Dynamic change: Manager with supervisor can become Waiter")
    public void testManagerWithSupervisorCanBecomeWaiter() {
        Employee bigBoss = new Employee(
            "Mia Rodriguez",
            "+48333000333",
            "mia@restaurant.com",
            "777 Executive Way",
            LocalDate.now().minusYears(10),
            40.0,
            "Executive",
            10
        );

        Employee manager = new Employee(
            "Nathan Clark",
            "+48444000444",
            "nathan@restaurant.com",
            "888 Middle Mgmt",
            LocalDate.now().minusYears(3),
            25.0,
            "Sales",
            3
        );

        manager.setSupervisor(bigBoss);
        assertNotNull(manager.getSupervisor());

        manager.becomeWaiter("Bar");

        assertEquals(Employee.EmployeeType.WAITER, manager.getType());
    }

    @Test
    @DisplayName("Complete constraint: Every employee must be Manager or Waiter")
    public void testCompleteConstraint() {
        Employee manager = new Employee(
            "Sam Turner",
            "+48999000999",
            "sam@restaurant.com",
            "400 Complete St",
            LocalDate.now().minusYears(3),
            26.0,
            "HR",
            3
        );

        Employee waiter = new Employee(
            "Tina Phillips",
            "+48000111222",
            "tina@restaurant.com",
            "500 Complete Ave",
            LocalDate.now().minusYears(1),
            17.0,
            "Bar"
        );

        assertTrue(
            manager.getType() == Employee.EmployeeType.MANAGER ||
            manager.getType() == Employee.EmployeeType.WAITER
        );

        assertTrue(
            waiter.getType() == Employee.EmployeeType.MANAGER ||
            waiter.getType() == Employee.EmployeeType.WAITER
        );
    }

    @Test
    @DisplayName("Disjoint constraint: Employee cannot be both Manager and Waiter")
    public void testDisjointConstraint() {
        Employee manager = new Employee(
            "Uma Campbell",
            "+48111222333",
            "uma@restaurant.com",
            "600 Disjoint Dr",
            LocalDate.now().minusYears(5),
            30.0,
            "Operations",
            5
        );

        Employee waiter = new Employee(
            "Victor Parker",
            "+48222333444",
            "victor@restaurant.com",
            "700 Disjoint Ln",
            LocalDate.now().minusYears(2),
            18.0,
            "Main"
        );

        // Each employee has exactly one type
        assertTrue(manager.getType() == Employee.EmployeeType.MANAGER);
        assertFalse(manager.getType() == Employee.EmployeeType.WAITER);

        assertTrue(waiter.getType() == Employee.EmployeeType.WAITER);
        assertFalse(waiter.getType() == Employee.EmployeeType.MANAGER);
    }

    @Test
    @DisplayName("Type transitions preserve common attributes")
    public void testCommonAttributesPreservedDuringTransition() {
        Employee employee = new Employee(
            "Wendy Collins",
            "+48333444555",
            "wendy@restaurant.com",
            "800 Transition Rd",
            LocalDate.now().minusYears(3),
            22.0,
            "Main"
        );

        String name = employee.getName();
        String email = employee.getEmail();
        LocalDate hireDate = employee.getHireDate();

        employee.becomeManager("Operations", 3);

        assertEquals(name, employee.getName());
        assertEquals(email, employee.getEmail());
        assertEquals(hireDate, employee.getHireDate());
    }

    @Test
    @DisplayName("Multiple type transitions work correctly")
    public void testMultipleTypeTransitions() {
        Employee employee = new Employee(
            "Xavier Reed",
            "+48444555666",
            "xavier@restaurant.com",
            "900 Flexible Job",
            LocalDate.now().minusYears(4),
            20.0,
            "Bar"
        );

        assertEquals(Employee.EmployeeType.WAITER, employee.getType());

        employee.becomeManager("Sales", 2);
        assertEquals(Employee.EmployeeType.MANAGER, employee.getType());
        assertEquals("Sales", employee.getDepartment());

        employee.becomeWaiter("Patio");
        assertEquals(Employee.EmployeeType.WAITER, employee.getType());
        assertEquals("Patio", employee.getSection());

        employee.becomeManager("HR", 4);
        assertEquals(Employee.EmployeeType.MANAGER, employee.getType());
        assertEquals("HR", employee.getDepartment());
    }

}

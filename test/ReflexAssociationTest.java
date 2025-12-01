
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.*;

public class ReflexAssociationTest {

    @BeforeEach
    void setup() {
        // clear static extent before each test
        Employee.clearExtent();
    }

    private Manager createManager(String name) {
        return new Manager(
                name,
                "555",
                name.toLowerCase() + "@mail.com",
                "Some Street 1",
                LocalDate.now().minusYears(2),
                40.0,
                "Management",
                5
        );
    }

    @Test
    void addSubordinate_createsReverseConnection() {
        Manager boss = createManager("Boss");
        Manager worker = createManager("Worker");

        boss.addSubordinate(worker);

        assertEquals(boss, worker.getSupervisor());
        assertTrue(boss.getSubordinates().contains(worker));
    }

    @Test
    void supervisorChange_updatesBothSides() {
        Manager boss1 = createManager("Boss1");
        Manager boss2 = createManager("Boss2");
        Manager worker = createManager("Worker");

        boss1.addSubordinate(worker);
        worker.setSupervisor(boss2);

        assertEquals(boss2, worker.getSupervisor());
        assertFalse(boss1.getSubordinates().contains(worker));
        assertTrue(boss2.getSubordinates().contains(worker));
    }

    @Test
    void cannotSetSelfAsSupervisor() {
        Manager m = createManager("Selfie");

        assertThrows(IllegalArgumentException.class, () -> m.setSupervisor(m));
    }

    @Test
    void cycleInHierarchy_isNotAllowed() {
        Manager a = createManager("A");
        Manager b = createManager("B");
        Manager c = createManager("C");

        a.setSupervisor(b);
        b.setSupervisor(c);

        assertThrows(IllegalStateException.class, () -> c.setSupervisor(a));
    }

    @Test
    void removeSubordinate_clearsSupervisorReference() {
        Manager boss = createManager("Boss");
        Manager worker = createManager("Worker");

        boss.addSubordinate(worker);
        boss.removeSubordinate(worker);

        assertNull(worker.getSupervisor());
        assertFalse(boss.getSubordinates().contains(worker));
    }

    @Test
    void removingNotManagedSubordinate_throwsException() {
        Manager boss = createManager("Boss");
        Manager worker = createManager("Worker");

        assertThrows(IllegalArgumentException.class, () -> boss.removeSubordinate(worker));
    }
}

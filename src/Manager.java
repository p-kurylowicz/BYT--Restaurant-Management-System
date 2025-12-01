import java.io.Serial;
import java.time.LocalDate;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

public class Manager extends Employee {
    @Serial
    private static final long serialVersionUID = 1L;

    private String department;
    private int accessLevel;

    // ===== Reflex association: Manager ↔ Manager =====
    // Each manager can have 0..1 supervisor and 0..* subordinates.
    private Manager supervisor;
    private final Set<Manager> subordinates = new HashSet<>();

    public Manager() {
        super();
    }

    public Manager(String name, String phone, String email, String address, LocalDate hireDate,
                   double hourlyRate, String department, int accessLevel) {
        super(name, phone, email, address, hireDate, hourlyRate);
        setDepartment(department);
        setAccessLevel(accessLevel);
    }

    // ===== Basic attribute getters =====

    public String getDepartment() {
        return department;
    }

    public int getAccessLevel() {
        return accessLevel;
    }

    // ===== Reflex association getters =====

    /**
     * Returns the supervisor of this manager (0..1).
     */
    public Manager getSupervisor() {
        return supervisor;
    }

    /**
     * Returns an unmodifiable view of this manager's subordinates (0..*).
     */
    public Set<Manager> getSubordinates() {
        return Collections.unmodifiableSet(subordinates);
    }

    // ===== Basic attribute setters =====

    public void setDepartment(String department) {
        if (department == null || department.trim().isEmpty()) {
            throw new IllegalArgumentException("Department cannot be null or empty");
        }
        this.department = department.trim();
    }

    public void setAccessLevel(int accessLevel) {
        if (accessLevel < 1 || accessLevel > 10) {
            throw new IllegalArgumentException("Access level must be between 1 and 10");
        }
        this.accessLevel = accessLevel;
    }

    // ===== Reflex association logic =====

    /**
     * Sets the supervisor for this manager.
     * Maintains the bidirectional association and prevents cycles in the hierarchy.
     *
     * @param newSupervisor new supervisor to assign, or null to clear the supervisor
     */
    public void setSupervisor(Manager newSupervisor) {
        if (newSupervisor == this) {
            throw new IllegalArgumentException("Manager cannot supervise themself");
        }

        // Prevent cycles: this must not appear above in the supervisor chain
        Manager current = newSupervisor;
        while (current != null) {
            if (current == this) {
                throw new IllegalStateException("Cycle in manager hierarchy is not allowed");
            }
            current = current.getSupervisor();
        }

        if (this.supervisor == newSupervisor) {
            // No change required
            return;
        }

        // Remove this manager from the old supervisor's subordinates
        if (this.supervisor != null) {
            this.supervisor.subordinates.remove(this);
        }

        // Set the new supervisor
        this.supervisor = newSupervisor;

        // Add this manager to the new supervisor's subordinates (reverse connection)
        if (newSupervisor != null && !newSupervisor.subordinates.contains(this)) {
            newSupervisor.subordinates.add(this);
        }
    }

    /**
     * Adds a subordinate to this manager.
     * Delegates the main logic to subordinate.setSupervisor(this).
     */
    public void addSubordinate(Manager subordinate) {
        if (subordinate == null) {
            throw new IllegalArgumentException("Subordinate cannot be null");
        }
        subordinate.setSupervisor(this);
    }

    /**
     * Removes a subordinate from this manager and clears its supervisor reference.
     */
    public void removeSubordinate(Manager subordinate) {
        if (subordinate == null || !subordinates.contains(subordinate)) {
            throw new IllegalArgumentException("Subordinate is not managed by this manager");
        }

        subordinates.remove(subordinate);

        if (subordinate.supervisor == this) {
            subordinate.supervisor = null;
        }
    }

    // ===== Existing business logic =====

    public void assignWaiterToSection(Waiter waiter, String section) {
        if (waiter == null) {
            throw new IllegalArgumentException("Waiter cannot be null");
        }
        waiter.setSection(section);
    }

    public void approveAvailabilityUpdate(MenuItem menuItem) {
        if (menuItem == null) {
            throw new IllegalArgumentException("Menu item cannot be null");
        }
        menuItem.recalculateAvailability();
    }

    @Override
    public String toString() {
        return String.format(
            "Manager[%s, department=%s, accessLevel=%d, %s]",
            getName(), department, accessLevel, super.toString()
        );
    }
}

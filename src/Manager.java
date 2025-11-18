import java.io.Serial;
import java.time.LocalDate;

public class Manager extends Employee {
    @Serial
    private static final long serialVersionUID = 1L;


    private String department;
    private int accessLevel;

    
    public Manager() {
        super();
    }


    public Manager(String name, String phone, String email, String address, LocalDate hireDate,
                  double hourlyRate, String department, int accessLevel) {
        super(name, phone, email, address, hireDate, hourlyRate);
        setDepartment(department);
        setAccessLevel(accessLevel);
    }

    
    public String getDepartment() { return department; }
    public int getAccessLevel() { return accessLevel; }

    
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
        return String.format("Manager[%s, department=%s, accessLevel=%d, %s]",
            getName(), department, accessLevel, super.toString());
    }
}

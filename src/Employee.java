import java.io.*;
import java.time.LocalDate;
import java.time.Period;
import java.util.*;

public class Employee implements Serializable {
    @Serial
    private static final long serialVersionUID = 1L;

    public enum EmployeeType {
        MANAGER, WAITER
    }

    private static List<Employee> allEmployees = new ArrayList<>();

    private String name;
    private ContactInfo contactInfo;
    private LocalDate hireDate;
    private double hourlyRate;
    
    private EmployeeType type;

    private String department;
    private Integer accessLevel;
    private Employee supervisor;
    private Set<Employee> subordinates;

    private String section;
    private Double tipTotal;

    public Employee(String name, String phone, String email, String address, LocalDate hireDate, double hourlyRate, String department, int accessLevel) {
        setName(name);
        setContactInfo(new ContactInfo(phone, email, address));
        setHireDate(hireDate);
        setHourlyRate(hourlyRate);

        this.type = EmployeeType.MANAGER;
        setDepartment(department);
        setAccessLevel(accessLevel);
        this.subordinates = new HashSet<>();

        addEmployeeToExtent(this);
    }

    public Employee(String name, String phone, String email, String address, LocalDate hireDate, double hourlyRate, String section) {
        setName(name);
        setContactInfo(new ContactInfo(phone, email, address));
        setHireDate(hireDate);
        setHourlyRate(hourlyRate);

        this.type = EmployeeType.WAITER;
        setSection(section);
        this.tipTotal = 0.0;

        addEmployeeToExtent(this);
    }

    public void becomeManager(String department, int accessLevel) {
        if (this.type == EmployeeType.MANAGER) return;

        // TODO: CRITICAL BUG FIX NEEDED - When waiter supervision is implemented,
        // must clean up waiter's supervisor relationship before promotion:
        // if (this.waiterSupervisor != null) {
        //     this.waiterSupervisor.removeSupervisedWaiter(this);
        //     this.waiterSupervisor = null;
        // }
        // See: Documents/Employee_Implementation_Issues.md for full details

        this.section = null;
        this.tipTotal = null;

        this.type = EmployeeType.MANAGER;
        setDepartment(department);
        setAccessLevel(accessLevel);
        this.subordinates = new HashSet<>();
    }

    public void becomeWaiter(String section) {
        if (this.type == EmployeeType.WAITER) return;

        if (this.subordinates != null && !this.subordinates.isEmpty()) {
            throw new IllegalStateException("Cannot change to Waiter while having subordinates");
        }
        
        if (this.supervisor != null) {
            this.supervisor.removeSubordinate(this);
            this.supervisor = null;
        }
        
        this.department = null;
        this.accessLevel = null;
        this.subordinates = null;
        
        this.type = EmployeeType.WAITER;
        setSection(section);
        this.tipTotal = 0.0;
    }

    public String getName() { return name; }
    public ContactInfo getContactInfo() { return contactInfo; }
    public String getEmail() { return contactInfo != null ? contactInfo.getEmail() : null; }
    public String getPhone() { return contactInfo != null ? contactInfo.getPhone() : null; }
    public String getAddress() { return contactInfo != null ? contactInfo.getAddress() : null; }
    public LocalDate getHireDate() { return hireDate; }
    public double getHourlyRate() { return hourlyRate; }
    public EmployeeType getType() { return type; }

    public int getYearsOfService() {
        if (hireDate == null) return 0;
        return Period.between(hireDate, LocalDate.now()).getYears();
    }

    public boolean getIsExperienced() {
        return getYearsOfService() >= 5;
    }

    public void setName(String name) {
        if (name == null || name.trim().isEmpty()) throw new IllegalArgumentException("Name cannot be empty");
        this.name = name.trim();
    }

    public void setContactInfo(ContactInfo contactInfo) {
        if (contactInfo == null) throw new IllegalArgumentException("Contact info cannot be null");
        this.contactInfo = contactInfo;
    }

    public void updateContactInfo(String phone, String email, String address) {
        if (this.contactInfo == null) {
            this.contactInfo = new ContactInfo(phone, email, address);
        } else {
            this.contactInfo.updateContactInfo(phone, email, address);
        }
    }

    public void setHireDate(LocalDate hireDate) {
        if (hireDate == null) throw new IllegalArgumentException("Hire date cannot be null");
        if (hireDate.isAfter(LocalDate.now())) throw new IllegalArgumentException("Hire date cannot be in future");
        this.hireDate = hireDate;
    }

    public void setHourlyRate(double hourlyRate) {
        if (hourlyRate <= 0) throw new IllegalArgumentException("Hourly rate must be > 0");
        this.hourlyRate = hourlyRate;
    }

    private void checkManager() {
        if (type != EmployeeType.MANAGER) throw new IllegalStateException("Operation only for Managers");
    }

    public String getDepartment() { checkManager(); return department; }
    public int getAccessLevel() { checkManager(); return accessLevel; }
    public Employee getSupervisor() { checkManager(); return supervisor; }
    public Set<Employee> getSubordinates() { checkManager(); return Collections.unmodifiableSet(subordinates); }

    public void setDepartment(String department) {
        checkManager();
        if (department == null || department.trim().isEmpty()) throw new IllegalArgumentException("Department cannot be empty");
        this.department = department.trim();
    }

    public void setAccessLevel(int accessLevel) {
        checkManager();
        if (accessLevel < 1 || accessLevel > 10) throw new IllegalArgumentException("Access level must be 1-10");
        this.accessLevel = accessLevel;
    }

    public void setSupervisor(Employee newSupervisor) {
        checkManager();
        if (newSupervisor == this) throw new IllegalArgumentException("Cannot supervise self");
        if (newSupervisor != null && newSupervisor.getType() != EmployeeType.MANAGER) {
            throw new IllegalArgumentException("Supervisor must be a Manager");
        }

        Employee current = newSupervisor;
        while (current != null) {
            if (current == this) throw new IllegalStateException("Cycle detected");
            current = current.getSupervisor();
        }

        if (this.supervisor != null) {
            this.supervisor.subordinates.remove(this);
        }
        this.supervisor = newSupervisor;
        if (newSupervisor != null && !newSupervisor.subordinates.contains(this)) {
            newSupervisor.subordinates.add(this);
        }
    }

    public void addSubordinate(Employee subordinate) {
        checkManager();
        if (subordinate == null) throw new IllegalArgumentException("Subordinate cannot be null");
        subordinate.setSupervisor(this);
    }

    public void removeSubordinate(Employee subordinate) {
        checkManager();
        if (subordinate == null || !subordinates.contains(subordinate)) {
            throw new IllegalArgumentException("Not a subordinate of this manager");
        }
        subordinate.setSupervisor(null);
    }

    public void assignWaiterToSection(Employee waiter, String section) {
        checkManager();
        if (waiter == null || waiter.getType() != EmployeeType.WAITER) {
            throw new IllegalArgumentException("Must assign a Waiter");
        }
        waiter.setSection(section);
    }
    
    public void approveAvailabilityUpdate(MenuItem menuItem) {
        checkManager();
        if (menuItem == null) throw new IllegalArgumentException("Item cannot be null");
        menuItem.recalculateAvailability();
    }

    private void checkWaiter() {
        if (type != EmployeeType.WAITER) throw new IllegalStateException("Operation only for Waiters");
    }

    public String getSection() { checkWaiter(); return section; }
    public double getTipTotal() { checkWaiter(); return tipTotal; }

    public void setSection(String section) {
        checkWaiter();
        if (section == null || section.trim().isEmpty()) throw new IllegalArgumentException("Section cannot be empty");
        this.section = section.trim();
    }

    public void addTip(double tip) {
        checkWaiter();
        if (tip < 0) throw new IllegalArgumentException("Tip cannot be negative");
        this.tipTotal += tip;
    }
    
    public void clearTableAssignments() {
        checkWaiter();
    }

    private static void addEmployeeToExtent(Employee employee) {
        if (employee == null) throw new IllegalArgumentException("Employee cannot be null");
        allEmployees.add(employee);
    }

    public static List<Employee> getAllEmployeesFromExtent() {
        return Collections.unmodifiableList(allEmployees);
    }

    public static void clearExtent() {
        allEmployees.clear();
    }

    public static void saveExtent(String filename) throws IOException {
        String filepath = PersistenceConfig.getDataFilePath(filename);
        try (ObjectOutputStream out = new ObjectOutputStream(new FileOutputStream(filepath))) {
            out.writeObject(allEmployees);
        }
    }

    @SuppressWarnings("unchecked")
    public static boolean loadExtent(String filename) {
        String filepath = PersistenceConfig.getDataFilePath(filename);
        try (ObjectInputStream in = new ObjectInputStream(new FileInputStream(filepath))) {
            allEmployees = (List<Employee>) in.readObject();
            return true;
        } catch (Exception e) {
            allEmployees.clear();
            return false;
        }
    }

    @Override
    public String toString() {
        String base = String.format("Employee[%s, type=%s, years=%d, exp=%s]", 
            name, type, getYearsOfService(), getIsExperienced());
        
        if (type == EmployeeType.MANAGER) {
            return base + String.format(" [Mgr: Dept=%s, Lvl=%d]", department, accessLevel);
        } else {
            return base + String.format(" [Wtr: Sect=%s, Tip=%.2f]", section, tipTotal);
        }
    }
}

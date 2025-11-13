import java.io.*;
import java.time.LocalDate;
import java.time.Period;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public abstract class Employee implements Serializable {
    @Serial
    private static final long serialVersionUID = 1L;

    
    private static List<Employee> allEmployees = new ArrayList<>();

    
    private String name;
    private String email;
    private String phone;
    private LocalDate hireDate;
    private double hourlyRate;

    
    protected Employee() {}

    
    protected Employee(String name, String email, String phone, LocalDate hireDate, double hourlyRate) {
        setName(name);
        setEmail(email);
        setPhone(phone);
        setHireDate(hireDate);
        setHourlyRate(hourlyRate);
        addEmployee(this);
    }

    
    public String getName() { return name; }
    public String getEmail() { return email; }
    public String getPhone() { return phone; }
    public LocalDate getHireDate() { return hireDate; }
    public double getHourlyRate() { return hourlyRate; }

    
    public int getYearsOfService() {
        if (hireDate == null) {
            return 0;
        }
        return Period.between(hireDate, LocalDate.now()).getYears();
    }

    
    public boolean getIsExperienced() {
        return getYearsOfService() >= 5;
    }

    
    public void setName(String name) {
        if (name == null || name.trim().isEmpty()) {
            throw new IllegalArgumentException("Employee name cannot be null or empty");
        }
        this.name = name.trim();
    }

    public void setEmail(String email) {
        if (email == null || email.trim().isEmpty()) {
            throw new IllegalArgumentException("Email cannot be null or empty");
        }
        if (!email.contains("@")) {
            throw new IllegalArgumentException("Email must contain @ symbol");
        }
        this.email = email.trim();
    }

    public void setPhone(String phone) {
        if (phone == null || phone.trim().isEmpty()) {
            throw new IllegalArgumentException("Phone cannot be null or empty");
        }
        this.phone = phone.trim();
    }

    public void setHireDate(LocalDate hireDate) {
        if (hireDate == null) {
            throw new IllegalArgumentException("Hire date cannot be null");
        }
        if (hireDate.isAfter(LocalDate.now())) {
            throw new IllegalArgumentException("Hire date cannot be in the future");
        }
        this.hireDate = hireDate;
    }

    public void setHourlyRate(double hourlyRate) {
        if (hourlyRate <= 0) {
            throw new IllegalArgumentException("Hourly rate must be greater than zero");
        }
        this.hourlyRate = hourlyRate;
    }

    
    private static void addEmployee(Employee employee) {
        if (employee == null) {
            throw new IllegalArgumentException("Employee cannot be null");
        }
        allEmployees.add(employee);
    }

    public static List<Employee> getAllEmployees() {
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
        } catch (FileNotFoundException e) {
            allEmployees.clear();
            return false;
        } catch (IOException | ClassNotFoundException e) {
            allEmployees.clear();
            return false;
        }
    }

    @Override
    public String toString() {
        return String.format("Employee[%s, email=%s, phone=%s, hired=%s, yearsOfService=%d, experienced=%s, rate=%.2f]",
            name, email, phone, hireDate, getYearsOfService(), getIsExperienced(), hourlyRate);
    }
}

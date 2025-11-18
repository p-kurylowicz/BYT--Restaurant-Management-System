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
    private ContactInfo contactInfo;
    private LocalDate hireDate;
    private double hourlyRate;

    
    protected Employee() {}


    protected Employee(String name, String phone, String email, String address, LocalDate hireDate, double hourlyRate) {
        setName(name);
        setContactInfo(new ContactInfo(phone, email, address));
        setHireDate(hireDate);
        setHourlyRate(hourlyRate);
        addEmployee(this);
    }

    protected Employee(String name, ContactInfo contactInfo, LocalDate hireDate, double hourlyRate) {
        setName(name);
        setContactInfo(contactInfo);
        setHireDate(hireDate);
        setHourlyRate(hourlyRate);
        addEmployee(this);
    }


    public String getName() { return name; }
    public ContactInfo getContactInfo() { return contactInfo; }
    public String getEmail() { return contactInfo != null ? contactInfo.getEmail() : null; }
    public String getPhone() { return contactInfo != null ? contactInfo.getPhone() : null; }
    public String getAddress() { return contactInfo != null ? contactInfo.getAddress() : null; }
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

    public void setContactInfo(ContactInfo contactInfo) {
        if (contactInfo == null) {
            throw new IllegalArgumentException("Contact info cannot be null");
        }
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
        return String.format("Employee[%s, contact=%s, hired=%s, yearsOfService=%d, experienced=%s, rate=%.2f]",
            name, contactInfo, hireDate, getYearsOfService(), getIsExperienced(), hourlyRate);
    }
}

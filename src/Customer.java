import java.io.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class Customer implements Serializable {
    @Serial
    private static final long serialVersionUID = 1L;

    private static List<Customer> allCustomers = new ArrayList<>();

    private String name;
    private String surname;
    private String email;
    private String phone;
    private LocalDateTime registrationDate;

    public Customer() {}

    public Customer(String name, String surname, String email, String phone, LocalDateTime registrationDate) {
        setName(name);
        setSurname(surname);
        setEmail(email);
        setPhone(phone);
        setRegistrationDate(registrationDate);
        addCustomer(this);
    }

    public String getName() { return name; }
    public String getSurname() { return surname; }
    public String getEmail() { return email; }
    public String getPhone() { return phone; }
    public LocalDateTime getRegistrationDate() { return registrationDate; }
    public void setName(String name) {
        if (name == null || name.trim().isEmpty()) {
            throw new IllegalArgumentException("Name cannot be null or empty");
        }
        this.name = name.trim();
    }

    public void setSurname(String surname) {
        if (surname == null || surname.trim().isEmpty()) {
            throw new IllegalArgumentException("Surname cannot be null or empty");
        }
        this.surname = surname.trim();
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

    public void setRegistrationDate(LocalDateTime registrationDate) {
        if (registrationDate == null) {
            throw new IllegalArgumentException("Registration date cannot be null");
        }
        if (registrationDate.isAfter(LocalDateTime.now())) {
            throw new IllegalArgumentException("Registration date cannot be in the future");
        }
        this.registrationDate = registrationDate;
    }

    private static void addCustomer(Customer customer) {
        if (customer == null) {
            throw new IllegalArgumentException("Customer cannot be null");
        }
        allCustomers.add(customer);
    }

    public static List<Customer> getAllCustomers() {
        return Collections.unmodifiableList(allCustomers);
    }

    public static void clearExtent() {
        allCustomers.clear();
    }

    public static void saveExtent(String filename) throws IOException {
        String filepath = PersistenceConfig.getDataFilePath(filename);
        try (ObjectOutputStream out = new ObjectOutputStream(new FileOutputStream(filepath))) {
            out.writeObject(allCustomers);
        }
    }

    @SuppressWarnings("unchecked")
    public static boolean loadExtent(String filename) {
        String filepath = PersistenceConfig.getDataFilePath(filename);
        try (ObjectInputStream in = new ObjectInputStream(new FileInputStream(filepath))) {
            allCustomers = (List<Customer>) in.readObject();
            return true;
        } catch (IOException | ClassNotFoundException e) {
            allCustomers.clear();
            return false;
        }
    }

    @Override
    public String toString() {
        return String.format("Customer[%s %s, email=%s, phone=%s, registered=%s]",
            name, surname, email, phone, registrationDate);
    }
}

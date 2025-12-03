import java.io.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class Customer implements Serializable {
    @Serial
    private static final long serialVersionUID = 1L;

    private static List<Customer> allCustomers = new ArrayList<>();

    private String name;
    private String surname;
    private String email;
    private String phone;
    private LocalDateTime registrationDate;
    private List<Feedback> feedbacks = new ArrayList<>();

    // Basic Association: Customer -> Order (0..*)
    private Set<Order> orders = new HashSet<>();

    private Map<LocalDateTime, Reservation> reservations = new HashMap<>();
    
    public Customer() {
        this.orders = new HashSet<>();
    }

    public Customer(String name, String surname, String email, String phone, LocalDateTime registrationDate) {
        this.orders = new HashSet<>();
        setName(name);
        setSurname(surname);
        setEmail(email);
        setPhone(phone);
        setRegistrationDate(registrationDate);
        addCustomer(this);
    }

    public Set<Order> getOrders() {
        return Collections.unmodifiableSet(orders);
    }

    public void addOrder(Order order) {
        if (order == null) {
            throw new IllegalArgumentException("Order cannot be null");
        }
        if (!orders.contains(order)) {
            orders.add(order);
            order.setCustomer(this);
        }
    }

    public void removeOrder(Order order) {
        if (order == null) {
            throw new IllegalArgumentException("Order cannot be null");
        }
        if (orders.contains(order)) {
            orders.remove(order);
            order.removeCustomer();
        }
    }


    public Map<LocalDateTime, Reservation> getReservations() {
        return Collections.unmodifiableMap(reservations);
    }

    public void addReservation(Reservation reservation) {
        if (reservation == null) {
            throw new IllegalArgumentException("Reservation cannot be null");
        }
        
        LocalDateTime key = LocalDateTime.of(reservation.getDate(), reservation.getTime());

        if (!reservations.containsKey(key)) {
            reservations.put(key, reservation);
            reservation.setCustomer(this);
        }
    }

    public Reservation getReservation(LocalDateTime dateAndTime) {
        if (dateAndTime == null) return null;
        return reservations.get(dateAndTime);
    }

    public void removeReservation(Reservation reservation) {
        if (reservation != null) {
            LocalDateTime key = LocalDateTime.of(reservation.getDate(), reservation.getTime());
            if (reservations.containsKey(key)) {
                reservations.remove(key);
                reservation.setCustomer(null);
            }
        }
    }

    /**
     * Updates the key for a reservation in the map when the qualifier (date/time) changes.
     * This method handles re-keying without triggering reverse connections.
     *
     * @param reservation The reservation whose key needs updating
     * @param oldKey The old LocalDateTime key
     * @param newKey The new LocalDateTime key
     */
    void updateReservationKey(Reservation reservation, LocalDateTime oldKey, LocalDateTime newKey) {
        if (reservation == null || oldKey == null || newKey == null) {
            throw new IllegalArgumentException("Reservation and keys cannot be null");
        }

        // Remove old key and add with new key without triggering setCustomer
        if (reservations.containsKey(oldKey)) {
            reservations.remove(oldKey);
            reservations.put(newKey, reservation);
        }
    }

    public String getName() { return name; }
    public String getSurname() { return surname; }
    public String getEmail() { return email; }
    public String getPhone() { return phone; }
    public LocalDateTime getRegistrationDate() { return registrationDate; }
    public List<Feedback> getFeedbacks() {
        return Collections.unmodifiableList(feedbacks);
    }

    
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

    void addFeedback(Feedback feedback) {
        if (feedback == null) throw new IllegalArgumentException("Feedback cannot be null");
        feedbacks.add(feedback);
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

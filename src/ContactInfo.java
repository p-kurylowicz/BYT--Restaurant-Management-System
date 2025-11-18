import java.io.Serial;
import java.io.Serializable;

/**
 * Complex attribute representing contact information.
 * Groups phone, email, and address as an atomic unit.
 */
public class ContactInfo implements Serializable {
    @Serial
    private static final long serialVersionUID = 1L;

    private String phone;
    private String email;
    private String address;


    public ContactInfo() {}


    public ContactInfo(String phone, String email, String address) {
        validatePhone(phone);
        validateEmail(email);
        validateAddress(address);

        this.phone = phone.trim();
        this.email = email.trim();
        this.address = address.trim();
    }


    public String getPhone() {
        return phone;
    }

    public String getEmail() {
        return email;
    }

    public String getAddress() {
        return address;
    }


    private void validatePhone(String phone) {
        if (phone == null || phone.trim().isEmpty()) {
            throw new IllegalArgumentException("Phone cannot be null or empty");
        }
    }

    private void validateEmail(String email) {
        if (email == null || email.trim().isEmpty()) {
            throw new IllegalArgumentException("Email cannot be null or empty");
        }
        if (!email.contains("@")) {
            throw new IllegalArgumentException("Email must contain @ symbol");
        }
    }

    private void validateAddress(String address) {
        if (address == null || address.trim().isEmpty()) {
            throw new IllegalArgumentException("Address cannot be null or empty");
        }
    }


    public void updateContactInfo(String phone, String email, String address) {
        validatePhone(phone);
        validateEmail(email);
        validateAddress(address);

        this.phone = phone.trim();
        this.email = email.trim();
        this.address = address.trim();
    }

    @Override
    public String toString() {
        return String.format("ContactInfo[phone=%s, email=%s, address=%s]",
            phone, email, address);
    }


}

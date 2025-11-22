import java.io.Serial;
import java.io.Serializable;
import java.util.Arrays;
import java.util.List;

public class Address implements Serializable {
    @Serial
    private static final long serialVersionUID = 1L;

    private static final List<String> APPROVED_COUNTRIES = Arrays.asList("PL", "DE", "US", "FR");

    // Basic Attribute
    private String street;
    // Basic Attribute
    private String city;
    // Basic Attribute
    private String zipCode;
    // Basic Attribute
    private String country; 

    public Address() {}

    public Address(String street, String city, String zipCode, String country) {
        setStreet(street);
        setCity(city);
        setZipCode(zipCode);
        setCountry(country);
    }

    public String getStreet() { return street; }
    public String getCity() { return city; }
    public String getZipCode() { return zipCode; }
    public String getCountry() { return country; }

    public void setStreet(String street) {
        if (street == null || street.trim().isEmpty()) {
            throw new IllegalArgumentException("Street cannot be null or empty");
        }
        this.street = street.trim();
    }

    public void setCity(String city) {
        if (city == null || city.trim().isEmpty()) {
            throw new IllegalArgumentException("City cannot be null or empty");
        }
        this.city = city.trim();
    }

    public void setZipCode(String zipCode) {
        if (zipCode == null || zipCode.trim().isEmpty()) {
            throw new IllegalArgumentException("Zip code cannot be null or empty");
        }
        if (zipCode.trim().length() < 3 || zipCode.trim().length() > 10) {
            throw new IllegalArgumentException("Zip code must be between 3 and 10 characters");
        }
        this.zipCode = zipCode.trim();
    }

    public void setCountry(String country) {
        if (country == null || country.trim().isEmpty()) {
            throw new IllegalArgumentException("Country cannot be null or empty");
        }
        String upperCountry = country.trim().toUpperCase();

        if (!APPROVED_COUNTRIES.contains(upperCountry)) {
            throw new IllegalArgumentException(
                "Country '" + country + "' is not an approved billing region: " + APPROVED_COUNTRIES
            );
        }
        this.country = upperCountry;
    }

    @Override
    public String toString() {
        return String.format("%s, %s, %s, %s", street, city, zipCode, country);
    }
}

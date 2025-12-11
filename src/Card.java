import java.io.Serial;

public class Card extends Payment {
    @Serial
    private static final long serialVersionUID = 1L;
    private static final double LARGE_TRANSACTION_THRESHOLD = 500.0;


    private String authorizationCode;
    private String lastFourDigits;
    private String cardType;


    /**
     * Creates a Card payment for the given Order.
     * Composition: Card cannot exist without an Order.
     *
     * @param amountPayed The payment amount
     * @param order The Order this Card payment belongs to
     * @param lastFourDigits Last 4 digits of the card
     * @param cardType Type of card (Visa, MasterCard, etc.)
     */
    public Card(double amountPayed, Order order, String lastFourDigits, String cardType) {
        super(amountPayed, order);
        setLastFourDigits(lastFourDigits);
        setCardType(cardType);

        // Set to IN_TRANSACTION if amount > 500 PLN
        if (amountPayed > LARGE_TRANSACTION_THRESHOLD) {
            setInTransaction();
        }
    }


    public String getAuthorizationCode() { return authorizationCode; }
    public String getLastFourDigits() { return lastFourDigits; }
    public String getCardType() { return cardType; }

    
    public void setAuthorizationCode(String authorizationCode) {
        if (authorizationCode != null && !authorizationCode.trim().isEmpty()) {
            this.authorizationCode = authorizationCode.trim();
        } else {
            this.authorizationCode = null;
        }
    }

    public void setLastFourDigits(String lastFourDigits) {
        if (lastFourDigits == null || lastFourDigits.trim().isEmpty()) {
            throw new IllegalArgumentException("Last four digits cannot be null or empty");
        }
        if (lastFourDigits.length() != 4 || !lastFourDigits.matches("\\d{4}")) {
            throw new IllegalArgumentException("Last four digits must be exactly 4 digits");
        }
        this.lastFourDigits = lastFourDigits;
    }

    public void setCardType(String cardType) {
        if (cardType == null || cardType.trim().isEmpty()) {
            throw new IllegalArgumentException("Card type cannot be null or empty");
        }
        this.cardType = cardType.trim();
    }

    @Override
    public String toString() {
        return String.format("Card[%s ending in %s, authCode=%s, %s]",
            cardType, lastFourDigits, authorizationCode, super.toString());
    }
}
